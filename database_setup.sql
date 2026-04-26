SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS lessons, courses, results, options, questions, profiles, quizzes, users;
SET FOREIGN_KEY_CHECKS = 1;

-- Recreate all tables with the correct structure.
-- Table 1: users
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) DEFAULT 'USER',
    active BOOLEAN DEFAULT TRUE
);

-- Insert default admin user
INSERT INTO users (username, password, email, role) VALUES ('admin', 'admin123', 'admin@zgen.com', 'ADMIN');
INSERT INTO users (username, password, email, role) VALUES ('user1', 'user123', 'user1@zgen.com', 'USER');

-- Table 2: quizzes (with the 'category' column)
CREATE TABLE quizzes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    subject VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'General'
);

-- Table 3: questions (with the 'difficulty' column)
CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    quiz_id INT,
    question_text TEXT NOT NULL,
    difficulty ENUM('Easy', 'Medium', 'Hard') NOT NULL DEFAULT 'Easy',
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Table 4: options
CREATE TABLE options (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT,
    option_text VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Table 5: results (with the 'total_questions' column)
CREATE TABLE results (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    quiz_id INT,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    date_taken TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Table 6: profiles
CREATE TABLE profiles (
    user_id INT PRIMARY KEY,
    full_name VARCHAR(100),
    bio TEXT,
    profile_image_url VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert all sample data.
-- Quiz 1: Java Basics
INSERT INTO quizzes (id, subject, category) VALUES (1, 'Java Basics', 'General');
INSERT INTO questions (quiz_id, question_text, difficulty) VALUES 
(1, 'What is the purpose of the `public static void main(String[] args)` method?', 'Easy'),
(1, 'Which data type would you use to store a single character like ''A''?', 'Easy'),
(1, 'What does the ''final'' keyword mean when applied to a variable?', 'Medium');
INSERT INTO options (question_id, option_text, is_correct) VALUES 
(1, 'It is the entry point for a Java application.', TRUE), (1, 'It loads the Java Virtual Machine.', FALSE), (1, 'It is used to declare public variables.', FALSE),
(2, 'String', FALSE), (2, 'character', FALSE), (2, 'char', TRUE),
(3, 'The variable can be accessed from any package.', FALSE), (3, 'The variable''s value cannot be changed once assigned.', TRUE), (3, 'The variable is shared among all instances of a class.', FALSE);


-- Quiz 2: Data Structures & Algorithms
INSERT INTO quizzes (id, subject, category) VALUES (2, 'Data Structures & Algorithms', 'DSA');
INSERT INTO questions (quiz_id, question_text, difficulty) VALUES 
(2, 'What is the time complexity of a lookup in a hash table in the average case?', 'Easy'),
(2, 'Which data structure is typically used to implement a priority queue?', 'Medium'),
(2, 'Which sorting algorithm is NOT a comparison-based sort?', 'Hard');
INSERT INTO options (question_id, option_text, is_correct) VALUES 
(4, 'O(1)', TRUE), (4, 'O(n)', FALSE), (4, 'O(log n)', FALSE),
(5, 'Stack', FALSE), (5, 'Heap', TRUE), (5, 'Queue', FALSE),
(6, 'Merge Sort', FALSE), (6, 'Quick Sort', FALSE), (6, 'Counting Sort', TRUE);


-- Quiz 3: DBMS & SQL
INSERT INTO quizzes (id, subject, category) VALUES (3, 'DBMS & SQL', 'DBMS');
INSERT INTO questions (quiz_id, question_text, difficulty) VALUES 
(3, 'What does the SQL command `JOIN` do?', 'Easy'),
(3, 'Which normalization form deals with transitive dependency?', 'Medium'),
(3, 'What is the primary difference between `DELETE` and `TRUNCATE` in SQL?', 'Hard');
INSERT INTO options (question_id, option_text, is_correct) VALUES 
(7, 'Deletes rows from a table', FALSE), (7, 'Combines rows from two or more tables based on a related column', TRUE), (7, 'Creates a new table', FALSE),
(8, '1NF', FALSE), (8, '2NF', FALSE), (8, '3NF', TRUE),
(9, 'TRUNCATE is faster and cannot be rolled back', TRUE), (9, 'DELETE is faster and cannot be rolled back', FALSE), (9, 'They are functionally identical', FALSE);

-- Table 7: courses
CREATE TABLE courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    introduction TEXT,
    created_by_user_id INT,
    FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table 8: lessons
CREATE TABLE lessons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT,
    title VARCHAR(100) NOT NULL,
    material TEXT,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Table 9: feedback
CREATE TABLE feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    quiz_title VARCHAR(100) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Let you know the script is done.
SELECT 'Database reset and seeded successfully!' AS status;
