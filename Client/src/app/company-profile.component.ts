import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CompanySummary } from './models/api';
import { JobService } from './services/job.service';

@Component({ standalone: true, imports: [CommonModule, RouterLink], templateUrl: './company-profile.component.html', styleUrl: './company-profile.component.scss' })
export class CompanyProfileComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly jobService = inject(JobService);
  readonly company = signal<CompanySummary | null>(null);
  readonly error = signal('');
  ngOnInit(): void {
    const name = this.route.snapshot.paramMap.get('name');
    if (name) this.jobService.getCompany(name).subscribe({ next: (company) => this.company.set(company), error: () => this.error.set('Company profile not found.') });
  }
}
