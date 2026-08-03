# 🎯 Smart Job Portal: AI-Powered Hiring Platform

A modern, full-stack smart job portal built with **Angular** (frontend) and **Spring Boot** (backend). This platform delivers a seamless candidate and recruiter experience with AI resume analysis, job matching, application tracking, and mock interviews.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [API Documentation](#api-documentation)
- [Running the Application](#running-the-application)
- [Development](#development)
- [Contributing](#contributing)

---

## 📌 Overview

Smart Job Portal is designed to connect job seekers, recruiters, and administrators through intelligent hiring workflows.

- **Frontend**: Angular-based single-page application with candidate and recruiter dashboards
- **Backend**: Spring Boot REST API for authentication, job management, resume processing, and application tracking
- **AI Capabilities**: Resume analysis, ATS scoring, job recommendations, and mock interview scoring
- **Roles**: Candidate, Recruiter, Admin

---

## 🛠️ Tech Stack

### Frontend
- **Framework**: Angular
- **Language**: TypeScript
- **Styling**: CSS / SCSS
- **Build Tool**: Angular CLI
- **Client**: Angular HttpClient with RxJS

### Backend
- **Framework**: Spring Boot
- **Language**: Java
- **Build Tool**: Maven
- **API**: RESTful Web Services
- **Database**: H2 (development) and MySQL (optional production)

### Development Tools
- **Version Control**: Git & GitHub
- **Editor**: VS Code / IntelliJ IDEA
- **Formatter**: Prettier
- **API Testing**: Postman
- **Shell**: PowerShell / Bash

---

## ✨ Features

### Candidate Features
- Resume upload and AI resume analysis
- ATS resume scoring and resume parsing
- Job search and job recommendations
- Apply to jobs with application status tracking
- Candidate profile management
- Mock interview creation, answering, and scoring
- Download resumes and view application history

### Recruiter Features
- Post and manage job listings
- View and shortlist applicants
- Schedule interviews and send notifications
- Download candidate resumes
- Manage recruiter dashboard and application workflows

### Admin Features
- Manage users and recruiters
- Delete invalid job postings
- View dashboard analytics and application statistics

### Backend Features
- JWT authentication with role-based authorization
- REST API endpoints for jobs, users, applications, resumes, mock interviews, and more
- H2 database for development and MySQL support for production
- Email notification integration
- Error handling and security configuration

---

## 📁 Project Structure

```
Smart-Job-Potal/
├── .gitignore
├── README.md
├── pom.xml
├── Client/
│   ├── angular.json
│   ├── package.json
│   ├── package-lock.json
│   ├── tsconfig.json
│   ├── src/
│   │   ├── app/
│   │   ├── assets/
│   │   └── index.html
│   └── README.md
├── src/
│   ├── main/
│   │   ├── java/com/smartjobportal/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-mysql.properties
│   │       ├── data.sql
│   │       └── static/
│   │           └── browser/
│   └── test/
└── target/
```

---

## 🚀 Setup & Installation

### Prerequisites
- Node.js 18+ with npm
- Java 17+
- Maven 3.8+
- Git
- MySQL (optional for production)

### 1. Clone the repository
```bash
git clone https://github.com/alexsteven311-design/Smart-Job-Potal.git
cd Smart-Job-Potal
```

### 2. Backend setup
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Frontend setup
```bash
cd Client
npm install
npm start
```

### 4. Configuration
- Update `src/main/resources/application.properties` for H2 configuration
- Update `src/main/resources/application-mysql.properties` for MySQL credentials
- Use `Client/package.json` scripts for frontend build and testing

---

## 📘 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Candidate Endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/candidates/profile`
- `PUT /api/candidates/profile`
- `POST /api/candidates/profile/upload-resume`
- `POST /api/candidates/jobs/{jobId}/apply`
- `GET /api/candidates/applications`
- `GET /api/candidates/applications/{applicationId}`
- `GET /api/candidates/recommendations`
- `GET /api/candidates/resume-score`
- `POST /api/candidates/mock-interviews`
- `POST /api/candidates/mock-interviews/questions/{questionId}/answer`
- `POST /api/candidates/mock-interviews/{sessionId}/complete`
- `GET /api/candidates/mock-interviews`

### Recruiter Endpoints
- `POST /api/recruiter/jobs`
- `GET /api/recruiter/jobs/{jobId}/applicants`
- `GET /api/recruiter/applications/{id}/resume`
- `PUT /api/recruiter/applications/{id}/status`
- `POST /api/recruiter/applications/{id}/schedule`
- `POST /api/recruiter/applications/{id}/email`

### Admin Endpoints
- `GET /api/admin/users`
- `DELETE /api/admin/users/{id}`
- `GET /api/admin/recruiters`
- `DELETE /api/admin/recruiters/{id}`
- `DELETE /api/admin/jobs/{id}`
- `GET /api/admin/dashboard`
- `GET /api/admin/applications/statistics`

---

## ▶️ Running the Application

### Start the backend
```bash
mvn spring-boot:run
```

### Start the frontend
```bash
cd Client
npm start
```

### Verify
1. Open `http://localhost:4200`
2. Confirm frontend connects to backend
3. Test candidate and recruiter flows

---

## 🔧 Development

### Frontend
- `npm install`
- `npm start`
- `npm run build`
- `npm test`

### Backend
- `mvn clean install`
- `mvn spring-boot:run`

### Formatting
- Prettier for frontend code

---

## 🤝 Contributing

Contributions are welcome! Create a pull request or open an issue to suggest new features, bug fixes, or improvements.

**Happy Coding!✌️**

👤 Author
- Alex Steven

GitHub: @alexsteven311-design

**<h2>🏠 Home Page</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23226_localhost.jpeg?raw=true)

**Overview** :

The home page provides an attractive landing experience for job seekers. It highlights TalentBridge's AI-powered features, displays platform statistics, and provides quick navigation to browse jobs, access dashboards, and use AI tools.

**Key components:**

- Modern Hero Section

- AI Resume Score Preview

- Interview Notification Cards

- Platform Statistics

- Feature Highlights

- Call-to-Action Buttons

- Responsive Dark Theme

**<h2>👤 User Profile Management</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_232132_localhost.jpeg?raw=true)

**Overview:** User settings page displaying personal details, contact info, professional links, and technical skill sets.

**Key Components:**

- Resume Autofill banner allowing quick profile setup from parsed resume data.

- Input fields for contact details, LinkedIn, GitHub portfolio, and personal website links.

- Experience summary section featuring years of experience, preferred role, and an indexed list of technical skills.

**<h2>👤 Candidate Registration</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_3-8-2026_124441_localhost.jpeg?raw=true)

**Overview:**

The registration page allows new candidates to create an account by providing their professional information, skills, and preferred job role.

**Key Components:**

- Candidate registration

- Recruiter/Candidate selection

- Skills input

-Preferred role selection

- Experience details

- Secure account creation

**<h2>🔐 Login Page</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_3-8-2026_124413_localhost.jpeg?raw=true)

