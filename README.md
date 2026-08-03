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

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23226_localhost.jpeg?raw=true)

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23338_localhost.jpeg?raw=true)

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_2353_localhost.jpeg?raw=true)

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_2361_localhost.jpeg?raw=true)

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23816_localhost.jpeg?raw=true)

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23845_localhost.jpeg?raw=true)

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_23931_localhost.jpeg?raw=true)

![image alt](https://github.com/alexsteven311-design/Smart-Job-Potal/blob/main/Screenshot_1-8-2026_231023_localhost.jpeg?raw=true)


