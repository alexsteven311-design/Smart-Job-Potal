import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { JobService } from './services/job.service';
import { ToastService } from './services/toast.service';
import { Job } from './models/api';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './jobs.component.html',
  styleUrls: ['./jobs.component.scss']
})
export class JobsComponent implements OnInit {
  private readonly fb         = inject(FormBuilder);
  private readonly jobService = inject(JobService);
  private readonly router     = inject(Router);
  private readonly toast      = inject(ToastService);
  readonly auth               = inject(AuthService);

  allJobs: Job[] = [];
  jobs: Job[] = [];
  loading = false;
  error?: string;
  selectedJobId?: number;
  savedJobIds = new Set<number>();
  resumeFile?: File;
  resumeFileName = '';
  selectedType = '';

  readonly jobTypes = [
    'Software & Tech', 'Data & Analytics', 'Design & UX', 'DevOps & Cloud',
    'Mobile', 'QA & Testing', 'Management & Product', 'Finance & Accounting',
    'HR & Recruitment', 'Marketing & Sales', 'Legal', 'Healthcare',
    'Engineering', 'Logistics & Operations', 'Education', 'Other'
  ];

  readonly typeKeywords: Record<string, string[]> = {
    'Software & Tech':        ['developer', 'engineer', 'software', 'java', 'python', 'backend', 'fullstack', 'full stack', 'web', 'blockchain', 'embedded', 'erp', 'sap'],
    'Data & Analytics':       ['data', 'analyst', 'scientist', 'bi ', 'business intelligence', 'big data', 'ml ', 'machine learning', 'ai ', 'analytics'],
    'Design & UX':            ['design', 'ui', 'ux', 'graphic', 'fashion', 'jewellery', 'architect'],
    'DevOps & Cloud':         ['devops', 'cloud', 'network', 'system admin', 'infrastructure', 'security'],
    'Mobile':                 ['android', 'ios', 'react native', 'mobile'],
    'QA & Testing':           ['qa', 'quality', 'test'],
    'Management & Product':   ['manager', 'product', 'operations', 'director', 'lead', 'head'],
    'Finance & Accounting':   ['finance', 'accountant', 'banking', 'investment', 'financial', 'chartered'],
    'HR & Recruitment':       ['hr', 'human resource', 'talent', 'recruitment', 'payroll'],
    'Marketing & Sales':      ['marketing', 'sales', 'content', 'seo', 'brand', 'digital marketing'],
    'Legal':                  ['legal', 'lawyer', 'counsel', 'compliance', 'law'],
    'Healthcare':             ['doctor', 'nurse', 'medical', 'pharma', 'clinical', 'health', 'radiolog', 'ayurved'],
    'Engineering':            ['mechanical', 'electrical', 'civil', 'mining', 'automobile', 'metallurg', 'rubber', 'steel'],
    'Logistics & Operations': ['logistics', 'supply chain', 'warehouse', 'delivery', 'export', 'plantation'],
    'Education':              ['teacher', 'coach', 'professor', 'trainer', 'instructor'],
    'Other':                  ['tourism', 'journalist', 'reporter', 'game', 'agricultural', 'sports']
  };

  applyForm = this.fb.group({
    fullName: [''], email: [''], phone: [''], currentLocation: [''],
    experienceYears: [''], currentCompany: [''], currentCtc: [''],
    expectedCtc: [''], noticePeriod: [''], coverLetter: ['']
  });

  searchForm = this.fb.group({ title: [''], company: [''], location: [''] });

  ngOnInit(): void { this.loadJobs(); this.loadSavedIds(); }

  loadSavedIds() {
    if (!this.auth.isAuthenticated()) return;
    this.jobService.getSavedJobs().subscribe({
      next: (list: any[]) => list.forEach(item => this.savedJobIds.add(item.job?.id ?? item.id))
    });
  }

  toggleSave(job: Job, event: Event) {
    event.stopPropagation();
    if (!this.auth.isAuthenticated()) { this.toast.error('Please log in to save jobs.'); return; }
    const id = job.id!;
    if (this.savedJobIds.has(id)) {
      this.jobService.unsaveJob(id).subscribe(() => { this.savedJobIds.delete(id); this.toast.info('Job removed from saved.'); });
    } else {
      this.jobService.saveJob(id).subscribe(() => { this.savedJobIds.add(id); this.toast.success('Job saved!'); });
    }
  }

