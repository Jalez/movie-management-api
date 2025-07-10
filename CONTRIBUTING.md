# Contributing to Movie Management API

We welcome contributions to the Movie Management API! This document provides guidelines for contributing to the project.

## 🚀 Quick Start for Contributors

### Prerequisites

- **Docker & Docker Compose** (recommended for development)
- **Java 17 or higher** (if running locally)
- **Git** for version control

### Setting Up Development Environment

1. **Fork and Clone the Repository**
```bash
git clone https://github.com/YOUR_USERNAME/movie-management-api.git
cd movie-management-api
```

2. **Set Up Environment Variables**
```bash
cp .env.example .env
# Edit .env with your development values
```

3. **Start Development Environment with Docker**
```bash
docker-compose up --build
```

4. **Verify Setup**
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

## 🔧 Development Workflow

### Branch Strategy

- `main` - Production-ready code
- `develop` - Integration branch for features
- `feature/issue-X-description` - Feature branches
- `bugfix/issue-X-description` - Bug fix branches

### Making Changes

1. **Create a Feature Branch**
```bash
git checkout -b feature/issue-X-description
```

2. **Make Your Changes**
   - Follow existing code style and patterns
   - Add tests for new functionality
   - Update documentation as needed

3. **Test Your Changes**
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.movieapi.controller.MovieControllerTest"

# Check test coverage
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

4. **Build and Verify**
```bash
# Full build with tests
./gradlew clean build

# Test with Docker
docker-compose down
docker-compose up --build
```

## 📝 Code Standards

### Java Code Style

- **Java 17+ features** are encouraged
- **Spring Boot conventions** should be followed
- **Clear, descriptive method and variable names**
- **Javadoc for public methods** in service and controller classes

### Example Controller Method
```java
/**
 * Retrieves a movie by its ID.
 *
 * @param id the movie ID
 * @return the movie with the specified ID
 * @throws MovieNotFoundException if no movie exists with the given ID
 */
@GetMapping("/{id}")
public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
    Movie movie = movieService.findById(id);
    return ResponseEntity.ok(movie);
}
```

### Database Changes

- **Use Flyway migrations** for schema changes
- **Create migration files** in `src/main/resources/db/migration/`
- **Follow naming convention**: `V{version}__{description}.sql`

### Testing Standards

- **Unit tests** for service layer business logic
- **Integration tests** for controller endpoints
- **Test coverage** should be maintained above 80%
- **Use meaningful test names** that describe the scenario

### Example Test
```java
@Test
void shouldReturnMovieWhenValidIdProvided() {
    // Given
    Long movieId = 1L;
    Movie expectedMovie = createTestMovie();
    when(movieRepository.findById(movieId)).thenReturn(Optional.of(expectedMovie));
    
    // When
    Movie actualMovie = movieService.findById(movieId);
    
    // Then
    assertThat(actualMovie).isEqualTo(expectedMovie);
}
```

## 🐛 Bug Reports

When reporting bugs, please include:

1. **Clear description** of the issue
2. **Steps to reproduce** the problem
3. **Expected vs actual behavior**
4. **Environment details** (OS, Java version, Docker version)
5. **Relevant logs** or error messages

### Bug Report Template
```markdown
**Bug Description**
A clear description of what the bug is.

**To Reproduce**
1. Run command X
2. Access endpoint Y
3. See error

**Expected Behavior**
What you expected to happen.

**Environment**
- OS: [e.g., macOS 14.0]
- Java: [e.g., 17.0.8]
- Docker: [e.g., 24.0.6]

**Additional Context**
Add any other context about the problem here.
```

## ✨ Feature Requests

Before submitting a feature request:

1. **Check existing issues** to avoid duplicates
2. **Consider the scope** - is this aligned with project goals?
3. **Provide clear use cases** and examples

### Feature Request Template
```markdown
**Feature Description**
A clear description of the feature you'd like to see.

**Use Case**
Why would this feature be useful? Provide specific examples.

**Proposed Implementation**
If you have ideas about how this could be implemented.

**Alternatives Considered**
Other solutions you've considered.
```

## 🔍 Pull Request Process

### Before Submitting

- [ ] Code follows project style guidelines
- [ ] Tests pass locally (`./gradlew test`)
- [ ] New functionality includes tests
- [ ] Documentation is updated if needed
- [ ] Commit messages are clear and descriptive

### Pull Request Template

```markdown
**Description**
Brief description of changes made.

**Type of Change**
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

**Testing**
- [ ] Tests pass locally
- [ ] New tests added for new functionality
- [ ] Manual testing completed

**Checklist**
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes to existing API
```

### Review Process

1. **Automated checks** must pass (GitHub Actions)
2. **Code review** by project maintainers
3. **Testing** in development environment
4. **Documentation review** if applicable

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/movieapi/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access
│   │   ├── entity/         # JPA entities
│   │   ├── dto/            # Data transfer objects
│   │   ├── exception/      # Custom exceptions
│   │   └── config/         # Configuration classes
│   └── resources/
│       ├── db/migration/   # Flyway migrations
│       └── application.properties
└── test/
    └── java/com/movieapi/  # Mirror main structure
```

## 🧪 Testing Guidelines

### Test Categories

1. **Unit Tests** (`*Test.java`)
   - Test individual components in isolation
   - Mock external dependencies
   - Fast execution

2. **Integration Tests** (`*IntegrationTest.java`)
   - Test complete request/response cycles
   - Use test database (H2)
   - Test API endpoints end-to-end

### Running Tests

```bash
# All tests
./gradlew test

# Unit tests only
./gradlew test --tests "*Test"

# Integration tests only
./gradlew test --tests "*IntegrationTest"

# Specific test class
./gradlew test --tests "MovieControllerTest"

# With detailed output
./gradlew test --info

# Generate coverage report
./gradlew jacocoTestReport
```

## 📚 Documentation Guidelines

### API Documentation

- Use **OpenAPI/Swagger annotations** for endpoint documentation
- Include **request/response examples**
- Document **error scenarios** and status codes
- Keep **Swagger UI** up to date

### Code Documentation

- **Javadoc** for public methods and classes
- **Inline comments** for complex business logic
- **README updates** for new features or changes

## 🚢 Release Process

1. **Feature branches** are merged to `develop`
2. **Release candidates** are created from `develop`
3. **Testing and validation** in staging environment
4. **Merge to main** for production release
5. **Tag release** with semantic versioning

## 🤝 Code of Conduct

### Our Standards

- **Be respectful** and inclusive
- **Constructive feedback** only
- **Collaborative approach** to problem-solving
- **Professional communication** in all interactions

### Unacceptable Behavior

- Harassment or discrimination
- Trolling or insulting comments
- Publishing private information
- Other unprofessional conduct

## 📞 Getting Help

- **GitHub Issues** for bugs and feature requests
- **GitHub Discussions** for questions and ideas
- **Code reviews** for technical feedback

## 🏷️ Issue Labels

- `bug` - Something isn't working
- `enhancement` - New feature or request
- `documentation` - Documentation needs
- `good first issue` - Good for newcomers
- `help wanted` - Extra attention is needed

Thank you for contributing to Movie Management API! 🎬