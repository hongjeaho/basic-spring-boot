-- CDATA 테스트를 위한 특수 문자 포함 도서 데이터
-- XML 특수 문자: <, >, &, '

INSERT INTO books (title, author, category, created_at, updated_at) VALUES
('C++ 완벽 가이드', 'Bjarne Stroustrup', 'PROGRAMMING', NOW(), NOW()),
('Effective Java & Design Patterns', 'Scott Meyers', 'PROGRAMMING', NOW(), NOW()),
('Tom''s Hardware Guide', 'Tom Buchanan', 'COMPUTER', NOW(), NOW()),
('<HTML5> 웹 개발', 'John Doe', 'WEB', NOW(), NOW()),
('AT&T 네트워크 설계', 'Network Architects', 'NETWORK', NOW(), NOW()),
('O''Reilly 프로그래밍 필독', 'Various Authors', 'PROGRAMMING', NOW(), NOW());
