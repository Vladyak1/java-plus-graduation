DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE if not exists comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text                    VARCHAR(2048) NOT NULL,
    event_id                BIGINT,
    author_id               BIGINT,
    created_on              TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);