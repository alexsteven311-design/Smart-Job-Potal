import { Component, inject, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from './services/notification.service';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-notification-bell',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notif-wrap" *ngIf="auth.isAuthenticated()">
      <button class="bell-btn" (click)="toggle()" [class.has-unread]="notifService.unreadCount() > 0" title="Notifications">
        🔔
        <span class="badge" *ngIf="notifService.unreadCount() > 0">{{ notifService.unreadCount() }}</span>
      </button>

      <div class="notif-dropdown" *ngIf="open">
        <div class="notif-header">
          <span>Notifications</span>
          <button class="mark-all" (click)="markAll()" *ngIf="notifService.unreadCount() > 0">Mark all read</button>
        </div>
        <div class="notif-list">
          <div *ngIf="notifService.notifications().length === 0" class="notif-empty">No notifications yet.</div>
          <div
            *ngFor="let n of notifService.notifications()"
            class="notif-item"
            [class.unread]="!n.read"
            (click)="markOne(n.id)">
            <span class="notif-icon">{{ icon(n.type) }}</span>
            <div class="notif-body">
              <p class="notif-msg">{{ n.message }}</p>
              <p class="notif-time">{{ n.createdAt | date:'MMM d, h:mm a' }}</p>
            </div>
            <span class="unread-dot" *ngIf="!n.read"></span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .notif-wrap { position: relative; }

    .bell-btn {
      position: relative;
      background: none;
      border: none;
      font-size: 1.25rem;
      cursor: pointer;
      padding: 0.4rem 0.5rem;
      border-radius: 8px;
      transition: background 0.15s;
      line-height: 1;
    }
    .bell-btn:hover { background: var(--gray-100, #f3f4f6); }
    .bell-btn.has-unread { animation: ring 1s ease; }

    @keyframes ring {
      0%,100% { transform: rotate(0); }
      20%      { transform: rotate(-15deg); }
      40%      { transform: rotate(15deg); }
      60%      { transform: rotate(-10deg); }
      80%      { transform: rotate(10deg); }
    }

    .badge {
      position: absolute;
      top: 2px; right: 2px;
      background: #ef4444;
      color: white;
      font-size: 0.65rem;
      font-weight: 700;
      min-width: 16px;
      height: 16px;
      border-radius: 999px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 3px;
      line-height: 1;
    }

    .notif-dropdown {
      position: absolute;
      right: 0;
      top: calc(100% + 8px);
      width: 340px;
      background: white;
      border: 1px solid #e5e7eb;
      border-radius: 12px;
      box-shadow: 0 8px 24px rgba(0,0,0,0.12);
      z-index: 999;
      overflow: hidden;
    }

    .notif-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 1rem;
      border-bottom: 1px solid #f3f4f6;
      font-weight: 600;
      font-size: 0.9rem;
    }

    .mark-all {
      background: none;
      border: none;
      color: #6366f1;
      font-size: 0.78rem;
      cursor: pointer;
      font-weight: 500;
    }
    .mark-all:hover { text-decoration: underline; }

    .notif-list { max-height: 360px; overflow-y: auto; }

    .notif-empty {
      padding: 2rem 1rem;
      text-align: center;
      color: #9ca3af;
      font-size: 0.88rem;
    }

    .notif-item {
      display: flex;
      align-items: flex-start;
      gap: 0.6rem;
      padding: 0.75rem 1rem;
      cursor: pointer;
      transition: background 0.12s;
      border-bottom: 1px solid #f9fafb;
      position: relative;
    }
    .notif-item:hover { background: #f9fafb; }
    .notif-item.unread { background: #eef2ff; }
    .notif-item.unread:hover { background: #e0e7ff; }

    .notif-icon { font-size: 1.1rem; margin-top: 2px; flex-shrink: 0; }

    .notif-body { flex: 1; min-width: 0; }
    .notif-msg { margin: 0; font-size: 0.84rem; color: #111827; line-height: 1.4; }
    .notif-time { margin: 0.2rem 0 0; font-size: 0.75rem; color: #9ca3af; }

    .unread-dot {
      width: 8px; height: 8px;
      background: #6366f1;
      border-radius: 50%;
      flex-shrink: 0;
      margin-top: 6px;
    }
  `]
})
export class NotificationBellComponent implements OnInit {
  readonly notifService = inject(NotificationService);
  readonly auth = inject(AuthService);
  open = false;

  ngOnInit() {
    if (this.auth.isAuthenticated()) this.notifService.startPolling();
  }

  toggle() {
    this.open = !this.open;
    if (this.open) this.notifService.load();
  }

  markAll() { this.notifService.markAllRead(); }

  markOne(id: number) { this.notifService.markRead(id); }

  icon(type: string): string {
    switch (type) {
      case 'APPLICATION_SUBMITTED':  return '✅';
      case 'APPLICATION_SHORTLISTED': return '⭐';
      case 'STATUS_CHANGED':         return '📋';
      case 'INTERVIEW_SCHEDULED':    return '📅';
      case 'JOB_RECOMMENDATION':     return '💼';
      default:                       return '🔔';
    }
  }

  @HostListener('document:click', ['$event'])
  onOutsideClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('app-notification-bell')) this.open = false;
  }
}
