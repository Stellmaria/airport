-- CREATE DATABASE airport_repository;
--
-- CREATE SCHEMA airport_storage;

CREATE TABLE country
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE NOT NULL
);

CREATE TABLE city
(
    id         SERIAL PRIMARY KEY,
    country_id INT REFERENCES country (id) NOT NULL,
    name       VARCHAR(128)                NOT NULL
);

CREATE TABLE airport
(
    code    CHAR(3) PRIMARY KEY,
    city_id INT REFERENCES city (id) NOT NULL
);

CREATE TABLE aircompany
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE NOT NULL
);

CREATE TABLE airplane
(
    id            SERIAL PRIMARY KEY,
    model         VARCHAR(128)                   NOT NULL,
    aircompany_id INT REFERENCES aircompany (id) NOT NULL
);


CREATE TABLE seat
(
    airplane_id INT REFERENCES airplane (id) NOT NULL,
    seat_no     VARCHAR(4)                   NOT NULL,
    PRIMARY KEY (airplane_id, seat_no)
);

CREATE TABLE route
(
    id                     BIGSERIAL PRIMARY KEY,
    departure_date         TIMESTAMP                         NOT NULL,
    departure_airport_code CHAR(3) REFERENCES airport (code) NOT NULL,
    arrival_date           TIMESTAMP                         NOT NULL,
    arrival_airport_code   CHAR(3) REFERENCES airport (code) NOT NULL,
    airplane_id            INT REFERENCES airplane (id)      NOT NULL,
    status                 VARCHAR(32)                       NOT NULL
);

CREATE TABLE users
(
    id          SERIAL PRIMARY KEY,
    first_name  VARCHAR(128)        NOT NULL,
    last_name   VARCHAR(128)        NOT NULL,
    passport_no VARCHAR(32)         NOT NULL,
    birthday    DATE                NOT NULL,
    email       varchar(124) UNIQUE NOT NULL,
    role        VARCHAR(32)         NOT NULL,
    gender      VARCHAR(16)         NOT NULL
);

CREATE TABLE ticket
(
    id       BIGSERIAL PRIMARY KEY,
    user_id  INT REFERENCES users (id) NOT NULL,
    route_id INT REFERENCES route (id) NOT NULL,
    seat_no  VARCHAR(4)                NOT NULL,
    cost     NUMERIC(8, 2)
);

CREATE TABLE login
(
    id       SERIAL PRIMARY KEY,
    user_id  INT REFERENCES users (id) NOT NULL,
    login    VARCHAR(128) UNIQUE       NOT NULL,
    password VARCHAR(32)               NOT NULL
);

-- INSERT INTO users(first_name, last_name, passport_no, birthday, email, role, gender)
-- VALUES ('Иван', 'Иванов', 'QO2300', '2000-01-01', 'ivanivanov@gmail.com', 'user', 'male');
--
-- INSERT INTO login(user_id, login, password)
-- VALUES (1, 'ivan', '123');
--
-- INSERT INTO aircompany(name)
-- VALUES ('Belavia');
--
-- INSERT into airplane(model, aircompany_id)
-- VALUES ('Boeing-737', 1);
--
-- INSERT INTO country(name)
-- VALUES ('Belarus'),
--        ('Great Britain');
--
-- INSERT INTO city(country_id, name)
-- VALUES (1, 'Minsk'),
--        (2, 'London');
--
-- INSERT INTO airport(code, city_id)
-- VALUES ('MNK', 1),
--        ('LDN', 2);
--
-- INSERT into route(departure_date, departure_airport_code, arrival_date, arrival_airport_code, airplane_id, status)
-- VALUES ('24-09-2022T18:00', 'MNK',
--         '25-09-2022T00:00', 'LDN', 1, 'ARRIVED');
--
-- INSERT INTO seat (airplane_id, seat_no)
-- SELECT id, s.column1
-- FROM airplane
--          CROSS JOIN (VALUES ('A1'), ('A2'), ('B1'), ('B2'), ('C1'), ('C2'), ('D1'), ('D2') ORDER BY 1) s;
--
-- INSERT into ticket(user_id, route_id, seat_no, cost)
-- VALUES (1, 1, 'A1', 200.00);

-- SELECT count(seat_no)
-- FROM seat
-- WHERE airplane_id = 2;
--
-- INSERT INTO seat (airplane_id, seat_no)
-- SELECT id, s.column1
-- FROM airplane
--          CROSS JOIN (VALUES ('A1'), ('A2'), ('B1'), ('B2'), ('C1'), ('C2'), ('D1'), ('D2') ORDER BY 1) s
-- WHERE aircompany_id = 2;