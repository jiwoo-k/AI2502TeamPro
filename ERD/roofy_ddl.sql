SET SESSION FOREIGN_KEY_CHECKS = 0;

/* 기존 테이블 삭제 (의존관계가 있는 순서로 삭제) */
DROP TABLE IF EXISTS user_tag;
DROP TABLE IF EXISTS user_hate;
DROP TABLE IF EXISTS user_authorities;
DROP TABLE IF EXISTS pick;
DROP TABLE IF EXISTS post_tag;
DROP TABLE IF EXISTS follower;
DROP TABLE IF EXISTS attachment;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS posttype;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS authority;
DROP TABLE IF EXISTS user;

/* ------------------------------
   테이블 생성
   ------------------------------ */

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
    name VARCHAR(40) NULL,
    PRIMARY KEY (id)
) COMMENT '권한';

CREATE TABLE category
(
    id   INT         NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(10) DEFAULT '#808080',
    PRIMARY KEY (id)
) COMMENT '대분류';

CREATE TABLE comment
(
    id              INT      NOT NULL AUTO_INCREMENT,
    user_id         INT      NOT NULL,
    post_id         INT      NOT NULL,
    parentcommentid INT      NOT NULL,
    content         TEXT     NULL,
    regdate         DATETIME NULL DEFAULT now(),
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
    comment_id INT NULL,
    PRIMARY KEY (post_id)
) COMMENT '선택된댓글';

CREATE TABLE post
(
    id          INT          NOT NULL AUTO_INCREMENT,
    user_id     INT          NOT NULL,
    posttype_id INT          NOT NULL,
    title       VARCHAR(100) NOT NULL,
    content     LONGTEXT     NULL,
    regdate     DATETIME     NULL DEFAULT now(),
    isfinish    BOOLEAN      NULL DEFAULT false,
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
    name VARCHAR(100) NULL,
    PRIMARY KEY (id)
) COMMENT '게시물종류';

CREATE TABLE tag
(
    id          INT   NOT NULL AUTO_INCREMENT,
    category_id INT      NOT NULL,
    name        LONGTEXT NOT NULL,
    PRIMARY KEY (id)
) COMMENT '태그';

CREATE TABLE user
(
    id         INT          NOT NULL AUTO_INCREMENT,
    username   VARCHAR(100) NOT NULL UNIQUE COMMENT '사용자아이디',
    name       VARCHAR(80)  NOT NULL UNIQUE COMMENT '닉네임',
    password   VARCHAR(100) NOT NULL,
    juminNo    VARCHAR(13) UNIQUE NOT NULL,
    createdAt    DATETIME  DEFAULT now(),
    provider   VARCHAR(40)  NULL,
    providerId VARCHAR(200) NULL,
    latitude   DOUBLE,
    longitude  DOUBLE,
    status     enum('active', 'paused', 'banned')  NOT NULL default 'active',
    PRIMARY KEY (id)
) COMMENT '사용자';

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

/* ------------------------------
   ALTER TABLE 으로 제약조건 추가
   ------------------------------ */

/* UNIQUE 제약조건 */
ALTER TABLE user
    ADD CONSTRAINT UQ_username UNIQUE (username);

ALTER TABLE authority
    ADD CONSTRAINT UQ_authority_name UNIQUE (name);

/* user_authorities 외래키 */
ALTER TABLE user_authorities
    ADD CONSTRAINT FK_user_TO_user_authorities
        FOREIGN KEY (user_id)
            REFERENCES user (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE user_authorities
    ADD CONSTRAINT FK_authority_TO_user_authorities
        FOREIGN KEY (authority_id)
            REFERENCES authority (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

/* post 외래키 */
ALTER TABLE post
    ADD CONSTRAINT FK_user_TO_post
        FOREIGN KEY (user_id)
            REFERENCES user (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE post
    ADD CONSTRAINT FK_posttype_TO_post
        FOREIGN KEY (posttype_id)
            REFERENCES posttype (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

/* comment 외래키 */
ALTER TABLE comment
    ADD CONSTRAINT FK_user_TO_comment
        FOREIGN KEY (user_id)
            REFERENCES user (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE comment
    ADD CONSTRAINT FK_post_TO_comment
        FOREIGN KEY (post_id)
            REFERENCES post (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE comment
    ADD CONSTRAINT FK_comment_TO_comment
        FOREIGN KEY (parentcommentid)
            REFERENCES comment (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

/* pick 외래키 */
ALTER TABLE pick
    ADD CONSTRAINT FK_post_TO_pick
        FOREIGN KEY (post_id)
            REFERENCES post (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE pick
    ADD CONSTRAINT FK_comment_TO_pick
        FOREIGN KEY (comment_id)
            REFERENCES comment (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

/* post_tag 외래키 */
ALTER TABLE post_tag
    ADD CONSTRAINT FK_post_TO_post_tag
        FOREIGN KEY (guest_post_id)
            REFERENCES post (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE post_tag
    ADD CONSTRAINT FK_tag_TO_post_tag
        FOREIGN KEY (tag_id)
            REFERENCES tag (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

/* tag 외래키 */
ALTER TABLE tag
    ADD CONSTRAINT FK_category_TO_tag
        FOREIGN KEY (category_id)
            REFERENCES category (id)
            ON UPDATE RESTRICT;

/* attachment 외래키 */
ALTER TABLE attachment
    ADD CONSTRAINT FK_post_TO_attachment
        FOREIGN KEY (post_id)
            REFERENCES post (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

/* follower 외래키 */
ALTER TABLE follower
    ADD CONSTRAINT FK_follower_user_1
        FOREIGN KEY (following_userid)
            REFERENCES user (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE follower
    ADD CONSTRAINT FK_follower_user_2
        FOREIGN KEY (followed_userid)
            REFERENCES user (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

/* user_hate 외래키 */
ALTER TABLE user_hate
    ADD CONSTRAINT FK_user_hate_user_1
        FOREIGN KEY (complaint_userid)
            REFERENCES user (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE user_hate
    ADD CONSTRAINT FK_user_hate_user_2
        FOREIGN KEY (offender_userid)
            REFERENCES user (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

/* user_tag 외래키 */
ALTER TABLE user_tag
    ADD CONSTRAINT FK_user_tag_user
        FOREIGN KEY (user_id)
            REFERENCES user (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

ALTER TABLE user_tag
    ADD CONSTRAINT FK_user_tag_tag
        FOREIGN KEY (tag_id)
            REFERENCES tag (id)
            ON UPDATE RESTRICT ON DELETE CASCADE;

SET SESSION FOREIGN_KEY_CHECKS = 1;
