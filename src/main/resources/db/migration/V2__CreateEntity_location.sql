--expected: postgres
CREATE TABLE culturelog.location
(
    id SERIAL PRIMARY KEY,  --SERIAL for psql, others will use bigint and AUTOINCREMENT
    name character varying(255) NOT NULL,
    description character varying(255),
    user_id SERIAL NOT NULL REFERENCES culturelog.user (id), --SERIAL for psql, others will use bigint; REFERENCES for psql, other will use different code
    constraint location_nameUser_unique unique (name, user_id)
)