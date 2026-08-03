# Smart Job Portal

## Overview

The Smart Job Portal is a full-stack application that connects job seekers, recruiters, and administrators with intelligent hiring workflows. It includes an Angular frontend and a Spring Boot backend, with features for resume analysis, job matching, application tracking, mock interviews, and recruiter management.

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Smart Job Portal features](#smart-job-portal-features)
- [Run backend locally](#run-backend-locally)
- [Run frontend locally](#run-frontend-locally)
- [Database support](#database-support)

## Tech Stack

- Backend: Java, Spring Boot, Spring Data JPA, Spring Security
- Frontend: Angular
- Database: H2 (development) and MySQL (optional production profile)
- Build tools: Maven for backend, npm/Angular CLI for frontend
- Authentication: JWT-based role access for candidates, recruiters, and admins
- Development tools: IntelliJ IDEA / VS Code, Git, Postman, Maven Wrapper, npm scripts, Prettier

## Project Structure

```
Server/
├── .gitignore
├── README.md
├── pom.xml
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
├── target/
└── Client/
    ├── angular.json
    ├── package.json
    ├── package-lock.json
    ├── tsconfig.json
    ├── src/
    │   ├── app/
    │   ├── assets/
    │   └── index.html
    └── README.md
```

- `Server/` - Spring Boot backend source, API controllers, services, models, repositories, and application configuration
- `Server/Client/` - Angular frontend source, UI components, routes, and client app configuration
- `Server/src/main/resources/static/` - static frontend build assets served by the backend
- `Server/src/main/resources/application.properties` - default Spring Boot configuration
- `Server/src/main/resources/application-mysql.properties` - MySQL profile configuration

## Setup and Installation

### Prerequisites

- Java 17 or newer
- Maven 3.8+ installed
- Node.js 18+ and npm 11+ installed
- MySQL database (optional, for production)

### Backend setup

1. Open a terminal in the `Server/` folder.
2. Install dependencies and build the backend:
   ```powershell
   mvn clean install
   ```
3. Run the backend service:
   ```powershell
   mvn spring-boot:run
   ```

### Frontend setup

1. Open a terminal in the `Server/Client/` folder.
2. Install frontend dependencies:
   ```powershell
   npm install
   ```
3. Start the Angular development server:
   ```powershell
   npm start
   ```

### Configuration

- Update backend database configuration in `Server/src/main/resources/application.properties` for H2
- Update `Server/src/main/resources/application-mysql.properties` for MySQL credentials
- Use `npm` scripts from `Server/Client/package.json` to build or test the frontend

## Smart Job Portal features

- AI Resume Analysis and scoring
- ATS Resume matching
- Job recommendations for candidates
- Resume autofill and resume parsing
- Job application tracking and status updates
- Recruiter job posting and applicant management
- Corporate feed and post management
- Candidate dashboard and profile management
- Mock interview creation, answering, and scoring
- Authentication with role-based access for candidates, recruiters, and admins
- Email notifications and interview scheduling
- H2 and MySQL database support

## Run backend locally

1. Open a terminal in the `Server` folder.
2. Run:
   ```powershell
   mvn spring-boot:run
   ```
3. Open browser at `http://localhost:8080`.

## Run frontend locally

1. Open a terminal in the `Client` folder.
2. Run:
   ```powershell
   npm install
   npm start
   ```
3. Open browser at `http://localhost:4200`.

## API endpoints

- `GET /api/jobs` - list all jobs
- `GET /api/jobs/{id}` - get a job by ID
- `POST /api/jobs` - create a job
- `PUT /api/jobs/{id}` - update a job
- `DELETE /api/jobs/{id}` - delete a job

- `GET /api/users` - list all users
- `GET /api/users/{id}` - get a user by ID
- `POST /api/users` - create a user
- `PUT /api/users/{id}` - update a user
- `DELETE /api/users/{id}` - delete a user

## Authentication and candidate module

- `POST /api/auth/register` - register a candidate or employer and receive a JWT token
- `POST /api/auth/login` - log in and receive a JWT token
- `GET /api/candidates/profile` - get the logged-in candidate profile
- `PUT /api/candidates/profile` - update the logged-in candidate profile
- `POST /api/candidates/profile/upload-resume` - upload a resume (PDF/DOCX)
- `POST /api/candidates/jobs/{jobId}/apply` - apply for a job
- `GET /api/candidates/applications` - list logged-in candidate applications
- `GET /api/candidates/applications/{applicationId}` - view a specific application status
- `GET /api/candidates/recommendations` - get recommended jobs
- `GET /api/candidates/resume-score` - view the candidate resume score
- `POST /api/candidates/mock-interviews` - start a tailored mock interview (`targetRole`, `difficulty`, `questionCount`)
- `POST /api/candidates/mock-interviews/questions/{questionId}/answer` - save and score an answer
- `POST /api/candidates/mock-interviews/{sessionId}/complete` - finish a session and calculate its overall score
- `GET /api/candidates/mock-interviews` - view mock interview history

## Recruiter module

- `POST /api/recruiter/jobs` - post a new job
- `GET /api/recruiter/jobs/{jobId}/applicants` - view applicants for a job
- `GET /api/recruiter/applications/{id}/resume` - download a candidate resume
- `PUT /api/recruiter/applications/{id}/status` - shortlist/reject a candidate
- `POST /api/recruiter/applications/{id}/schedule` - schedule an interview
- `POST /api/recruiter/applications/{id}/email` - send an email to the candidate

## Admin module

- `GET /api/admin/users` - list all users
- `DELETE /api/admin/users/{id}` - remove a user account
- `GET /api/admin/recruiters` - list recruiter accounts
- `DELETE /api/admin/recruiters/{id}` - remove a recruiter account
- `DELETE /api/admin/jobs/{id}` - remove a fake or invalid job posting
- `GET /api/admin/dashboard` - view dashboard analytics
- `GET /api/admin/applications/statistics` - view application statistics

**Note:** these routes require a user with role `admin`.

## H2 console

Access the H2 database console at:

`http://localhost:8080/h2-console`

JDBC URL: `jdbc:h2:file:./data/smartjobdb`
User: `sa`
Password: (empty)

## MySQL database (optional)

This project also includes a MySQL profile so you can run against a real SQL database.

1. Create a MySQL database named `smartjobdb`.
2. Update the credentials in `src/main/resources/application-mysql.properties` if needed.
3. Run with the MySQL profile:
   ```powershell
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

## Next steps

- Add authentication and authorization
- Add job applications and candidate matching
- Add frontend UI or integrate with a React/Angular/Vue app
