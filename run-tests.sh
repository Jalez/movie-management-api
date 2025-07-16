#!/bin/bash

# Movie Management API Test Runner
# This script provides easy commands to run tests and generate reports

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to run tests
run_tests() {
    print_status "Running test suite..."
    ./gradlew test
    if [ $? -eq 0 ]; then
        print_success "Tests completed successfully!"
    else
        print_error "Tests failed!"
        exit 1
    fi
}

# Function to open coverage report
open_coverage_report() {
    local report_path="build/reports/jacoco/test/html/index.html"
    if [ -f "$report_path" ]; then
        print_status "Opening coverage report..."
        case "$(uname -s)" in
            Darwin*)    # macOS
                open "$report_path"
                ;;
            Linux*)     # Linux
                if command -v xdg-open > /dev/null; then
                    xdg-open "$report_path"
                elif command -v sensible-browser > /dev/null; then
                    sensible-browser "$report_path"
                else
                    print_warning "Could not automatically open report. Please open manually: $report_path"
                fi
                ;;
            CYGWIN*|MINGW32*|MSYS*|MINGW*)  # Windows
                start "$report_path"
                ;;
            *)
                print_warning "Could not automatically open report. Please open manually: $report_path"
                ;;
        esac
    else
        print_warning "Coverage report not found at: $report_path"
    fi
}

# Function to generate coverage report
generate_coverage() {
    print_status "Generating coverage report..."
    ./gradlew jacocoTestReport
    if [ $? -eq 0 ]; then
        print_success "Coverage report generated successfully!"
        print_status "Coverage report location: build/reports/jacoco/test/html/index.html"
        open_coverage_report
    else
        print_error "Failed to generate coverage report!"
        exit 1
    fi
}

# Function to run tests with coverage
run_tests_with_coverage() {
    print_status "Running tests with coverage..."
    ./gradlew test jacocoTestReport
    if [ $? -eq 0 ]; then
        print_success "Tests and coverage completed successfully!"
        print_status "Coverage report location: build/reports/jacoco/test/html/index.html"
        open_coverage_report
    else
        print_error "Tests or coverage failed!"
        exit 1
    fi
}

# Function to clean build
clean_build() {
    print_status "Cleaning build..."
    ./gradlew clean
    print_success "Build cleaned successfully!"
}

# Function to run tests with Docker (includes database)
run_tests_with_docker() {
    print_status "Running test suite with Docker (includes database)..."
    docker compose up db -d
    print_status "Waiting for database to be ready..."
    sleep 5
    ./gradlew test
    if [ $? -eq 0 ]; then
        print_success "Tests completed successfully!"
    else
        print_error "Tests failed!"
        docker compose down
        exit 1
    fi
    docker compose down
}

# Function to show help
show_help() {
    echo "Movie Management API Test Runner"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  test              Run the test suite (requires local database)"
    echo "  test-docker       Run the test suite with Docker (includes database)"
    echo "  coverage          Generate coverage report"
    echo "  test-coverage     Run tests and generate coverage report"
    echo "  test-coverage-docker Run tests and coverage with Docker"
    echo "  clean             Clean the build"
    echo "  help              Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 test           # Run tests only (requires local PostgreSQL)"
    echo "  $0 test-docker    # Run tests with Docker (recommended)"
    echo "  $0 coverage       # Generate coverage report"
    echo "  $0 test-coverage  # Run tests and generate coverage (requires local PostgreSQL)"
    echo "  $0 test-coverage-docker # Run tests and coverage with Docker"
    echo "  $0 clean          # Clean build directory"
    echo ""
    echo "Note: Use test-docker or test-coverage-docker if you don't have PostgreSQL running locally"
}

# Main script logic
case "${1:-help}" in
    "test")
        run_tests
        ;;
    "test-docker")
        run_tests_with_docker
        ;;
    "coverage")
        generate_coverage
        ;;
    "test-coverage")
        run_tests_with_coverage
        ;;
    "test-coverage-docker")
        print_status "Running tests and coverage with Docker..."
        docker compose up db -d
        print_status "Waiting for database to be ready..."
        sleep 5
        ./gradlew test jacocoTestReport
        if [ $? -eq 0 ]; then
            print_success "Tests and coverage completed successfully!"
            print_status "Coverage report location: build/reports/jacoco/test/html/index.html"
            open_coverage_report
        else
            print_error "Tests or coverage failed!"
            docker compose down
            exit 1
        fi
        docker compose down
        ;;
    "clean")
        clean_build
        ;;
    "help"|"-h"|"--help")
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac 