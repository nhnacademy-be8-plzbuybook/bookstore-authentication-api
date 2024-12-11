use nhn_academy_46;

CREATE TABLE member
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    username VARCHAR(255) NULL,
    password VARCHAR(255) NULL,
    `role`   VARCHAR(255) NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

SHOW TABLES;