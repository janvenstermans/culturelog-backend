--expected: postgres
CREATE TABLE culturelog.location
(
    id SERIAL PRIMARY KEY,  --SERIAL for psql, others will use bigint and AUTOINCREMENT
    description character varying(255) NOT NULL,
    user_id SERIAL NOT NULL REFERENCES culturelog.user (id) --SERIAL for psql, others will use bigint; REFERENCES for psql, other will use different code
)