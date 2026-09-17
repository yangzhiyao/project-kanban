import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'projects' },
  {
    path: 'projects',
    loadComponent: () =>
      import('./pages/projects/project-list/project-list.component').then((m) => m.ProjectListComponent)
  },
  {
    path: 'health',
    loadComponent: () => import('./pages/home/home.component').then((m) => m.HomeComponent)
  },
  { path: '**', redirectTo: 'projects' }
];
