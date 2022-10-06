CREATE TABLE jobs
(
    name          varchar(255) UNIQUE NOT NULL,
    config        varchar(255)        NOT NULL,
    creation_time TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (name)
);

CREATE TABLE images
(
    name          varchar(255) UNIQUE NOT NULL,
    job           varchar(255)        NOT NULL,
    creation_time TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (name),
    FOREIGN KEY (job) REFERENCES jobs.name
);

CREATE TABLE containers
(
    id            varchar(255) UNIQUE NOT NULL,
    name          varchar(255)        NOT NULL,
    job           varchar(255)        NOT NULL,
    creation_time TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (job) REFERENCES jobs.name
);


