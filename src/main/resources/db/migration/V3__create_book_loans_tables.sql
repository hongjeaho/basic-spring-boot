-- 도서 테이블
CREATE TABLE books (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50) NOT NULL,
    category VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 회원 테이블
CREATE TABLE members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 대출기록 테이블
CREATE TABLE loans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    loan_date DATE NOT NULL,
    return_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_category ON books(category);
CREATE INDEX idx_loans_member_id ON loans(member_id);
CREATE INDEX idx_loans_book_id ON loans(book_id);
CREATE INDEX idx_loans_date ON loans(loan_date);

-- 샘플 데이터
INSERT INTO books (title, author, category) VALUES
('Clean Code', 'Robert C. Martin', 'PROGRAMMING'),
('Effective Java', 'Joshua Bloch', 'PROGRAMMING'),
('Design Patterns', 'Gang of Four', 'PROGRAMMING'),
('The Pragmatic Programmer', 'Andy Hunt', 'PROGRAMMING'),
('Introduction to Algorithms', 'Thomas H. Cormen', 'ALGORITHM');

INSERT INTO members (name, email) VALUES
('Hong Jae Ho', 'hongjaeho@example.com'),
('Kim Min Su', 'kang@example.com'),
('Lee Ji Eun', 'jieun@example.com');

INSERT INTO loans (member_id, book_id, loan_date, return_date) VALUES
(1, 1, '2026-06-01', '2026-06-15'),
(1, 2, '2026-06-05', NULL),
(2, 3, '2026-06-03', '2026-06-10'),
(3, 4, '2026-06-07', NULL);
