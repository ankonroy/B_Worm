# B-Worm: Book Exchange Platform

B-Worm is a community book-sharing web application. It helps members donate books they no longer need and request books shared by others, while administrators moderate platform activity.

The application is built with Spring Boot, server-rendered views, role-based security, and a relational database.

## Overview

- **Goal:** enable a simple, trust-based book donation and request process
- **Primary users:** community members and platform administrators
- **Tech stack:** Java 21, Spring Boot, Spring Security, Spring Data JPA, Thymeleaf, PostgreSQL
- **Deployment style:** local runtime or containerized with Docker Compose

## Main Features

### Member Features

- Create an account and sign in with username and password
- Browse and search available books
- Donate books with title, author, ISBN, description, and condition
- Send requests for books donated by other members
- Track and manage request actions (including cancellation)
- View profile information and update personal details
- Receive activity notifications and mark them as read

### Admin Features

- Access a protected admin area
- Enable or disable user accounts
- Review books and perform moderation actions such as ban or unban

## Book Request Lifecycle

The platform uses clear status transitions for book exchange:

1. A donated book starts as **AVAILABLE**.
2. When members request it, the state can move to **REQUESTED**.
3. If the donor approves a request, the book becomes **TAKEN**.
4. If moderation is required, an admin can set the book to **BANNED**.

This workflow allows multiple requests to be handled fairly while keeping status visibility for users.

## Security Model

- Form-based authentication for login
- Role-based authorization (`MEMBER`, `ADMIN`)
- Restricted admin endpoints for administrative actions
- Passwords stored with secure hashing

## Notification Behavior

Users are notified for important platform events, including:

- new request received by donor
- request approved
- request rejected
- book banned by admin

Notifications support unread counts and read-state updates.

## Initial Seed Accounts

When the application starts with an empty user table, default accounts are created:

- `admin / admin123`
- `member1 / password123`
- `member2 / password123`

These are useful for first-time setup and manual testing.

## How to Run

### Option 1: Docker Compose

Create a `.env` file with database values, then run:

```powershell
Set-Location "F:\SEPM_Project\B_Worm\b_worm"
docker-compose up --build
```

For watch mode during development:

```powershell
Set-Location "F:\SEPM_Project\B_Worm\b_worm"
docker-compose up --watch
```

### Option 2: Local Java Runtime

After setting database environment variables:

```powershell
Set-Location "F:\SEPM_Project\B_Worm\b_worm"
mvn clean package
mvn spring-boot:run
```

If Maven is not installed locally, use the Docker option.

## Testing Summary

Based on current source-level test analysis:

- **Total test classes:** 15
- **Total test methods:** 119
- **Integration-style classes:** 5
- **Unit-style classes:** 10
- **Integration-style methods:** 34
- **Unit-style methods:** 85

Integration-style tests are identified by Spring test annotations (context, MVC, or JPA slice tests).

## Current Scope and Known Gaps

- Some user-facing flows are partially implemented and need completion.
- A few view paths referenced by controllers are not yet fully available.
- CSRF is disabled in the current security setup and should be reviewed before production release.

## Environment Variables

The runtime expects these values:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `PORT` (optional; defaults to `8080`)

## CI/CD Pipeline (Recommended)

A CI/CD pipeline helps keep the project stable by automatically validating code changes and preparing safe deployments.

### 1) Continuous Integration (CI)

On every push or pull request, the pipeline should run:

- Checkout source code
- Set up Java 21
- Resolve dependencies and build the project
- Run unit and integration tests
- Publish test reports and build artifacts

Typical CI stages:

1. **Lint/Static Checks**: code style and basic quality gates
2. **Build**: compile and package the application
3. **Test**: execute unit and integration test suites
4. **Artifact**: produce a versioned JAR and (optionally) Docker image

### 2) Continuous Delivery / Deployment (CD)

After CI passes on the main branch:

- Build and tag a Docker image (for example using commit SHA + latest)
- Push image to a container registry
- Deploy to staging environment
- Run smoke checks
- Promote to production (automatic or manual approval)

A practical release flow:

- **Feature branches** -> CI validation only
- **Main branch** -> CI + staging deployment
- **Version tag/release** -> CI + production deployment

### 3) Environment Strategy

Use separate environments and secrets:

- **Development**: local Docker Compose and fast feedback
- **Staging**: production-like verification
- **Production**: stable release with controlled rollout

Keep credentials in secure secret managers, and inject runtime variables such as:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `PORT`

### 4) Quality Gates for This Project

To reduce regressions in this book-exchange workflow, enforce:

- All tests must pass before merge
- No critical security warnings
- Build artifact generation must succeed
- Basic smoke test after deployment (`/`, login page, books listing)

## Conclusion

B-Worm provides a solid foundation for a community-driven book exchange platform with authentication, role-based access, request lifecycle management, notifications, and admin moderation.

With a structured CI/CD pipeline, the team can improve delivery speed, maintain quality, and deploy changes more safely. Completing the remaining feature gaps and tightening production security settings will move the project from a strong academic/prototype stage toward a production-ready platform.
