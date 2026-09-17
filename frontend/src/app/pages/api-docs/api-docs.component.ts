import { Component, OnInit, inject, signal } from '@angular/core';
import { NzAlertModule } from 'ng-zorro-antd/alert';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { environment } from '../../../environments/environment';
import { ApiGroup, HttpMethod } from '../../models/api-docs.model';
import { ApiDocsService } from '../../services/api-docs.service';
import { describeProblem } from '../../services/problem.util';
import { toApiGroups } from './openapi.mapper';

interface ApiInfo {
  title: string;
  version: string;
  description: string;
}

/**
 * 后端接口文档页（Swagger UI 风格），数据来自 springdoc 自动生成的 OpenAPI 规范。
 *
 * <p>规范地址为 {@code /api/openapi}，与后端注解保持单一数据源；
 * 页面只负责把 {@link toApiGroups} 转换后的视图模型渲染出来。
 */
@Component({
  selector: 'app-api-docs',
  standalone: true,
  imports: [NzAlertModule, NzButtonModule, NzCardModule, NzIconModule, NzSpinModule],
  templateUrl: './api-docs.component.html',
  styleUrl: './api-docs.component.less'
})
export class ApiDocsComponent implements OnInit {
  private readonly apiDocsService = inject(ApiDocsService);

  readonly baseUrl = environment.apiUrl;
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly info = signal<ApiInfo | null>(null);
  readonly specVersion = signal('');
  readonly groups = signal<ApiGroup[]>([]);

  /** 当前展开的接口（一次只展开一个），值为 `METHOD path` */
  readonly expanded = signal<string | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);

    this.apiDocsService.getSpec().subscribe({
      next: (spec) => {
        this.info.set({
          title: spec.info.title ?? '后端接口文档',
          version: spec.info.version ?? '',
          description: spec.info.description ?? ''
        });
        this.specVersion.set(spec.openapi);
        this.groups.set(toApiGroups(spec));
        this.expanded.set(null);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.error.set(describeProblem(error));
        this.loading.set(false);
      }
    });
  }

  toggle(method: HttpMethod, path: string): void {
    const key = this.keyOf(method, path);
    this.expanded.update((current) => (current === key ? null : key));
  }

  isExpanded(method: HttpMethod, path: string): boolean {
    return this.expanded() === this.keyOf(method, path);
  }

  private keyOf(method: HttpMethod, path: string): string {
    return `${method} ${path}`;
  }
}
