CREATE TABLE IF NOT EXISTS employee (
       id varchar(32) not null,
        description varchar(120),
        name varchar(50),
        age int,
        departmentId varchar(32),
        primary key (id)
    );


CREATE TABLE IF NOT EXISTS department (
       id varchar(32) not null,
        description varchar(120),
        name varchar(50),
        primary key (id)
    );

INSERT INTO department (id, name, description)
SELECT 'dep01', 'HR', 'hr in hq'
    WHERE
    NOT EXISTS (
        SELECT id FROM department WHERE id = 'dep01'
    );

INSERT INTO department (id, name, description)
SELECT 'dep02', 'AFC', 'afc in omc'
    WHERE
    NOT EXISTS (
        SELECT id FROM department WHERE id = 'dep02'
    );

INSERT INTO employee (id, age, name, description, departmentId)
SELECT '111', 25, 'Adam', 'employe 01', 'dep01'
    WHERE
    NOT EXISTS (
        SELECT id FROM employee WHERE id = '111'
    );

INSERT INTO employee (id, age, name, description, departmentId)
SELECT '222', 26, 'Bob', 'employe 02', 'dep01'
    WHERE
    NOT EXISTS (
        SELECT id FROM employee WHERE id = '222'
    );

INSERT INTO employee (id, age, name, description, departmentId)
SELECT '333', 31, 'Chill', 'employe 03', 'dep02'
    WHERE
    NOT EXISTS (
        SELECT id FROM employee WHERE id = '333'
    );