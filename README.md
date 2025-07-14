# Movie Management API

A REST API for managing movies built with Spring Boot, PostgreSQL, and Gradle.

## 🐳 Quick Start with Docker (Recommended)

The easiest way to run this application is using Docker Compose:

### Prerequisites for Docker
- **Docker** (20.10+)
- **Docker Compose** (v2.0+)

### Getting Started

0. **Install Docker and Docker Compose:**
   - Follow the [official Docker installation guide](https://docs.docker.com/get-docker/) for your OS.
   - Docker Compose is included with Docker Desktop installations.
   - For Linux, install Docker Compose separately if needed (ensure it's v2.0+):
     ```bash
     sudo apt-get install docker-compose-plugin
     ```
   - Verify installation:
      ```bash
      docker-compose --version
      ``` 

1. **Clone the repository:**
```bash
git clone https://github.com/Jalez/movie-management-api.git
cd movie-management-api
```

2. **Set up environment variables:**
```bash
# Copy the example environment file
cp .env.example .env

# Edit the .env file with your secure values
nano .env  # or use your preferred editor
```

3. **Start the application:**
```bash
# Build and start all services
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

4. **Access the application:**
- **API Base URL:** http://localhost:8080
- **Swagger Documentation:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health

5. **Stop the application:**
```bash
docker-compose down
```

### Environment Configuration

**Required variables in `.env`:**
```ini
# Database credentials (required)
DB_PASSWORD=your-secure-password

# Application security (required)
ADMIN_PASSWORD=your-secure-admin-password
```

**Optional variables:**
```ini
# JVM tuning (optional)
JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC

# Application settings (optional)
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

**Note:** The `.env` file is git-ignored for security. Never commit sensitive credentials to version control.

## 📋 Manual Setup (Alternative)

If you prefer to run the application locally without Docker:

### Prerequisites for Manual Setup

- **Java 17 or higher** (tested with Java 21/24)
- **PostgreSQL 17.5** (installed via Homebrew)
- **Gradle** (wrapper included, or install via Homebrew)

### 1. Install Dependencies for Manual Setup

```bash
# Install Java (if not already installed)
brew install openjdk

# Install PostgreSQL
brew install postgresql@17

# Install Gradle (optional, wrapper included)
brew install gradle
```

### 2. Database Setup for Manual Installation

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

### 3. Environment Variables for Manual Setup

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

### Using Docker (Recommended)

See the [Quick Start with Docker](#-quick-start-with-docker-recommended) section above.

### Using Gradle Wrapper (Manual Setup)

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

- **Application:** http://localhost:8080  - the main entry point
- **Health Check:** http://localhost:8080/actuator/health - the health status of the application (returns `{"status":"UP"}`)
- **Application Info:** http://localhost:8080/actuator/info - information about the application (e.g., version, build time)
- **Metrics:** http://localhost:8080/actuator/metrics - Application metrics (e.g., memory usage, request counts)
- **Swagger API Documentation:** http://localhost:8080/swagger-ui.html - Interactive API documentation

## 🚀 CI/CD Pipeline

This project includes a comprehensive CI/CD pipeline with the following features:

### Quality Checks
- **Tests:** Unit and integration tests with JUnit 5
- **Code Coverage:** JaCoCo with 80% minimum coverage requirement
- **Code Quality:** Checkstyle for code style enforcement
- **Security:** SpotBugs for static analysis
- **Dependency Security:** OWASP Dependency Check for vulnerability scanning

### Pipeline Stages
1. **Build & Test:** Compiles code and runs all tests
2. **Quality Checks:** Runs code quality and security tools
3. **Docker Build:** Creates and tests Docker image
4. **Security Scan:** Runs Trivy vulnerability scanner
5. **Notification:** Reports pipeline status

### OWASP Dependency Check Note
The OWASP Dependency Check may fail with a 403 error when accessing the NVD (National Vulnerability Database) API. This is a known issue that can be resolved by:

1. **Getting a free NVD API key** from https://nvd.nist.gov/developers/request-an-api-key
2. **The dependency check is configured to continue on error** in the CI pipeline, so it won't block builds

For production use, it's recommended to obtain an NVD API key and configure it in your CI environment variables.

### Docker Container Monitoring

When running with Docker, you can also monitor containers:

```bash
# View running containers
docker-compose ps

# View logs
docker-compose logs -f app    # Application logs
docker-compose logs -f db     # Database logs

# View all logs
docker-compose logs -f

# Execute commands in containers
docker-compose exec db psql -U movieuser -d moviedb
docker-compose exec app sh    # Shell into app container
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

### Docker Issues

**1. Container Build Failed**
```bash
# Clean up containers and rebuild
docker-compose down -v
docker system prune -f
docker-compose up --build

# Check Docker disk space
docker system df
```

**2. Database Connection Issues in Docker**
```bash
# Check if all services are running
docker-compose ps

# Check database logs
docker-compose logs db

# Restart database service
docker-compose restart db
```


**3. Environment Variables Not Loading**
```bash
# Verify .env file exists and has correct format
cat .env

# Restart with explicit env file
docker-compose --env-file .env up --build
```

**4. Container Memory Issues**
```bash
# Check container resource usage
docker stats

# Add memory limits to docker-compose.yml:
# services:
#   app:
#     mem_limit: 1g
```

### Common Issues (Manual Setup)

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

### Swagger/OpenAPI Documentation

This API includes interactive Swagger UI documentation for easy testing and exploration:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

The Swagger UI provides:
- Interactive API explorer with "Try it out" functionality
- Complete request/response schemas
- Sample data and examples
- Error response documentation
- Authentication support (if implemented)

### Sample Test Data

The application comes pre-loaded with diverse sample data for testing:

#### Classic Movies (1940s-1970s)
- Citizen Kane (1941) - Drama, Orson Welles, 8.3
- Casablanca (1942) - Romance, Michael Curtiz, 8.5
- Seven Samurai (1954) - Action, Akira Kurosawa, 8.6
- 2001: A Space Odyssey (1968) - Sci-Fi, Stanley Kubrick, 8.3
- The Godfather (1972) - Crime, Francis Ford Coppola, 9.2

#### Modern Blockbusters (1990s-2020s)
- Pulp Fiction (1994) - Crime, Quentin Tarantino, 8.9
- The Matrix (1999) - Sci-Fi, Lana Wachowski, Lilly Wachowski, 8.7
- The Dark Knight (2008) - Action, Christopher Nolan, 9.0
- Inception (2010) - Sci-Fi, Christopher Nolan, 8.8
- Parasite (2019) - Thriller, Bong Joon-ho, 8.6

#### Animated Films
- The Lion King (1994) - Animation, Roger Allers, Rob Minkoff, 8.5
- Finding Nemo (2003) - Animation, Andrew Stanton, 8.2
- Spider-Man: Into the Spider-Verse (2018) - Animation, Bob Persichetti, 8.4

#### Genre Variety
- **Sci-Fi**: Inception, Interstellar, The Matrix, 2001: A Space Odyssey
- **Action**: The Dark Knight, The Avengers, Seven Samurai
- **Crime**: Pulp Fiction, The Godfather, Goodfellas
- **Horror**: The Shining
- **Comedy**: Barbie, Everything Everywhere All at Once
- **Romance**: Casablanca, Titanic
- **Animation**: Finding Nemo, The Lion King

### Manual Testing Guide

### HTTP Status Codes

| Code | Description | When |
|------|-------------|------|
| 200 | OK | Successful GET, PUT operations |
| 201 | Created | Successful POST operation |
| 204 | No Content | Successful DELETE operation |
| 400 | Bad Request | Validation errors, invalid data types |
| 404 | Not Found | Movie with specified ID doesn't exist |
| 409 | Conflict | Duplicate movie (title + director combination) |
| 415 | Unsupported Media Type | Missing or wrong Content-Type header |
| 500 | Internal Server Error | Unexpected server errors |

### Validation Rules

**Movie Fields:**
- **title**: Required, max 255 characters, cannot be blank
- **director**: Required, max 255 characters, cannot be blank  
- **genre**: Required, max 100 characters, cannot be blank
- **releaseYear**: Required, must be 1888 or later, cannot be more than year 2100
- **rating**: Required, decimal between 0.0 and 10.0, max 1 decimal place

**Search Parameters:**
- **genre**: Optional, cannot be empty if provided
- **releaseYear**: Optional, must be 1900 or later if provided
- **minRating**: Optional, must be 0.0-10.0 if provided
- **director**: Optional, cannot be empty if provided

### Performance Testing

Test with larger datasets and concurrent requests:

```bash
# Test with multiple concurrent requests
for i in {1..10}; do
  curl -X GET "http://localhost:8080/movies" &
done
wait

# Test search performance
time curl -X GET "http://localhost:8080/movies/search?minRating=8.0"
```

**Health & Monitoring:**
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

**Movie Management API:**
- `GET /movies` - Get all movies
- `GET /movies/{id}` - Get movie by ID (404 if not found)
- `POST /movies` - Create new movie (201 Created, 400 on validation error, 409 on duplicate)
- `PUT /movies/{id}` - Update movie (200 OK, 404 if not found, 409 on duplicate)
- `DELETE /movies/{id}` - Delete movie (204 No Content, 404 if not found)

- `GET /movies/search` - Search for movies by criteria (see below)


### Movie Search Endpoint

**Endpoint:** `GET /movies/search`

**Description:**
Search for movies using one or more optional query parameters. All parameters are optional and can be combined. Returns a list of movies matching all provided criteria.

### Pagination Support

All search endpoints (including `/movies/search`) support pagination using the following query parameters:

| Parameter | Type    | Description                                  |
|-----------|---------|----------------------------------------------|
| page      | Integer | Page number (0-based, e.g., 0 for first page)|
| size      | Integer | Number of results per page (default: 20)     |

**Example:**

Get the first page of 5 movies:
```bash
curl -X GET "http://localhost:8080/movies/search?page=0&size=5"
```

Get the second page of 10 movies:
```bash
curl -X GET "http://localhost:8080/movies/search?page=1&size=10"
```



**Notes:**
- If `page` or `size` are not provided, defaults are used.
- If no results match, `content` will be an empty array.
- Pagination is zero-based (`page=0` is the first page).
**Query Parameters:**

| Parameter    | Type     | Description                                                      |
|--------------|----------|------------------------------------------------------------------|
| genre        | String   | Filter by genre (case-sensitive, exact match)                    |
| releaseYear  | Integer  | Filter by release year (e.g., 2010)                              |
| minRating    | Decimal  | Filter by minimum rating (inclusive, e.g., 8.5)                  |
| director     | String   | Filter by director (case-insensitive, partial match allowed)     |

**Sample Data Coverage:**
- 🎬 **39 movies** across **multiple decades** (1941-2025)
- 🎭 **10+ genres**: Sci-Fi, Action, Crime, Romance, Horror, Animation, Drama, Comedy, etc.
- ⭐ **Rating range**: 0.0 to 10.0 (full spectrum for testing)
- 🎯 **Edge cases**: Long titles, special characters, boundary values
- �‍🎬 **Famous directors**: Christopher Nolan, Quentin Tarantino, Martin Scorsese, etc.

**Testing Capabilities:**
- **Interactive Testing**: Use Swagger UI for point-and-click API testing
- **Automated Testing**: Export OpenAPI spec for Postman collections
- **Manual Testing**: Complete curl command examples provided
- **Error Testing**: Validation errors, not found, conflicts, malformed data
- **Performance Testing**: Concurrent request examples

**API Documentation Features:**
- 📋 **Complete endpoint documentation** with OpenAPI 3.x annotations
- 🔄 **Request/response schemas** with validation rules
- 🚨 **Error response formats** with field-level validation details
- 📊 **HTTP status codes** properly documented
- 🧪 **Try-it-out functionality** in Swagger UI
- 📖 **Comprehensive examples** for all endpoints

**Quick Test Commands:**
```bash
# Get all movies
curl http://localhost:8080/movies

# Search Sci-Fi movies with rating ≥ 8.5
curl "http://localhost:8080/movies/search?genre=Sci-Fi&minRating=8.5"

# Test error handling
curl http://localhost:8080/movies/999

# View API documentation
open http://localhost:8080/swagger-ui.html
```

**Next Steps:**
- Use Swagger UI for comprehensive manual testing
- Export OpenAPI spec as Postman collection alternative
- All CRUD and search operations ready for production use

---


## 🚀 Advanced Docker Configuration

### Available Environment Variables

The following environment variables can be configured:

**Application:**
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password (use DB_PASSWORD in .env)
- `SPRING_JPA_HIBERNATE_DDL_AUTO` - Hibernate DDL mode
- `SPRING_JPA_SHOW_SQL` - SQL logging
- `SPRING_FLYWAY_ENABLED` - Flyway migration status
- `SPRING_FLYWAY_BASELINE_ON_MIGRATE` - Flyway baseline behavior
- `JAVA_OPTS` - JVM configuration options
- `SPRING_PROFILES_ACTIVE` - Active Spring profile
- `SERVER_TOMCAT_MAX_THREADS` - Maximum worker threads
- `SERVER_TOMCAT_MIN_SPARE_THREADS` - Minimum spare threads

**Database:**
- `DB_NAME` - Database name
- `DB_USER` - Database user
- `DB_PASSWORD` - Database password (use DB_PASSWORD in .env)

**Security:**
- `ADMIN_PASSWORD` - Admin user password
- `SPRING_SECURITY_USER_NAME` - Admin username (defaults to 'admin')

### Development Tips with Docker

1. **Environment Setup:**
```bash
# Development setup
cp .env.example .env
nano .env  # Set your development variables

# Production setup
cp .env.example .env.prod
nano .env.prod  # Set your production variables
```

2. **Running Different Environments:**
```bash
# Development (uses docker-compose.yml)
docker-compose --env-file .env up --build

# Production (uses docker-compose.prod.yml)
docker-compose -f docker-compose.prod.yml --env-file .env.prod up --build
```

3. **Rebuild and Restart Services:**
```bash
# Rebuild a single service
docker-compose up --build app

# Restart with updated environment
docker-compose down
docker-compose --env-file .env up -d
```

4. **Clean up:**
```bash
# Stop and remove containers
docker-compose down

# Remove containers and volumes (careful in production!)
docker-compose down -v

# Clean up unused resources
docker system prune
```

### Production Deployment Notes

For production deployment:
1. Use appropriate environment variables
2. Consider using Docker secrets for sensitive data
3. Configure proper memory limits
4. Set up monitoring and logging
5. Use production-grade PostgreSQL configuration
6. Enable automatic health checks and recovery

## 🔄 CI/CD Pipeline

This project includes a comprehensive Continuous Integration and Continuous Deployment (CI/CD) pipeline using GitHub Actions to ensure code quality, reliability, and automated testing.

### 🚀 Pipeline Overview

The CI/CD pipeline runs automatically on:
- **Push** to `main` and `develop` branches
- **Pull Requests** to `main` and `develop` branches

### 📋 Pipeline Stages

#### 1. Build and Test
- **Multi-JDK Testing**: Tests against Java 17, 21, and 24
- **Gradle Build**: Compiles the project and runs all tests
- **Test Execution**: Unit tests, integration tests, and H2 database tests
- **Code Coverage**: JaCoCo coverage reports with 80% minimum requirement
- **Artifact Upload**: Test results and coverage reports are archived

#### 2. Quality Checks
- **Checkstyle**: Code style and formatting validation
- **SpotBugs**: Static analysis for potential bugs and security issues
- **OWASP Dependency Check**: Security vulnerability scanning
- **FindSecBugs**: Security-focused static analysis

#### 3. Docker Build (Push Events Only)
- **Docker Image Build**: Creates production-ready Docker image
- **Container Testing**: Validates Docker image functionality
- **Health Check**: Ensures application starts correctly in container

#### 4. Security Scanning
- **Trivy Vulnerability Scanner**: Comprehensive security analysis
- **GitHub Security Tab**: Results uploaded to GitHub Security tab
- **SARIF Reports**: Standardized security report format

### 📊 Quality Metrics

The pipeline enforces the following quality standards:

| Metric | Requirement | Tool |
|--------|-------------|------|
| **Code Coverage** | ≥80% overall, ≥75% per class | JaCoCo |
| **Code Style** | Checkstyle compliance | Checkstyle |
| **Static Analysis** | No high/critical issues | SpotBugs |
| **Security Vulnerabilities** | CVSS < 7.0 | OWASP Dependency Check |
| **Build Success** | All tests pass | Gradle |

### 🔧 Configuration Files

The CI/CD pipeline uses the following configuration files:

- **`.github/workflows/ci.yml`** - Main GitHub Actions workflow
- **`config/checkstyle/checkstyle.xml`** - Code style rules
- **`config/spotbugs/exclude.xml`** - Static analysis exclusions
- **`config/dependency-check/suppressions.xml`** - Security scan suppressions

### 📈 Pipeline Reports

After each pipeline run, the following artifacts are available:

#### Test Reports
- **Test Results**: `build/reports/tests/` - Detailed test execution results
- **Coverage Reports**: `build/reports/jacoco/` - Code coverage analysis
- **Build Artifacts**: `build/libs/` - Compiled JAR files

#### Quality Reports
- **Checkstyle Reports**: `build/reports/checkstyle/` - Code style violations
- **SpotBugs Reports**: `build/reports/spotbugs/` - Static analysis results
- **Security Reports**: `build/reports/dependency-check/` - Vulnerability scan results

### 🚨 Pipeline Failure Handling

The pipeline will fail if:
- ❌ Any tests fail
- ❌ Code coverage drops below 80%
- ❌ Checkstyle violations are found
- ❌ Critical security vulnerabilities are detected
- ❌ Build compilation fails

### 🔍 Local Quality Checks

You can run quality checks locally before pushing:

```bash
# Run all quality checks
./gradlew check

# Run specific checks
./gradlew checkstyleMain checkstyleTest
./gradlew spotbugsMain spotbugsTest
./gradlew dependencyCheckAnalyze
./gradlew jacocoTestCoverageVerification

# Generate reports
./gradlew jacocoTestReport
```

## 📄 License

This project is for educational purposes. See the [LICENSE](LICENSE) file for details.
