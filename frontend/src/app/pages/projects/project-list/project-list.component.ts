import { DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzPopconfirmModule } from 'ng-zorro-antd/popconfirm';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzTagModule } from 'ng-zorro-antd/tag';
import {
  PROJECT_PRIORITY_OPTIONS,
  PROJECT_STATUS_OPTIONS,
  Project,
  ProjectPriority,
  ProjectStatus,
  ProjectSummary
} from '../../../models/project.model';
import { describeProblem } from '../../../services/problem.util';
import { ProjectService } from '../../../services/project.service';
import { ProjectFormComponent } from '../project-form/project-form.component';

const EMPTY_SUMMARY: ProjectSummary = {
  receivableAmount: 0,
  payableAmount: 0,
  receivedAmount: 0,
  paidAmount: 0,
  expectedProfit: 0,
  actualProfit: 0
};

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [
    DecimalPipe,
    FormsModule,
    NzButtonModule,
    NzCardModule,
    NzIconModule,
    NzInputModule,
    NzPopconfirmModule,
    NzProgressModule,
    NzSelectModule,
    NzTableModule,
    NzTagModule,
    ProjectFormComponent
  ],
  templateUrl: './project-list.component.html',
  styleUrl: './project-list.component.less'
})
export class ProjectListComponent implements OnInit {
  private readonly projectService = inject(ProjectService);
  private readonly message = inject(NzMessageService);

  readonly statusOptions = PROJECT_STATUS_OPTIONS;
  readonly priorityOptions = PROJECT_PRIORITY_OPTIONS;

  readonly projects = signal<Project[]>([]);
  readonly summary = signal<ProjectSummary>(EMPTY_SUMMARY);
  readonly total = signal(0);
  readonly loading = signal(false);

  /** 查询条件（nz-select 清空时为 null） */
  keyword = '';
  status: ProjectStatus | null = null;

  /** nz-table 页码从 1 开始，接口从 0 开始 */
  pageIndex = 1;
  pageSize = 10;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.projectService
      .list({
        keyword: this.keyword.trim(),
        status: this.status,
        page: this.pageIndex - 1,
        size: this.pageSize
      })
      .subscribe({
        next: (page) => {
          this.projects.set(page.items);
          this.summary.set(page.summary);
          this.total.set(page.total);
          this.loading.set(false);
        },
        error: (error: unknown) => {
          this.loading.set(false);
          this.message.error(describeProblem(error));
        }
      });
  }

  search(): void {
    this.pageIndex = 1;
    this.load();
  }

  reset(): void {
    this.keyword = '';
    this.status = null;
    this.pageIndex = 1;
    this.load();
  }

  onPageIndexChange(pageIndex: number): void {
    this.pageIndex = pageIndex;
    this.load();
  }

  onPageSizeChange(pageSize: number): void {
    this.pageSize = pageSize;
    this.pageIndex = 1;
    this.load();
  }

  /** 软删除项目；删除后若当前页已空则回退一页。 */
  remove(project: Project): void {
    this.projectService.remove(project.id).subscribe({
      next: () => {
        this.message.success(`已删除「${project.name}」`);
        if (this.projects().length === 1 && this.pageIndex > 1) {
          this.pageIndex -= 1;
        }
        this.load();
      },
      error: (error: unknown) => this.message.error(describeProblem(error))
    });
  }

  statusLabel(status: ProjectStatus): string {
    return this.statusOptions.find((option) => option.value === status)?.label ?? status;
  }

  statusColor(status: ProjectStatus): string {
    return this.statusOptions.find((option) => option.value === status)?.color ?? 'default';
  }

  priorityLabel(priority: ProjectPriority): string {
    return this.priorityOptions.find((option) => option.value === priority)?.label ?? priority;
  }

  priorityColor(priority: ProjectPriority): string {
    return this.priorityOptions.find((option) => option.value === priority)?.color ?? 'default';
  }
}
