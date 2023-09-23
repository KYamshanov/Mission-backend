DROP TABLE IF EXISTS "tasks" CASCADE;
DROP TYPE IF EXISTS task_priority CASCADE;

/**
  Скрипт для создания таблиц tasks
 */

CREATE TYPE task_priority AS ENUM ('primary');

CREATE TABLE tasks
(
    id              VARCHAR(36) PRIMARY KEY,
    title           VARCHAR(60) NOT NULL,
    description     TEXT        NOT NULL DEFAULT '',
    creation_time   DATE        NOT NULL,
    update_time     DATE        NOT NULL,
    completion_time DATE                 DEFAULT NULL,
    priority        task_priority        DEFAULT NULL,
    owner           VARCHAR(36) NOT NULL
);
