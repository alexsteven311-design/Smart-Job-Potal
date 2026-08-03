import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_BASE } from './api.config';
import { Job, JobApplication } from '../models/api';

@Injectable({ providedIn: 'root' })
export class RecruiterService {
  private readonly base = `${API_BASE}/api/recruiter`;

  constructor(private http: HttpClient) {}

  postJob(job: Partial<Job>) {
    return this.http.post<Job>(`${this.base}/jobs`, job);
  }

  getApplicants(jobId: number) {
    return this.http.get<JobApplication[]>(`${this.base}/jobs/${jobId}/applicants`);
  }

  updateStatus(applicationId: number, status: string) {
    return this.http.put<JobApplication>(`${this.base}/applications/${applicationId}/status`, { status });
  }

  scheduleInterview(applicationId: number, payload: { scheduledAt: string; location?: string; meetingLink?: string; notes?: string }) {
    return this.http.post(`${this.base}/applications/${applicationId}/schedule`, payload);
  }

  downloadResume(applicationId: number) {
    return this.http.get(`${this.base}/applications/${applicationId}/resume`, { responseType: 'blob' });
  }
}
