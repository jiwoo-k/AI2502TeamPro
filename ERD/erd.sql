
CREATE TABLE attachment
(
  id         INT          NOT NULL,
  post_id    INT          NOT NULL,
  sourcename VARCHAR(100) NOT NULL,
  filename   VARCHAR(100) NOT NULL,
  PRIMARY KEY (id)
) COMMENT '사진첨부';

CREATE TABLE authority
(
  id   INT         NOT NULL AUTO_INCREMENT,
  name VARCHAR(40) NULL    ,
  PRIMARY KEY (id)
) COMMENT '권한';

CREATE TABLE category
(
  id   INT         NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NULL    ,
  PRIMARY KEY (id)
) COMMENT '카테고리';

CREATE TABLE comment
(
  id              INT      NOT NULL AUTO_INCREMENT,
  user_id         INT      NOT NULL,
  post_id         INT      NOT NULL,
  parentcommentid INT      NOT NULL,
  content         TEXT     NULL    ,
  regdate         DATETIME NULL     DEFAULT now(),
  PRIMARY KEY (id)
) COMMENT '댓글';

CREATE TABLE follower
(
  following_userid INT NOT NULL,
  followed_userid  INT NOT NULL,
  PRIMARY KEY (following_userid, followed_userid)
) COMMENT '사용자팔로워수';

CREATE TABLE pick
(
  post_id    INT NOT NULL,
  comment_id INT NULL    ,
  PRIMARY KEY (post_id)
) COMMENT '선택된댓글';

CREATE TABLE post
(
  id          INT          NOT NULL AUTO_INCREMENT,
  user_id     INT          NOT NULL,
  posttype_id INT          NOT NULL,
  title       VARCHAR(100) NOT NULL,
  content     LONGTEXT     NULL    ,
  regdate     DATETIME     NULL     DEFAULT now(),
  PRIMARY KEY (id)
) COMMENT '게시글';

CREATE TABLE post_tag
(
  guest_post_id INT    NOT NULL,
  tag_id        BIGINT NOT NULL,
  PRIMARY KEY (guest_post_id, tag_id)
) COMMENT '게시물에 있는 태그';

CREATE TABLE posttype
(
  id   INT          NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NULL    ,
  PRIMARY KEY (id)
) COMMENT '게시물종류';

CREATE TABLE tag
(
  id          BIGINT   NOT NULL AUTO_INCREMENT,
  category_id INT      NOT NULL,
  name        LONGTEXT NOT NULL,
  PRIMARY KEY (id)
) COMMENT '태그';

CREATE TABLE user
(
  id         INT          NOT NULL AUTO_INCREMENT,
  username   VARCHAR(100) NOT NULL COMMENT '사용자아이디',
  name       VARCHAR(80)  NOT NULL COMMENT '닉네임',
  password   VARCHAR(100) NOT NULL,
  juminno    VARCHAR(10)  NOT NULL,
  regdate    DATETIME     NULL     DEFAULT now(),
  provider   VARCHAR(40)  NULL    ,
  providerid VARCHAR(200) NULL    ,
  latitude   DOUBLE       NOT NULL,
  longitude  DOUBLE       NOT NULL,
  status     BOOLEAN      NULL    ,
  PRIMARY KEY (id)
) COMMENT '사용자';

ALTER TABLE user
  ADD CONSTRAINT UQ_username UNIQUE (username);

CREATE TABLE user_authorities
(
  user_id      INT NOT NULL,
  authority_id INT NOT NULL,
  PRIMARY KEY (user_id, authority_id)
) COMMENT '사용자별 권한리스트';

CREATE TABLE user_hate
(
  complaint_userid INT NOT NULL,
  offender_userid  INT NOT NULL,
  PRIMARY KEY (complaint_userid, offender_userid)
) COMMENT '사용자별신고수';

CREATE TABLE user_tag
(
  user_id INT    NOT NULL,
  tag_id  BIGINT NOT NULL,
  PRIMARY KEY (user_id, tag_id)
) COMMENT '사용자 태그';

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
    REFERENCES category (id);

ALTER TABLE post_tag
  ADD CONSTRAINT FK_post_TO_post_tag
    FOREIGN KEY (guest_post_id)
    REFERENCES post (id);

ALTER TABLE post_tag
  ADD CONSTRAINT FK_tag_TO_post_tag
    FOREIGN KEY (tag_id)
    REFERENCES tag (id);

ALTER TABLE comment
  ADD CONSTRAINT FK_user_TO_comment
    FOREIGN KEY (user_id)
    REFERENCES user (id);

ALTER TABLE comment
  ADD CONSTRAINT FK_post_TO_comment
    FOREIGN KEY (post_id)
    REFERENCES post (id);

ALTER TABLE pick
  ADD CONSTRAINT FK_comment_TO_pick
    FOREIGN KEY (comment_id)
    REFERENCES comment (id);

ALTER TABLE pick
  ADD CONSTRAINT FK_post_TO_pick
    FOREIGN KEY (post_id)
    REFERENCES post (id);

ALTER TABLE user_tag
  ADD CONSTRAINT FK_user_TO_user_tag
    FOREIGN KEY (user_id)
    REFERENCES user (id);

ALTER TABLE user_tag
  ADD CONSTRAINT FK_tag_TO_user_tag
    FOREIGN KEY (tag_id)
    REFERENCES tag (id);

ALTER TABLE attachment
  ADD CONSTRAINT FK_post_TO_attachment
    FOREIGN KEY (post_id)
    REFERENCES post (id);

ALTER TABLE post
  ADD CONSTRAINT FK_posttype_TO_post
    FOREIGN KEY (posttype_id)
    REFERENCES posttype (id);

ALTER TABLE comment
  ADD CONSTRAINT FK_comment_TO_comment
    FOREIGN KEY (parentcommentid)
    REFERENCES comment (id);

ALTER TABLE follower
  ADD CONSTRAINT FK_user_TO_follower
    FOREIGN KEY (following_userid)
    REFERENCES user (id);

ALTER TABLE follower
  ADD CONSTRAINT FK_user_TO_follower1
    FOREIGN KEY (followed_userid)
    REFERENCES user (id);

ALTER TABLE user_hate
  ADD CONSTRAINT FK_user_TO_user_hate
    FOREIGN KEY (complaint_userid)
    REFERENCES user (id);

ALTER TABLE user_hate
  ADD CONSTRAINT FK_user_TO_user_hate1
    FOREIGN KEY (offender_userid)
    REFERENCES user (id);
