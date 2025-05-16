insert into user(username, name, password)
values('USER1', '회원1', '$2a$10$6gVaMy7.lbezp8bGRlV2fOArmA3WAk2EHxSKxncnzs28/m3DXPyA2'),
      ('USER2', '회원2', '$2a$10$6gVaMy7.lbezp8bGRlV2fOArmA3WAk2EHxSKxncnzs28/m3DXPyA2'),
      ('USER3', '회원3', '$2a$10$6gVaMy7.lbezp8bGRlV2fOArmA3WAk2EHxSKxncnzs28/m3DXPyA2'),
      ('ADMIN', '관리자1', '$2a$10$6gVaMy7.lbezp8bGRlV2fOArmA3WAk2EHxSKxncnzs28/m3DXPyA2')
;



insert into category(name)
values ('sport'), ('travel'), ('food');


insert into tag(category_id, name)
VALUES (3, '같이밥먹어요'),
       (3, '햄버거'),
       (2, '여행갈사람'),
       (1, '헬스장');

insert into post(user_id, type, title, content)
values(1, 'guest', 'title1', 'content1'),
      (2, 'helper', 'title2', 'content2'),
      (2, 'guest', 'title3', 'content3')
;


insert into post_tag
values(1, 1),
      (1, 2),
      (1, 3),
      (2, 2),
      (2, 3),
      (2, 4),
      (3, 1),
      (3, 2)
      ;


insert into user_tag
values(1, 1),
      (1, 2),
      (1, 3),
      (2, 4),
      (2, 1)
;

INSERT INTO authority (name) VALUES
('ROLE_MEMBER'), ('ROLE_ADMIN');

-- 샘플 사용자
INSERT INTO user_authorities
VALUES (1, 1),
       (2, 1),
       (4, 1),
       (4, 2)
;

INSERT INTO attachment (post_id, sourcename, filename)
VALUES (1, 'face01.png', 'face01.png'),
       (1, 'face02.png', 'face02.png'),
       (2, 'face03.png', 'face03.png'),
       (2, 'face04.png', 'face04.png'),
       (1, 'picture05.jpg', 'picture05_stored.jpg'),
       (2, 'picture06.jpg', 'picture06_stored.jpg');


select * from post;
select * from user order by id desc;
select * from post_tag;
select * from tag;
select * from category;
select * from user_tag;

delete from user where id=6;

update user
set latitude=null, longitude=null
where id=5;

