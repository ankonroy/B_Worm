#!/bin/bash

echo "🚀 Starting development environment with live reload..."

if [ ! -f .env ]; then
    echo "❌ Error: .env file not found!"
    exit 1
fi

set -a
source .env
set +a

# Build with no cache to ensure fresh start
echo "📦 Building Docker image..."
docker-compose build --no-cache

# Run
echo "🚀 Running application..."
docker-compose up