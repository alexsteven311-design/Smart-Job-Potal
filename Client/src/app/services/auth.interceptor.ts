import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { tap } from 'rxjs/operators';

const PUBLIC_PATH_PREFIXES = [
  '/api/auth',
  '/api/jobs',
  '/api/companies',
  '/api/posts',
  '/api/candidates/resume-analyze',
];

function isPublicUrl(url: string): boolean {
  try {
    const path = new URL(url).pathname;
    return PUBLIC_PATH_PREFIXES.some(prefix => path.startsWith(prefix));
  } catch {
    return PUBLIC_PATH_PREFIXES.some(prefix => url.startsWith(prefix));
  }
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);

  if (!isPublicUrl(req.url)) {
    const token = auth.getToken();
    if (token) {
      req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
    }
  }

  return next(req).pipe(
    tap({ error: (err) => {
      if (err instanceof HttpErrorResponse && err.status === 401 && !isPublicUrl(req.url)) {
        try { auth.logout(); } catch { }
      }
    }})
  );
};
