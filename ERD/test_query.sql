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

insert into user_follow
values(3, 2);

select * from user_follow;

select * from attachment;
select * from user_tag;
select * from user;

select  distinct(u.id), u.latitude, u.longitude
    from user u, post p
where u.id=p.user_id;

select * from post
where user_id in (1, 2);

select p.id, p.user_id, p.type, p.title, p.content, p.createdat, p.isdeleted, p.deletedat
    from post p, user u
where p.user_id=u.id and u.id in (1, 2) and p.type='guest';

select distinct u.id "id",
                u.username "username",
                u.password "password",
                u.name "name",
                u.createdAt "createdAt",
                u.provider "provider",
                u.providerId "providerId",
                u.latitude "latitude",
                u.longitude "longitude",
                u.status "status"
from user u, post p
where u.id=p.user_id;

update user
set latitude=37.27760888368095, longitude=127.05015732955697
    where id=3;

select w.complaint_user_id "complaint_user_id",
       u.username "u_username",
       u.name "u_name",
       p.id "p_id",
       p.title "p_title",
       w.reason "w_reason",
       w.warndate "w_warnDate"
from user u, warning w, post p
where w.post_id = p.id and u.id = p.user_id and p.user_id = 8;

select
    w.complaint_user_id "complaint_user_id",         -- 신고한 사용자 ID
    complainer.username "complainer_username",       -- 신고한 사용자 이름
    complainer.name "complainer_name",               -- 신고한 사용자 닉네임
    reported_user.username "reported_user_username", -- 신고당한 사용자 이름 (원래 u.username)
    reported_user.name "reported_user_name",         -- 신고당한 사용자 닉네임 (원래 u.name)
    p.id "p_id",
    p.title "p_title",
    w.reason "w_reason",
    w.warndate "w_warnDate"
from
    warning w
        join post p on w.post_id = p.id
        join user reported_user on p.user_id = reported_user.id  -- 신고당한 사용자 (게시글 작성자)
        join user complainer on w.complaint_user_id = complainer.id -- 신고한 사용자
where
    reported_user.id = 1;

select u.id "id",
       u.username "username",
       u.status "status",
       u.pause_end_date "pauseEndDate",
       count(*) "warningCount"
from user u, warning w, post p
where w.post_id = p.id and u.id = p.user_id
group by u.id, u.username, u.status
having count(*) between 10 and 14;

select * from user;

select t.category_id, count(*) postsCount
    from post_tag p, tag t
where p.tag_id=t.id
group by t.category_id
order by t.category_id desc ;

SELECT
    c.id "c_id",
    c.name "c_name",
    c.color "c_color",
    COUNT(t.id) "tag_count",
    COUNT(pt.post_id)
FROM
    category c
        LEFT JOIN tag t ON c.id = t.category_id
        LEFT JOIN post_tag pt ON pt.tag_id = t.id
GROUP BY
    c.id, c.name, c.color
ORDER BY tag_count desc;

select * from loginhistory;

SELECT DISTINCT
    CAST(loginat AS DATE) AS login_date,
    count(*)
FROM
    loginhistory
WHERE
    CAST(loginat AS DATE) BETWEEN CAST('2025-05-01 18:00:00' as  date ) AND CAST('2025-05-30 18:00:00' as date )
group by login_date
ORDER BY
    login_date;

SELECT DISTINCT
    CAST(loginat AS DATE) AS login_date, user_id
FROM
    loginhistory
WHERE
    CAST(loginat AS DATE) BETWEEN CAST('2025-05-01 18:00:00' as  date ) AND CAST('2025-05-30 18:00:00' as date )
group by loginat
ORDER BY
    login_date;

select distinct cast(loginat AS date) as login_date, user_id
    from loginhistory;

select l.login_date "loginAt", count(*) "userCount"
    from (select distinct cast(loginat AS date) as login_date, user_id
          from loginhistory) l
where l.login_date between  cast('2025-05-01 18:00:00' as date ) and cast('2025-05-30 18:00:00' as date )
group by loginAt
order by loginAt;
