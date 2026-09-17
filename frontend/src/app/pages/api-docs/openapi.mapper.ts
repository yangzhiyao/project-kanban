import { ApiEndpoint, ApiField, ApiGroup, HttpMethod } from '../../models/api-docs.model';
import {
  OpenApiOperation,
  OpenApiOperationMethod,
  OpenApiParameter,
  OpenApiSchema,
  OpenApiSpec
} from '../../models/openapi.model';

const METHOD_ORDER: OpenApiOperationMethod[] = ['get', 'post', 'put', 'patch', 'delete'];

/**
 * 把 OpenAPI 规范转换成文档页的视图模型。
 *
 * <p>接口摘要、参数说明、字段约束、请求/响应示例全部来自后端生成的规范，
 * 新增接口或修改注解后这里无需改动。
 */
export function toApiGroups(spec: OpenApiSpec): ApiGroup[] {
  const groups = new Map<string, ApiGroup>();

  for (const tag of spec.tags ?? []) {
    groups.set(tag.name, { tag: tag.name, description: tag.description ?? '', endpoints: [] });
  }

  for (const [path, operations] of Object.entries(spec.paths ?? {})) {
    for (const method of METHOD_ORDER) {
      const operation = operations[method];
      if (!operation) {
        continue;
      }
      const tag = operation.tags?.[0] ?? '默认';
      if (!groups.has(tag)) {
        groups.set(tag, { tag, description: '', endpoints: [] });
      }
      groups.get(tag)!.endpoints.push(toEndpoint(method, path, operation, spec));
    }
  }

  return [...groups.values()]
    .map((group) => ({ ...group, endpoints: group.endpoints.sort((a, b) => a.path.localeCompare(b.path)) }))
    .filter((group) => group.endpoints.length > 0);
}

function toEndpoint(
  method: OpenApiOperationMethod,
  path: string,
  operation: OpenApiOperation,
  spec: OpenApiSpec
): ApiEndpoint {
  const parameters = operation.parameters ?? [];
  const bodySchema = operation.requestBody?.content?.['application/json']?.schema;
  const body = bodySchema ? schemaToFields(deref(bodySchema, spec), spec) : { fields: [], example: undefined };

  return {
    method: method.toUpperCase() as HttpMethod,
    path,
    summary: operation.summary ?? '',
    description: operation.description,
    pathParams: parameters.filter((p) => p.in === 'path').map((p) => parameterToField(p, spec, true)),
    queryParams: parameters.filter((p) => p.in === 'query').map((p) => parameterToField(p, spec, false)),
    bodyFields: body.fields,
    requestExample: body.example ? JSON.stringify(body.example, null, 2) : undefined,
    responseExample: successExample(operation, spec),
    responses: Object.entries(operation.responses ?? {}).map(([status, response]) => ({
      status,
      description: response.description ?? ''
    }))
  };
}

function parameterToField(parameter: OpenApiParameter, spec: OpenApiSpec, pathParam: boolean): ApiField {
  const schema = deref(parameter.schema, spec);
  return {
    name: parameter.name,
    type: formatType(schema, spec),
    required: Boolean(parameter.required ?? pathParam),
    description: withDetails(parameter.description ?? schema.description ?? '', schema)
  };
}

function schemaToFields(
  schema: OpenApiSchema,
  spec: OpenApiSpec
): { fields: ApiField[]; example: Record<string, unknown> | undefined } {
  const properties = schema.properties ?? {};
  const required = new Set(schema.required ?? []);
  const fields: ApiField[] = [];
  const example: Record<string, unknown> = {};

  for (const [name, rawProperty] of Object.entries(properties)) {
    const property = deref(rawProperty, spec);
    fields.push({
      name,
      type: formatType(property, spec),
      required: required.has(name),
      description: withDetails(property.description ?? '', property)
    });
    example[name] = buildExample(rawProperty, spec, 0);
  }

  return { fields, example: Object.keys(example).length ? example : undefined };
}

function successExample(operation: OpenApiOperation, spec: OpenApiSpec): string | undefined {
  const entry = Object.entries(operation.responses ?? {}).find(([status]) => status.startsWith('2'));
  const schema = entry?.[1].content?.['application/json']?.schema;
  if (!schema) {
    return undefined;
  }
  return JSON.stringify(buildExample(schema, spec, 0), null, 2);
}

function buildExample(rawSchema: OpenApiSchema | undefined, spec: OpenApiSpec, depth: number): unknown {
  if (!rawSchema || depth > 6) {
    return null;
  }
  const schema = deref(rawSchema, spec);

  if (schema.example !== undefined) {
    return schema.example;
  }
  if (schema.default !== undefined) {
    return schema.default;
  }
  if (schema.enum?.length) {
    return schema.enum[0];
  }

  switch (primaryType(schema)) {
    case 'object': {
      const result: Record<string, unknown> = {};
      for (const [key, value] of Object.entries(schema.properties ?? {})) {
        result[key] = buildExample(value, spec, depth + 1);
      }
      return result;
    }
    case 'array':
      return [buildExample(schema.items, spec, depth + 1)];
    case 'integer':
    case 'number':
      return 0;
    case 'boolean':
      return true;
    case 'string':
    default:
      if (schema.format === 'date') {
        return '2026-01-01';
      }
      if (schema.format === 'date-time') {
        return '2026-01-01T00:00:00Z';
      }
      return '';
  }
}

function formatType(rawSchema: OpenApiSchema, spec: OpenApiSpec): string {
  const schema = deref(rawSchema, spec);
  if (schema.enum?.length) {
    return 'enum';
  }
  const type = primaryType(schema);
  if (type === 'array') {
    return `${formatType(schema.items ?? {}, spec)}[]`;
  }
  if (schema.format === 'date' || schema.format === 'date-time') {
    return schema.format;
  }
  return type;
}

/** 字段说明追加枚举取值，其余约束后端已写在描述里。 */
function withDetails(description: string, schema: OpenApiSchema): string {
  if (!schema.enum?.length) {
    return description;
  }
  const values = schema.enum.map((value) => String(value)).join(' | ');
  return description ? `${description}（取值：${values}）` : `取值：${values}`;
}

function primaryType(schema: OpenApiSchema): string {
  const type = schema.type;
  if (Array.isArray(type)) {
    return type.find((item) => item !== 'null') ?? 'string';
  }
  if (type) {
    return type;
  }
  if (schema.properties) {
    return 'object';
  }
  if (schema.items) {
    return 'array';
  }
  return 'string';
}

function deref(schema: OpenApiSchema | undefined, spec: OpenApiSpec): OpenApiSchema {
  if (!schema) {
    return {};
  }
  if (schema.$ref) {
    const name = schema.$ref.split('/').pop() ?? '';
    return spec.components?.schemas?.[name] ?? {};
  }
  return schema;
}
