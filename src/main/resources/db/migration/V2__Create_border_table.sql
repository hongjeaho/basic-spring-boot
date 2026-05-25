CREATE TABLE IF NOT EXISTS board (
                                     id          BIGINT          AUTO_INCREMENT  PRIMARY KEY  COMMENT '게시글 ID',
                                     title       VARCHAR(200)    NOT NULL                     COMMENT '제목',
    content     TEXT            NOT NULL                     COMMENT '내용',
    author      VARCHAR(50)     NOT NULL                     COMMENT '작성자',
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '작성일시',
    updated_at  DATETIME        DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP  COMMENT '수정일시'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
    COMMENT='게시판';

-- 테스트 데이터 삽입
INSERT INTO board (title, content, author) VALUES
('Spring Boot 교육 시작!', '오늘부터 Spring Boot 교육을 시작합니다.\ Servlet/JSP 로 게시판을 만들어보겠습니다.', '홍재호'),
('Servlet 이란?', 'Servlet은 Java로 작성된 서버 측 프로그램입니다.\nHTTP 요청을 처리하고 응답을 생성합니다.', '홍재호'),
('MVC 패턴이란?', 'Model-View-Controller 패턴입니다.\n- Model: 데이터(DTO, DAO)\n- View: JSP\n- Controller: Servlet', '홍재호');