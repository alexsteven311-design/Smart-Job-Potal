import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MockInterviewSession, ResumeAnalysisResult, ResumeScore, User } from '../models/api';
import { API_PREFIX } from './api.config';

@Injectable({
  providedIn: 'root'
})
export class CandidateService {
  constructor(private http: HttpClient) {}

  private base = API_PREFIX;

  getProfile() {
    return this.http.get<User>(`${this.base}/candidates/profile`);
  }

  updateProfile(user: User) {
    return this.http.put<User>(`${this.base}/candidates/profile`, user);
  }

  getResumeScore() {
    return this.http.get<ResumeScore>(`${this.base}/candidates/resume-score`);
  }

  analyzeResume(file: File, jobTitle?: string) {
    const formData = new FormData();
    formData.append('file', file);
    if (jobTitle?.trim()) {
      formData.append('jobTitle', jobTitle.trim());
    }
    return this.http.post<ResumeAnalysisResult>(`${this.base}/candidates/resume-analyze`, formData);
  }

  getApplications() {
    return this.http.get<any[]>(`${this.base}/candidates/applications`);
  }

  startMockInterview(request: { jobId?: number; targetRole?: string; difficulty: string; questionCount: number }) {
    return this.http.post<MockInterviewSession>(`${this.base}/candidates/mock-interviews`, request);
  }

  submitMockInterviewAnswer(questionId: number, answer: string) {
    return this.http.post<MockInterviewSession['questions'][number]>(
      `${this.base}/candidates/mock-interviews/questions/${questionId}/answer`, { answer });
  }

  completeMockInterview(sessionId: number) {
    return this.http.post<MockInterviewSession>(`${this.base}/candidates/mock-interviews/${sessionId}/complete`, {});
  }

  generateCoverLetter(file: File, jobId: number) {
    const form = new FormData();
    form.append('file', file);
    form.append('jobId', String(jobId));
    return this.http.post<{ coverLetter: string; jobTitle: string; company: string }>(
      `${this.base}/candidates/cover-letter/generate`, form);
  }
}
