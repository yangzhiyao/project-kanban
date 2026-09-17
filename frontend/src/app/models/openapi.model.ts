/** OpenAPI 3.1 规范的最小类型子集，只覆盖本项目用到的部分。 */
export interface OpenApiSchema {
  $ref?: string;
  type?: string | string[];
  format?: string;
  description?: string;
  example?: unknown;
  default?: unknown;
  enum?: unknown[];
  nullable?: boolean;
  maxLength?: number;
  minLength?: number;
  minimum?: number;
  maximum?: number;
  pattern?: string;
  properties?: Record<string, OpenApiSchema>;
  items?: OpenApiSchema;
  required?: string[];
}

export interface OpenApiParameter {
  name: string;
  in: 'path' | 'query' | 'header' | 'cookie';
  description?: string;
  required?: boolean;
  example?: unknown;
  schema?: OpenApiSchema;
}

export interface OpenApiMediaType {
  schema?: OpenApiSchema;
}

export interface OpenApiRequestBody {
  required?: boolean;
  content?: Record<string, OpenApiMediaType>;
}

export interface OpenApiResponse {
  description?: string;
  content?: Record<string, OpenApiMediaType>;
}

export type OpenApiOperationMethod = 'get' | 'post' | 'put' | 'patch' | 'delete';

export interface OpenApiOperation {
  tags?: string[];
  summary?: string;
  description?: string;
  operationId?: string;
  parameters?: OpenApiParameter[];
  requestBody?: OpenApiRequestBody;
  responses?: Record<string, OpenApiResponse>;
}

export interface OpenApiSpec {
  openapi: string;
  info: { title?: string; version?: string; description?: string };
  tags?: { name: string; description?: string }[];
  paths: Record<string, Partial<Record<OpenApiOperationMethod, OpenApiOperation>>>;
  components?: { schemas?: Record<string, OpenApiSchema> };
}
