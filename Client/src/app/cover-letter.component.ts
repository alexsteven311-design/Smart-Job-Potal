import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JobService } from './services/job.service';
import { CandidateService } from './services/candidate.service';
import { ToastService } from './services/toast.service';
import { Job } from './models/api';

@Component({
  selector: 'app-cover-letter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cover-letter.component.html',
  styleUrl: './cover-letter.component.scss'
})
export class CoverLetterComponent {
  private readonly jobService       = inject(JobService);
  private readonly candidateService = inject(CandidateService);
  private readonly toast            = inject(ToastService);

  step = signal<1 | 2 | 3>(1);

  resumeFile: File | null = null;
  resumeError = '';

  jobs: Job[] = [];
  filteredJobs: Job[] = [];
  selectedJob: Job | null = null;
  searchQuery = '';
  loadingJobs = false;

  coverLetter = '';
  jobTitle = '';
  company = '';
  generating = false;
  genError = '';
  copied = false;

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    this.resumeError = '';
    if (!file) return;
    const allowed = ['application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
    if (!allowed.includes(file.type)) { this.resumeError = 'Only PDF or DOCX files are accepted.'; return; }
    if (file.size > 5 * 1024 * 1024) { this.resumeError = 'File must be under 5 MB.'; return; }
    this.resumeFile = file;
    this.toast.info(`Resume "${file.name}" ready.`);
  }

  goToStep2() {
    if (!this.resumeFile) { this.resumeError = 'Please upload your resume first.'; return; }
    this.step.set(2);
    this.loadingJobs = true;
    this.jobService.getJobs().subscribe({
      next: jobs => { this.jobs = jobs; this.filteredJobs = jobs; this.loadingJobs = false; },
      error: () => { this.loadingJobs = false; this.toast.error('Failed to load jobs. Please try again.'); }
    });
  }

  filterJobs() {
    const q = this.searchQuery.toLowerCase();
    this.filteredJobs = q
      ? this.jobs.filter(j => j.title.toLowerCase().includes(q) || j.company.toLowerCase().includes(q))
      : this.jobs;
  }

  selectJob(job: Job) { this.selectedJob = job; }

  generate() {
    if (!this.resumeFile || !this.selectedJob?.id) return;
    this.generating = true;
    this.genError = '';
    this.step.set(3);
    this.candidateService.generateCoverLetter(this.resumeFile, this.selectedJob.id).subscribe({
      next: res => {
        this.coverLetter = res.coverLetter;
        this.jobTitle = res.jobTitle;
        this.company = res.company;
        this.generating = false;
        this.toast.success('Cover letter generated successfully.');
      },
      error: () => {
        this.genError = 'Failed to generate cover letter. Please try again.';
        this.generating = false;
        this.toast.error('Failed to generate cover letter. Please try again.');
      }
    });
  }

  copy() {
    navigator.clipboard.writeText(this.coverLetter).then(() => {
      this.copied = true;
      this.toast.success('Cover letter copied to clipboard.');
      setTimeout(() => this.copied = false, 2000);
    });
  }

  download() {
    const blob = new Blob([this.coverLetter], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `cover-letter-${this.company.replace(/\s+/g, '-')}.txt`;
    a.click();
    URL.revokeObjectURL(url);
    this.toast.info('Cover letter downloaded.');
  }

  restart() { this.step.set(1); this.resumeFile = null; this.selectedJob = null; this.coverLetter = ''; this.searchQuery = ''; this.genError = ''; }
}
