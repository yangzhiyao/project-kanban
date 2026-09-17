import { Component, DestroyRef, EventEmitter, Output, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NzDatePickerModule } from 'ng-zorro-antd/date-picker';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzInputNumberModule } from 'ng-zorro-antd/input-number';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { NzSelectModule } from 'ng-zorro-antd/select';
import {
  PROJECT_PRIORITY_OPTIONS,
  PROJECT_STATUS_OPTIONS,
  Project,
  ProjectPayload,
  ProblemDetail
} from '../../../models/project.model';
import { describeProblem } from '../../../services/problem.util';
import { ProjectService } from '../../../services/project.service';

/** 各字段的校验提示文案，键为「错误类型」。 */
const FIELD_MESSAGES: Record<string, Record<string, string>> = {
  code: { required: '请输入项目编号', pattern: '仅允许字母、数字、- 和 _，长度 2-32' },
  name: { required: '请输入项目名称', maxlength: '项目名称最长 100 字' },
  description: { maxlength: '项目描述最长 1000 字' },
  status: { required: '请选择项目状态' },
  priority: { required: '请选择优先级' },
  owner: { maxlength: '负责人最长 50 字' },
  endDate: { dateRange: '结束日期不能早于开始日期' },
  progress: { min: '进度不能小于 0', max: '进度不能大于 100' },
  receivableAmount: { min: '应收金额不能为负数' },
  payableAmount: { min: '应付金额不能为负数' },
  receivedAmount: { min: '实收金额不能为负数' },
  paidAmount: { min: '实付金额不能为负数' }
};

/**
 * 项目新建/编辑弹窗。
 *
 * <p>由父组件通过模板引用调用 {@link open}：传 {@code null} 表示新建，传项目表示编辑；
 * 保存成功后触发 {@link saved} 事件，父组件据此刷新列表。
 *
 * <p>校验提示不使用 ng-zorro 的 {@code nzErrorTip}：它只在控件 statusChanges 时重算，
 * 而提交时调用的 {@code markAllAsTouched()} 不会触发该事件（zoneless 下也不会刷新视图）。
 * 这里改为用 signal 保存错误文案，保证提交后能立即显示。
 */
@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NzDatePickerModule,
    NzFormModule,
    NzInputModule,
    NzInputNumberModule,
    NzModalModule,
    NzSelectModule
  ],
  templateUrl: './project-form.component.html',
  styleUrl: './project-form.component.less'
})
export class ProjectFormComponent {
  @Output() readonly saved = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly projectService = inject(ProjectService);
  private readonly message = inject(NzMessageService);
  private readonly destroyRef = inject(DestroyRef);

  readonly statusOptions = PROJECT_STATUS_OPTIONS;
  readonly priorityOptions = PROJECT_PRIORITY_OPTIONS;

  readonly visible = signal(false);
  readonly submitting = signal(false);
  readonly fieldErrors = signal<Record<string, string>>({});

  /** 为空表示新建，否则为被编辑项目的 id */
  editingId: number | null = null;

  /** 是否点击过保存，用于「未交互也要提示必填」 */
  private submitted = false;

