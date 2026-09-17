import { Component, signal } from '@angular/core';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { API_GROUPS, HttpMethod, PROBLEM_DETAIL_EXAMPLE } from '../../models/api-docs.model';

/**
 * 后端接口文档页（Swagger UI 风格）。
 *
 * <p>接口元数据维护在 {@link API_GROUPS}，页面只负责渲染，便于与后端同步。
 */
@Component({
  selector: 'app-api-docs',
  standalone: true,
  imports: [NzCardModule, NzIconModule],
  templateUrl: './api-docs.component.html',
  styleUrl: './api-docs.component.less'
})
export class ApiDocsComponent {
  readonly groups = API_GROUPS;
  readonly problemExample = PROBLEM_DETAIL_EXAMPLE;

  /** 当前展开的接口（一次只展开一个），值为 `METHOD path` */
  readonly expanded = signal<string | null>(null);

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
