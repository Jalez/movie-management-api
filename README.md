# Movie Management API

A REST API for managing movies built with Spring Boot, PostgreSQL, and Gradle.

## 🚀 Current Status

**Completed Components:**
- ✅ Spring Boot 3.5.3 application setup
- ✅ PostgreSQL 17.5 database configuration
- ✅ Flyway database migrations
- ✅ Basic project structure with Gradle
- ✅ Health monitoring with Spring Boot Actuator
- ✅ Test environment with H2 in-memory database

**Currently Implementing:**
- 🔄 Movie entity and repository layer (Issue 3)

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

- **Java 17 or higher** (tested with Java 24)
- **PostgreSQL 17.5** (installed via Homebrew)
- **Gradle** (wrapper included, or install via Homebrew)

## 🛠️ Installation & Setup

### 1. Install Dependencies

```bash
# Install Java (if not already installed)
brew install openjdk

# Install PostgreSQL
brew install postgresql@17

# Install Gradle (optional, wrapper included)
brew install gradle
```

### 2. Database Setup

```bash
# Start PostgreSQL service
brew services start postgresql@17

# Add PostgreSQL to PATH (for current session)
export PATH="/opt/homebrew/opt/postgresql@17/bin:$PATH"

# Create database and user (if not already done)
createdb moviedb
psql moviedb -c "CREATE USER movieuser WITH PASSWORD 'moviepass';"
psql moviedb -c "GRANT ALL PRIVILEGES ON DATABASE moviedb TO movieuser;"
psql moviedb -c "GRANT ALL ON SCHEMA public TO movieuser;"
```

### 3. Environment Variables

Set up your environment:

```bash
# Set Java Home (for current session)
export JAVA_HOME=/opt/homebrew/Cellar/openjdk/24.0.1/libexec/openjdk.jdk/Contents/Home

# Add PostgreSQL to PATH (for current session)
export PATH="/opt/homebrew/opt/postgresql@17/bin:$PATH"
```

For permanent setup, add these to your `~/.zshrc`:

```bash
echo 'export JAVA_HOME=/opt/homebrew/Cellar/openjdk/24.0.1/libexec/openjdk.jdk/Contents/Home' >> ~/.zshrc
echo 'export PATH="/opt/homebrew/opt/postgresql@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

## 🏃‍♂️ Running the Application

### Using Gradle Wrapper (Recommended)

```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun
```

### Using Gradle (if installed globally)

```bash
# Build the application
gradle build

# Run the application
gradle bootRun
```

## 🧪 Testing

### Run All Tests

```bash
# Run unit and integration tests
./gradlew test

# Run tests with detailed output
./gradlew test --info
```

### Test Coverage

```bash
# Generate test report
./gradlew test
# Reports available at: build/reports/tests/test/index.html
```

## 🔍 Monitoring & Health Checks

Once the application is running, you can access:

- **Application:** http://localhost:8080  - the main entry point (not implemented yet)
- **Health Check:** http://localhost:8080/actuator/health - the health status of the application (returns `{"status":"UP"}`)
- **Application Info:** http://localhost:8080/actuator/info - information about the application (e.g., version, build time)
- **Metrics:** http://localhost:8080/actuator/metrics - Application metrics (e.g., memory usage, request counts)

### Health Check Response
```json
{"status":"UP"}
```

## 🗄️ Database

### Current Schema

The application uses PostgreSQL with the following main table:

```sql
CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    release_year INTEGER NOT NULL,
    director VARCHAR(255) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Sample Data

The database is initialized with sample movies:
- Inception (2010)
- Interstellar (2014)
- The Dark Knight (2008)
- Pulp Fiction (1994)
- The Matrix (1999)

### Database Access

```bash
# Connect to database directly
psql moviedb -U movieuser

# View tables
\dt

# View movies
SELECT * FROM movies;
```

## 🔧 Configuration

### Application Properties

**Production (`src/main/resources/application.properties`):**
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/moviedb
spring.datasource.username=movieuser
spring.datasource.password=moviepass

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# Server Configuration
server.port=8080
```

**Testing (`src/test/resources/application.properties`):**
```properties
# H2 In-Memory Database for Tests
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

## 🐛 Troubleshooting

### Common Issues

**1. Database Connection Failed**
```bash
# Check if PostgreSQL is running
brew services list | grep postgresql

# Start PostgreSQL if stopped
brew services start postgresql@17
```

**2. Java Version Issues**
```bash
# Check Java version
java -version

# Verify JAVA_HOME
echo $JAVA_HOME
```

**3. Port Already in Use**
```bash
# Check what's using port 8080
lsof -i :8080

# Kill process if needed
kill -9 <PID>
```

**4. Permission Denied**
```bash
# Make gradlew executable
chmod +x gradlew
```

### Logs

Application logs are available at:
- Console output when running `./gradlew bootRun`
- Application logs in `app.log` (if run in background)

## 📚 API Documentation

### Current Endpoints

**Health & Monitoring:**
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

**Planned Endpoints (Coming in Issues 3-6):**
- `GET /movies` - Get all movies
- `GET /movies/{id}` - Get movie by ID
- `POST /movies` - Create new movie
- `PUT /movies/{id}` - Update movie
- `DELETE /movies/{id}` - Delete movie
- `GET /movies/search` - Search movies with filters

## 🚧 Development Status

### Completed (Issues 1-2)
- [x] Spring Boot project initialization
- [x] PostgreSQL database setup
- [x] Flyway migrations
- [x] Basic application configuration
- [x] Test environment setup

### In Progress (Issue 3)
- [ ] Movie JPA entity
- [ ] Movie repository interface
- [ ] Repository unit tests

### Upcoming (Issues 4-14)
- [ ] Service layer implementation
- [ ] REST controller endpoints
- [ ] Integration tests
- [ ] Docker configuration
- [ ] API documentation with Swagger

## 🤝 Contributing

This is a learning project following a structured approach with GitHub issues. Each issue represents a complete feature implementation.

## 📄 License

This project is for educational purposes.
