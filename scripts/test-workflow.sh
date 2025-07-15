#!/bin/bash

# Script to test the streamlined CI workflow locally with act

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Get the project root directory (parent of scripts)
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

echo "🚀 Testing streamlined CI workflow locally..."
echo "📁 Working directory: $(pwd)"

# Check if we're in the right directory
if [ ! -d ".github/workflows" ]; then
    echo "❌ .github/workflows directory not found!"
    echo "   Please run this script from the project root directory"
    exit 1
fi

# Check if act is installed
if ! command -v act &> /dev/null; then
    echo "❌ act is not installed. Please install it first:"
    echo "   brew install act"
    exit 1
fi

# Create .secrets file if it doesn't exist
if [ ! -f .secrets ]; then
    echo "📝 Creating .secrets file with default values..."
    cat > .secrets << EOF
DB_PASSWORD=moviepass
ADMIN_PASSWORD=adminpass
EOF
    echo "✅ Created .secrets file with default values"
    echo "   You can edit .secrets to change the values"
fi

echo "🔧 Running streamlined CI workflow..."
echo "   This single job runs all checks in sequence for better performance..."

# Run the streamlined CI workflow
act --secret-file .secrets --container-architecture linux/amd64

echo "✅ Streamlined CI workflow completed!" 