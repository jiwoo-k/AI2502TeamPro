# ALTER TABLE `user`
#     DROP COLUMN `juminNo`;

insert into user(username, name, password, juminNo)
values ('USER1', '회원1', '$2a$10$6gVaMy7.lbezp8bGRlV2fOArmA3WAk2EHxSKxncnzs28/m3DXPyA2', '0009254222222')
;

insert into user (username, name, password, juminNo)
values ('USER2', '회원2', '1234', '9201021333333'),
       ('USER3', '회원3', '1234', '0402023444444');



insert into user(username, name, password, juminNo)
values ('USER4', '회원4', '1234', '0101013333333'),
       ('USER5', '회원5', '1234', '0202024444444'),
       ('USER6', '회원6', '1234', '0303034444444'),
       ('USER7', '회원7', '1234', '9801011111111'),
       ('USER8', '회원8', '1234', '9802032222222'),
       ('USER9', '회원9', '1234', '9705052222222'),
       ('USER10', '회원10', '1234', '9705053333333'),
       ('USER11', '회원11', '1234', '0309013333333'),
       ('USER12', '회원12', '1234', '0601024444444'),
       ('USER13', '회원13', '1234', '0803034444444'),
       ('USER14', '회원14', '1234', '0912123333333'),
       ('USER15', '회원15', '1234', '1005034444444');

insert into category(name)
values ('sport'),
       ('travel'),
       ('food');


insert into tag(category_id, name)
VALUES (3, '같이밥먹어요'),
       (3, '햄버거'),
       (2, '여행갈사람'),
       (1, '헬스장');

insert into post(user_id, type, title, content)
values (1, 'guest', 'title1', 'content1')
     , (2, 'helper', 'title2', 'content2')
     , (3, 'guest', 'title3', 'content3')
     , (4, 'helper', 'title4', 'content4')
     , (1, 'guest', 'title5', 'content5')
     , (2, 'helper', 'title6', 'content6')
     , (3, 'guest', 'title7', 'content7')
     , (4, 'helper', 'title8', 'content8')
     , (1, 'guest', 'title9', 'content9')
     , (2, 'helper', 'title10', 'content10')
     , (3, 'guest', 'title11', 'content11')
     , (4, 'helper', 'title12', 'content12')
     , (1, 'guest', 'title13', 'content13')
     , (2, 'helper', 'title14', 'content14')
     , (3, 'guest', 'title15', 'content15')
     , (4, 'helper', 'title16', 'content16')
     , (1, 'guest', 'title17', 'content17')
     , (2, 'helper', 'title18', 'content18')
     , (3, 'guest', 'title19', 'content19')
     , (4, 'helper', 'title20', 'content20')
     , (1, 'guest', 'title21', 'content21')
     , (2, 'helper', 'title22', 'content22')
     , (3, 'guest', 'title23', 'content23')
     , (4, 'helper', 'title24', 'content24')
     , (1, 'guest', 'title25', 'content25')
     , (2, 'helper', 'title26', 'content26')
     , (3, 'guest', 'title27', 'content27')
     , (4, 'helper', 'title28', 'content28')
     , (1, 'guest', 'title29', 'content29')
     , (2, 'helper', 'title30', 'content30')
     , (3, 'guest', 'title31', 'content31')
     , (4, 'helper', 'title32', 'content32')
     , (1, 'guest', 'title33', 'content33')
     , (2, 'helper', 'title34', 'content34')
     , (3, 'guest', 'title35', 'content35')
     , (4, 'helper', 'title36', 'content36')
     , (1, 'guest', 'title37', 'content37')
     , (2, 'helper', 'title38', 'content38')
     , (3, 'guest', 'title39', 'content39')
     , (4, 'helper', 'title40', 'content40')
;


insert into user_tag
values (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (2, 2),
       (2, 3),
       (2, 4),
       (3, 2)
;

INSERT INTO authority (name)
VALUES ('ROLE_MEMBER'),
       ('ROLE_ADMIN');

-- 샘플 사용자-권한
INSERT INTO user_authorities
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (3, 2)
;

INSERT INTO attachment (post_id, sourcename, filename)
VALUES (1, 'face01.png', 'face01.png')
     , (2, 'face02.png', 'face02.png')
     , (3, 'face03.png', 'face03.png')
     , (4, 'face04.png', 'face04.png')
;



select *
from post;
select *
from tag;
select *
from user;
select *
from post_tag;
select *
from tag;
select *
from category;
select *
from user_tag;
select *
from post;
select *
from user;
select *
from post_tag;
select *
from tag;
select *
from category;
select *
from warning


ALTER TABLE post
    MODIFY type VARCHAR(10);


SELECT t.id          AS id,
       t.category_id AS category_id,
       t.name        AS name,
       c.color       AS color
FROM tag t
         JOIN category c ON t.category_id = c.id
         JOIN user_tag ut ON ut.tag_id = t.id
         JOIN post p ON ut.user_id = p.user_id
WHERE p.id = 1;


insert into warning (post_id, complaint_user_id, reason)
values (2, 2, '그냥 신고하고 싶음');



INSERT INTO user_follow (following_userid, followed_userid)
VALUES ((SELECT id FROM user WHERE username = 'user1'),
        (SELECT id FROM user WHERE username = 'user4'));

INSERT INTO comment (user_id, post_id, content)
VALUES (
           -- user 테이블에서 username='user1'인 행 중 하나만 가져오기
               (SELECT id FROM user WHERE username = 'user1' LIMIT 1),

           -- post 테이블에서 title='title1'인 행 중 하나만 가져오기
               (SELECT id FROM post WHERE title = 'title1' LIMIT 1),
               '테스트 댓글입니다.');

INSERT INTO comment (user_id, post_id, content)
VALUES (
           -- user 테이블에서 username='user1'인 행 중 하나만 가져오기
               (SELECT id FROM user WHERE username = 'user1' LIMIT 1),

           -- post 테이블에서 title='title1'인 행 중 하나만 가져오기
               (SELECT id FROM post WHERE title = 'title1' LIMIT 1),
               '테스트 댓글입니다.');

