import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CompanySummary } from './models/api';
import { JobService } from './services/job.service';

@Component({ standalone: true, imports: [CommonModule, RouterLink], templateUrl: './companies.component.html', styleUrl: './companies.component.scss' })
export class CompaniesComponent implements OnInit {
  private readonly jobService = inject(JobService);
  readonly companies = signal<CompanySummary[]>([]);
  readonly error = signal('');
  ngOnInit(): void { this.jobService.getCompanies().subscribe({ next: (companies) => this.companies.set(companies), error: () => this.error.set('Unable to load company profiles.') }); }
}