  readonly form = this.fb.group({
    code: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9_-]{2,32}$/)]],
    name: ['', [Validators.required, Validators.maxLength(100)]],
    description: [null as string | null, [Validators.maxLength(1000)]],
    status: ['PLANNING', [Validators.required]],
    priority: ['MEDIUM', [Validators.required]],
    owner: [null as string | null, [Validators.maxLength(50)]],
    startDate: [null as Date | null],
    endDate: [null as Date | null],
    progress: [0 as number | null, [Validators.min(0), Validators.max(100)]],
    receivableAmount: [0 as number | null, [Validators.min(0)]],
    payableAmount: [0 as number | null, [Validators.min(0)]],
    receivedAmount: [0 as number | null, [Validators.min(0)]],
    paidAmount: [0 as number | null, [Validators.min(0)]]
  });

  constructor() {
    this.form.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => this.refreshErrors());
  }

  get title(): string {
    return this.editingId === null ? '新建项目' : '编辑项目';
  }

  /** 打开弹窗：project 为 null 时进入新建模式。 */
  open(project: Project | null): void {
    this.editingId = project?.id ?? null;
    this.submitted = false;
    this.form.reset({
      code: project?.code ?? '',
      name: project?.name ?? '',
      description: project?.description ?? null,
      status: project?.status ?? 'PLANNING',
      priority: project?.priority ?? 'MEDIUM',
      owner: project?.owner ?? null,
      startDate: this.fromIsoDate(project?.startDate ?? null),
      endDate: this.fromIsoDate(project?.endDate ?? null),
      progress: project?.progress ?? 0,
      receivableAmount: project?.receivableAmount ?? 0,
      payableAmount: project?.payableAmount ?? 0,
      receivedAmount: project?.receivedAmount ?? 0,
      paidAmount: project?.paidAmount ?? 0
    });
    this.form.markAsPristine();
    this.form.markAsUntouched();
    this.refreshErrors();
    this.visible.set(true);
  }

  close(): void {
    this.visible.set(false);
  }

  submit(): void {
    this.submitted = true;
    this.validateDateRange();
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.refreshErrors();
      return;
    }

    const payload = this.toPayload();
    const creating = this.editingId === null;
    const request = creating
      ? this.projectService.create(payload)
      : this.projectService.update(this.editingId as number, payload);

    this.submitting.set(true);
    request.subscribe({
      next: () => {
        this.submitting.set(false);
        this.message.success(creating ? '项目已创建' : '项目已更新');
        this.visible.set(false);
        this.saved.emit();
      },
      error: (error: unknown) => {
        this.submitting.set(false);
        this.applyServerErrors(error);
        this.message.error(describeProblem(error));
      }
    });
  }

  private toPayload(): ProjectPayload {
    const value = this.form.getRawValue();
    return {
      code: (value.code ?? '').trim(),
      name: (value.name ?? '').trim(),
      description: this.nullIfBlank(value.description),
      status: value.status as ProjectPayload['status'],
      priority: value.priority as ProjectPayload['priority'],
      owner: this.nullIfBlank(value.owner),
      startDate: this.toIsoDate(value.startDate),
      endDate: this.toIsoDate(value.endDate),
      progress: value.progress ?? 0,
      receivableAmount: value.receivableAmount ?? 0,
      payableAmount: value.payableAmount ?? 0,
      receivedAmount: value.receivedAmount ?? 0,
      paidAmount: value.paidAmount ?? 0
    };
  }

  /** 后端返回的字段级校验错误回填到对应控件，实现行内提示。 */
  private applyServerErrors(error: unknown): void {
    const problem = (error as { error?: ProblemDetail })?.error;
    if (!problem?.errors) {
      this.refreshErrors();
      return;
    }
    Object.entries(problem.errors).forEach(([field, message]) => {
      this.form.get(field)?.setErrors({ server: message });
    });
    this.form.markAllAsTouched();
    this.refreshErrors();
  }

  /** 汇总需要展示的错误文案（提交过、或字段已交互且不合法）。 */
  private refreshErrors(): void {
    const messages: Record<string, string> = {};

    Object.keys(this.form.controls).forEach((name) => {
      const control = this.form.get(name);
      if (!control || control.valid) {
        return;
      }
      if (!this.submitted && !control.touched && !control.dirty) {
        return;
      }
      const message = this.messageFor(name, control.errors);
      if (message) {
        messages[name] = message;
      }
    });

    this.fieldErrors.set(messages);
  }

  private messageFor(name: string, errors: AbstractControl['errors']): string | null {
    if (!errors) {
      return null;
    }
    if (errors['server']) {
      return String(errors['server']);
    }
    const messages = FIELD_MESSAGES[name] ?? {};
    for (const key of Object.keys(messages)) {
      if (errors[key]) {
        return messages[key];
      }
    }
    return '输入不合法';
  }

  private validateDateRange(): void {
    const { startDate, endDate } = this.form.getRawValue();
    const control = this.form.controls.endDate;
    const errors: Record<string, unknown> = { ...(control.errors ?? {}) };
    delete errors['server'];
    delete errors['dateRange'];

    if (startDate && endDate && endDate.getTime() < startDate.getTime()) {
      errors['dateRange'] = true;
    }
    control.setErrors(Object.keys(errors).length ? errors : null);
  }

  private nullIfBlank(value: string | null | undefined): string | null {
    const trimmed = (value ?? '').trim();
    return trimmed.length ? trimmed : null;
  }

  private toIsoDate(value: Date | null | undefined): string | null {
    if (!value) {
      return null;
    }
    const month = `${value.getMonth() + 1}`.padStart(2, '0');
    const day = `${value.getDate()}`.padStart(2, '0');
    return `${value.getFullYear()}-${month}-${day}`;
  }

  private fromIsoDate(value: string | null): Date | null {
    if (!value) {
      return null;
    }
    const [year, month, day] = value.split('-').map(Number);
    return new Date(year, month - 1, day);
  }
}
