DROP TABLE IF EXISTS tasks CASCADE;
DROP TYPE IF EXISTS task_priority CASCADE;
DROP TYPE IF EXISTS task_status CASCADE;

/**
  Скрипт для создания таблиц tasks
 */

CREATE TYPE task_priority AS ENUM ('PRIMARY','LOW');
CREATE TYPE task_status AS ENUM ('CREATED','IN_PROCESSING','COMPLETED');
CREATE TYPE task_type AS ENUM ('TODAYS','WEEKS');

CREATE TABLE tasks
(
    id              VARCHAR(32) PRIMARY KEY,
    title           VARCHAR(60) NOT NULL,
    description     TEXT        NOT NULL DEFAULT '',
    creation_time   TIMESTAMPTZ NOT NULL,
    update_time     TIMESTAMPTZ          DEFAULT NULL,
    completion_time TIMESTAMPTZ          DEFAULT NULL,
    priority        task_priority        DEFAULT NULL,
    type            task_type            DEFAULT NULL,
    status          task_status NOT NULL DEFAULT 'CREATED',
    owner           VARCHAR(32) NOT NULL
);

INSERT INTO tasks
VALUES ('a59d8ae116d84c6d8381a9c76c2c_my1', 'Point: работа над проектом в гите',
        'https://github.com/users/KYamshanov/projects/2/views/1', '2023-11-10 10:26:33.599982 +00:00', null, null, null,
        'WEEKS', 'CREATED', 'KYamshanov');

INSERT INTO tasks
VALUES ('a59d8ae116d84c6d8381a976cce_my2', 'Задача2',
        'https://github.com/users/KYamshanov/projects/2/views/1', '2023-11-10 10:26:33.599982 +00:00', null, null, null,
        'WEEKS', 'CREATED', 'KYamshanov');

INSERT INTO tasks
VALUES ('a59d8ae116d84cd8381a9c76c2c_my3', 'Задача3',
        'https://github.com/users/KYamshanov/projects/2/views/1', '2023-11-10 10:26:33.599982 +00:00', null, null, null,
        'WEEKS', 'CREATED', 'KYamshanov');


---Order--

DROP TABLE IF EXISTS tasks_order;

CREATE TABLE tasks_order
(
    id         VARCHAR(32) PRIMARY KEY REFERENCES tasks (id),
    next       VARCHAR(32) NULL, /*NULL means tail*/
    updated_at TIMESTAMP   NOT NULL,
    CONSTRAINT not_equal CHECK ( id NOT LIKE next)
);

SELECT tasks_order.id, tasks_order.next, tasks_order.updated_at, tasks.title, t2.title
FROM tasks_order
         LEFT JOIN tasks ON tasks_order.id = tasks.id
         LEFT JOIN tasks t2 ON tasks_order.next = t2.id
ORDER BY updated_at;



