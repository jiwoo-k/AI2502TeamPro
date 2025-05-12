SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE IF EXISTS attachment;
DROP TABLE IF EXISTS user_authorities;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS user;

CREATE TABLE attachment
(
  id         INT          NOT NULL AUTO_INCREMENT,
  post_id    INT          NOT NULL,
  sourcename VARCHAR(100) NOT NULL COMMENT '원본파일명',
  filename   VARCHAR(100) NOT NULL COMMENT '저장된파일명',
  PRIMARY KEY (id)
) COMMENT '첨부파일';

CREATE TABLE authority
(
  id   INT         NOT NULL AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL,
  PRIMARY KEY (id)
) COMMENT '권한';

ALTER TABLE authority
  ADD CONSTRAINT UQ_name UNIQUE (name);

CREATE TABLE comment
(
  id      INT      NOT NULL AUTO_INCREMENT,
  user_id INT      NOT NULL,
  post_id INT      NOT NULL,
  content TEXT     NOT NULL,
  regdate DATETIME NULL     DEFAULT now(),
  PRIMARY KEY (id)
) COMMENT '댓글';

CREATE TABLE post
(
  id      INT          NOT NULL AUTO_INCREMENT,
  user_id INT          NOT NULL,
  subject VARCHAR(200) NOT NULL,
  content LONGTEXT     NULL    ,
  viewcnt INT          NULL     DEFAULT 0,
  regdate DATETIME     NULL     DEFAULT now(),
  PRIMARY KEY (id)
) COMMENT '게시글';

CREATE TABLE user
(
  id       INT          NOT NULL AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  name     VARCHAR(80)  NOT NULL,
  email    VARCHAR(80)  NULL    ,
  regdate  DATETIME     NULL     DEFAULT now(),
  PRIMARY KEY (id)
) COMMENT '회원';

ALTER TABLE user
  ADD CONSTRAINT UQ_username UNIQUE (username);

CREATE TABLE user_authorities
(
  user_id      INT NOT NULL,
  authority_id INT NOT NULL,
  PRIMARY KEY (user_id, authority_id)
);

ALTER TABLE post
  ADD CONSTRAINT FK_t5_user_TO_t5_post
    FOREIGN KEY (user_id)
    REFERENCES user (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;


ALTER TABLE comment
  ADD CONSTRAINT FK_t5_user_TO_t5_comment
    FOREIGN KEY (user_id)
    REFERENCES user (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE comment
  ADD CONSTRAINT FK_t5_post_TO_t5_comment
    FOREIGN KEY (post_id)
    REFERENCES post (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE attachment
  ADD CONSTRAINT FK_t5_post_TO_t5_attachment
    FOREIGN KEY (post_id)
    REFERENCES post (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE user_authorities
  ADD CONSTRAINT FK_t5_user_TO_t5_user_authorities
    FOREIGN KEY (user_id)
    REFERENCES user (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;

ALTER TABLE user_authorities
  ADD CONSTRAINT FK_t5_authority_TO_t5_user_authorities
    FOREIGN KEY (authority_id)
    REFERENCES authority (id)
    ON UPDATE RESTRICT
    ON DELETE CASCADE
;
