import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './services/auth.service';
import { Job, User } from './models/api';

interface RecommendedJob extends Job {
  matchPercent: number;
  salaryLpa: number;
  skills: string[];
}

interface Filters {
  location: string;
  experienceMin: number | null;
  experienceMax: number | null;
  salaryMin: number | null;
  salaryMax: number | null;
  skills: string;
}

@Component({
  selector: 'app-recommendations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recommendations.component.html',
  styleUrl: './recommendations.component.scss'
})
export class RecommendationsComponent implements OnInit {

  user: User | null = null;
  allJobs: RecommendedJob[] = [];
  filtered: RecommendedJob[] = [];
  loading = true;

  filters: Filters = {
    location: '',
    experienceMin: null,
    experienceMax: null,
    salaryMin: null,
    salaryMax: null,
    skills: ''
  };

  locations: string[] = [];
  sortBy: 'match' | 'salary' = 'match';

  // Salary lookup by role keywords (LPA)
  private readonly salaryMap: { keywords: string[]; min: number; max: number }[] = [
    { keywords: ['architect', 'principal', 'director'], min: 25, max: 45 },
    { keywords: ['senior', 'lead', 'manager', 'scientist'], min: 15, max: 30 },
    { keywords: ['engineer', 'developer', 'analyst', 'designer'], min: 6, max: 18 },
    { keywords: ['executive', 'associate', 'officer', 'coordinator'], min: 4, max: 9 },
    { keywords: ['fresher', 'trainee', 'intern'], min: 3, max: 6 },
  ];

  // Skills lookup by role keywords
  private readonly skillsMap: { keywords: string[]; skills: string[] }[] = [
    { keywords: ['java', 'spring', 'backend'], skills: ['Java', 'Spring Boot', 'REST APIs', 'Microservices'] },
    { keywords: ['python', 'data', 'ml', 'machine learning', 'ai', 'scientist'], skills: ['Python', 'TensorFlow', 'Pandas', 'SQL'] },
    { keywords: ['react', 'angular', 'frontend', 'ui', 'ux'], skills: ['React', 'Angular', 'TypeScript', 'CSS'] },
    { keywords: ['devops', 'cloud', 'aws', 'azure', 'kubernetes'], skills: ['AWS', 'Docker', 'Kubernetes', 'CI/CD'] },
    { keywords: ['android', 'ios', 'mobile', 'flutter'], skills: ['Kotlin', 'Swift', 'React Native', 'Flutter'] },
    { keywords: ['data engineer', 'spark', 'kafka', 'etl'], skills: ['Spark', 'Kafka', 'Hadoop', 'SQL'] },
    { keywords: ['security', 'cyber'], skills: ['Penetration Testing', 'SIEM', 'Firewalls', 'OWASP'] },
    { keywords: ['hr', 'talent', 'recruit'], skills: ['Recruitment', 'HRMS', 'Onboarding', 'Payroll'] },
    { keywords: ['finance', 'account', 'chartered'], skills: ['Tally', 'SAP FICO', 'GST', 'Excel'] },
  ];