  loadJobs() {
    this.loading = true;
    this.error = undefined;
    const v = this.searchForm.value;
    this.jobService.getJobs({ title: v.title ?? undefined, company: v.company ?? undefined, location: v.location ?? undefined }).subscribe({
      next: (jobs) => { this.allJobs = jobs; this.applyTypeFilter(); this.loading = false; },
      error: () => { this.error = 'Unable to load job listings. Please try again later.'; this.loading = false; }
    });
  }

  selectType(type: string) { this.selectedType = this.selectedType === type ? '' : type; this.applyTypeFilter(); }

  applyTypeFilter() {
    if (!this.selectedType) { this.jobs = this.allJobs; return; }
    const keywords = this.typeKeywords[this.selectedType] || [];
    this.jobs = this.allJobs.filter(job => {
      const text = `${job.title} ${job.role} ${job.description}`.toLowerCase();
      return keywords.some(k => text.includes(k));
    });
  }

  companyLogo(job: Job): string { return job.logoUrl?.trim() || this.companyMonogram(job.company); }

  useMonogram(event: Event, company: string) {
    const image = event.target as HTMLImageElement;
    image.onerror = null;
    image.src = this.companyMonogram(company);
  }

  private companyMonogram(company: string): string {
    const initials = company.replace(/[^a-zA-Z0-9 ]/g, ' ').split(/\s+/).filter(Boolean).slice(0, 2).map(w => w[0]).join('').toUpperCase() || 'CO';
    const hue = Array.from(company).reduce((v, c) => (v * 31 + c.charCodeAt(0)) % 360, 0);
    const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64"><rect width="64" height="64" rx="14" fill="hsl(${hue} 65% 42%)"/><text x="32" y="39" text-anchor="middle" fill="white" font-family="Arial, sans-serif" font-size="24" font-weight="700">${initials}</text></svg>`;
    return `data:image/svg+xml;base64,${btoa(svg)}`;
  }

  onResumeSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) { this.resumeFile = input.files[0]; this.resumeFileName = this.resumeFile.name; }
  }

  apply(job: Job) {
    this.error = undefined;
    if (!this.auth.isAuthenticated()) { this.toast.error('Please log in before applying for a job.'); return; }
    if (!job.id) { this.toast.error('Invalid job selected. Please close and open the apply panel again.'); return; }

    const { coverLetter, fullName, email, phone, currentLocation, experienceYears, currentCompany, currentCtc, expectedCtc, noticePeriod } = this.applyForm.value;
    if (!coverLetter?.trim()) { this.toast.error('Please provide a cover letter for your application.'); return; }

    const enrichedCoverLetter = `Name: ${fullName || ''}\nEmail: ${email || ''}\nPhone: ${phone || ''}\nLocation: ${currentLocation || ''}\nExperience: ${experienceYears || ''} years\nCurrent Company: ${currentCompany || 'N/A'}\nCurrent CTC: ${currentCtc || 'N/A'}\nExpected CTC: ${expectedCtc || ''}\nNotice Period: ${noticePeriod || ''}\n\n${coverLetter}`;

    const doApply = () => {
      this.jobService.apply(job.id!, enrichedCoverLetter).subscribe({
        next: () => {
          this.applyForm.reset();
          this.resumeFile = undefined;
          this.resumeFileName = '';
          this.selectedJobId = undefined;
          this.toast.success(`Application submitted for ${job.title} at ${job.company}.`);
          this.router.navigate(['/applied-jobs'], { state: { justApplied: true, jobTitle: job.title, company: job.company } });
        },
        error: () => this.toast.error('Application failed. Confirm that you are logged in and try again.')
      });
    };

    if (this.resumeFile) {
      this.jobService.uploadResume(this.resumeFile).subscribe({
        next: () => doApply(),
        error: (err) => this.toast.error(err.status === 401
          ? 'Your session has expired. Please log in again before uploading a resume.'
          : 'Resume upload failed. Please try again.')
      });
    } else {
      doApply();
    }
  }
}
