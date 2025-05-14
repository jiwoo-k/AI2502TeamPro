insert into user(username, name, password, juminNo)
values('USER1', '회원1', '1234', '0009254222222'),
      ('USER2', '회원2', '1234', '9201021333333'),
      ('USER3', '회원3', '1234', '0402023444444');


insert into user(username, name, password, juminNo)
values('USER4', '회원4', '1234', '0101013333333'),
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
values ('sport'), ('travel'), ('food');


insert into tag(category_id, name)
VALUES (3, '같이밥먹어요'),
       (3, '햄버거'),
       (2, '여행갈사람'),
       (1, '헬스장');

insert into post(user_id, type, title, content)
values(1, 'guest', 'title1', 'content1'),
      (1, 'helper', 'title2', 'content2'),
      (3, 'guest', 'title3', 'content3')
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
      (1, 4),
      (2, 2),
      (2, 3),
      (2, 4),
      (3, 2)
;

INSERT INTO authority (name) VALUES
('ROLE_MEMBER'), ('ROLE_ADMIN');

-- 샘플 사용자-권한
INSERT INTO user_authorities
VALUES (1, 1),
       (3, 1),
       (3, 2)
;

select * from post;
select * from user;
select * from post_tag;
select * from tag;
select * from category;
select * from user_tag;