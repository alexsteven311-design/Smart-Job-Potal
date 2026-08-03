import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CandidateService } from './services/candidate.service';
import { AuthService } from './services/auth.service';
import { ResumeDataService } from './services/resume-data.service';
import { ToastService } from './services/toast.service';
import { User } from './models/api';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  private readonly fb               = inject(FormBuilder);
  private readonly candidateService = inject(CandidateService);
  private readonly resumeData       = inject(ResumeDataService);
  private readonly toast            = inject(ToastService);
  readonly auth                     = inject(AuthService);

  profileForm = this.fb.group({
    name: [''],
    phoneNumber: [''],
    location: [''],
    linkedInProfileUrl: [''],
    githubPortfolioUrl: [''],
    portfolioWebsite: [''],
    skills: [''],
    experienceYears: [0],
    preferredRole: ['']
  });

  resumeScore?: number;
  resumeFilename?: string;
  autofilled = false;

  ngOnInit(): void {
    this.loadProfile();
    this.loadResumeScore();
    this.checkAutofill();
  }

  private checkAutofill() {
    const data = this.resumeData.parsedData();
    if (!data) return;
    this.profileForm.patchValue({
      name:            data.name            || this.profileForm.value.name,
      skills:          data.skills          || this.profileForm.value.skills,
      experienceYears: data.experienceYears ?? this.profileForm.value.experienceYears,
      preferredRole:   data.preferredRole   || this.profileForm.value.preferredRole
    });
    this.autofilled = true;
    this.resumeData.clear();
  }

  private loadProfile() {
    this.candidateService.getProfile().subscribe({
      next: (profile) => {
        const current = this.profileForm.value;
        this.profileForm.patchValue({
          name:               current.name               || profile.name,
          phoneNumber:        current.phoneNumber        || profile.phoneNumber        || '',
          location:           current.location           || profile.location           || '',
          linkedInProfileUrl: current.linkedInProfileUrl || profile.linkedInProfileUrl || '',
          githubPortfolioUrl: current.githubPortfolioUrl || profile.githubPortfolioUrl || '',
          portfolioWebsite:   current.portfolioWebsite   || profile.portfolioWebsite   || '',
          skills:             current.skills             || profile.skills             || '',
          experienceYears:    current.experienceYears    || profile.experienceYears    || 0,
          preferredRole:      current.preferredRole      || profile.preferredRole      || ''
        });
      },
      error: () => this.toast.error('Unable to load profile information.')
    });
  }

  private loadResumeScore() {
    this.candidateService.getResumeScore().subscribe({
      next: (score) => { this.resumeScore = score.resumeScore; this.resumeFilename = score.resumeFilename; },
      error: () => {}
    });
  }

  dismissAutofill() { this.autofilled = false; }

  save() {
    const profile: User = {
      name:               this.profileForm.value.name               ?? '',
      email:              this.auth.user()?.email                   ?? '',
      role:               this.auth.user()?.role                    ?? 'candidate',
      phoneNumber:        this.profileForm.value.phoneNumber        ?? undefined,
      location:           this.profileForm.value.location           ?? undefined,
      linkedInProfileUrl: this.profileForm.value.linkedInProfileUrl ?? undefined,
      githubPortfolioUrl: this.profileForm.value.githubPortfolioUrl ?? undefined,
      portfolioWebsite:   this.profileForm.value.portfolioWebsite   ?? undefined,
      skills:             this.profileForm.value.skills             ?? undefined,
      experienceYears:    this.profileForm.value.experienceYears    ?? undefined,
      preferredRole:      this.profileForm.value.preferredRole      ?? undefined
    };
    this.candidateService.updateProfile(profile).subscribe({
      next: () => { this.autofilled = false; this.toast.success('Profile updated successfully.'); },
      error: () => this.toast.error('Unable to update profile. Please try again.')
    });
  }
}
