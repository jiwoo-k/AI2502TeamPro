DROP TABLE IF EXISTS warning;
DROP TABLE IF EXISTS user_authorities;
DROP TABLE IF EXISTS post_tag;
DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS attachment;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS user_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS loginhistory;


CREATE TABLE attachment

(
  id         INT          NOT NULL AUTO_INCREMENT,
  post_id    INT          NOT NULL,
  sourcename VARCHAR(100) NOT NULL,
  filename   VARCHAR(100) NOT NULL,
  PRIMARY KEY (id)
) COMMENT '사진첨부';

CREATE TABLE authority
(
  id   INT         NOT NULL AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY (id)
) COMMENT '권한';

CREATE TABLE category
(
  id    INT         NOT NULL AUTO_INCREMENT,
  name  VARCHAR(50) NOT NULL,
  color VARCHAR(10) NULL     DEFAULT '#808080' COMMENT 'RGB HEX',
  PRIMARY KEY (id)
) COMMENT '대분류 (카테고리)';

CREATE TABLE comment
(
  id        INT      NOT NULL AUTO_INCREMENT,
  user_id   INT      NOT NULL,
  post_id   INT      NOT NULL,
  parent_id INT      NULL    ,
  content   TEXT     NULL    ,
  createdat DATETIME NULL     DEFAULT now(),
  ispicked  BOOLEAN  NOT NULL DEFAULT false COMMENT '픽여부',
  PRIMARY KEY (id)
) COMMENT '댓글';

CREATE TABLE loginhistory
(
  user_id INT      NOT NULL,
  loginat DATETIME NOT NULL     DEFAULT now(),
  PRIMARY KEY (user_id, loginat)
) COMMENT '로그인 이력';

CREATE TABLE post
(
  id        INT          NOT NULL AUTO_INCREMENT,
  user_id   INT          NOT NULL,
  type      ENUM('guest', 'helper')    NOT NULL DEFAULT 'guest' COMMENT '게시판 유형',
  title     VARCHAR(100) NOT NULL,
  content   LONGTEXT     NULL    ,
  createdat DATETIME     NULL     DEFAULT now(),
  isdeleted BOOLEAN      NOT NULL DEFAULT false,
  deletedat DATETIME     NULL     DEFAULT NULL,
  PRIMARY KEY (id)
) COMMENT '게시글';

CREATE TABLE post_tag
(
  post_id INT NOT NULL,
  tag_id  INT NOT NULL,
  PRIMARY KEY (post_id, tag_id)
) COMMENT '게시물에 있는 태그';

CREATE TABLE tag
(
  id          INT         NOT NULL AUTO_INCREMENT,
  category_id INT         NOT NULL,
  name        VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
) COMMENT '소분류 (태그)';

ALTER TABLE tag
  ADD CONSTRAINT UQ_category_id UNIQUE (category_id, name);

CREATE TABLE user
(
  id         INT          NOT NULL AUTO_INCREMENT,
  username   VARCHAR(100) NOT NULL COMMENT '사용자아이디',
  name       VARCHAR(80)  NOT NULL COMMENT '닉네임',
  password   VARCHAR(100) NOT NULL,
  juminNo    VARCHAR(13)  NOT NULL,
  createdAt  DATETIME     NULL     DEFAULT now(),
  provider   VARCHAR(40)  NULL    ,
  providerId VARCHAR(200) NULL    ,
  latitude   DOUBLE       NULL    ,
  longitude  DOUBLE       NULL    ,
  status     ENUM('active', 'paused', 'banned')         NOT NULL DEFAULT 'active' COMMENT '계정상태(active, paused, banned)',
  PRIMARY KEY (id)
) COMMENT '사용자';

ALTER TABLE user
  ADD CONSTRAINT UQ_username UNIQUE (username);

ALTER TABLE user
  ADD CONSTRAINT UQ_name UNIQUE (name);

ALTER TABLE user
  ADD CONSTRAINT UQ_juminNo UNIQUE (juminNo);

