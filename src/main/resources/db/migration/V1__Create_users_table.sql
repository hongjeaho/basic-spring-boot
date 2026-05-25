create table users
(
    seq           BIGINT                             NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 일련번호',
    user_email    VARCHAR(100)                                COMMENT '사용자 이메일',
    user_name     VARCHAR(30)                        NOT NULL COMMENT '사용자명',
    user_id       VARCHAR(30)                        NOT NULL COMMENT '사용자 아이디',
    user_password VARCHAR(100)                       NOT NULL COMMENT '비밀번호',
    created_by    BIGINT                             NOT NULL COMMENT '생성자',
    created_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',
    updated_by    BIGINT COMMENT '수정자',
    updated_time  DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',

    UNIQUE KEY UK_USERS_USER_ID (user_id),
    UNIQUE KEY UK_USERS_EMAIL (user_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='사용자 테이블';

create table roles
(
    seq            BIGINT                             NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'Role 일련번호',
    user_role_name VARCHAR(100)                       NOT NULL COMMENT 'Role명',

    UNIQUE KEY UK_USER_ROLES_NAME (user_role_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='사용자 권한 테이블';

create table user_roles
(
    user_seq     BIGINT                             NOT NULL COMMENT '사용자 일련번호',
    role_seq     BIGINT                             NOT NULL COMMENT '사용자 일련번호',
    created_by   BIGINT                             NOT NULL COMMENT '생성자',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '생성일',

    PRIMARY KEY (user_seq, role_seq),

    constraint FK_USER_ROLES_USER
        FOREIGN KEY (user_seq)
            REFERENCES users(seq)
            ON DELETE CASCADE,

    CONSTRAINT FK_USER_ROLES_ROLE
        FOREIGN KEY (role_seq)
            REFERENCES roles(seq)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='사용자 권한 매핑 테이블';

-- passwordis 12345
insert into users (user_email, user_name, user_id, user_password, created_by, updated_by)
values ('admin@gmail.com', '어드민', 'admin',
        '$2a$10$zUBEUh5T1ehLA98ly2n2NeiEevUhyK1axz.2evM.5uyP8cvTWKNay', 1, 1);

-- 사업 시행자
insert into roles (user_role_name)
values ('USER');
-- 관리자
insert into roles (user_role_name)
values ('ADMIN');

insert into user_roles (user_seq, role_seq, created_by)
values (1, 2, 1);