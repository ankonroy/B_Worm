# B-Worm Application

A Spring Boot 4.0.3 application with Java 21, PostgreSQL, and Docker support.

## Prerequisites

- Docker
- Docker Compose
- (Optional) A PostgreSQL database (or use the provided configuration)

## Quick Start

### 1. Create Environment Variables File

Create a `.env` file in the project root with your database credentials:

```env
DB_URL=jdbc:postgresql://localhost:5432/your_database
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### 2. Run with Docker Compose Watch Mode

To run the application in watch mode (auto-rebuild on code changes):

```bash
docker-compose up --watch
```

Or build and run:

```bash
docker-compose up --build --watch
```

The application will be available at `http://localhost:8080`.

## How Watch Mode Works

The `docker-compose.yml` is configured with `develop.watch` that monitors:

- `./src` directory - When source files change, the Docker image is rebuilt
- `pom.xml` - When dependencies change, the Docker image is rebuilt

When you modify any file in `src/`, Docker Compose will automatically rebuild the container.

## Alternative: Using dev.sh Script

Alternatively, you can use the provided `dev.sh` script:

```bash
./dev.sh
```

This script:
1. Checks for the `.env` file
2. Loads environment variables
3. Builds the Docker image with `--no-cache`
4. Runs the container with `docker-compose up`

## Available Scripts

| Script | Description |
|--------|-------------|
| `docker-compose up --watch` | Run in watch mode with auto-rebuild |
| `./dev.sh` | Run using the dev script |
| `./start.sh` | Run with trap for clean shutdown |

## Stopping the Application

Press `Ctrl+C` to stop the running container. If using `start.sh`, it will automatically run `docker-compose down`.

## Project Structure

```
b_worm/
├── src/                    # Source code
├── Dockerfile              # Multi-stage Docker build
├── docker-compose.yml      # Docker Compose configuration
├── docker-entrypoint.sh    # Container entrypoint with file watching
├── dev.sh                  # Development script
├── start.sh                # Production start script
└── pom.xml                 # Maven configuration
```

