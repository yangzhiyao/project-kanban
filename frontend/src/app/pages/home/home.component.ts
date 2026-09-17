import { Component, OnInit, inject, signal } from '@angular/core';
import { NzAlertModule } from 'ng-zorro-antd/alert';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { HealthResponse } from '../../models/health.model';
import { HealthService } from '../../services/health.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NzAlertModule, NzCardModule, NzSpinModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.less'
})
export class HomeComponent implements OnInit {
  private readonly healthService = inject(HealthService);

  readonly loading = signal(true);
  readonly health = signal<HealthResponse | null>(null);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.healthService.getHealth().subscribe({
      next: (health) => {
        this.health.set(health);
        this.loading.set(false);
      },
      error: (err: Error) => {
        this.error.set(err.message);
        this.loading.set(false);
      }
    });
  }
}