CREATE TABLE user_authorities
(
  user_id      INT NOT NULL,
  authority_id INT NOT NULL,
  PRIMARY KEY (user_id, authority_id)
) COMMENT '사용자별 권한리스트';

CREATE TABLE user_follow
(
  following_userid INT NOT NULL,
  followed_userid  INT NOT NULL,
  PRIMARY KEY (following_userid, followed_userid)
) COMMENT '팔로우관계 ';

CREATE TABLE user_tag
(
  user_id INT NOT NULL,
  tag_id  INT NOT NULL,
  PRIMARY KEY (user_id, tag_id)
) COMMENT '사용자 태그';

CREATE TABLE warning
(
  post_id           INT      NOT NULL COMMENT '신고게시물',
  complaint_user_id INT      NOT NULL COMMENT '신고한사용자',
  warndate          DATETIME NULL     DEFAULT now(),
  reason            LONGTEXT NOT NULL COMMENT '신고사유',
  PRIMARY KEY (post_id, complaint_user_id)
) COMMENT '신고정보';

ALTER TABLE user_authorities
  ADD CONSTRAINT FK_user_TO_user_authorities
    FOREIGN KEY (user_id)
    REFERENCES user (id);

ALTER TABLE user_authorities
  ADD CONSTRAINT FK_authority_TO_user_authorities
    FOREIGN KEY (authority_id)
    REFERENCES authority (id);

ALTER TABLE post
  ADD CONSTRAINT FK_user_TO_post
    FOREIGN KEY (user_id)
    REFERENCES user (id);

ALTER TABLE tag
  ADD CONSTRAINT FK_category_TO_tag
    FOREIGN KEY (category_id)
    REFERENCES category (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE post_tag
  ADD CONSTRAINT FK_post_TO_post_tag
    FOREIGN KEY (post_id)
    REFERENCES post (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE;

ALTER TABLE post_tag
  ADD CONSTRAINT FK_tag_TO_post_tag
    FOREIGN KEY (tag_id)
    REFERENCES tag (id)
;

ALTER TABLE comment
  ADD CONSTRAINT FK_user_TO_comment
    FOREIGN KEY (user_id)
    REFERENCES user (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE;

ALTER TABLE comment
  ADD CONSTRAINT FK_post_TO_comment
    FOREIGN KEY (post_id)
    REFERENCES post (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE user_tag
  ADD CONSTRAINT FK_user_TO_user_tag
    FOREIGN KEY (user_id)
    REFERENCES user (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE user_tag
  ADD CONSTRAINT FK_tag_TO_user_tag
    FOREIGN KEY (tag_id)
    REFERENCES tag (id)
    ON DELETE CASCADE
;

ALTER TABLE attachment
  ADD CONSTRAINT FK_post_TO_attachment
    FOREIGN KEY (post_id)
    REFERENCES post (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE comment
  ADD CONSTRAINT FK_comment_TO_comment
    FOREIGN KEY (parent_id)
    REFERENCES comment (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE user_follow
  ADD CONSTRAINT FK_user_TO_user_follow
    FOREIGN KEY (following_userid)
    REFERENCES user (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE user_follow
  ADD CONSTRAINT FK_user_TO_user_follow1
    FOREIGN KEY (followed_userid)
    REFERENCES user (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE loginhistory
  ADD CONSTRAINT FK_user_TO_loginhistory
    FOREIGN KEY (user_id)
    REFERENCES user (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE;

ALTER TABLE warning
  ADD CONSTRAINT FK_post_TO_warning
    FOREIGN KEY (post_id)
    REFERENCES post (id);

ALTER TABLE warning
  ADD CONSTRAINT FK_user_TO_warning
    FOREIGN KEY (complaint_user_id)
    REFERENCES user (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE `user`
    DROP COLUMN `juminNo`;




# insert into user_follow (
#     following_userid ,
#     followed_userid
# )  values
#     (2,2)
