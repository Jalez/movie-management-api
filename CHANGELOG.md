# Changelog

All notable changes to the Movie Management API project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Comprehensive documentation and README improvements
- Contributing guidelines (CONTRIBUTING.md)
- This changelog file

### Changed
- README reorganized to emphasize Docker deployment first
- Improved Docker deployment instructions and production guidance

### Fixed
- Java version compatibility (changed from 24 to 17)

## [0.1.0] - 2024-12-XX

### Added
- Initial Spring Boot 3.5.3 application setup
- PostgreSQL 17.5 database integration with Flyway migrations
- Complete Movie entity with JPA annotations
- CRUD REST API endpoints for movie management
- Movie search functionality with multiple criteria
- Comprehensive validation and error handling
- Docker containerization with docker-compose
- Interactive Swagger/OpenAPI documentation
- Extensive test suite (unit and integration tests)
- Spring Boot Actuator for health monitoring
- Sample movie dataset for testing
- Environment-based configuration

### Technical Details

#### Core Features
- **Movie CRUD Operations**: Create, read, update, delete movies
- **Advanced Search**: Filter by genre, director, release year, and minimum rating
- **Data Validation**: Comprehensive input validation with detailed error messages
- **Error Handling**: Global exception handler with consistent error responses

#### API Endpoints
- `GET /movies` - Retrieve all movies
- `GET /movies/{id}` - Get specific movie by ID
- `POST /movies` - Create new movie
- `PUT /movies/{id}` - Update existing movie
- `DELETE /movies/{id}` - Delete movie
- `GET /movies/search` - Search movies with criteria
- `GET /actuator/health` - Application health check
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

#### Technology Stack
- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: PostgreSQL 17.5 with Flyway migrations
- **Build Tool**: Gradle 8.14.2
- **Testing**: JUnit 5, H2 in-memory database for tests
- **Documentation**: Swagger/OpenAPI 3.x
- **Containerization**: Docker with Alpine Linux
- **Monitoring**: Spring Boot Actuator

#### Database Schema
```sql
CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    release_year INTEGER NOT NULL,
    director VARCHAR(255) NOT NULL,
    rating DECIMAL(3,1) NOT NULL CHECK (rating >= 0.0 AND rating <= 10.0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Validation Rules
- **Title**: Required, max 255 characters, cannot be blank
- **Director**: Required, max 255 characters, cannot be blank
- **Genre**: Required, max 100 characters, cannot be blank
- **Release Year**: Required, 1888-2100 range
- **Rating**: Required, 0.0-10.0 decimal range

#### Sample Data
Pre-loaded with 39 diverse movies spanning:
- **Time Periods**: 1941-2025
- **Genres**: Sci-Fi, Action, Crime, Romance, Horror, Animation, Drama, Comedy
- **Rating Range**: 0.0-10.0 (full spectrum)
- **Notable Directors**: Christopher Nolan, Quentin Tarantino, Martin Scorsese, etc.

#### Docker Configuration
- **Multi-stage build** for optimized image size
- **Alpine Linux** base for minimal footprint
- **Health checks** for application and database
- **Environment variable** configuration
- **Volume persistence** for database data
- **Network isolation** with custom bridge network

#### Testing Coverage
- **Unit Tests**: Service layer business logic
- **Integration Tests**: End-to-end API testing with H2 database
- **Test Coverage**: 80%+ minimum requirement
- **JaCoCo Reports**: Automated coverage reporting
- **Manual Testing**: Comprehensive curl examples and Swagger UI

#### Development Features
- **Hot Reload**: Development environment with file watching
- **Database Migrations**: Flyway for schema versioning
- **Code Quality**: JaCoCo coverage reporting
- **API Documentation**: Interactive Swagger UI at `/swagger-ui.html`
- **Health Monitoring**: Actuator endpoints for operational insights

### Dependencies Implemented

#### Core Dependencies
- `spring-boot-starter-web` - REST API framework
- `spring-boot-starter-data-jpa` - Database access layer
- `spring-boot-starter-validation` - Input validation
- `spring-boot-starter-actuator` - Health monitoring
- `postgresql` - PostgreSQL database driver
- `flyway-core` - Database migration tool
- `springdoc-openapi-starter-webmvc-ui` - Swagger documentation

#### Development Dependencies
- `spring-boot-starter-test` - Testing framework
- `h2database` - In-memory database for tests
- `jacoco` - Code coverage reporting

### Issues Resolved

- **Issue #1**: Project initialization and setup
- **Issue #2**: Database configuration and migrations
- **Issue #3**: Movie entity and repository implementation
- **Issue #4**: Service layer business logic
- **Issue #5**: REST API controller with validation
- **Issue #6**: Advanced search functionality
- **Issue #7**: Comprehensive integration testing
- **Issue #8**: API documentation and manual testing
- **Issue #9**: Docker containerization and deployment
- **Issue #10**: Documentation and README improvements

### Breaking Changes
- None (initial release)

### Deprecated
- None (initial release)

### Removed
- None (initial release)

### Security
- Input validation prevents SQL injection
- Environment variable configuration for sensitive data
- Database user with limited privileges
- Health check endpoints for monitoring

---

## Release Schedule

This project follows semantic versioning:
- **MAJOR** version for incompatible API changes
- **MINOR** version for backwards-compatible functionality additions
- **PATCH** version for backwards-compatible bug fixes

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for information on how to contribute to this project.

## License

This project is for educational purposes. See the [LICENSE](LICENSE) file for details.