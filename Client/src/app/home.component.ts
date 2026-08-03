import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  readonly authService = inject(AuthService);

  readonly stats = [
    { value: '10,000+', label: 'Jobs Posted' },
    { value: '5,000+', label: 'Companies' },
    { value: '50,000+', label: 'Candidates' },
    { value: '28', label: 'States Covered' },
  ];

  readonly features = [
    { icon: '🤖', title: 'AI Resume Checker', desc: 'Get instant ATS score, skill gap analysis, and personalized improvement tips powered by AI.' },
    { icon: '🔔', title: 'Smart Notifications', desc: 'Stay updated with real-time alerts for application status, interview schedules, and job recommendations.' },
    { icon: '📊', title: 'Candidate Dashboard', desc: 'Track your applications, profile completion, resume score, and upcoming interviews in one place.' },
    { icon: '🗺️', title: 'Pan-India Jobs', desc: 'Explore opportunities across all 28 Indian states — remote, hybrid, and on-site roles.' },
    { icon: '💼', title: 'Easy Apply', desc: 'Apply to jobs in seconds with your saved profile and resume — no repetitive form filling.' },
    { icon: '🎯', title: 'Skill-Based Matching', desc: 'Get job recommendations tailored to your skills, experience level, and preferred role.' },
  ];

  readonly currentYear = new Date().getFullYear();

  readonly categories = [
    { icon: '💻', label: 'Software Dev' },
    { icon: '📱', label: 'Mobile' },
    { icon: '☁️', label: 'Cloud & DevOps' },
    { icon: '🔒', label: 'Cybersecurity' },
    { icon: '📊', label: 'Data Science' },
    { icon: '🎨', label: 'UI/UX Design' },
    { icon: '📈', label: 'Product' },
    { icon: '🤝', label: 'Sales & HR' },
  ];
}
