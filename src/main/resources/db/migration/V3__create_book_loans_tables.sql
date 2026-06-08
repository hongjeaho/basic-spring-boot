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
INSERT INTO books (id, title, author, category, created_at, updated_at) VALUES (1, '토비의 스프링 3.1', '이일만', 'PROGRAMMING', '2026-06-08 21:04:28', '2026-06-09 08:03:36');
INSERT INTO books (id, title, author, category, created_at, updated_at) VALUES (2, 'Effective Java', 'Joshua Bloch', 'PROGRAMMING', '2026-06-08 21:04:28', '2026-06-08 21:04:28');
INSERT INTO books (id, title, author, category, created_at, updated_at) VALUES (3, 'Design Patterns', 'Gang of Four', 'PROGRAMMING', '2026-06-08 21:04:28', '2026-06-08 21:04:28');
INSERT INTO books (id, title, author, category, created_at, updated_at) VALUES (4, '클로드 코드 마스터', '이남희, 백승현', 'PROGRAMMING', '2026-06-08 21:04:28', '2026-06-09 08:04:09');
INSERT INTO books (id, title, author, category, created_at, updated_at) VALUES (5, '하네스 엔지니어링 with 클로드 코드', '황민호', 'ALGORITHM', '2026-06-08 21:04:28', '2026-06-09 08:05:14');

INSERT INTO members (id, name, email, created_at, updated_at) VALUES (1, '홍재호1', 'hongjaeho1@example.com', '2026-06-08 21:04:28', '2026-06-09 08:06:14');
INSERT INTO members (id, name, email, created_at, updated_at) VALUES (2, '홍재호2', 'hongjaeho2@example.com', '2026-06-08 21:04:28', '2026-06-09 08:06:14');
INSERT INTO members (id, name, email, created_at, updated_at) VALUES (3, '홍재호3', 'hongjaeho3@example.com', '2026-06-08 21:04:28', '2026-06-09 08:06:14');

INSERT INTO loans (id, member_id, book_id, loan_date, return_date, created_at, updated_at) VALUES (1, 1, 1, '2026-06-01', '2026-06-15', '2026-06-08 21:04:28', '2026-06-08 21:04:28');
INSERT INTO loans (id, member_id, book_id, loan_date, return_date, created_at, updated_at) VALUES (2, 1, 2, '2026-06-05', null, '2026-06-08 21:04:28', '2026-06-08 21:04:28');
INSERT INTO loans (id, member_id, book_id, loan_date, return_date, created_at, updated_at) VALUES (3, 2, 3, '2026-06-03', '2026-06-10', '2026-06-08 21:04:28', '2026-06-08 21:04:28');
INSERT INTO loans (id, member_id, book_id, loan_date, return_date, created_at, updated_at) VALUES (4, 3, 4, '2026-06-07', null, '2026-06-08 21:04:28', '2026-06-08 21:04:28');

