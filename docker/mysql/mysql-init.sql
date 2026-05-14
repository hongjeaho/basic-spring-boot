CREATE DATABASE IF NOT EXISTS sample;

SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES';

-- MySQL 8.0 호환성: root 사용자에게 모든 호스트에서의 접속 권한 부여
-- Docker 컨테이너에서 호스트(Mac)의 연결을 허용하기 위함
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;