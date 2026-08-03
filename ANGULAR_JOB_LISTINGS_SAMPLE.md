# Angular Job Listings Sample

This sample shows how an Angular app can fetch job listings from the Spring Boot backend and display them with title, company, and location filters.

## Job model (`job.model.ts`)

```ts
export interface Job {
  id: number;
  title: string;
  company: string;
  location: string;
  description: string;
  role?: string;
  requiredExperienceYears?: number;
  postedAt: string;
  remote: boolean;
}
```

## Job service (`job.service.ts`)

```ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Job } from './job.model';

@Injectable({
  providedIn: 'root'
})
export class JobService {
  private apiUrl = '/api/jobs';

  constructor(private http: HttpClient) {}

  searchJobs(title?: string, company?: string, location?: string): Observable<Job[]> {
    let params = new HttpParams();
    if (title) { params = params.set('title', title); }
    if (company) { params = params.set('company', company); }
    if (location) { params = params.set('location', location); }
    return this.http.get<Job[]>(this.apiUrl, { params });
  }
}
```

## Job list component (`job-list.component.ts`)

```ts
import { Component, OnInit } from '@angular/core';
import { JobService } from './job.service';
import { Job } from './job.model';

@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.css']
})
export class JobListComponent implements OnInit {
  jobs: Job[] = [];
  title = '';
  company = '';
  location = '';

  constructor(private jobService: JobService) {}

  ngOnInit(): void {
    this.loadJobs();
  }

  loadJobs(): void {
    this.jobService.searchJobs(this.title, this.company, this.location)
      .subscribe(result => this.jobs = result);
  }
}
```

## Template (`job-list.component.html`)

```html
<div class="job-search">
  <label for="title-input">
    Title:
  </label>
  <input id="title-input" name="title" type="text" [(ngModel)]="title" placeholder="Search by title" />

  <label for="company-input">
    Company:
  </label>
  <input id="company-input" name="company" type="text" [(ngModel)]="company" placeholder="Search by company" />

  <label for="location-input">
    Location:
  </label>
  <input id="location-input" name="location" type="text" [(ngModel)]="location" placeholder="Search by location" />

  <button (click)="loadJobs()">Search</button>
</div>

<div class="job-list">
  <div *ngFor="let job of jobs" class="job-card">
    <h3>{{ job.title }}</h3>
    <p><strong>Company:</strong> {{ job.company }}</p>
    <p><strong>Location:</strong> {{ job.location }}</p>
    <p><strong>Description:</strong> {{ job.description }}</p>
    <p><strong>Role:</strong> {{ job.role }}</p>
    <p><strong>Experience:</strong> {{ job.requiredExperienceYears }} years</p>
    <p><strong>Remote:</strong> {{ job.remote ? 'Yes' : 'No' }}</p>
  </div>
</div>
```

## Notes

- The backend endpoint is `/api/jobs`.
- Search values are sent as query parameters: `?title=...&company=...&location=...`.
- Make sure `HttpClientModule` and `FormsModule` are imported in your Angular module.
- If the backend is on a separate port, configure a proxy or CORS accordingly.
