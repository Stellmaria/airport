# Airport JDBC

Educational Java project that models an airport database and implements a JDBC DAO layer for its core entities.

The project intentionally focuses on relational modelling, SQL, JDBC, connection management, and CRUD. The old generated JSP/Servlet `Hello World` scaffold was removed because there was no web application implemented on top of it.

## What is implemented

- PostgreSQL schema in `airport_storage`;
- countries, cities, airports, airlines, airplanes and seats;
- routes, passengers, logins and tickets;
- complete JDBC CRUD for the mutable entities;
- composite-key lookup/delete for seats;
- reusable JDBC connection pool;
- environment-based database configuration;
- database integrity checks for route dates/statuses, ticket price and seat uniqueness;
- a database trigger that rejects a ticket when the requested seat does not belong to the airplane assigned to the route;
- JUnit integration tests against PostgreSQL;
- GitHub Actions CI.

A seat consists only of its composite key `(airplane_id, seat_no)`, so it has no mutable columns. `SeatDao.update` therefore explicitly rejects updates; rename a seat by deleting the old key and saving a new seat.

## Requirements

- Java 17+
- Maven Wrapper is included
- PostgreSQL 15+ or Docker

## Start PostgreSQL with Docker

```bash
docker compose up -d
```

The compose file creates the `airport_repository` database and initializes it from:

```text
src/main/resources/db/airport.sql
```

The local Docker credentials are development-only:

```text
host: localhost
port: 5432
database: airport_repository
user: postgres
password: postgres
```

## Configuration

`PropertiesUtil` first checks environment variables and then falls back to `application.properties`.

| Property | Environment variable | Default |
|---|---|---|
| `db.url` | `DB_URL` | `jdbc:postgresql://localhost:5432/airport_repository` |
| `db.username` | `DB_USERNAME` | `postgres` |
| `db.password` | `DB_PASSWORD` | `change_me` |
| `db.pool.size` | `DB_POOL_SIZE` | `5` |

For the Docker setup on Bash/macOS/Linux:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/airport_repository
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export DB_POOL_SIZE=5
```

PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/airport_repository"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "postgres"
$env:DB_POOL_SIZE = "5"
```

Do not commit real database passwords. Local `.env`/secret material is ignored by Git.

## Initialize an existing PostgreSQL database manually

```bash
psql -h localhost -U postgres -d airport_repository \
  -v ON_ERROR_STOP=1 \
  -f src/main/resources/db/airport.sql
```

The bootstrap script is intended for a fresh educational database. It is safe to rerun for the included seed records, but existing schemas created by older project versions should be recreated so that all current constraints are present.

## Build

On Bash/macOS/Linux, invoke the tracked wrapper through Bash so the build also works when the checkout does not preserve its executable bit:

```bash
bash mvnw clean verify
```

On Windows:

```powershell
.\mvnw.cmd clean verify
```

The PostgreSQL integration test is enabled when `DB_URL` is present in the environment. This keeps a plain compilation/test run usable when no local database is running while ensuring CI exercises the real database.

## DAO coverage

The DAO layer covers:

- `AircompanyDao`
- `AirplaneDao`
- `AirportDao`
- `CityDao`
- `CountryDao`
- `LoginDao`
- `RouteDao`
- `SeatDao`
- `TicketDao`
- `UserDao`

Each mutable entity supports save, update, delete, lookup by id and list-all operations. DAO methods that receive an existing `Connection` can be composed into larger transactions without taking an additional connection from the pool.

## Database guarantees

The schema enforces, among other things:

- unique country and airline names;
- unique airport codes;
- unique passenger passport/email and login names;
- route arrival after departure;
- different departure and arrival airports;
- allowed route statuses: `CANCELED`, `ARRIVED`, `DEPARTED`, `SCHEDULED`;
- non-negative ticket prices;
- one ticket per `(route_id, seat_no)`;
- ticket seat must exist on the airplane assigned to that route.

The included login seed stores a BCrypt-formatted hash, not a plaintext password. Authentication itself is outside the scope of this JDBC DAO assignment.

## CI

`.github/workflows/ci.yml` starts PostgreSQL 15, applies `airport.sql`, then runs the Maven Wrapper build. The existing `Secret Scan` workflow remains enabled as an additional check for accidentally committed credentials.
