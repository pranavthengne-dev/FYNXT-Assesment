# Order Booking & Portfolio API - PostgreSQL Docker Version

This is the same FYNXT assignment project, configured to use PostgreSQL in Docker instead of the in-memory H2 database.

## Start PostgreSQL

```bash
docker compose up -d
```

PostgreSQL runs at:

```text
localhost:5432
```

Database credentials:

```text
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

The PostgreSQL server is pre-registered as `FYNXT OrderBook PostgreSQL`.
When prompted for the DB password, enter:

```text
orderbook_password
```

## Run The App

```bash
mvn spring-boot:run
```

The app runs on:

```text
http://localhost:8082
```

## Test APIs

The examples use `T001` as a sample trader ID. You can use any trader ID; the trader is created automatically when you add holdings or place an order.

```bash
curl http://localhost:8082/portfolio/T001
```

Add holdings:

```bash
curl -X POST http://localhost:8082/portfolio/T001/add \
  -H "Content-Type: application/json" \
  -d '{"stock":"AAPL","sector":"TECH","quantity":100}'
```

Check portfolio:

```bash
curl http://localhost:8082/portfolio/T001
```

Check overlap:

```bash
curl http://localhost:8082/portfolio/T001/overlap
```

## See Data In PostgreSQL

```bash
docker exec -it fynxt-orderbook-postgres psql -U orderbook_user -d orderbook
```

Useful SQL:

```sql
SELECT * FROM traders;
SELECT * FROM holdings;
SELECT * FROM orders;
```

In pgAdmin, browse:

```text
Servers -> FYNXT OrderBook PostgreSQL -> Databases -> orderbook -> Schemas -> public -> Tables
```

## Stop PostgreSQL

Stop container but keep data:

```bash
docker compose down
```

Stop container and delete database volume:

```bash
docker compose down -v
```

## Notes

- Application data persists in the Docker volume `orderbook_postgres_data`.
- `schema.sql` still creates the database tables and constraints on app startup.
- Tests use H2 in `src/test/resources/application.properties`, so `mvn test` can run without Docker.
