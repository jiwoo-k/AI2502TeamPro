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
FROM user_tag;
ALTER TABLE user_tag
    AUTO_INCREMENT = 1;


DELETE
FROM user_follow;
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
VALUES ('USER1', '$2a$10$6gVaMy7.lbezp8bGRlV2fOArmA3WAk2EHxSKxncnzs28/m3DXPyA2', '회원1', '0009254000000', 'active');



-- [샘플 게시글]
-- post 테이블 구조 : (user_id, type, title, content)
INSERT INTO post (user_id, type, title, content)
VALUES (1, 1, '첫 번째 게시글', '첫 번째 게시글의 내용입니다.'),
       (2, 1, '두 번째 게시글', '두 번째 게시글의 내용입니다.');

-- ----------------------------------------------------
-- [샘플 댓글 및 답글 데이터]
-- ----------------------------------------------------

/*
  <Post 1에 대한 샘플 댓글 및 답글>
  - 최상위 댓글들은 parent_id 가 0
  - 답글은 parentcommentid에 상위 댓글의 id를 할당하여 계층구조를 형성
  가정: Comment 테이블의 AUTO_INCREMENT가 1부터 시작하면,
    첫 번째 INSERT문에 의해 생성된 댓글의 id = 1, 두 번째 = 2, 등으로 채번됨.
*/

-- Post 1에 최상위 댓글 2개 삽입
INSERT INTO comment (user_id, post_id, parent_id, content)
VALUES (1, 1, null, 'Post1 - 최상위 댓글 1: USER1이 작성'),
       (2, 1, null, 'Post1 - 최상위 댓글 2: ADMIN1이 작성');
-- 위 결과, id 1과 id 2 할당

-- Post 1, 댓글 id=1에 대한 답글 (2개)
INSERT INTO comment (user_id, post_id, parent_id, content)
VALUES (2, 1, 1, 'Post1 - 댓글1에 대한 답글 1: ADMIN1 작성'),
       (1, 1, 1, 'Post1 - 댓글1에 대한 답글 2: USER1 작성');
-- 이 때 새 댓글 id = 3, 4


-- Post 2에 최상위 댓글 3개 삽입
INSERT INTO comment (user_id, post_id, parent_id, content)
VALUES (1, 2, null, 'Post2 - 최상위 댓글 1: USER1이 작성'),
       (2, 2, null, 'Post2 - 최상위 댓글 2: ADMIN1이 작성'),
       (1, 2, null, 'Post2 - 최상위 댓글 3: USER1이 작성');
-- 결과로 id가 차례로 할당되어, id = 5, 6, 7 (가정)

-- Post 2, 최상위 댓글 id=8에 대해 다수의 답글 추가
INSERT INTO comment (user_id, post_id, parent_id, content)
VALUES (2, 2, 8, 'Post2 - 댓글 id=8에 답글 1: ADMIN1 작성'), -- id = 8
       (1, 2, 8, 'Post2 - 댓글 id=8에 답글 2: USER1 작성'),  -- id = 9
       (2, 2, 8, 'Post2 - 댓글 id=8에 답글 3: ADMIN1 작성'), -- id = 10
       (1, 2, 8, 'Post2 - 댓글 id=8에 답글 4: USER1 작성');  -- id = 11


-- 추가로 Post 2에 최상위 댓글 2개 삽입
INSERT INTO comment (user_id, post_id, parent_id, content)
VALUES (2, 2, null, 'Post2 - 최상위 댓글 4: ADMIN1이 작성'), -- id = 12
       (1, 2, null, 'Post2 - 최상위 댓글 5: USER1이 작성');  -- id = 13

-- [샘플 첨부파일 데이터]
-- attachment 테이블 구조: (id, post_id, sourcename, filename)
-- id는 AUTO_INCREMENT 로 자동 생성되므로, post_id, sourcename, filename 만 넣습니다.

INSERT INTO attachment (post_id, sourcename, filename)
VALUES (1, 'face01.png', 'face01.png'),
       (1, 'face02.png', 'face02.png'),
       (2, 'face03.png', 'face03.png'),
       (2, 'face04.png', 'face04.png'),
       (1, 'picture05.jpg', 'picture05_stored.jpg'),
       (2, 'picture06.jpg', 'picture06_stored.jpg');


insert into post (user_id,type, title, content) values
                                                     ( 3,'helper', '안녕하세요','그지같은거')
;

select *
from user;

insert into user_follow (following_userid, followed_userid) value
    (2,1),
    (1,3);

insert into user_follow (following_userid, followed_userid) value
(1,2)

SELECT t.name ,post.id
FROM tag t
         JOIN post_tag pt ON pt.tag_id = t.id join post on pt.post_id = post.id
WHERE pt.post_id =1

delete from post where id = 45

delete from category;

select * from category

insert into category (name, color) values
                                       ('음식', '#FF6347'),
                                       ('운동', '#6B8E23'),
                                       ('빵', '#D2B48C');


insert into tag (category_id, name) values
                                        (1, '아 몰라'),
                                        (2,'귀찮아'),
                                        (3, '눈 아파'),
                                        (2,'그렇구나'),
                                        (1, '어쩌라고');

select * from tag

delete from user_tag;

insert into user_tag (user_id, tag_id) VALUE
(1,1),
    (1,2),
    (1,3),
    (2,1),
    (2,2)
,(3,3)

select * from post;

insert into post_tag (post_id, tag_id)
values (14, 2),
        (15, 1);