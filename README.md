# Movie Management API

This repository contains a simple Spring Boot API for storing and querying movies. The easiest way to run it is with Docker, but you can also run it locally with Gradle, though it will require additional setup.

## 🚀 Quick Start

**Want to get started quickly?** Use our shell scripts (needs docker):

```bash
# Clone and setup
git clone https://github.com/Jalez/movie-management-api.git
cd movie-management-api

# Start everything with Docker and open Swagger UI
./run-dockerized-api.sh start
```

The script will automatically:
- Create `.env` file from `.env.example` (it will use the default values)
- Build and start Docker containers
- Wait for the API to be ready
- Open Swagger UI in your browser

**Need to run tests?** Use the test runner script:
```bash
./run-tests.sh test-docker  # Runs tests with database. BEWARE: Removes containers at the end of run.
```


## Running Locally without Docker

### Prerequisites

- **Java 21** - Required for running the application
- **PostgreSQL 17.5+** - Database server (see setup instructions below)
- **Gradle** - Build tool (optional, wrapper included)

### Database Setup

⚠️ **Important**: You need PostgreSQL running for both the application and tests.

#### Option 1: Install PostgreSQL Locally

1. **Install PostgreSQL** on your system:
   - **macOS**: `brew install postgresql`
   - **Ubuntu/Debian**: `sudo apt-get install postgresql postgresql-contrib`
   - **Windows**: Download from [postgresql.org](https://www.postgresql.org/download/windows/)

2. **Start PostgreSQL service**:
   ```bash
   # macOS
   brew services start postgresql
   
   # Ubuntu/Debian
   sudo systemctl start postgresql
   ```

3. **Create database and user**:
   ```bash
   # Connect as postgres user
   sudo -u postgres psql
   
   # Create user and database
   CREATE USER movieuser WITH PASSWORD 'moviepass';
   CREATE DATABASE moviedb OWNER movieuser;
   GRANT ALL PRIVILEGES ON DATABASE moviedb TO movieuser;
   \q
   ```

#### Option 2: Use Docker for Database Only

If you prefer not to install PostgreSQL locally:

```bash
# Start only the database container
docker compose up db -d

# Set environment variables
export DB_PASSWORD=moviepass
export ADMIN_PASSWORD=adminpass

```

### Running the Application (once DB setup complete)

1. **Build and run**:
   ```bash
   ./gradlew bootRun
   ```

3. **Access the API**:
   - Open <http://localhost:8080/swagger-ui.html> to explore the API
   - API documentation available at <http://localhost:8080/v3/api-docs>

## Running Tests

### Quick Test Runner (Recommended)

This project includes a convenient test runner script that handles database setup automatically:

```bash
# Run tests with Docker (includes database) - RECOMMENDED
./run-tests.sh test-docker

# Run tests and generate coverage with Docker
./run-tests.sh test-coverage-docker

# Show all available commands
./run-tests.sh help
```

### Manual Test Execution

If you prefer to run tests manually, you have two options:

#### Option 1: With Local PostgreSQL (requires setup)
```bash
# Ensure PostgreSQL is running and accessible
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport
```

#### Option 2: With Docker Database
```bash
# Start the database container
docker compose up db -d

# Wait for database to be ready, then run tests
./gradlew test

# Stop the database when done
docker compose down
```

### Test Requirements

⚠️ **Important**: Tests require a PostgreSQL database to run. The test runner script automatically handles this for you.

- **Database**: PostgreSQL 17.5 or compatible
- **Database Name**: `moviedb`
- **Username**: `movieuser`
- **Password**: `moviepass` (default)

### Common Test Issues

**Problem**: Tests fail with "ApplicationContext failure threshold exceeded"
```
ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context
```

**Solution**: Use the Docker-based test commands:
```bash
./run-tests.sh test-docker
```

**Problem**: "Connection to server on socket failed: No such file or directory"
```
createdb: error: connection to server on socket "/tmp/.s.PGSQL.5432" failed: No such file or directory
```

**Solution**: PostgreSQL is not running. Use Docker commands or start PostgreSQL locally.

### Test Coverage

After running tests with coverage, view the report at:
```
build/reports/jacoco/test/html/index.html
```

## Troubleshooting

### PostgreSQL Setup Issues

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

### Test Runner Script Issues

**Problem**: Script is not executable
```bash
-bash: ./run-tests.sh: Permission denied
```

**Solution**: Make the script executable
```bash
chmod +x run-tests.sh
```

**Problem**: Docker not running
```bash
Error response from daemon: Cannot connect to the Docker daemon
```

**Solution**: Start Docker Desktop or Docker daemon

**Problem**: Port 5432 already in use
```bash
Error starting userland proxy: listen tcp 0.0.0.0:5432: bind: address already in use
```

**Solution**: Stop any existing PostgreSQL instances or change the port in `docker-compose.yml`

### Application Issues

**Problem**: Application fails to start with database connection error
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Solution**: Ensure PostgreSQL is running and accessible, or use Docker:
```bash
docker compose up db -d
```

**Problem**: Environment variables not set
```
java.lang.IllegalArgumentException: Required environment variable 'DB_PASSWORD' not set
```

**Solution**: Set the required environment variables:
```bash
export DB_PASSWORD=moviepass
export ADMIN_PASSWORD=adminpass
```



For more help, see the [PostgreSQL documentation](https://www.postgresql.org/docs/current/).


## License

This project is for educational purposes only. See [LICENSE](LICENSE) for details.
