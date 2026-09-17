export type ProjectStatus = 'PLANNING' | 'IN_PROGRESS' | 'ON_HOLD' | 'COMPLETED' | 'CANCELLED';

export type ProjectPriority = 'LOW' | 'MEDIUM' | 'HIGH';

/** 项目（对应后端 ProjectResponse）。金额字段为 2 位小数，利润由后端派生。 */
export interface Project {
  id: number;
  code: string;
  name: string;
  description: string | null;
  status: ProjectStatus;
  priority: ProjectPriority;
  owner: string | null;
  startDate: string | null;
  endDate: string | null;
  progress: number;
  receivableAmount: number;
  payableAmount: number;
  receivedAmount: number;
  paidAmount: number;
  /** 预计利润 = 应收 - 应付 */
  expectedProfit: number;
  /** 实际利润 = 实收 - 实付 */
  actualProfit: number;
  createdAt: string;
  updatedAt: string;
}

/** 列表金额合计（当前筛选条件下的全部数据，非当前页）。 */
export interface ProjectSummary {
  receivableAmount: number;
  payableAmount: number;
  receivedAmount: number;
  paidAmount: number;
  expectedProfit: number;
  actualProfit: number;
}

/** GET /api/projects 响应体。 */
export interface ProjectPage {
  items: Project[];
  total: number;
  page: number;
  size: number;
  summary: ProjectSummary;
}

/** 列表查询参数。 */
export interface ProjectQuery {
  keyword?: string;
  status?: ProjectStatus | null;
  page?: number;
  size?: number;
}

/** 新建/更新请求体（对应后端 ProjectRequest）。 */
export interface ProjectPayload {
  code: string;
  name: string;
  description?: string | null;
  status: ProjectStatus;
  priority: ProjectPriority;
  owner?: string | null;
  startDate?: string | null;
  endDate?: string | null;
  progress: number;
  receivableAmount: number;
  payableAmount: number;
  receivedAmount: number;
  paidAmount: number;
}

/** 后端统一错误响应（RFC 7807 ProblemDetail）。 */
export interface ProblemDetail {
  status: number;
  title: string;
  detail: string;
  instance?: string;
  /** 仅参数校验失败（400）时出现 */
  errors?: Record<string, string>;
}

export const PROJECT_STATUS_OPTIONS: ReadonlyArray<{
  value: ProjectStatus;
  label: string;
  color: string;
}> = [
  { value: 'PLANNING', label: '规划中', color: 'default' },
  { value: 'IN_PROGRESS', label: '进行中', color: 'processing' },
  { value: 'ON_HOLD', label: '已暂停', color: 'warning' },
  { value: 'COMPLETED', label: '已完成', color: 'success' },
  { value: 'CANCELLED', label: '已取消', color: 'error' }
];

export const PROJECT_PRIORITY_OPTIONS: ReadonlyArray<{
  value: ProjectPriority;
  label: string;
  color: string;
}> = [
  { value: 'LOW', label: '低', color: 'default' },
  { value: 'MEDIUM', label: '中', color: 'blue' },
  { value: 'HIGH', label: '高', color: 'red' }
];