**Overview:**

A secure authentication page that allows registered users to access their personalized dashboard and manage applications.

**Key Components:**

- Email authentication

- Secure password login

- Clean responsive design

- Redirect to dashboard after login

**<h2>💼 Job Listings</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23338_localhost.jpeg?raw=true)

**Overview:**

The Jobs page allows users to search and explore opportunities from different companies using advanced filtering options.

**Key Components:**

- Search jobs by title

- Search by company

- Search by location

-  Category filters

- Save jobs

- Apply Now functionality

- Responsive job cards

- Experience-based filtering

**<h2>📄 Easy Apply Portal</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_3-8-2026_124936_localhost.jpeg?raw=true)

**Overview:**

The application page enables candidates to submit job applications effortlessly. Personal details are automatically populated, allowing users to upload resumes and send personalized cover letters in just one click.

**Key Components:**
- Auto-filled candidate information

- Resume upload

- Personalized cover letter

- Expected salary input

- Notice period selection

- One-click application submission

- Validation and confirmation

**<h2>🏢 Company Directory & Ratings</h2>**
![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_2353_localhost.jpeg?raw=true)

**Overview:** Searchable employer discovery module for exploring potential workplaces.

**Key Components:**

- Alphabetically structured directory of top corporate and public sector employers.

- User rating indicators and review counts for company reputation insight.

