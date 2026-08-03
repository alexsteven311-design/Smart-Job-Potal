import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { JobService } from './services/job.service';
import { ToastService } from './services/toast.service';
import { AuthService } from './services/auth.service';
import { Job } from './models/api';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './post-job.component.html',
  styleUrls: ['./post-job.component.scss']
})
export class PostJobComponent {
  private readonly fb         = inject(FormBuilder);
  private readonly router     = inject(Router);
  private readonly jobService = inject(JobService);
  private readonly toast      = inject(ToastService);
  private readonly auth       = inject(AuthService);

  form = this.fb.group({
    title: [''], company: [''], location: [''], description: [''],
    role: [''], requiredExperienceYears: [0], remote: [false]
  });

  submit() {
    if (!this.auth.isAuthenticated()) {
      this.toast.error('You must be logged in to post a job.');
      this.router.navigate(['/login']);
      return;
    }
    if (!this.auth.isRecruiter()) {
      this.toast.error('Only recruiter or employer accounts can post jobs.');
      return;
    }
    const v = this.form.value;
    if (!v.title?.trim() || !v.company?.trim() || !v.location?.trim() || !v.description?.trim()) {
      this.toast.error('Title, company, location and description are required.');
      return;
    }
    this.jobService.createJob(v as Job).subscribe({
      next: () => { this.toast.success('Job posted successfully.'); this.router.navigate(['/jobs']); },
      error: (err: HttpErrorResponse) => {
        if (err.status === 401) {
          this.toast.error('Session expired. Please log in again.');
          this.auth.logout();
        } else if (err.status === 403) {
          this.toast.error('Your account does not have permission to post jobs.');
        } else {
          this.toast.error(`Failed to post job (${err.status}). Please try again.`);
        }
      }
    });
  }
}
