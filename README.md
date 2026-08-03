# Smart Job Portal

This repository contains both the backend Spring Boot API server and the Angular frontend client for the Smart Job Portal.

## Project structure

- `Server/` - Spring Boot backend
- `Client/` - Angular frontend source

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
