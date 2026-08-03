export interface User {
  id?: number;
  name: string;
  email: string;
  role: string;
  phoneNumber?: string;
  location?: string;
  linkedInProfileUrl?: string;
  githubPortfolioUrl?: string;
  portfolioWebsite?: string;
  skills?: string;
  experienceYears?: number;
  preferredRole?: string;
  resumeFilename?: string;
  resumeScore?: number;
}

export interface Job {
  id?: number;
  title: string;
  company: string;
  location: string;
  description: string;
  role?: string;
  requiredExperienceYears?: number;
  postedAt?: string;
  remote?: boolean;
  /** Optional company-provided logo. A generated monogram is shown when absent. */
  logoUrl?: string;
}

export interface CompanySummary {
  name: string;
  rating: number;
  reviewCount: number;
  openPositionCount: number;
  logoUrl?: string;
  locations?: string[];
  openPositions?: Job[];
}

export interface AuthResponse {
  token: string;
  email: string;
  name: string;
  role: string;
}

export interface ResumeScore {
  resumeFilename?: string;
  resumeScore?: number;
  skills?: string;
}

export interface ResumeAnalysisResult {
  overallScore: number;
  skillScore: number;
  atsScore: number;
  experienceScore: number;
  keywordScore: number;
  expLevel: string;
  foundSkills: string[];
  matchedKeywords: string[];
  missingKeywords: string[];
  missingAtsSections: string[];
  suggestions: string[];
  parsedName: string;
  parsedEmail: string;
  parsedSkills: string[];
  jobSuggestions: { id: number; title: string; company: string; location: string; remote: boolean; role: string; requiredExperienceYears: number }[];
  skillTips: { tip: string; resource: string; resourceLabel: string }[];
}

export interface JobApplicationRequest {
  coverLetter: string;
}

export interface JobApplication {
  id: number;
  status: string;
  coverLetter?: string;
  job: Job;
}

export interface MockInterviewQuestion {
  id: number;
  order: number;
  category: string;
  question: string;
  answer?: string;
  score?: number;
  feedback?: string;
}

export interface MockInterviewSession {
  id: number;
  targetRole: string;
  jobId?: number;
  jobTitle?: string;
  difficulty: string;
  status: string;
  overallScore?: number;
  questions: MockInterviewQuestion[];
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: string;
  company?: string;
  phoneNumber?: string;
  location?: string;
  linkedInProfileUrl?: string;
  githubPortfolioUrl?: string;
  portfolioWebsite?: string;
  skills?: string;
  experienceYears?: number;
  preferredRole?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}
