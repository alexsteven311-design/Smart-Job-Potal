import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RecruiterService } from './services/recruiter.service';
import { AuthService } from './services/auth.service';
import { ToastService } from './services/toast.service';
import { Job, JobApplication } from './models/api';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './recruiter-dashboard.component.html',
  styleUrls: ['./recruiter-dashboard.component.scss']
})
export class RecruiterDashboardComponent implements OnInit {
  private readonly fb        = inject(FormBuilder);
  private readonly recruiter = inject(RecruiterService);
  private readonly toast     = inject(ToastService);
  readonly auth              = inject(AuthService);

  activeTab: 'post' | 'applicants' = 'post';
  postedJobs: Job[] = [];
  selectedJobId?: number;
  applicants: JobApplication[] = [];
  loadingApplicants = false;

  jobForm = this.fb.group({
    title: [''], company: [''], location: [''], description: [''],
    role: [''], requiredExperienceYears: [0], remote: [false]
  });

  ngOnInit() {}

  submitJob() {
    if (this.jobForm.invalid) { this.toast.error('Please fill in all required fields.'); return; }
    this.recruiter.postJob(this.jobForm.value as Partial<Job>).subscribe({
      next: (job) => {
        this.toast.success('Job posted successfully!');
        this.postedJobs = [job, ...this.postedJobs];
        this.jobForm.reset({ requiredExperienceYears: 0, remote: false });
      },
      error: () => this.toast.error('Failed to post job. Please try again.')
    });
  }

  viewApplicants(jobId: number) {
    this.selectedJobId = jobId;
    this.activeTab = 'applicants';
    this.loadingApplicants = true;
    this.recruiter.getApplicants(jobId).subscribe({
      next: (apps) => { this.applicants = apps; this.loadingApplicants = false; },
      error: () => { this.toast.error('Failed to load applicants.'); this.loadingApplicants = false; }
    });
  }

  updateStatus(applicationId: number, status: string) {
    this.recruiter.updateStatus(applicationId, status).subscribe({
      next: (updated) => {
        this.applicants = this.applicants.map(a => a.id === applicationId ? { ...a, status: updated.status } : a);
        this.toast.success('Status updated.');
      },
      error: () => this.toast.error('Failed to update status.')
    });
  }

  statusClass(status: string) {
    switch (status?.toUpperCase()) {
      case 'SELECTED':  return 'badge-success';
      case 'REJECTED':  return 'badge-danger';
      case 'INTERVIEW': return 'badge-info';
      case 'REVIEWED':  return 'badge-warning';
      default:          return 'badge-default';
    }
  }
}
