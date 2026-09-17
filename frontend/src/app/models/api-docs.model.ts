export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

/** 接口参数或请求体字段说明。 */
export interface ApiField {
  name: string;
  type: string;
  required: boolean;
  description: string;
}

/** 单个接口。 */
export interface ApiEndpoint {
  method: HttpMethod;
  path: string;
  summary: string;
  description?: string;
  pathParams?: ApiField[];
  queryParams?: ApiField[];
  bodyFields?: ApiField[];
  requestExample?: string;
  responseExample: string;
  responses: { status: string; description: string }[];
}

/** 按业务分组的一组接口。 */
export interface ApiGroup {
  tag: string;
  description: string;
  endpoints: ApiEndpoint[];
}

const PROJECT_BODY_FIELDS: ApiField[] = [
  { name: 'code', type: 'string', required: true, description: '项目编号，全局唯一，仅字母/数字/-/_，长度 2-32' },
  { name: 'name', type: 'string', required: true, description: '项目名称，最长 100' },
  { name: 'description', type: 'string', required: false, description: '项目描述，最长 1000' },
  { name: 'status', type: 'enum', required: true, description: 'PLANNING | IN_PROGRESS | ON_HOLD | COMPLETED | CANCELLED' },
  { name: 'priority', type: 'enum', required: true, description: 'LOW | MEDIUM | HIGH' },
  { name: 'owner', type: 'string', required: false, description: '负责人，最长 50' },
  { name: 'startDate', type: 'date', required: false, description: '计划开始日期，yyyy-MM-dd' },
  { name: 'endDate', type: 'date', required: false, description: '计划结束日期，不得早于 startDate' },
  { name: 'progress', type: 'number', required: false, description: '进度百分比 0-100，缺省 0' },
  { name: 'receivableAmount', type: 'number', required: false, description: '应收金额，非负，最多 2 位小数，缺省 0' },
  { name: 'payableAmount', type: 'number', required: false, description: '应付金额，非负，最多 2 位小数，缺省 0' },
  { name: 'receivedAmount', type: 'number', required: false, description: '实收金额，非负，最多 2 位小数，缺省 0' },
  { name: 'paidAmount', type: 'number', required: false, description: '实付金额，非负，最多 2 位小数，缺省 0' }
];

const PROJECT_RESPONSE_EXAMPLE = `{
  "id": 1,
  "code": "PRJ-0001",
  "name": "企业门户网站重构",
  "description": "统一门户与内容管理平台重构",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "owner": "张三",
  "startDate": "2026-01-06",
  "endDate": "2026-06-30",
  "progress": 45,
  "receivableAmount": 1200000.00,
  "payableAmount": 700000.00,
  "receivedAmount": 500000.00,
  "paidAmount": 300000.00,
  "expectedProfit": 500000.00,
  "actualProfit": 200000.00,
  "createdAt": "2026-01-05T01:00:00Z",
  "updatedAt": "2026-03-01T01:00:00Z"
}`;

const PROJECT_PAGE_EXAMPLE = `{
  "items": [ /* ProjectResponse 数组 */ ],
  "total": 8,
  "page": 0,
  "size": 10,
  "summary": {
    "receivableAmount": 9700000.00,
    "payableAmount": 5860000.00,
    "receivedAmount": 4400000.00,
    "paidAmount": 2640000.00,
    "expectedProfit": 3840000.00,
    "actualProfit": 1760000.00
  }
}`;

const PROBLEM_EXAMPLE = `{
  "status": 400,
  "title": "请求参数校验失败",
  "detail": "共 2 项校验未通过",
  "instance": "/api/projects",
  "errors": {
    "code": "项目编号只能包含字母、数字、- 和 _，长度 2-32",
    "name": "项目名称不能为空"
  }
}`;

