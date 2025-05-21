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

-- 파라미터: 보고 싶은 사용자의 아이디 또는 아래 예시처럼 username
SELECT
    u.id       AS userId,
    u.username,
    u.name
FROM user_follow uf
         JOIN user u
              ON uf.followed_userid = u.id
WHERE uf.following_userid = (
    SELECT id
    FROM user
    WHERE username = 'user1'
);

-- 파라미터: 보고 싶은 사용자의 아이디 또는 아래 예시처럼 username
SELECT
    c.id        AS commentId,
    p.id        AS postId,
    p.title     AS postTitle,
    c.content,
    c.createdat
FROM comment c
         JOIN post p
              ON c.post_id = p.id
WHERE c.user_id = (
    SELECT id
    FROM user
    WHERE username = 'user1'
)
ORDER BY c.createdat DESC;



