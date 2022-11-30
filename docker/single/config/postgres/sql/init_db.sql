CREATE TABLE IF NOT EXISTS t_user (
                        id varchar(32) not null,
                        age int4,
                        name varchar(60),
                        primary key (id)
);


INSERT INTO t_user (id, age, name)
SELECT '111', 25, 'Adam'
    WHERE
    NOT EXISTS (
        SELECT id FROM t_user WHERE id = '111'
    );