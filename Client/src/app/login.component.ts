import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './services/auth.service';
import { LoginRequest } from './models/api';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);

  form = this.fb.group({
    email: [''],
    password: ['']
  });

  error?: string;

  submit() {
    if (this.form.invalid) {
      this.error = 'Please enter both email and password.';
      return;
    }

    const credentials = this.form.value as LoginRequest;
    this.auth.login(credentials).subscribe({
      next: () => {
        const role = this.auth.user()?.role?.toLowerCase();
        this.router.navigate([role === 'recruiter' || role === 'employer' ? '/recruiter-dashboard' : '/dashboard']);
      },
      error: () => {
        this.error = 'Login failed. Please check your credentials.';
      }
    });
  }
}
