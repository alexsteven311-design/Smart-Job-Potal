import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './services/auth.service';
import { RegisterRequest } from './models/api';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);

  form = this.fb.group({
    name: [''],
    email: [''],
    password: [''],
    role: ['candidate'],
    company: [''],
    skills: [''],
    experienceYears: [0],
    preferredRole: ['']
  });

  error?: string;

  get isRecruiter() {
    return this.form.value.role === 'recruiter';
  }

  submit() {
    if (this.form.invalid) {
      this.error = 'Please complete all required fields.';
      return;
    }

    const payload = this.form.value as RegisterRequest;
    this.auth.register(payload).subscribe({
      next: () => {
        const role = this.auth.user()?.role?.toLowerCase();
        this.router.navigate([role === 'recruiter' || role === 'employer' ? '/recruiter-dashboard' : '/dashboard']);
      },
      error: () => {
        this.error = 'Registration failed. Please check your data and try again.';
      }
    });
  }
}
