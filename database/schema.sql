CREATE DATABASE IF NOT EXISTS skill_swap_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE skill_swap_db;

CREATE TABLE IF NOT EXISTS students (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    teach_skill VARCHAR(120) NOT NULL,
    learn_skill VARCHAR(120) NOT NULL,
    additional_skills VARCHAR(500) NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_students_email (email)
);

-- These fixed email addresses make the sample inserts safe to run repeatedly.
INSERT INTO students (name, email, teach_skill, learn_skill, additional_skills)
VALUES
    ('Aarav Sharma', 'aarav.demo@skillswap.local', 'Java', 'SQL', 'HTML, Git'),
    ('Diya Patel', 'diya.demo@skillswap.local', 'SQL', 'Java', 'Python, Excel'),
    ('Kabir Singh', 'kabir.demo@skillswap.local', 'Python', 'Data Science', 'JavaScript')
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Inspection queries (safe to run; these do not modify student data):
-- SHOW COLUMNS FROM students;
-- SELECT id, name, email, teach_skill, learn_skill, additional_skills
-- FROM students
-- ORDER BY name, id;
