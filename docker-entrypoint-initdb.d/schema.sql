    CREATE TABLE instances (
        name varchar(255) UNIQUE NOT NULL,
        creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

        PRIMARY KEY (name)
    );

    CREATE TABLE images (
        name varchar(255) UNIQUE NOT NULL,
        creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

        PRIMARY KEY (name)
    );

    CREATE TABLE containers (
        id varchar(255) UNIQUE NOT NULL,
        name varchar(255) UNIQUE NOT NULL,
        instance varchar(255),
        creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

        PRIMARY KEY (id),
        FOREIGN KEY (instance) REFERENCES instances(name)
    );



