import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from './services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-stack" aria-live="polite" aria-atomic="false">
      <div
        *ngFor="let toast of toastService.toasts()"
        class="toast toast--{{ toast.type }}"
        role="alert">
        <span class="toast-icon">{{ icons[toast.type] }}</span>
        <span class="toast-msg">{{ toast.message }}</span>
        <button class="toast-close" (click)="toastService.dismiss(toast.id)" aria-label="Dismiss">✕</button>
      </div>
    </div>
  `,
  styles: [`
    .toast-stack {
      position: fixed;
      bottom: 1.5rem;
      right: 1.5rem;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
      max-width: 380px;
      width: calc(100vw - 3rem);
      pointer-events: none;
    }

    .toast {
      display: flex;
      align-items: flex-start;
      gap: 0.65rem;
      padding: 0.875rem 1rem;
      border-radius: 10px;
      box-shadow: 0 8px 32px rgba(10,10,30,.18), 0 2px 8px rgba(10,10,30,.1);
      font-size: 0.875rem;
      font-weight: 500;
      line-height: 1.45;
      pointer-events: all;
      animation: toast-in 0.2s cubic-bezier(.34,1.56,.64,1);
      border: 1px solid rgba(255,255,255,.15);
    }

    .toast--success { background: #057642; color: #fff; }
    .toast--error   { background: #cc1016; color: #fff; }
    .toast--info    { background: #0a66c2; color: #fff; }

    .toast-icon { font-size: 1rem; flex-shrink: 0; margin-top: 1px; }
    .toast-msg  { flex: 1; }

    .toast-close {
      background: transparent;
      border: none;
      color: rgba(255,255,255,.7);
      font-size: 0.75rem;
      padding: 0;
      line-height: 1;
      cursor: pointer;
      flex-shrink: 0;
      margin-top: 2px;
      transition: color 0.15s;
      &:hover { color: #fff; }
    }

    @keyframes toast-in {
      from { opacity: 0; transform: translateY(12px) scale(0.95); }
      to   { opacity: 1; transform: translateY(0) scale(1); }
    }

    @media (max-width: 480px) {
      .toast-stack { bottom: 1rem; right: 0.75rem; left: 0.75rem; width: auto; max-width: 100%; }
    }
  `]
})
export class ToastComponent {
  readonly toastService = inject(ToastService);
  readonly icons: Record<string, string> = { success: '✓', error: '✕', info: 'ℹ' };
}
