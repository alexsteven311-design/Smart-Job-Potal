import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ResumeAnalysisResult } from './models/api';
import { ResumeDataService } from './services/resume-data.service';
import { AuthService } from './services/auth.service';
import { CandidateService } from './services/candidate.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './resume-checker.component.html',
  styleUrls: ['./resume-checker.component.scss']
})
export class ResumeCheckerComponent {
  private router         = inject(Router);
  private resumeData     = inject(ResumeDataService);
  private candidateService = inject(CandidateService);
  readonly auth          = inject(AuthService);

  resumeFile?: File;
  resumeFileName = '';
  jobTitle = '';
  loading = false;
  error = '';
  result?: ResumeAnalysisResult;
  autofillReady = false;

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.resumeFile = input.files[0];
      this.resumeFileName = this.resumeFile.name;
      this.result = undefined;
      this.error = '';
      this.autofillReady = false;
    }
  }

  analyze() {
    if (!this.resumeFile) { this.error = 'Please upload your resume first.'; return; }
    this.loading = true;
    this.error = '';
    this.result = undefined;
    this.autofillReady = false;

    this.candidateService.analyzeResume(this.resumeFile, this.jobTitle).subscribe({
      next: (res) => {
        this.result = res;
        this.loading = false;

        // Store parsed data for autofill if user is logged in
        if (this.auth.isAuthenticated()) {
          const expYears = this.extractExpYears(res.expLevel);
          this.resumeData.set({
            name:           res.parsedName  || '',
            skills:         (res.parsedSkills?.length ? res.parsedSkills : res.foundSkills).join(', '),
            experienceYears: expYears,
            preferredRole:  this.jobTitle.trim() || ''
          });
          this.autofillReady = true;
        }
      },
      error: () => {
        this.error = 'Analysis failed. Make sure the backend is running.';
        this.loading = false;
      }
    });
  }

  goToProfile() {
    this.router.navigate(['/profile']);
  }

  private extractExpYears(expLevel: string): number {
    if (expLevel.includes('10+'))  return 10;
    if (expLevel.includes('7-10')) return 7;
    if (expLevel.includes('4-6'))  return 4;
    if (expLevel.includes('2-3'))  return 2;
    if (expLevel.includes('Fresher') || expLevel.includes('Intern')) return 0;
    return 1;
  }

  getScoreColor(score: number): string {
    if (score >= 75) return '#16a34a';
    if (score >= 50) return '#d97706';
    return '#dc2626';
  }

  getScoreLabel(score: number): string {
    if (score >= 75) return 'Excellent';
    if (score >= 50) return 'Good';
    if (score >= 30) return 'Fair';
    return 'Needs Work';
  }

  getDashOffset(score: number): number {
    return 339.292 - (score / 100) * 339.292;
  }
}
