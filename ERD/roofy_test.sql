-- roofy_test.sql

-- 1. 테이블 목록 확인
SHOW TABLES;

SELECT TABLE_NAME
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'db2502'
  AND TABLE_NAME IN (
                     'attachment',
                     'authority',
                     'category',
                     'comment',
                     'follower',
                     'pick',
                     'post',
                     'post_tag',
                     'posttype',
                     'tag',
                     'user',
                     'user_authorities',
                     'user_hate',
                     'user_tag'
    )
;

-- 2. 각 테이블의 데이터 확인
SELECT *
FROM authority;

SELECT *
FROM user
ORDER BY id DESC;

SELECT *
FROM user_authorities;
SELECT *
FROM post
ORDER BY id DESC;
SELECT *
FROM comment
ORDER BY id DESC;
SELECT *
FROM attachment
ORDER BY id DESC;

-- 3. 특정 id의 사용자 조회
SELECT id       "id",
       username "username",
       password "password",
       juminno  "juminno",
       name     "name",
       regdate  "regdate"
FROM user
WHERE 1 = 1
  AND id = 1
;

-- 4. 특정 name의 authority 조회
SELECT id   "id",
       name "name"
FROM authority
WHERE name = 'ROLE_ADMIN'
;

-- 5. 특정 사용자의 authority 조회
-- (여기서는 예시로 user_id가 2인 ADMIN1의 권한을 조회)
SELECT a.id   "id",
       a.name "name"
FROM authority a,
     user_authorities ua
WHERE a.id = ua.authority_id
  AND ua.user_id = 2
;

-- 6. 특정 글(post)과 작성자(user) 정보 조회
-- (post 테이블은 title , content, regdate, isfinish 컬럼을 사용합니다.)
SELECT p.id       "p_id",
       p.title    "p_title",
       p.content  "p_content",
       p.regdate  "p_regdate",
       p.isfinish "p_isfinish",
       u.id       "u_id",
       u.username "u_username",
       u.password "u_password",
       u.name     "u_name",
       u.juminno  "u_juminno",
       u.regdate  "u_regdate"
FROM post p,
     user u
WHERE p.user_id = u.id
  AND p.id = 1
;

-- 7. 페이징 테스트용 다량의 데이터
-- 먼저 기존 post 데이터를 한 건 복제하여 데이터량 증가(실제 운영 환경에서는 주의)
INSERT INTO post(user_id, posttype_id, title, content)
SELECT user_id, posttype_id, title, content
FROM post;

SELECT count(*)
FROM post;

SELECT *
FROM post
ORDER BY id DESC
LIMIT 5
;

SELECT *
FROM post
ORDER BY id DESC
LIMIT 5, 5
;

SELECT *
FROM post
ORDER BY id DESC
LIMIT 10, 5
;

-- 8. 특정 글에 첨부파일 INSERT
INSERT INTO attachment(sourcename, filename, post_id)
VALUES ('face01.png', 'face01.png', 5),
       ('face02.png', 'face02.png', 5),
       ('face03.png', 'face03.png', 5);

SELECT *
FROM attachment
WHERE id IN (5, 6, 7)
;

-- 9. 댓글 및 사용자 정보 (특정 글의 댓글)
SELECT c.id       "id",
       c.content  "content",
       c.regdate  "regdate",
       u.id       "user_id",
       u.username "user_username",
       u.password "user_password",
       u.name     "user_name",
       u.juminno  "user_juminno",
       u.regdate  "user_regdate"
FROM comment c,
     user u
WHERE c.user_id = u.id
  AND c.post_id = 1
ORDER BY c.id DESC
;

SELECT
    a.id,
    a.title,
    a.regdate,
    b.name
FROM post a, user b
order by id desc

