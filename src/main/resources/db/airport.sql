CREATE SCHEMA IF NOT EXISTS airport_storage;

CREATE TABLE IF NOT EXISTS airport_storage.country
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS airport_storage.city
(
    id         SERIAL PRIMARY KEY,
    country_id INT NOT NULL REFERENCES airport_storage.country (id),
    name       VARCHAR(128) NOT NULL,
    CONSTRAINT city_country_name_uk UNIQUE (country_id, name)
);

CREATE TABLE IF NOT EXISTS airport_storage.airport
(
    code    CHAR(3) PRIMARY KEY,
    city_id INT NOT NULL REFERENCES airport_storage.city (id)
);

CREATE TABLE IF NOT EXISTS airport_storage.aircompany
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(128) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS airport_storage.airplane
(
    id            SERIAL PRIMARY KEY,
    model         VARCHAR(128) NOT NULL,
    aircompany_id INT NOT NULL REFERENCES airport_storage.aircompany (id),
    CONSTRAINT airplane_model_company_uk UNIQUE (model, aircompany_id)
);

CREATE TABLE IF NOT EXISTS airport_storage.seat
(
    airplane_id INT NOT NULL REFERENCES airport_storage.airplane (id) ON DELETE CASCADE,
    seat_no     VARCHAR(4) NOT NULL,
    PRIMARY KEY (airplane_id, seat_no),
    CONSTRAINT seat_no_chk CHECK (seat_no ~ '^[A-Z][0-9]{1,3}$')
);

CREATE TABLE IF NOT EXISTS airport_storage.route
(
    id                     BIGSERIAL PRIMARY KEY,
    departure_date         TIMESTAMP NOT NULL,
    departure_airport_code CHAR(3) NOT NULL REFERENCES airport_storage.airport (code),
    arrival_date           TIMESTAMP NOT NULL,
    arrival_airport_code   CHAR(3) NOT NULL REFERENCES airport_storage.airport (code),
    airplane_id            INT NOT NULL REFERENCES airport_storage.airplane (id),
    status                 VARCHAR(32) NOT NULL,
    CONSTRAINT route_dates_chk CHECK (arrival_date > departure_date),
    CONSTRAINT route_airports_chk CHECK (departure_airport_code <> arrival_airport_code),
    CONSTRAINT route_status_chk CHECK (status IN ('CANCELED', 'ARRIVED', 'DEPARTED', 'SCHEDULED')),
    CONSTRAINT route_schedule_uk UNIQUE (
        departure_date, departure_airport_code, arrival_date, arrival_airport_code, airplane_id
    )
);

