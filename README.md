# Movie Management API

This repository contains a simple Spring Boot API for storing and querying movies. The easiest way to run it is with Docker, but you can also run it locally with Gradle.

## Quick Start with Docker

1. Install **Docker** and **Docker Compose**.
2. Clone the repository:
   ```bash
   git clone https://github.com/Jalez/movie-management-api.git
   cd movie-management-api
   ```
3. Copy the example environment file and adjust the secrets:
   ```bash
   cp .env.example .env
   # edit DB_PASSWORD and ADMIN_PASSWORD
   ```
4. Build and start the containers:
   ```bash
   docker-compose up --build
   ```
5. Open <http://localhost:8080/swagger-ui.html> to explore the API.
6. Stop the services with:
   ```bash
   docker-compose down
   ```

## Running Locally without Docker

Requirements:
- Java 17+
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
   The API will be available at <http://localhost:8080>.

## Running Tests

Execute the test suite with:
```bash
./gradlew test
```

## License

This project is for educational purposes only. See [LICENSE](LICENSE) for details.
