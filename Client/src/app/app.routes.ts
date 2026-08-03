import { Routes } from '@angular/router';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', loadComponent: () => import('./home.component').then((m) => m.HomeComponent) },
  { path: 'dashboard', loadComponent: () => import('./dashboard.component').then((m) => m.DashboardComponent), canActivate: [authGuard] },
  { path: 'recruiter-dashboard', loadComponent: () => import('./recruiter-dashboard.component').then((m) => m.RecruiterDashboardComponent), canActivate: [authGuard] },
  { path: 'jobs', loadComponent: () => import('./jobs.component').then((m) => m.JobsComponent) },
  { path: 'companies', loadComponent: () => import('./companies.component').then((m) => m.CompaniesComponent) },
  { path: 'companies/:name', loadComponent: () => import('./company-profile.component').then((m) => m.CompanyProfileComponent) },
  { path: 'login', loadComponent: () => import('./login.component').then((m) => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./register.component').then((m) => m.RegisterComponent) },
  { path: 'profile', loadComponent: () => import('./profile.component').then((m) => m.ProfileComponent), canActivate: [authGuard] },
  { path: 'post-job', loadComponent: () => import('./post-job.component').then((m) => m.PostJobComponent), canActivate: [authGuard] },
  { path: 'resume-checker', loadComponent: () => import('./resume-checker.component').then((m) => m.ResumeCheckerComponent) },
  { path: 'applied-jobs', loadComponent: () => import('./applied-jobs.component').then((m) => m.AppliedJobsComponent), canActivate: [authGuard] },
  { path: 'feed', loadComponent: () => import('./feed.component').then((m) => m.FeedComponent) },
  { path: 'recommendations', loadComponent: () => import('./recommendations.component').then((m) => m.RecommendationsComponent) },
  { path: 'mock-interview', loadComponent: () => import('./mock-interview.component').then((m) => m.MockInterviewComponent), canActivate: [authGuard] },
  { path: 'cover-letter', loadComponent: () => import('./cover-letter.component').then((m) => m.CoverLetterComponent), canActivate: [authGuard] },
  { path: '**', redirectTo: 'jobs' }
];
