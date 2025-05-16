insert into user(username, name, password, juminNo)
values ('USER1', '회원1', '$2a$10$6gVaMy7.lbezp8bGRlV2fOArmA3WAk2EHxSKxncnzs28/m3DXPyA2', '0009254222222')

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
values (1, 'guest', 'title1', 'content1'),
       (1, 'helper', 'title2', 'content2'),
       (3, 'guest', 'title3', 'content3')
insert into post (user_id, type, title, content)
values(1, 'guest', 'title1', 'content1')
;



insert into post_tag
values (1, 1),
       (1, 2),
       (1, 3),
       (2, 2),
       (2, 3),
       (2, 4),
       (3, 1),
       (3, 2)
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
       (3, 1),
       (3, 2)
;

INSERT INTO attachment (post_id, sourcename, filename)
VALUES (1, 'face01.png', 'face01.png'),
       (1, 'face02.png', 'face02.png'),
       (2, 'face03.png', 'face03.png'),
       (2, 'face04.png', 'face04.png'),
       (1, 'picture05.jpg', 'picture05_stored.jpg'),
       (2, 'picture06.jpg', 'picture06_stored.jpg');


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
from user_tag;
select * from post;
select * from user;
select * from post_tag;
select * from tag;
select * from category;
select * from warning


ALTER TABLE post MODIFY type VARCHAR(10);


SELECT
    t.id AS id,
    t.category_id AS category_id,
    t.name AS name,
    c.color AS color
FROM tag t
         JOIN category c ON t.category_id = c.id
         JOIN user_tag ut ON ut.tag_id = t.id
         JOIN post p ON ut.user_id = p.user_id
WHERE p.id = 1;


insert into warning (post_id, complaint_user_id, reason)values
                                                (2,2, '그냥 신고하고 싶음');


select *  from warning