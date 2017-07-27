--expected: postgres
CREATE TABLE culturelog.user
(
    id SERIAL PRIMARY KEY,  --SERIAL for psql, others will use bigint and AUTOINCREMENT
    active boolean NOT NULL,
    password character varying(255) NOT NULL,
    username character varying(255) NOT NULL UNIQUE
)