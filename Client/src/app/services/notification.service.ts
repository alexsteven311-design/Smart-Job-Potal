import { Injectable, inject, signal, computed, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';
import { API_PREFIX } from './api.config';

export interface Notification {
  id: number;
  message: string;
  type: string;
  read: boolean;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService implements OnDestroy {
  private http = inject(HttpClient);
  private readonly api = `${API_PREFIX}/notifications`;
  private pollInterval?: ReturnType<typeof setInterval>;

  notifications = signal<Notification[]>([]);
  unreadCount = computed(() => this.notifications().filter(n => !n.read).length);

  load() {
    this.http.get<Notification[]>(this.api).subscribe({
      next: (data) => this.notifications.set(data),
      error: (error: HttpErrorResponse) => {
        // The auth interceptor clears an invalid session on 401. Stop the
        // timer too, so it cannot keep issuing unauthorized requests.
        if (error.status === 401) this.stopPolling();
      }
    });
  }

  startPolling() {
    if (this.pollInterval) return;
    this.load();
    try {
      this.pollInterval = setInterval(() => this.load(), 30_000);
    } catch {
      this.pollInterval = undefined;
    }
  }

  stopPolling() {
    if (this.pollInterval) {
      clearInterval(this.pollInterval);
      this.pollInterval = undefined;
    }
  }

  ngOnDestroy() { this.stopPolling(); }

  markAllRead() {
    this.http.put(`${this.api}/mark-all-read`, {}).subscribe(() => {
      this.notifications.update(list => list.map(n => ({ ...n, read: true })));
    });
  }

  markRead(id: number) {
    this.http.put(`${this.api}/${id}/read`, {}).subscribe(() => {
      this.notifications.update(list => list.map(n => n.id === id ? { ...n, read: true } : n));
    });
  }
}
