import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CandidateService } from './services/candidate.service';
import { JobService } from './services/job.service';
import { ToastService } from './services/toast.service';
import { Job, MockInterviewSession } from './models/api';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mock-interview.component.html',
  styleUrl: './mock-interview.component.scss'
})
export class MockInterviewComponent implements OnInit {
  private readonly candidateService = inject(CandidateService);
  private readonly jobService       = inject(JobService);
  private readonly toast            = inject(ToastService);

  readonly jobs     = signal<Job[]>([]);
  readonly session  = signal<MockInterviewSession | null>(null);
  readonly loading  = signal(false);
  readonly error    = signal('');

  selectedJobId?: number;
  targetRole = '';
  difficulty = 'MEDIUM';
  questionCount = 5;
  answer = '';
  activeQuestion = 0;

  ngOnInit(): void {
    this.jobService.getJobs().subscribe({
      next: (jobs) => this.jobs.set(jobs),
      error: () => this.error.set('Unable to load jobs. You can still enter a role manually.')
    });
  }

  start(): void {
    if (!this.selectedJobId && !this.targetRole.trim()) {
      this.toast.error('Select a job or enter a target role to generate your interview.');
      return;
    }
    this.loading.set(true); this.error.set('');
    this.candidateService.startMockInterview({ jobId: this.selectedJobId, targetRole: this.targetRole, difficulty: this.difficulty, questionCount: this.questionCount })
      .subscribe({
        next: (session) => { this.session.set(session); this.activeQuestion = 0; this.answer = ''; this.loading.set(false); this.toast.success('Mock interview started. Good luck!'); },
        error: () => { this.toast.error('Unable to generate the interview. Please try again.'); this.loading.set(false); }
      });
  }

  submitAnswer(): void {
    const question = this.currentQuestion();
    if (!question || !this.answer.trim()) return;
    this.loading.set(true);
    this.candidateService.submitMockInterviewAnswer(question.id, this.answer.trim()).subscribe({
      next: (updated) => {
        const session = this.session();
        if (session) this.session.set({ ...session, questions: session.questions.map(q => q.id === updated.id ? updated : q) });
        this.answer = ''; this.loading.set(false);
        this.toast.success('Answer saved.');
      },
      error: () => { this.toast.error('Unable to save your answer.'); this.loading.set(false); }
    });
  }

  nextQuestion(): void { if (this.activeQuestion < (this.session()?.questions.length ?? 1) - 1) { this.activeQuestion++; this.answer = ''; } }
  previousQuestion(): void { if (this.activeQuestion > 0) { this.activeQuestion--; this.answer = ''; } }
  currentQuestion() { return this.session()?.questions[this.activeQuestion]; }

  complete(): void {
    const session = this.session(); if (!session) return;
    this.loading.set(true);
    this.candidateService.completeMockInterview(session.id).subscribe({
      next: (updated) => { this.session.set(updated); this.loading.set(false); this.toast.success(`Interview complete! Overall score: ${updated.overallScore ?? 0}/100.`); },
      error: () => { this.toast.error('Answer at least one question before completing.'); this.loading.set(false); }
    });
  }

  reset(): void { this.session.set(null); this.answer = ''; this.error.set(''); }
}
