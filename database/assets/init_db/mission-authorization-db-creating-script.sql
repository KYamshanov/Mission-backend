/**
  Скрипт для создания таблиц users, authorities
 */
DROP INDEX IF EXISTS ix_auth_username;
DROP TABLE IF EXISTS authorities CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS client CASCADE;
DROP TABLE IF EXISTS "authorization" CASCADE;
DROP TABLE IF EXISTS authorizationConsent CASCADE;


CREATE TABLE users
(
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(500) NOT NULL,
    enabled  BOOLEAN      NOT NULL
);

CREATE TABLE authorities
(
    username  VARCHAR(50) PRIMARY KEY REFERENCES users (username),
    authority VARCHAR(50) NOT NULL
);

CREATE UNIQUE INDEX ix_auth_username on authorities (username, authority);


CREATE TABLE client
(
    id                          varchar(255)                            NOT NULL,
    clientId                    varchar(255)                            NOT NULL,
    clientIdIssuedAt            timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    clientSecret                varchar(255)  DEFAULT NULL,
    clientSecretExpiresAt       timestamp     DEFAULT NULL,
    clientName                  varchar(255)                            NOT NULL,
    clientAuthenticationMethods varchar(1000)                           NOT NULL,
    authorizationGrantTypes     varchar(1000)                           NOT NULL,
    redirectUris                varchar(1000) DEFAULT NULL,
    postLogoutRedirectUris      varchar(1000) DEFAULT NULL,
    scopes                      varchar(1000)                           NOT NULL,
    clientSettings              varchar(2000)                           NOT NULL,
    tokenSettings               varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE "authorization"
(
    id                         varchar(255) NOT NULL,
    registeredClientId         varchar(255) NOT NULL,
    principalName              varchar(255) NOT NULL,
    authorizationGrantType     varchar(255) NOT NULL,
    authorizedScopes           varchar(1000) DEFAULT NULL,
    attributes                 varchar(4000) DEFAULT NULL,
    state                      varchar(500)  DEFAULT NULL,
    authorizationCodeValue     varchar(4000) DEFAULT NULL,
    authorizationCodeIssuedAt  timestamp     DEFAULT NULL,
    authorizationCodeExpiresAt timestamp     DEFAULT NULL,
    authorizationCodeMetadata  varchar(2000) DEFAULT NULL,
    accessTokenValue           varchar(4000) DEFAULT NULL,
    accessTokenIssuedAt        timestamp     DEFAULT NULL,
    accessTokenExpiresAt       timestamp     DEFAULT NULL,
    accessTokenMetadata        varchar(2000) DEFAULT NULL,
    accessTokenType            varchar(255)  DEFAULT NULL,
    accessTokenScopes          varchar(1000) DEFAULT NULL,
    refreshTokenValue          varchar(4000) DEFAULT NULL,
    refreshTokenIssuedAt       timestamp     DEFAULT NULL,
    refreshTokenExpiresAt      timestamp     DEFAULT NULL,
    refreshTokenMetadata       varchar(2000) DEFAULT NULL,
    oidcIdTokenValue           varchar(4000) DEFAULT NULL,
    oidcIdTokenIssuedAt        timestamp     DEFAULT NULL,
    oidcIdTokenExpiresAt       timestamp     DEFAULT NULL,
    oidcIdTokenMetadata        varchar(2000) DEFAULT NULL,
    oidcIdTokenClaims          varchar(2000) DEFAULT NULL,
    userCodeValue              varchar(4000) DEFAULT NULL,
    userCodeIssuedAt           timestamp     DEFAULT NULL,
    userCodeExpiresAt          timestamp     DEFAULT NULL,
    userCodeMetadata           varchar(2000) DEFAULT NULL,
    deviceCodeValue            varchar(4000) DEFAULT NULL,
    deviceCodeIssuedAt         timestamp     DEFAULT NULL,
    deviceCodeExpiresAt        timestamp     DEFAULT NULL,
    deviceCodeMetadata         varchar(2000) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE authorizationConsent
(
    registeredClientId varchar(255)  NOT NULL,
    principalName      varchar(255)  NOT NULL,
    authorities        varchar(1000) NOT NULL,
    PRIMARY KEY (registeredClientId, principalName)
);

INSERT INTO users (username, password, enabled)
VALUES ('user', '{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', true);

INSERT INTO authorities (username, authority)
VALUES ('user', 'ROLE_DEFAULT');

INSERT INTO client (id, clientId, clientIdIssuedAt, clientSecret, clientSecretExpiresAt, clientName,
                    clientAuthenticationMethods, authorizationGrantTypes, redirectUris, postLogoutRedirectUris, scopes,
                    clientSettings, tokenSettings)
VALUES ('oidc-client', 'desktop-client', '2023-08-01 00:00:00.000000', '{noop}secret', null, 'oidc-client',
        'client_secret_basic', 'refresh_token,authorization_code',
        'http://127.0.0.1:8080/desktop/authorized',
        'http://127.0.0.1:8080/desktop/logged-out', 'openid',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":true,"settings.client.require-authorization-consent":true}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}');