CREATE TABLE IF NOT EXISTS airport_storage.users
(
    id          SERIAL PRIMARY KEY,
    first_name  VARCHAR(128) NOT NULL,
    last_name   VARCHAR(128) NOT NULL,
    passport_no VARCHAR(32) UNIQUE NOT NULL,
    birthday    DATE NOT NULL,
    email       VARCHAR(124) UNIQUE NOT NULL,
    role        VARCHAR(32) NOT NULL,
    gender      VARCHAR(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS airport_storage.ticket
(
    id       BIGSERIAL PRIMARY KEY,
    user_id  INT NOT NULL REFERENCES airport_storage.users (id),
    route_id BIGINT NOT NULL REFERENCES airport_storage.route (id) ON DELETE CASCADE,
    seat_no  VARCHAR(4) NOT NULL,
    cost     NUMERIC(8, 2) NOT NULL,
    CONSTRAINT ticket_route_seat_uk UNIQUE (route_id, seat_no),
    CONSTRAINT ticket_cost_chk CHECK (cost >= 0)
);

CREATE TABLE IF NOT EXISTS airport_storage.login
(
    id       SERIAL PRIMARY KEY,
    user_id  INT NOT NULL REFERENCES airport_storage.users (id) ON DELETE CASCADE,
    login    VARCHAR(128) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE OR REPLACE FUNCTION airport_storage.validate_ticket_seat()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM airport_storage.route r
        JOIN airport_storage.seat s
          ON s.airplane_id = r.airplane_id
         AND s.seat_no = NEW.seat_no
        WHERE r.id = NEW.route_id
    ) THEN
        RAISE EXCEPTION 'Seat % does not belong to the airplane assigned to route %', NEW.seat_no, NEW.route_id;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS ticket_seat_matches_route ON airport_storage.ticket;
CREATE TRIGGER ticket_seat_matches_route
    BEFORE INSERT OR UPDATE OF route_id, seat_no
    ON airport_storage.ticket
    FOR EACH ROW
    EXECUTE FUNCTION airport_storage.validate_ticket_seat();

INSERT INTO airport_storage.country(name)
VALUES ('Belarus'), ('Great Britain')
ON CONFLICT (name) DO NOTHING;

INSERT INTO airport_storage.city(country_id, name)
SELECT c.id, v.city
FROM (VALUES ('Belarus', 'Minsk'), ('Great Britain', 'London')) AS v(country, city)
JOIN airport_storage.country c ON c.name = v.country
ON CONFLICT (country_id, name) DO NOTHING;

INSERT INTO airport_storage.airport(code, city_id)
SELECT v.code, c.id
FROM (VALUES ('MNK', 'Minsk'), ('LDN', 'London')) AS v(code, city)
JOIN airport_storage.city c ON c.name = v.city
ON CONFLICT (code) DO NOTHING;

INSERT INTO airport_storage.aircompany(name)
VALUES ('Belavia')
ON CONFLICT (name) DO NOTHING;

INSERT INTO airport_storage.airplane(model, aircompany_id)
SELECT 'Boeing-737', ac.id
FROM airport_storage.aircompany ac
WHERE ac.name = 'Belavia'
ON CONFLICT (model, aircompany_id) DO NOTHING;

INSERT INTO airport_storage.seat(airplane_id, seat_no)
SELECT a.id, s.seat_no
FROM airport_storage.airplane a
CROSS JOIN (VALUES ('A1'), ('A2'), ('B1'), ('B2'), ('C1'), ('C2'), ('D1'), ('D2')) AS s(seat_no)
WHERE a.model = 'Boeing-737'
ON CONFLICT (airplane_id, seat_no) DO NOTHING;

INSERT INTO airport_storage.users(first_name, last_name, passport_no, birthday, email, role, gender)
VALUES ('Иван', 'Иванов', 'QO2300', DATE '2000-01-01', 'ivanivanov@gmail.com', 'user', 'male')
ON CONFLICT (email) DO NOTHING;

INSERT INTO airport_storage.login(user_id, login, password)
SELECT u.id, 'ivan', '$2a$10$Xl0yhvzLIaJCDdKBS0Lld.ksK7c2Zytg/ZKFdtIYYQUv8rUfvCR4W'
FROM airport_storage.users u
WHERE u.email = 'ivanivanov@gmail.com'
ON CONFLICT (login) DO NOTHING;

INSERT INTO airport_storage.route(
    departure_date, departure_airport_code, arrival_date, arrival_airport_code, airplane_id, status
)
SELECT TIMESTAMP '2022-09-24 18:00:00', 'MNK', TIMESTAMP '2022-09-25 00:00:00', 'LDN', a.id, 'ARRIVED'
FROM airport_storage.airplane a
WHERE a.model = 'Boeing-737'
ON CONFLICT (
    departure_date, departure_airport_code, arrival_date, arrival_airport_code, airplane_id
) DO NOTHING;

INSERT INTO airport_storage.ticket(user_id, route_id, seat_no, cost)
SELECT u.id, r.id, 'A1', 200.00
FROM airport_storage.users u
JOIN airport_storage.route r
  ON r.departure_airport_code = 'MNK'
 AND r.arrival_airport_code = 'LDN'
 AND r.departure_date = TIMESTAMP '2022-09-24 18:00:00'
WHERE u.email = 'ivanivanov@gmail.com'
ON CONFLICT (route_id, seat_no) DO NOTHING;
