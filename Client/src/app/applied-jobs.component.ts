import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CandidateService } from './services/candidate.service';

export interface PipelineStep {
  key: string;
  label: string;
  icon: string;
  state: 'completed' | 'current' | 'pending';
}

@Component({
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './applied-jobs.component.html',
  styleUrls: ['./applied-jobs.component.scss']
})
export class AppliedJobsComponent implements OnInit {
  private candidateService = inject(CandidateService);
  private router = inject(Router);

  applications: any[] = [];
  loading = true;
  error = '';
  justApplied = false;
  appliedJobTitle = '';
  appliedCompany = '';

  // Pipeline steps in order
  readonly STEPS = ['APPLIED', 'REVIEWED', 'INTERVIEW', 'OFFERED', 'SELECTED'];
  readonly STEP_LABELS: Record<string, string> = {
    APPLIED:  'Applied',
    REVIEWED: 'Screening',
    INTERVIEW:'Technical',
    OFFERED:  'HR Round',
    SELECTED: 'Offer'
  };
  readonly STEP_ICONS: Record<string, string> = {
    APPLIED:  '📨',
    REVIEWED: '🔍',
    INTERVIEW:'💻',
    OFFERED:  '🤝',
    SELECTED: '🎉'
  };

  ngOnInit() {
    const nav = this.router.getCurrentNavigation();
    const state = nav?.extras?.state ?? history.state;
    if (state?.['justApplied']) {
      this.justApplied = true;
      this.appliedJobTitle = state['jobTitle'] ?? '';
      this.appliedCompany  = state['company']  ?? '';
      setTimeout(() => this.justApplied = false, 6000);
    }
    this.candidateService.getApplications().subscribe({
      next: (data) => { this.applications = data; this.loading = false; },
      error: () => { this.error = 'Failed to load applications.'; this.loading = false; }
    });
  }

  getPipeline(status: string): PipelineStep[] {
    const s = status?.toUpperCase();
    const rejected = s === 'REJECTED';
    const currentIdx = rejected ? 0 : this.STEPS.indexOf(s);

    return this.STEPS.map((key, i) => ({
      key,
      label: this.STEP_LABELS[key],
      icon:  this.STEP_ICONS[key],
      state: rejected
        ? (i === 0 ? 'current' : 'pending')
        : i < currentIdx  ? 'completed'
        : i === currentIdx ? 'current'
        : 'pending'
    }));
  }

  isRejected(status: string) { return status?.toUpperCase() === 'REJECTED'; }

  statusSummary(status: string): string {
    switch (status?.toUpperCase()) {
      case 'SELECTED':  return 'Offer Received 🎉';
      case 'REJECTED':  return 'Not Selected';
      case 'OFFERED':   return 'HR Round';
      case 'INTERVIEW': return 'Technical Round';
      case 'REVIEWED':  return 'Screening';
      default:          return 'Applied';
    }
  }
}
