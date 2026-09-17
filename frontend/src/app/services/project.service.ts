import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Project, ProjectPage, ProjectPayload, ProjectQuery } from '../models/project.model';

/** 项目 CRUD 接口服务，对应后端 /api/projects。 */
@Injectable({ providedIn: 'root' })
export class ProjectService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/projects`;

  /** 分页查询，keyword 模糊匹配编号/名称，status 精确过滤。 */
  list(query: ProjectQuery = {}): Observable<ProjectPage> {
    let params = new HttpParams()
      .set('page', String(query.page ?? 0))
      .set('size', String(query.size ?? 10));

    if (query.keyword) {
      params = params.set('keyword', query.keyword);
    }
    if (query.status) {
      params = params.set('status', query.status);
    }

    return this.http.get<ProjectPage>(this.baseUrl, { params });
  }

  get(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.baseUrl}/${id}`);
  }

  create(payload: ProjectPayload): Observable<Project> {
    return this.http.post<Project>(this.baseUrl, payload);
  }

  update(id: number, payload: ProjectPayload): Observable<Project> {
    return this.http.put<Project>(`${this.baseUrl}/${id}`, payload);
  }

  remove(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
