import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './services/auth.service';
import { ToastService } from './services/toast.service';

interface Post {
  id: number;
  category: string;
  title: string;
  content: string;
  authorName: string;
  authorRole: string;
  company: string;
  tags: string;
  likes: number;
  comments: number;
  shares: number;
  createdAt: string;
  authorId?: number;
}

interface Comment {
  id: number;
  authorName: string;
  content: string;
  createdAt: string;
}

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.scss'
})
export class FeedComponent implements OnInit {
  tabs = ['All', 'News', 'Hiring', 'Success'];
  activeTab = 'All';
  posts: Post[] = [];
  loading = false;
  likedIds = new Set<number>();

  showForm = false;
  submitting = false;
  newPost = { category: 'NEWS', title: '', content: '', authorRole: '', company: '', tags: '' };

  // comments
  openCommentPostId: number | null = null;
  commentMap = new Map<number, Comment[]>();
  commentInput = new Map<number, string>();
  commentLoading = new Map<number, boolean>();
  commentSubmitting = new Map<number, boolean>();

  constructor(private http: HttpClient, public auth: AuthService, private toast: ToastService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    const cat = this.activeTab === 'All' ? '' : this.activeTab.toUpperCase();
    const url = cat ? `/api/posts?category=${cat}` : '/api/posts';
    this.http.get<Post[]>(url).subscribe({
      next: p => { this.posts = p; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  selectTab(tab: string) { this.activeTab = tab; this.load(); }

  like(post: Post) {
    if (this.likedIds.has(post.id)) return;
    this.http.post<{ likes: number }>(`/api/posts/${post.id}/like`, {}).subscribe(r => {
      post.likes = r.likes;
      this.likedIds.add(post.id);
    });
  }

  toggleComments(post: Post) {
    if (this.openCommentPostId === post.id) {
      this.openCommentPostId = null;
      return;
    }
    this.openCommentPostId = post.id;
    if (!this.commentMap.has(post.id)) {
      this.commentLoading.set(post.id, true);
      this.http.get<Comment[]>(`/api/posts/${post.id}/comments`).subscribe({
        next: c => { this.commentMap.set(post.id, c); this.commentLoading.set(post.id, false); },
        error: () => this.commentLoading.set(post.id, false)
      });
    }
  }

  submitComment(post: Post) {
    const text = (this.commentInput.get(post.id) ?? '').trim();
    if (!text) return;
    this.commentSubmitting.set(post.id, true);
    this.http.post<Comment>(`/api/posts/${post.id}/comments`, { content: text }).subscribe({
      next: c => {
        const list = this.commentMap.get(post.id) ?? [];
        this.commentMap.set(post.id, [...list, c]);
        post.comments++;
        this.commentInput.set(post.id, '');
        this.commentSubmitting.set(post.id, false);
      },
      error: () => {
        this.toast.error('Login to comment');
        this.commentSubmitting.set(post.id, false);
      }
    });
  }

  share(post: Post) {
    const url = `${window.location.origin}/feed#post-${post.id}`;
    navigator.clipboard.writeText(url).then(() => this.toast.success('Link copied!'));
    this.http.post<{ shares: number }>(`/api/posts/${post.id}/share`, {}).subscribe(r => post.shares = r.shares);
  }

  submit() {
    if (!this.newPost.title.trim() || !this.newPost.content.trim()) return;
    this.submitting = true;
    this.http.post<Post>('/api/posts', this.newPost).subscribe({
      next: p => {
        this.posts.unshift(p);
        this.newPost = { category: 'NEWS', title: '', content: '', authorRole: '', company: '', tags: '' };
        this.showForm = false;
        this.submitting = false;
      },
      error: () => { this.submitting = false; }
    });
  }

  delete(post: Post) {
    if (!confirm('Delete this post?')) return;
    this.http.delete(`/api/posts/${post.id}`).subscribe(() => {
      this.posts = this.posts.filter(p => p.id !== post.id);
    });
  }

  canDelete(post: Post): boolean {
    const user = this.auth.user();
    return !!user && (user.role === 'admin' || post.authorId === (user as any).id);
  }

  timeAgo(dateStr: string): string {
    const diff = Date.now() - new Date(dateStr).getTime();
    const m = Math.floor(diff / 60000);
    if (m < 60) return `${m}m ago`;
    const h = Math.floor(m / 60);
    if (h < 24) return `${h}h ago`;
    return `${Math.floor(h / 24)}d ago`;
  }

  categoryIcon(cat: string): string {
    return { NEWS: '📰', HIRING: '🚀', SUCCESS: '🏆' }[cat] ?? '📌';
  }

  categoryLabel(cat: string): string {
    return { NEWS: 'News', HIRING: 'Hiring', SUCCESS: 'Success Story' }[cat] ?? cat;
  }

  tagList(tags: string): string[] {
    return tags ? tags.split(',').map(t => t.trim()).filter(Boolean) : [];
  }
}
