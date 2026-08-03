-- SQL seed and schema helper for the Smart Job Portal backend
-- Database name must match application-mysql.properties: smartjobdb
CREATE DATABASE IF NOT EXISTS smartjobdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smartjobdb;

DROP TABLE IF EXISTS job;
CREATE TABLE job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    company VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    role VARCHAR(100),
    required_experience_years INT,
    posted_at DATETIME NOT NULL,
    remote TINYINT(1) NOT NULL DEFAULT 0
);

INSERT INTO job (title, company, location, description, role, required_experience_years, posted_at, remote)
VALUES
('Full Stack Developer', 'TechNova Solutions', 'Bengaluru', 'Build end-to-end web applications using Spring Boot and Angular.', 'Full Stack Developer', 1, '2026-06-01 09:00:00', 0),
('Backend Developer', 'CloudEdge Pvt Ltd', 'Remote', 'Develop REST APIs, work with microservices and PostgreSQL.', 'Backend Developer', 2, '2026-05-28 10:30:00', 1),
('Frontend Engineer', 'PixelWave Studios', 'Chennai', 'Create responsive UI screens with Angular, RxJS and CSS.', 'Frontend Engineer', 1, '2026-06-02 08:45:00', 0),
('Java Developer', 'Nimbus Tech', 'Hyderabad', 'Write scalable backend services with Spring Boot and Hibernate.', 'Java Developer', 3, '2026-05-30 15:20:00', 0),
('UI/UX Developer', 'Waveform Labs', 'Mumbai', 'Implement accessible Angular components and design systems.', 'UI/UX Developer', 1, '2026-06-03 11:10:00', 1);

SELECT * FROM job;
