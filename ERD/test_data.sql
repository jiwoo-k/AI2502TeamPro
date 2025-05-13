insert into user(username, name, password, juminNo)
values('user2', '회원2', '1234', '9201013333333');

insert into category(name)
values ('sport'), ('travel'), ('food');

select * from category;

insert into tag(category_id, name)
VALUES (3, '같이밥먹어요'),
       (3, '햄버거'),
       (2, '여행갈사람'),
       (1, '헬스장');

insert into post(user_id, type, title)
values(1, '손님', 'test1'),
      (2, '손님', 'test2')
;


insert into post_tag
values(1, 1),
      (1,2),
      (1,3),
      (2, 2),
      (2,3),
      (2,4)
      ;

insert into user_tag
values(1, 1),
      (1,2),
      (1, 3),
      (1,4),
      (2,2),
      (2, 3),
      (2,4)
;


select * from post;
select * from user;
select * from post_tag;
select * from tag;
select * from category;
select * from user_tag;