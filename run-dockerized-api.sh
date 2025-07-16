#!/bin/bash

# Movie Management API Docker Runner
# This script provides easy commands to run the API with Docker

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

# Function to open URL in browser
open_url() {
    local url="$1"
    print_status "Opening $url in browser..."
    case "$(uname -s)" in
        Darwin*)
            open "$url" ;;
        Linux*)
            xdg-open "$url" ;;
        CYGWIN*|MINGW32*|MSYS*|MINGW*)
            start "$url" ;;
        *)
            print_warning "Could not automatically open browser. Please open manually: $url" ;;
    esac
}

# Function to setup environment file
setup_env() {
    if [ ! -f ".env" ]; then
        if [ -f ".env.example" ]; then
            print_status "Creating .env file from .env.example..."
            cp .env.example .env
            print_success "Environment file created!"
        else
            print_warning "No .env.example found. You may need to create .env manually."
        fi
    else
        print_status "Environment file .env already exists, skipping creation."
    fi
}

# Function to start API with Docker
start_api() {
    print_status "Starting Movie Management API with Docker..."
    
    # Setup environment file
    setup_env
    
    # Build and start containers
    print_status "Building and starting containers..."
    docker compose up --build -d
    
    # Wait for API to be ready
    print_status "Waiting for API to be ready..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "API is ready!"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            print_warning "API may not be fully ready yet, but attempting to open anyway..."
            break
        fi
        
        print_status "Waiting for API... (attempt $attempt/$max_attempts)"
        sleep 2
        attempt=$((attempt + 1))
    done
    
    # Open Swagger UI
    open_url "http://localhost:8080/swagger-ui.html"
    
    print_success "Movie Management API is running!"
    print_status "API URL: http://localhost:8080"
    print_status "Swagger UI: http://localhost:8080/swagger-ui.html"
    print_status "API Docs: http://localhost:8080/v3/api-docs"
    print_status ""
    print_status "To stop the API, run: $0 stop"
    print_status "To view logs, run: $0 logs"
}

# Function to stop API
stop_api() {
    print_status "Stopping Movie Management API..."
    docker compose down
    print_success "API stopped successfully!"
}

# Function to restart API
restart_api() {
    print_status "Restarting Movie Management API..."
    docker compose down
    docker compose up --build -d
    
    # Wait for API to be ready
    print_status "Waiting for API to be ready..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "API is ready!"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            print_warning "API may not be fully ready yet, but attempting to open anyway..."
            break
        fi
        
        print_status "Waiting for API... (attempt $attempt/$max_attempts)"
        sleep 2
        attempt=$((attempt + 1))
    done
    
    # Open Swagger UI
    open_url "http://localhost:8080/swagger-ui.html"
    
    print_success "API restarted successfully!"
}

# Function to show logs
show_logs() {
    print_status "Showing API logs..."
    docker compose logs -f
}

# Function to show status
show_status() {
    print_status "Checking API status..."
    if docker compose ps | grep -q "Up"; then
        print_success "API is running!"
        print_status "API URL: http://localhost:8080"
        print_status "Swagger UI: http://localhost:8080/swagger-ui.html"
        
        # Check if API is responding
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "API is responding to requests!"
        else
            print_warning "API is running but not responding to requests yet."
        fi
    else
        print_warning "API is not running."
        print_status "Use '$0 start' to start the API."
    fi
}

# Function to open Swagger UI
open_swagger() {
    print_status "Opening Swagger UI..."
    open_url "http://localhost:8080/swagger-ui.html"
}

# Function to show help
show_help() {
    echo "Movie Management API Docker Runner"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start             Start the API with Docker (builds if needed)"
    echo "  stop              Stop the API and remove containers"
    echo "  restart           Restart the API"
    echo "  logs              Show API logs (follow mode)"
    echo "  status            Check API status"
    echo "  swagger           Open Swagger UI in browser"
    echo "  help              Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start          # Start API and open Swagger UI"
    echo "  $0 stop           # Stop API"
    echo "  $0 restart        # Restart API"
    echo "  $0 logs           # View logs"
    echo "  $0 status         # Check if API is running"
    echo "  $0 swagger        # Open Swagger UI"
    echo ""
    echo "The start command will:"
    echo "  1. Create .env file from .env.example (if .env doesn't exist)"
    echo "  2. Build and start Docker containers"
    echo "  3. Wait for API to be ready"
    echo "  4. Open Swagger UI in your browser"
}

# Main script logic
case "${1:-help}" in
    "start")
        start_api
        ;;
    "stop")
        stop_api
        ;;
    "restart")
        restart_api
        ;;
    "logs")
        show_logs
        ;;
    "status")
        show_status
        ;;
    "swagger")
        open_swagger
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