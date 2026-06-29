# FYNXT OrderBook - PostgreSQL Docker Version

This project is the persistent SQL database version of the FYNXT Order Booking & Portfolio API.

It uses:

- Spring Boot
- Java 17+
- Spring Data JPA
- PostgreSQL in Docker
- SQL schema from `src/main/resources/schema.sql`

Unlike the H2 version, data is not lost when the Spring Boot app restarts. Data is stored in a Docker volume.

## Start Database

From this project folder:

```bash
docker compose up -d
```

PostgreSQL connection:

```text
Host: localhost
Port: 5432
Database: orderbook
Username: orderbook_user
Password: orderbook_password
JDBC URL: jdbc:postgresql://localhost:5432/orderbook
```

pgAdmin runs at:

```text
http://localhost:5050
```

pgAdmin login:

```text
Email: admin@example.com
Password: admin
```

The PostgreSQL server is pre-registered in pgAdmin as:

```text
FYNXT OrderBook PostgreSQL
```

When pgAdmin asks for the database password, use:

```text
orderbook_password
```

## Run Application

```bash
mvn spring-boot:run
```

The API runs on:

```text
http://localhost:8082
```

Trader rows are created dynamically when you use a new `traderId` in `POST /portfolio/{traderId}/add` or `POST /orders`.

## API Examples

The examples use `T001` as a sample trader ID. You can replace it with any trader ID; the app creates the trader row automatically on the first write request.

Get portfolio:

```bash
curl http://localhost:8082/portfolio/T001
```

Add holding:

```bash
curl -X POST http://localhost:8082/portfolio/T001/add \
  -H "Content-Type: application/json" \
  -d '{"stock":"AAPL","sector":"TECH","quantity":100}'
```

Check overlap:

```bash
curl http://localhost:8082/portfolio/T001/overlap
```

Place BUY order:

```bash
curl -X POST http://localhost:8082/orders \
  -H "Content-Type: application/json" \
  -d '{"traderId":"T001","stock":"NVDA","sector":"TECH","quantity":50,"side":"BUY"}'
```

Fill order:

```bash
curl -X POST http://localhost:8082/orders/1/fill
```

## View Database

Open PostgreSQL shell:

```bash
docker exec -it fynxt-orderbook-postgres psql -U orderbook_user -d orderbook
```

Useful queries:

```sql
SELECT * FROM traders;
SELECT * FROM holdings;
SELECT * FROM orders;
```

Or use pgAdmin:

```text
http://localhost:5050
```

Path in pgAdmin:

```text
Servers
  -> FYNXT OrderBook PostgreSQL
    -> Databases
      -> orderbook
        -> Schemas
          -> public
            -> Tables
```

Tables:

```text
traders
holdings
orders
```

## Stop Database

Keep data:

```bash
docker compose down
```

Delete data also:

```bash
docker compose down -v
```

## Troubleshooting Startup

If Spring Boot fails with:

```text
Failed to execute database script
dataSourceScriptDatabaseInitializer
```

then PostgreSQL is reachable, but `schema.sql` failed while creating tables. The most common cause during local development is an old Docker volume with a previous/incompatible table structure.

For a clean local reset, run:

```bash
docker compose down -v
docker compose up -d
mvn spring-boot:run
```

This deletes the old local PostgreSQL volume and recreates the schema from `src/main/resources/schema.sql`.

If you want to keep existing data, do not run `down -v`; instead inspect the database manually:

```bash
docker exec -it fynxt-orderbook-postgres psql -U orderbook_user -d orderbook
```

Then check:

```sql
\dt
\d traders
\d orders
\d holdings
```

## Tests

Tests use H2 through `src/test/resources/application.properties`, so they can run without Docker:

```bash
mvn test
```

## Design Notes

- API-facing service methods return DTOs, not JPA entities.
- Entities stay internal to service/repository logic.
- The order pending-limit rule uses pessimistic locking on the trader row.
- Holdings use pessimistic locking per trader and stock.
- PostgreSQL data persists through the Docker volume `orderbook_postgres_data`.
