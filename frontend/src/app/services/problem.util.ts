import { HttpErrorResponse } from '@angular/common/http';
import { ProblemDetail } from '../models/project.model';

/**
 * 从后端统一错误响应（RFC 7807 ProblemDetail）中提取可直接展示的提示文本。
 * 字段校验失败时优先返回第一条字段级错误。
 */
export function describeProblem(error: unknown): string {
  const problem = error instanceof HttpErrorResponse ? (error.error as ProblemDetail | null) : null;
  const fieldError = problem?.errors ? Object.values(problem.errors)[0] : undefined;

  if (fieldError) {
    return fieldError;
  }
  if (problem?.detail) {
    return problem.detail;
  }
  if (error instanceof HttpErrorResponse) {
    return error.status === 0 ? '无法连接后端服务' : `请求失败（HTTP ${error.status}）`;
  }
  return '请求失败，请稍后重试';
}
