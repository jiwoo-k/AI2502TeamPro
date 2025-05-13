insert into user(username, name, password, juminNo)
values('user1', '회원1', '1234', '0009254222222'),
      ('user2', '회원2', '1234', '9201021333333'),
      ('user3', '회원3', '1234', '0402023444444');

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


select * from post;
select * from user;
select * from post_tag;
select * from tag;
select * from category;
select * from user_tag;