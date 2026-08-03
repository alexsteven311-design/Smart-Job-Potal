import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CompanySummary, Job } from '../models/api';
import { API_PREFIX } from './api.config';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  constructor(private http: HttpClient) {}
  private base = API_PREFIX;

  getJobs(filters?: { title?: string; company?: string; location?: string }) {
    let params = new HttpParams();
    if (filters?.title?.trim()) params = params.set('title', filters.title);
    if (filters?.company?.trim()) params = params.set('company', filters.company);
    if (filters?.location?.trim()) params = params.set('location', filters.location);
    return this.http.get<Job[]>(`${this.base}/jobs`, { params });
  }

  getCompanies() { return this.http.get<CompanySummary[]>(`${this.base}/companies`); }

  getCompany(name: string) { return this.http.get<CompanySummary>(`${this.base}/companies/${encodeURIComponent(name)}`); }

  createJob(job: Job) {
    return this.http.post<Job>(`${this.base}/recruiter/jobs`, job);
  }

  apply(jobId: number, coverLetter: string) {
    return this.http.post(`${this.base}/candidates/jobs/${jobId}/apply`, { coverLetter });
  }

  uploadResume(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.base}/candidates/profile/upload-resume`, formData);
  }

  getSavedJobs() { return this.http.get<any[]>(`${this.base}/candidates/saved-jobs`); }
  saveJob(jobId: number) { return this.http.post<any>(`${this.base}/candidates/saved-jobs/${jobId}`, {}); }
  unsaveJob(jobId: number) { return this.http.delete<any>(`${this.base}/candidates/saved-jobs/${jobId}`); }
}