/** 接口元数据。新增/调整接口时同步维护此文件即可。 */
export const API_GROUPS: readonly ApiGroup[] = [
  {
    tag: '健康检查',
    description: '用于联通性自检与部署探活。',
    endpoints: [
      {
        method: 'GET',
        path: '/api/health',
        summary: '服务状态',
        description: '返回固定 status=UP、应用名与服务器当前时间（UTC），不需要任何参数。',
        responseExample: `{
  "status": "UP",
  "application": "kanban-project",
  "timestamp": "2026-09-17T04:29:06.242896Z"
}`,
        responses: [{ status: '200', description: '服务正常' }]
      }
    ]
  },
  {
    tag: '项目',
    description: '项目 CRUD。删除为软删除（deleted=true），列表与详情均不再返回已删除数据。',
    endpoints: [
      {
        method: 'GET',
        path: '/api/projects',
        summary: '分页查询项目列表',
        description: '按更新时间倒序返回，并给出当前筛选条件下全部数据的金额合计（不是当前页合计）。',
        queryParams: [
          { name: 'keyword', type: 'string', required: false, description: '模糊匹配项目编号或名称（忽略大小写）' },
          { name: 'status', type: 'enum', required: false, description: '按状态精确过滤' },
          { name: 'page', type: 'number', required: false, description: '页码，从 0 开始，默认 0' },
          { name: 'size', type: 'number', required: false, description: '每页条数，默认 10，最大 100' }
        ],
        responseExample: PROJECT_PAGE_EXAMPLE,
        responses: [
          { status: '200', description: '分页结果与合计' },
          { status: '400', description: 'status 枚举值非法' }
        ]
      },
      {
        method: 'GET',
        path: '/api/projects/{id}',
        summary: '查询项目详情',
        pathParams: [{ name: 'id', type: 'number', required: true, description: '项目主键' }],
        responseExample: PROJECT_RESPONSE_EXAMPLE,
        responses: [
          { status: '200', description: '项目详情' },
          { status: '404', description: '项目不存在或已删除' }
        ]
      },
      {
        method: 'POST',
        path: '/api/projects',
        summary: '新建项目',
        description: '编号重复返回 409；日期区间不合法返回 400。',
        bodyFields: PROJECT_BODY_FIELDS,
        requestExample: `{
  "code": "PRJ-0009",
  "name": "新项目",
  "description": "项目描述",
  "status": "PLANNING",
  "priority": "MEDIUM",
  "owner": "张三",
  "startDate": "2026-05-01",
  "endDate": "2026-10-31",
  "progress": 0,
  "receivableAmount": 100000.00,
  "payableAmount": 60000.00,
  "receivedAmount": 0.00,
  "paidAmount": 0.00
}`,
        responseExample: PROJECT_RESPONSE_EXAMPLE,
        responses: [
          { status: '201', description: '创建成功，返回创建后的项目' },
          { status: '400', description: '字段校验失败或日期区间不合法' },
          { status: '409', description: '项目编号已存在' }
        ]
      },
      {
        method: 'PUT',
        path: '/api/projects/{id}',
        summary: '更新项目（全量覆盖）',
        description: '未提交的可选字段会回落到默认值；编号可修改，但不得与其他项目重复。',
        pathParams: [{ name: 'id', type: 'number', required: true, description: '项目主键' }],
        bodyFields: PROJECT_BODY_FIELDS,
        requestExample: `{
  "code": "PRJ-0009",
  "name": "新项目-已改名",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "progress": 30,
  "receivableAmount": 120000.00,
  "payableAmount": 70000.00,
  "receivedAmount": 30000.00,
  "paidAmount": 10000.00
}`,
        responseExample: PROJECT_RESPONSE_EXAMPLE,
        responses: [
          { status: '200', description: '更新成功' },
          { status: '400', description: '字段校验失败或日期区间不合法' },
          { status: '404', description: '项目不存在或已删除' },
          { status: '409', description: '项目编号被其他项目占用' }
        ]
      },
      {
        method: 'DELETE',
        path: '/api/projects/{id}',
        summary: '删除项目（软删除）',
        description: '仅置 deleted=true，数据保留；重复删除返回 404。',
        pathParams: [{ name: 'id', type: 'number', required: true, description: '项目主键' }],
        responseExample: '(无响应体)',
        responses: [
          { status: '204', description: '删除成功' },
          { status: '404', description: '项目不存在或已删除' }
        ]
      }
    ]
  }
];

/** 统一错误响应（RFC 7807）示例，供文档页顶部展示。 */
export const PROBLEM_DETAIL_EXAMPLE = PROBLEM_EXAMPLE;
