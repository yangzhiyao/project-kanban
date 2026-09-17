import { DOCUMENT } from '@angular/common';
import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NzTooltipModule } from 'ng-zorro-antd/tooltip';
import { timeout } from 'rxjs';
import { HealthService } from '../../services/health.service';

type HealthStatus = 'checking' | 'up' | 'down';

/** 轮询间隔 */
const POLL_INTERVAL_MS = 30_000;
/** 单次检测超时，避免后端/代理挂住时状态一直停留在旧值 */
const REQUEST_TIMEOUT_MS = 5_000;

/**
 * 全局健康指示灯：在页面不显眼处用一个小圆点表示后端连通性。
 *
 * <p>绿色=正常，红色=不可用，灰色=检测中。挂载后立即检测，之后每 30 秒轮询一次；
 * 另外在浏览器标签页重新可见时立即补测一次（后台标签的定时器会被浏览器节流），
 * 点击指示灯也可手动触发检测。鼠标悬停显示状态、检测时间与操作提示。
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
  private readonly document = inject(DOCUMENT);

  readonly status = signal<HealthStatus>('checking');
  readonly tooltip = signal('检测中…');

  private timer?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.check();

    this.timer = setInterval(() => this.check(), POLL_INTERVAL_MS);
    this.document.addEventListener('visibilitychange', this.onVisibilityChange);
    this.destroyRef.onDestroy(() => {
      clearInterval(this.timer);
      this.document.removeEventListener('visibilitychange', this.onVisibilityChange);
    });
  }

  /** 手动检测一次（点击指示灯）。 */
  refresh(): void {
    this.check();
  }

  private readonly onVisibilityChange = (): void => {
    if (this.document.visibilityState === 'visible') {
      this.check();
    }
  };

  private check(): void {
    this.healthService
      .getHealth()
      .pipe(timeout(REQUEST_TIMEOUT_MS), takeUntilDestroyed(this.destroyRef))
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
