/**
  Скрипт для создания таблиц users, authorization
 */
DROP TABLE IF EXISTS "mission-id".users CASCADE;
DROP TABLE IF EXISTS "mission-id".authorities CASCADE;
DROP TABLE IF EXISTS "mission-id".authorization CASCADE;
DROP TABLE IF EXISTS "mission-id".credential CASCADE;
DROP TABLE IF EXISTS "mission-id".ex_auth CASCADE;
DROP TABLE IF EXISTS "mission-id".user_authorities CASCADE;
DROP TABLE IF EXISTS "mission-id".client_service CASCADE;
DROP TYPE IF EXISTS "mission-id".social_service CASCADE;

CREATE TABLE "mission-id".users
(
    id      UUID PRIMARY KEY,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE "mission-id".credential
(
    user_id  UUID PRIMARY KEY REFERENCES "mission-id".users (id),
    username VARCHAR(30)  NOT NULL UNIQUE,
    password VARCHAR(500) NOT NULL
);


CREATE TABLE "mission-id".authorities
(
    id     UUID PRIMARY KEY,
    scopes VARCHAR(500) NOT NULL
);


CREATE TABLE "mission-id".user_authorities
(
    user_id      UUID PRIMARY KEY REFERENCES "mission-id".users (id),
    authority_id UUID NOT NULL REFERENCES "mission-id".authorities (id)
);

CREATE TYPE "mission-id".social_service AS ENUM ('GITHUB');

CREATE TABLE "mission-id".clients
(
    id                            VARCHAR(20) PRIMARY KEY,
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
    id                             UUID PRIMARY KEY,
    client_id                      VARCHAR(20)                 NOT NULL REFERENCES "mission-id".clients (id),
    user_id                        UUID                        NULL REFERENCES "mission-id".users (id),
    issued_at                      TIMESTAMP                   NOT NULL,
    authorization_grant_type       VARCHAR(200)                NULL,
    authorization_metadata         JSON                        NULL DEFAULT NULL,
    authentication_code            VARCHAR(128)                NOT NULL,
    authentication_code_expires_at TIMESTAMP                   NOT NULL,
    user_metadata                  JSON                        NULL,
    scopes                         VARCHAR(1000)               NOT NULL,
    access_token_value             VARCHAR(4000)               NULL,
    access_token_issued_at         TIMESTAMP                   NULL,
    access_token_expires_at        TIMESTAMP                   NULL,
    refresh_token_value            VARCHAR(4000)               NULL,
    refresh_token_issued_at        TIMESTAMP                   NULL,
    refresh_token_expires_at       TIMESTAMP                   NULL,
    social_service                 "mission-id".social_service NULL
);


CREATE TABLE "mission-id".ex_auth
(
    user_id           UUID                        NOT NULL REFERENCES "mission-id".users (id),
    social_service_id "mission-id".social_service NOT NULL,
    external_user_id  VARCHAR(128)                NOT NULL
);


CREATE TABLE "mission-id".client_service
(
    client_id         VARCHAR(20)                 NOT NULL REFERENCES "mission-id".clients (id),
    social_service_id "mission-id".social_service NOT NULL,
    enabled           BOOLEAN                     NOT NULL
);


INSERT INTO "mission-id".clients
VALUES ('desktop-client',
        'https://oauth.pstmn.io/v1/callback',
        'point',
        '2023-11-09 12:33:32.000000',
        true,
        'basic',
        'authorization_code',
        'code',
        '2025-11-09 12:33:32.000000',
        'null',
        '{
          "accessTokenLifetimeInMS": 60000,
          "refreshTokenLifetimeInMS": 3600000,
          "authenticationCodeLifeTimeInMs": 30000,
          "secret": "secret"
        }');

INSERT INTO "mission-id".client_service
VALUES ('desktop-client',
        'GITHUB',
        true);