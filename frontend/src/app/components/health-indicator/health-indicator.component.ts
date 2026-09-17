import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NzTooltipModule } from 'ng-zorro-antd/tooltip';
import { HealthService } from '../../services/health.service';

type HealthStatus = 'checking' | 'up' | 'down';

const POLL_INTERVAL_MS = 30_000;

/**
 * 全局健康指示灯：在页面不显眼处用一个小圆点表示后端连通性。
 *
 * <p>绿色=正常，红色=不可用，灰色=检测中；鼠标悬停显示状态与检测时间。
 * 挂载后立即检测一次，之后每 30 秒轮询一次 {@code GET /api/health}。
 */
@Component({
  selector: 'app-health-indicator',
  standalone: true,
  imports: [NzTooltipModule],
  templateUrl: './health-indicator.component.html',
  styleUrl: './health-indicator.component.less'
})
export class HealthIndicatorComponent implements OnInit {
  private readonly healthService = inject(HealthService);
  private readonly destroyRef = inject(DestroyRef);

  readonly status = signal<HealthStatus>('checking');
  readonly tooltip = signal('检测中…');

  private timer?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.check();
    this.timer = setInterval(() => this.check(), POLL_INTERVAL_MS);
    this.destroyRef.onDestroy(() => clearInterval(this.timer));
  }

  private check(): void {
    this.healthService
      .getHealth()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (health) => {
          this.status.set(health.status === 'UP' ? 'up' : 'down');
          this.tooltip.set(`后端服务：${health.status} · ${this.formatTime(new Date())}`);
        },
        error: () => {
          this.status.set('down');
          this.tooltip.set(`后端服务：不可用 · ${this.formatTime(new Date())}`);
        }
      });
  }

  private formatTime(date: Date): string {
    const pad = (value: number) => `${value}`.padStart(2, '0');
    return `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
  }
}
