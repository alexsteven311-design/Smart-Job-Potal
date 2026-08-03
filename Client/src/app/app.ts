import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { NotificationBellComponent } from './notification-bell.component';
import { NotificationService } from './services/notification.service';
import { ToastComponent } from './toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, NotificationBellComponent, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  readonly authService = inject(AuthService);
  readonly notifService = inject(NotificationService);
  readonly title = signal('Smart Job Portal');
  readonly darkMode = signal(this.getInitialTheme() === 'dark');
  sidebarCollapsed = window.innerWidth < 768;
  get isMobile() { return window.innerWidth < 768; }

  ngOnInit() {
    this.applyTheme();
    if (this.authService.isAuthenticated()) {
      try {
        this.notifService.startPolling();
      } catch {
        // Ignore notification startup failures and keep the shell usable.
      }
    }
  }

  toggleTheme(): void {
    this.darkMode.update((enabled) => !enabled);
    this.applyTheme();
  }

  private getInitialTheme(): 'light' | 'dark' {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'light' || savedTheme === 'dark') {
      return savedTheme;
    }
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }

  private applyTheme(): void {
    const theme = this.darkMode() ? 'dark' : 'light';
    document.documentElement.dataset['theme'] = theme;
    localStorage.setItem('theme', theme);
  }
}
