-- roofy_data.sql

DELETE
FROM attachment;
ALTER TABLE attachment
    AUTO_INCREMENT = 1;

DELETE
FROM comment;
ALTER TABLE comment
    AUTO_INCREMENT = 1;

DELETE
FROM post;
ALTER TABLE post
    AUTO_INCREMENT = 1;

DELETE
FROM authority;
ALTER TABLE authority
    AUTO_INCREMENT = 1;

DELETE
FROM user;
ALTER TABLE user
    AUTO_INCREMENT = 1;



DELETE
FROM category;
ALTER TABLE category
    AUTO_INCREMENT = 1;

DELETE
FROM tag;
ALTER TABLE tag
    AUTO_INCREMENT = 1;

DELETE
FROM post_tag;
ALTER TABLE post_tag
    AUTO_INCREMENT = 1;

DELETE
FROM user_tag;
ALTER TABLE user_tag
    AUTO_INCREMENT = 1;

DELETE
FROM user_authorities;
ALTER TABLE user_authorities
    AUTO_INCREMENT = 1;

DELETE
FROM user_follow;
ALTER TABLE user_follow
    AUTO_INCREMENT = 1;

DELETE
FROM warning;
ALTER TABLE warning
    AUTO_INCREMENT = 1;



-- (user_authorities, user_hate, user_tag 등 복합키 테이블은 AUTO_INCREMENT 없음)

------------------------------------------------------
-- 샘플 데이터 삽입
------------------------------------------------------

-- [샘플 authority]
INSERT INTO authority (name)
VALUES ('ROLE_MEMBER'),
       ('ROLE_ADMIN');

-- [샘플 사용자]
-- user 테이블 구조 : (username, password, name, juminno, latitude, longitude, status)
INSERT INTO user (username, password, name, juminNo, status)
VALUES
       ('ADMIN1', '1234', '관리자1', '9801013000000', 'active');



-- [샘플 게시글]
-- post 테이블 구조 : (user_id, type, title, content)
INSERT INTO post (user_id, type, title, content)
VALUES (1, 1, '첫 번째 게시글', '첫 번째 게시글의 내용입니다.');


-- ----------------------------------------------------
-- [샘플 댓글 및 답글 데이터]
-- ----------------------------------------------------

/*
  <Post 1에 대한 샘플 댓글 및 답글>
  - 최상위 댓글들은 parentcommentid 가 0
  - 답글은 parentcommentid에 상위 댓글의 id를 할당하여 계층구조를 형성
  가정: Comment 테이블의 AUTO_INCREMENT가 1부터 시작하면,
    첫 번째 INSERT문에 의해 생성된 댓글의 id = 1, 두 번째 = 2, 등으로 채번됨.
*/

-- post

-- user








