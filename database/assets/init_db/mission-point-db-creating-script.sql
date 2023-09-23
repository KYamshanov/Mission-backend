DROP TABLE IF EXISTS tasks CASCADE;
DROP TYPE IF EXISTS task_priority CASCADE;
DROP TYPE IF EXISTS task_status CASCADE;

/**
  Скрипт для создания таблиц tasks
 */

CREATE TYPE task_priority AS ENUM ('PRIMARY');
CREATE TYPE task_status AS ENUM ('CREATED','IN_PROCESSING','COMPLETED');

CREATE TABLE tasks
(
    id              VARCHAR(32) PRIMARY KEY,
    title           VARCHAR(60) NOT NULL,
    description     TEXT        NOT NULL DEFAULT '',
    creation_time   TIMESTAMPTZ NOT NULL,
    update_time     TIMESTAMPTZ          DEFAULT NULL,
    completion_time TIMESTAMPTZ          DEFAULT NULL,
    priority        task_priority        DEFAULT NULL,
    status          task_status NOT NULL DEFAULT 'CREATED',
    owner           VARCHAR(32) NOT NULL
);
