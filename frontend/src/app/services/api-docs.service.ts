import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { OpenApiSpec } from '../models/openapi.model';

/** 获取后端自动生成的 OpenAPI 文档（springdoc，路径 /api/openapi）。 */
@Injectable({ providedIn: 'root' })
export class ApiDocsService {
  private readonly http = inject(HttpClient);

  getSpec(): Observable<OpenApiSpec> {
    return this.http.get<OpenApiSpec>(`${environment.apiUrl}/openapi`);
  }
}
