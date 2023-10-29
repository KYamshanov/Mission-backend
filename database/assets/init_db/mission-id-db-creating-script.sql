/**
  Скрипт для создания таблиц users, authorization
 */
DROP TABLE IF EXISTS "mission-id".users CASCADE;
DROP TABLE IF EXISTS "mission-id"."authorization" CASCADE;


CREATE TABLE "mission-id".users
(
    id       VARCHAR(32) PRIMARY KEY,
    username VARCHAR(30)  NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL,
    enabled  BOOLEAN      NOT NULL
);

CREATE TABLE "mission-id"."authorization"
(
    id                       VARCHAR(36) PRIMARY KEY,
    service                  VARCHAR(6)   NOT NULL,
    auth_code                VARCHAR(128) NOT NULL,
    scopes                   VARCHAR(1000) DEFAULT NULL,
    service_metadata         JSON         NOT NULL,
    created_at               TIMESTAMP    NOT NULL,
    updated_at               TIMESTAMP    NOT NULL,
    code_challenge           VARCHAR(43)  NOT NULL,
    enabled                  BOOLEAN      NOT NULL,
    access_token             VARCHAR(1000) DEFAULT NULL,
    refresh_token            VARCHAR(500)  DEFAULT NULL,
    refresh_token_expires_at TIMESTAMP     DEFAULT NULL
);

INSERT INTO "mission-id".users (id, username, password, enabled)
VALUES ('04b89e9ef625402e9758d942d486e7bb', 'user',
        '{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', true);