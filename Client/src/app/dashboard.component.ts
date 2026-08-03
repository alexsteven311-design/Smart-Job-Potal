import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './services/auth.service';

interface DashboardData {
  name: string;
  profileCompletion: number;
  resumeScore: number;
  applicationCount: number;
  upcomingInterviews: number;
  recommendations: { id: number; title: string; company: string; location: string }[];
  recentActivity: { icon: string; label: string; company: string }[];
}

@Component({
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private http = inject(HttpClient);
  readonly auth = inject(AuthService);

  data?: DashboardData;
  loading = true;
  error = '';

  ngOnInit() {
    this.http.get<DashboardData>('/api/candidates/dashboard').subscribe({
      next: (d) => { this.data = d; this.loading = false; },
      error: () => { this.error = 'Failed to load dashboard.'; this.loading = false; }
    });
  }

  profileColor(pct: number): string {
    if (pct >= 80) return '#16a34a';
    if (pct >= 50) return '#d97706';
    return '#dc2626';
  }

  resumeColor(score: number): string {
    if (score >= 75) return '#16a34a';
    if (score >= 50) return '#d97706';
    return '#dc2626';
  }
}
