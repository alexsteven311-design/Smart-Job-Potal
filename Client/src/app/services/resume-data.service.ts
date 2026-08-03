import { Injectable, signal } from '@angular/core';

export interface ParsedResumeData {
  name: string;
  skills: string;
  experienceYears: number;
  preferredRole: string;
}

@Injectable({ providedIn: 'root' })
export class ResumeDataService {
  readonly parsedData = signal<ParsedResumeData | null>(null);

  set(data: ParsedResumeData) {
    this.parsedData.set(data);
  }

  clear() {
    this.parsedData.set(null);
  }
}
