/**
  Скрипт для создания таблиц users, authorization
 */
DROP TABLE IF EXISTS "mission-id".users CASCADE;
DROP TABLE IF EXISTS "mission-id".authorities CASCADE;
DROP TABLE IF EXISTS "mission-id".authorization CASCADE;
DROP TABLE IF EXISTS "mission-id".credential CASCADE;
DROP TABLE IF EXISTS "mission-id".ex_auth CASCADE;
DROP TABLE IF EXISTS "mission-id".user_authorities CASCADE;
DROP TABLE IF EXISTS "mission-id".clients CASCADE;
DROP TABLE IF EXISTS "mission-id".social_service CASCADE;
DROP TABLE IF EXISTS "mission-id".client_service CASCADE;

CREATE TABLE "mission-id".users
(
    id      VARCHAR(36) PRIMARY KEY,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE "mission-id".credential
(
    user_id  VARCHAR(36) PRIMARY KEY REFERENCES "mission-id".users (id),
    username VARCHAR(30)  NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL
);


CREATE TABLE "mission-id".authorities
(
    id     VARCHAR(16) PRIMARY KEY,
    scopes VARCHAR(500) NOT NULL
);


CREATE TABLE "mission-id".user_authorities
(
    user_id      VARCHAR(36) PRIMARY KEY REFERENCES "mission-id".users (id),
    authority_id VARCHAR(16) NOT NULL REFERENCES "mission-id".authorities (id)
);

CREATE TABLE "mission-id".social_service
(
    id            VARCHAR(36) PRIMARY KEY,
    code          VARCHAR(10) NOT NULL,
    configuration json        NOT NULL
);

CREATE TABLE "mission-id".clients
(
    id                            VARCHAR(36) PRIMARY KEY,
    redirect_url                  VARCHAR(200),
    scopes                        VARCHAR(1000) NOT NULL,
    issued_at                     TIMESTAMP DEFAULT (now()),
    enabled                       BOOLEAN       NOT NULL,
    client_authentication_methods VARCHAR(200)  NOT NULL,
    grand_types                   VARCHAR(1000) NOT NULL,
    response_types                VARCHAR(1000) NOT NULL,
    expires_at                    TIMESTAMP DEFAULT NULL,
    logout_redirect_url           VARCHAR(200),
    metadata                      JSON      DEFAULT NULL
);


CREATE TABLE "mission-id".authorization
(
    id                             VARCHAR(36) PRIMARY KEY,
    client_id                      VARCHAR(36)   NOT NULL REFERENCES "mission-id".clients (id),
    user_id                        VARCHAR(36)   NOT NULL REFERENCES "mission-id".users (id),
    authorization_grant_type       VARCHAR(200)  NOT NULL,
    authorization_metadata         JSON DEFAULT NULL,
    authentication_code            VARCHAR(128)  NOT NULL,
    authentication_code_expires_at TIMESTAMP     NOT NULL,
    user_metadata                  JSON          NOT NULL,
    scopes                         VARCHAR(1000) NOT NULL,
    access_token_value             VARCHAR(4000),
    access_token_issued_at         TIMESTAMP     NOT NULL,
    access_token_expires_at        TIMESTAMP     NOT NULL,
    refresh_token_value            VARCHAR(4000),
    refresh_token_issued_at        TIMESTAMP     NOT NULL,
    refresh_token_expires_at       TIMESTAMP     NOT NULL,
    social_service                 VARCHAR(36)   NOT NULL REFERENCES "mission-id".social_service (id)
);

CREATE TABLE "mission-id".ex_auth
(
    user_id           VARCHAR(36)  NOT NULL REFERENCES "mission-id".users (id),
    social_service_id VARCHAR(36)  NOT NULL REFERENCES "mission-id".social_service (id),
    external_user_id  VARCHAR(128) NOT NULL
);


CREATE TABLE "mission-id".client_service
(
    client_id         VARCHAR(36) NOT NULL REFERENCES "mission-id".clients (id),
    social_service_id VARCHAR(36) NOT NULL REFERENCES "mission-id".social_service (id)
);