  constructor(
    private http: HttpClient,
    public auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.user = this.auth.user();
    this.http.get<Job[]>('/api/jobs').subscribe({
      next: jobs => {
        this.allJobs = jobs.map(j => this.enrich(j)).sort((a, b) => b.matchPercent - a.matchPercent);
        this.locations = [...new Set(this.allJobs.map(j => j.location.split(',')[1]?.trim() ?? j.location))].sort();
        this.applyFilters();
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  private enrich(job: Job): RecommendedJob {
    const titleLower = (job.title + ' ' + (job.role ?? '') + ' ' + job.description).toLowerCase();
    return {
      ...job,
      matchPercent: this.calcMatch(titleLower),
      salaryLpa: this.calcSalary(titleLower),
      skills: this.calcSkills(titleLower)
    };
  }

  private calcMatch(text: string): number {
    if (!this.user) return Math.floor(Math.random() * 30) + 60;
    const userSkills = (this.user.skills ?? '').toLowerCase().split(/[,\s]+/).filter(Boolean);
    const userRole = (this.user.preferredRole ?? '').toLowerCase();
    const userExp = this.user.experienceYears ?? 0;

    let score = 50;

    // Role match
    if (userRole && text.includes(userRole)) score += 20;
    else if (userRole) {
      const roleWords = userRole.split(' ');
      const matched = roleWords.filter(w => w.length > 2 && text.includes(w)).length;
      score += Math.round((matched / Math.max(roleWords.length, 1)) * 15);
    }

    // Skills match
    if (userSkills.length > 0) {
      const matched = userSkills.filter(s => s.length > 1 && text.includes(s)).length;
      score += Math.round((matched / Math.max(userSkills.length, 1)) * 20);
    }

    // Experience match (within ±2 years = bonus)
    const reqExp = this.extractExp(text);
    if (reqExp !== null && Math.abs(userExp - reqExp) <= 2) score += 10;

    return Math.min(score, 99);
  }

  private extractExp(text: string): number | null {
    const m = text.match(/(\d+)\s*(?:years?|yrs?)/i);
    return m ? parseInt(m[1]) : null;
  }

  private calcSalary(text: string): number {
    for (const entry of this.salaryMap) {
      if (entry.keywords.some(k => text.includes(k))) {
        return Math.round((entry.min + entry.max) / 2);
      }
    }
    return 8;
  }

  private calcSkills(text: string): string[] {
    for (const entry of this.skillsMap) {
      if (entry.keywords.some(k => text.includes(k))) return entry.skills;
    }
    return ['Communication', 'Problem Solving', 'Teamwork'];
  }

  applyFilters() {
    const skillFilter = this.filters.skills.toLowerCase().split(/[,\s]+/).filter(Boolean);
    const locFilter = this.filters.location.toLowerCase();

    this.filtered = this.allJobs.filter(j => {
      if (locFilter && !j.location.toLowerCase().includes(locFilter)) return false;
      if (this.filters.experienceMin !== null && (j.requiredExperienceYears ?? 0) < this.filters.experienceMin) return false;
      if (this.filters.experienceMax !== null && (j.requiredExperienceYears ?? 0) > this.filters.experienceMax) return false;
      if (this.filters.salaryMin !== null && j.salaryLpa < this.filters.salaryMin) return false;
      if (this.filters.salaryMax !== null && j.salaryLpa > this.filters.salaryMax) return false;
      if (skillFilter.length > 0) {
        const jobText = (j.title + ' ' + j.description + ' ' + j.skills.join(' ')).toLowerCase();
        if (!skillFilter.some(s => jobText.includes(s))) return false;
      }
      return true;
    });

    this.filtered.sort((a, b) =>
      this.sortBy === 'match' ? b.matchPercent - a.matchPercent : b.salaryLpa - a.salaryLpa
    );
  }

  clearFilters() {
    this.filters = { location: '', experienceMin: null, experienceMax: null, salaryMin: null, salaryMax: null, skills: '' };
    this.applyFilters();
  }

  apply(jobId: number | undefined) {
    if (!this.auth.isAuthenticated()) { this.router.navigate(['/login']); return; }
    this.router.navigate(['/jobs'], { queryParams: { apply: jobId } });
  }

  matchColor(pct: number): string {
    if (pct >= 85) return '#16a34a';
    if (pct >= 70) return '#2563eb';
    return '#d97706';
  }

  matchLabel(pct: number): string {
    if (pct >= 85) return 'Excellent Match';
    if (pct >= 70) return 'Good Match';
    return 'Fair Match';
  }

  circumference = 2 * Math.PI * 28;
  dashOffset(pct: number) { return this.circumference * (1 - pct / 100); }
}
