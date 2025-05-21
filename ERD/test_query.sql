select t.id, t.category_id,t.name
    from post_tag pt, tag t
where pt.post_id = 1 and pt.tag_id = t.id;

select t.id, t.category_id,t.name
from user_tag ut, tag t
where ut.user_id = 1 and ut.tag_id = t.id;

-- 사용자가 담은 태그와 일치하는 게시글 목록을 들고오려면..
select p.id, p.user_id, p.type, p.title, p.content, p.createdat, p.isdeleted, p.deletedat
from post p, post_tag pt
where p.id=pt.post_id and pt.tag_id in (1, 2, 3, 4);

select c.id, c.name, c.color, count(*)
from category c, tag t
where c.id = t.category_id
group by category_id
order by count(*) desc;

select * from attachment;

select c.id "c_id",
       c.name "c_name",
       c.color "c_color",
       count(*) "tagCount"
from category c, tag t
where c.id = t.category_id
group by category_id
order by tagCount desc;

select * from user_tag where user_id=2;
select * from post_tag where tag_id=1;
select * from post;
select * from post_tag;

select * from user;

select * from attachment;