- Open position counts per organization with direct navigation links.

**<h2>🎯 AI-Powered Job Recommendations</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_2361_localhost.jpeg?raw=true)

**Overview**: Personalized job matching interface tailored to candidate skill sets.

**Key Components:**

- Advanced filter options based on location, years of experience, salary (LPA), and specific tech stacks.

- Percentage-based match score indicating alignment between candidate skills and job requirements.

- Card-based job postings displaying required skills, location, estimated salary, and direct application triggers.

**<h2>📰 Corporate News & Community Feed:</h2>**
![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23816_localhost.jpeg?raw=true)

**Overview:** Real-time social and tech industry news feed keeping job seekers informed.

**Key Components:**

- Categorized feed updates filtered by News, Hiring, and Success stories.

- Interactive posts with engagement metrics (likes, comments, reposts).

- Community post creation tool allowing users to share announcements.

**<h2>📊 User Dashboard</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23845_localhost.jpeg?raw=true)

**Overview:** Centralized hub tracking job application metrics and quick account status.

**Key Components:**

- Profile completion tracker and resume evaluation score card.

- Real-time metrics for submitted applications and upcoming scheduled interviews.

- Personalized job recommendations based on user activity and profile details.

**<h2>📄 AI Resume Score Checker</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23931_localhost.jpeg?raw=true)

**Overview:** Comprehensive ATS analysis tool providing feedback on resume formatting, keyword optimization, and skill alignment.

**Key Components:**

- Breakdown of metrics including Skills Match, ATS Compatibility, Experience Level, and Resume Keywords.

- Actionable Improvement Suggestions highlighting missing sections or certifications.

- Curated list of suggested jobs matching detected resume skills along with targeted skill improvement resources.

**<h2>📝 AI Cover Letter Generator</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_231023_localhost.jpeg?raw=true)

**Overview:** Automated tool that generates customized, role-specific cover letters using uploaded resume data and job details.

**Key Components:**

- Simple 3-step workflow: Upload Resume $\rightarrow$ Choose Job $\rightarrow$ Generate Cover Letter.

- Fully editable text preview area allowing customization before finalizing.

- One-click action buttons to copy to clipboard or download as a document.

**<h2>💬 AI Interview Question & Answer Interface</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23119_localhost.jpeg?raw=true)

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_231218_localhost.jpeg?raw=true)

**Overview:** Interactive practice interface for answering AI-generated behavioral and technical questions.

**Key Components:**

- Focuses on specific categories (e.g., Introduction, Technical, Collaboration).

- Rich-text answer box with real-time response saving and progress tracking.

- Navigation controls to seamlessly transition through interview questions.

**<h2>📈 AI Interview Evaluation & Feedback</h2>**
![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_231552_localhost.jpeg?raw=true)

**Overview:** Automated evaluation screen providing detailed feedback on completed interview responses.

**Key Components:**

- Overall Performance Score calculated based on relevance, structure, detail, and impact.

- Detailed breakdown of user responses alongside targeted suggestions for improvement.

- Question-by-question scoring to identify specific areas for growth.

**<h2>💼 Recruiter Dashboard (Job Posting)</h2>**

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_3-8-2026_104350_localhost.jpeg?raw=true)

**Overview:** Portal for recruiters and hiring managers to post new job openings and manage incoming candidates.

**Key Components:**

- Multi-field form for defining job details (Title, Company, Location, Role Category, and Experience required).

- Markdown-supported Job Description editor for structured, rich-text role details.

- Options to toggle workplace arrangements like Remote / Hybrid work settings.

