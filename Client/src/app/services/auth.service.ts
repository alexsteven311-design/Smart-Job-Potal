import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/api';
import { API_BASE } from './api.config';

interface Session {
  token: string;
  user: User;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly user = signal<User | null>(null);
  readonly isAuthenticated = computed(() => !!this.user());
  private token?: string;
  private readonly apiBase = `${API_BASE}/api/auth`;

  constructor(private http: HttpClient, private router: Router) {
    this.loadSession();
  }

  private safeStorageGet(key: string): string | null {
    try {
      return typeof window !== 'undefined' ? window.localStorage.getItem(key) : null;
    } catch {
      return null;
    }
  }

  private safeStorageSet(key: string, value: string) {
    try {
      if (typeof window !== 'undefined') {
        window.localStorage.setItem(key, value);
      }
    } catch {
      // Ignore storage failures (private mode, quota, etc.)
    }
  }

  private safeStorageRemove(key: string) {
    try {
      if (typeof window !== 'undefined') {
        window.localStorage.removeItem(key);
      }
    } catch {
      // Ignore storage failures
    }
  }

  login(credentials: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiBase}/login`, credentials).pipe(
      tap((response) => this.saveSession(response))
    );
  }

  register(payload: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.apiBase}/register`, payload).pipe(
      tap((response) => this.saveSession(response))
    );
  }

  logout() {
    this.safeStorageRemove('smartjob_session');
    this.token = undefined;
    this.user.set(null);
    try {
      this.router.navigate(['/login']);
    } catch {
      // Ignore navigation failures during shutdown
    }
  }

  getToken() {
    return this.token;
  }

  isRecruiter() {
    const role = this.user()?.role?.toLowerCase();
    return role === 'recruiter' || role === 'employer';
  }

  private saveSession(response: AuthResponse) {
    this.token = response.token;
    const user: User = {
      email: response.email,
      name: response.name,
      role: response.role
    };
    this.user.set(user);
    this.safeStorageSet('smartjob_session', JSON.stringify({ token: response.token, user }));
  }

  private loadSession() {
    const stored = this.safeStorageGet('smartjob_session');
    if (!stored) {
      return;
    }
    try {
      const parsed = JSON.parse(stored) as Session;
      if (!parsed.token || this.isTokenExpired(parsed.token)) {
        this.safeStorageRemove('smartjob_session');
        return;
      }
      this.token = parsed.token;
      this.user.set(parsed.user);
    } catch {
      this.safeStorageRemove('smartjob_session');
    }
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = token.split('.')[1];
      if (!payload) return true;
      const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const claims = JSON.parse(atob(base64)) as { exp?: number };
      return !claims.exp || claims.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}
