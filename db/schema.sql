CREATE DATABASE IF NOT EXISTS smart_exam_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE smart_exam_system;

DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS exams;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('admin', 'teacher', 'student') NOT NULL DEFAULT 'student',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_users_role (role)
) ENGINE=InnoDB;

CREATE TABLE exams (
  exam_id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(180) NOT NULL,
  subject VARCHAR(120) NOT NULL,
  description TEXT,
  duration_minutes INT NOT NULL,
  created_by INT NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_exams_created_by
    FOREIGN KEY (created_by) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT chk_exam_duration CHECK (duration_minutes BETWEEN 1 AND 360),
  INDEX idx_exams_active (active),
  INDEX idx_exams_teacher (created_by)
) ENGINE=InnoDB;

CREATE TABLE questions (
  question_id INT AUTO_INCREMENT PRIMARY KEY,
  exam_id INT NOT NULL,
  question_text TEXT NOT NULL,
  option_a VARCHAR(255) NOT NULL,
  option_b VARCHAR(255) NOT NULL,
  option_c VARCHAR(255) NOT NULL,
  option_d VARCHAR(255) NOT NULL,
  correct_answer ENUM('A', 'B', 'C', 'D') NOT NULL,
  marks INT NOT NULL DEFAULT 1,
  CONSTRAINT fk_questions_exam
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT chk_question_marks CHECK (marks BETWEEN 1 AND 100),
  INDEX idx_questions_exam (exam_id)
) ENGINE=InnoDB;

