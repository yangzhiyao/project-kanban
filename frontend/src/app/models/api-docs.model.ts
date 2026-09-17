export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';

/** 表格中展示的参数或字段。 */
export interface ApiField {
  name: string;
  type: string;
  required: boolean;
  description: string;
}

/** 单个接口（由 OpenAPI Operation 转换而来）。 */
export interface ApiEndpoint {
  method: HttpMethod;
  path: string;
  summary: string;
  description?: string;
  pathParams: ApiField[];
  queryParams: ApiField[];
  bodyFields: ApiField[];
  requestExample?: string;
  responseExample?: string;
  responses: { status: string; description: string }[];
}

/** 按 OpenAPI tag 分组的接口集合。 */
export interface ApiGroup {
  tag: string;
  description: string;
  endpoints: ApiEndpoint[];
}
