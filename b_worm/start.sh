#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Starting B-Worm application with Docker...${NC}"

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${RED}❌ Error: .env file not found!${NC}"
    echo "Please create .env file with your database credentials"
    exit 1
fi

# Load environment variables
set -a
source .env
set +a

# Build and run
echo -e "${GREEN}📦 Building Docker image...${NC}"
docker-compose build

echo -e "${GREEN}🚀 Starting container...${NC}"
docker-compose up

# This will run when you press Ctrl+C
trap 'echo -e "${GREEN}🛑 Stopping container...${NC}"; docker-compose down' INT