CREATE TABLE results (
  result_id INT AUTO_INCREMENT PRIMARY KEY,
  student_id INT NOT NULL,
  exam_id INT NOT NULL,
  score INT NOT NULL,
  total_marks INT NOT NULL,
  time_taken_seconds INT NOT NULL DEFAULT 0,
  submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_results_student
    FOREIGN KEY (student_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_results_exam
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT uq_result_student_exam UNIQUE (student_id, exam_id),
  CONSTRAINT chk_result_score CHECK (score >= 0 AND total_marks >= 0 AND score <= total_marks),
  INDEX idx_results_exam_score (exam_id, score DESC, time_taken_seconds ASC)
) ENGINE=InnoDB;

INSERT INTO users (id, name, email, password_hash, role) VALUES
(1, 'Admin User', 'admin@exam.com', 'pbkdf2$120000$AQIDBAUGBwgJCgsMDQ4PEA==$uIWw9x+WoLETdS8akh9PW+CZW55BzJmGY4H8jjZFt+o=', 'admin'),
(2, 'Teacher Demo', 'teacher@exam.com', 'pbkdf2$120000$AQIDBAUGBwgJCgsMDQ4PEA==$9L309frmA+VOp4ePlnr6DWS9yXE+89VeMlvEHp90Cgg=', 'teacher'),
(3, 'Student Demo', 'student@exam.com', 'pbkdf2$120000$AQIDBAUGBwgJCgsMDQ4PEA==$UbanIo5vfqOZiV1q6yeRQRBo3fAQQkVGdXu8TusyA8w=', 'student');

INSERT INTO exams (exam_id, title, subject, description, duration_minutes, created_by, active) VALUES
(1, 'Java Collections Fundamentals', 'Java', 'Core MCQs covering common Java collection classes and interfaces.', 30, 2, TRUE),
(2, 'Operating Systems Basics', 'Computer Science', 'Process, memory, scheduling, and synchronization concepts.', 30, 2, TRUE),
(3, 'Web Development Essentials', 'Web Development', 'HTML, CSS, JavaScript, and HTTP fundamentals.', 25, 2, TRUE);

INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer, marks) VALUES
(1, 'Which data structure follows the LIFO principle?', 'Queue', 'Tree', 'Stack', 'Graph', 'C', 1),
(1, 'HashMap is part of which package?', 'java.util', 'java.io', 'java.lang', 'java.net', 'A', 1),
(1, 'Which is not a valid Collection interface?', 'List', 'Set', 'Vector', 'Map', 'C', 1),
(1, 'Time complexity of HashMap get() operation in the average case?', 'O(n)', 'O(1)', 'O(log n)', 'O(n^2)', 'B', 1),
(1, 'Which interface does ArrayList implement?', 'Queue', 'List', 'Map', 'Stack', 'B', 1),
(1, 'Can ArrayList contain duplicate elements?', 'Yes', 'No', 'Depends on JVM', 'Only if synchronized', 'A', 1),
(1, 'Which collection does not allow duplicate elements?', 'ArrayList', 'HashSet', 'Vector', 'LinkedList', 'B', 1),
(1, 'Which class is synchronized or thread-safe?', 'ArrayList', 'Vector', 'LinkedList', 'HashMap', 'B', 1),
(1, 'What is the default initial capacity of an ArrayList after first insertion?', '10', '16', '0', '100', 'A', 1),
(1, 'Which interface provides FIFO-oriented operations?', 'List', 'Queue', 'Set', 'Map', 'B', 1),
(2, 'What is the core of an operating system?', 'Shell', 'Kernel', 'GUI', 'Driver', 'B', 1),
(2, 'Which is not a common process state?', 'Running', 'Ready', 'Waiting', 'Completed', 'D', 1),
(2, 'Which memory-management technique divides memory into fixed-size blocks?', 'Paging', 'Fragmentation', 'Swapping', 'Spooling', 'A', 1),
(2, 'A condition where processes wait forever for resources is called what?', 'Starvation', 'Deadlock', 'Mutex', 'Semaphore', 'B', 1),
(2, 'Which scheduling algorithm chooses the shortest next CPU burst?', 'FCFS', 'SJF', 'Round Robin', 'Priority Scheduling', 'B', 1),
(2, 'What is a lightweight process with its own stack?', 'Daemon', 'Thread', 'Child', 'System', 'B', 1),
(2, 'What provides an apparent expansion of RAM using disk?', 'RAM', 'ROM', 'Virtual Memory', 'Cache', 'C', 1),
(2, 'What is commonly used to solve critical section problems?', 'Pointers', 'Deadlock', 'Semaphore', 'Index', 'C', 1),
(2, 'What does LRU stand for in page replacement?', 'Last Running Unit', 'Least Recently Used', 'Live Resource Utility', 'Low Rate Usage', 'B', 1),
(2, 'Where is the operating system commonly loaded from during boot?', 'CPU', 'Hard Disk or SSD', 'ROM', 'Cache', 'B', 1),
(3, 'What does HTML stand for?', 'Hyper Text Markup Language', 'High Tech Main Links', 'Hyperlinks Medium Logic', 'Home Tool Markup Layout', 'A', 1),
(3, 'Which HTML tag is used for the largest heading?', '<h1>', '<h6>', '<head>', '<header>', 'A', 1),
(3, 'Which CSS property changes background color?', 'color', 'bgcolor', 'background-color', 'bg-color', 'C', 1),
(3, 'Which HTML attribute defines inline styles?', 'class', 'style', 'font', 'styles', 'B', 1),
(3, 'Which HTTP status code means Not Found?', '200', '403', '404', '500', 'C', 1),
(3, 'What is the correct syntax for referencing an external script?', '<script href="...">', '<script src="...">', '<script link="...">', '<link src="...">', 'B', 1),
(3, 'Which CSS layout model is one-dimensional?', 'Float', 'Flexbox', 'Grid', 'Relative', 'B', 1),
(3, 'How do you create a function in JavaScript?', 'function:myFunc()', 'function myFunc()', 'def myFunc()', 'void myFunc()', 'B', 1),
(3, 'What helps adapt layouts for mobile screens?', 'Frames', 'DIV triggers', 'Media Queries', 'Tables', 'C', 1),
(3, 'Which tag is used to draw graphics with JavaScript?', '<canvas>', '<svg>', '<graphics>', '<img>', 'A', 1);
