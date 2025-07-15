#!/bin/bash

# Script to test quality checks locally (without full CI)
# This runs the quality checks directly without GitHub Actions

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Get the project root directory (parent of scripts)
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

echo "🚀 Running quality checks locally..."
echo "📁 Working directory: $(pwd)"

# Check if we're in the right directory
if [ ! -f "gradlew" ]; then
    echo "❌ gradlew not found!"
    echo "   Please run this script from the project root directory"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    echo "   Please install Java 21"
    exit 1
fi

echo "🔧 Running quality checks..."
echo "   This runs checkstyle and spotbugs directly..."

# Grant execute permission for gradlew
chmod +x gradlew

# Run quality checks
./gradlew checkstyleMain spotbugsMain --no-daemon

echo "✅ Quality checks completed!" 