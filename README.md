# Movie Management API

This repository contains a simple Spring Boot API for storing and querying movies. The easiest way to run it is with Docker, but you can also run it locally with Gradle, though it may require additional setup.

## Quick Start with Docker

1. Install **Docker** and **Docker Compose**.
2. Clone the repository:
   ```bash
   git clone https://github.com/Jalez/movie-management-api.git
   cd movie-management-api
   ```
3. Copy the example environment file and adjust the secrets (or leave defaults if testing):
   ```bash
   cp .env.example .env
   # edit DB_PASSWORD and ADMIN_PASSWORD
   ```
4. Build and start the containers:
   ```bash
   docker compose up --build
   ```

  To run in detached mode, use:
   ```bash
   docker compose up --build -d
   ```

5. Open <http://localhost:8080/swagger-ui.html> to explore the API.
6. Stop the services with:
   ```bash
   docker compose down
   ```

   To remove all volumes and networks, use:
   ```bash
   docker compose down --volumes --remove-orphans
   ```

## Running Locally without Docker

Requirements:
- Java 21
- PostgreSQL (configured for user `movieuser` and database `moviedb`)
- (Optional) A global Gradle installation – the wrapper is included.

Steps:
1. Ensure PostgreSQL is running and create a database:
   ```bash
   createdb moviedb -U movieuser
   ```
2. Set the required environment variables (update with your values):
   ```bash
   export DB_PASSWORD=moviepass
   export ADMIN_PASSWORD=adminpass
   ```
3. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```

4. Open <http://localhost:8080/swagger-ui.html> to explore the API.

## Running Tests

Execute the test suite with:
```bash
./gradlew test
```


## Troubleshooting PostgreSQL Setup

If you encounter errors when creating or dropping the database, or connecting to PostgreSQL, try the following steps:

- **Permission denied to create database:**
  Grant the `CREATEDB` privilege to your database user (e.g., `movieuser`). Connect to PostgreSQL as a superuser (often your system username) and run:
  ```bash
  psql -U $(whoami) -d postgres
  # Then inside the psql prompt:
  ALTER USER movieuser CREATEDB;
  ```

- **Cannot connect to server or default database does not exist:**
  Connect to the `postgres` database instead of your username database:
  ```bash
  psql -U $(whoami) -d postgres
  ```

- **Drop database as correct user:**
  If you get an ownership error when dropping the database, use the owner or a superuser:
  ```bash
  dropdb moviedb -U $(whoami)
  ```

- **List roles and privileges:**
  ```bash
  psql -U <your_user> -d postgres -c "\du"
  ```

For more help, see the [PostgreSQL documentation](https://www.postgresql.org/docs/current/).


## License

This project is for educational purposes only. See [LICENSE](LICENSE) for details.
