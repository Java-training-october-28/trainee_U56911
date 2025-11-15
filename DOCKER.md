# Docker & GitHub Actions Setup

This project is configured with Docker containerization and GitHub Actions CI/CD pipeline.

## Docker Setup

### Building the Image Locally
```bash
docker build -t demo-app .
```

### Running the Container
```bash
docker run -p 8080:8080 demo-app
```

### Health Check
The application includes a health check endpoint at `/actuator/health` that is used by Docker's health check feature.

## GitHub Actions Workflow

The project includes a comprehensive CI/CD pipeline that:

1. **Tests** - Runs Maven tests on every push and PR
2. **Builds** - Creates multi-architecture Docker images (linux/amd64, linux/arm64)
3. **Pushes** - Stores images in GitHub Container Registry (ghcr.io)
4. **Scans** - Performs security vulnerability scanning with Trivy
5. **Reports** - Generates test reports and SBOMs

### Image Tags

Images are automatically tagged based on:
- Branch name for branch pushes
- Semantic versioning for tagged releases
- SHA for all builds
- `latest` for main branch

### Accessing Images

Images are published to: `ghcr.io/[username]/[repository]:tag`

### Security Features

- Multi-stage build for smaller image size
- Non-root user execution
- Vulnerability scanning with Trivy
- SBOM (Software Bill of Materials) generation
- Security findings uploaded to GitHub Security tab

## Configuration

### Environment Variables
The application can be configured using standard Spring Boot environment variables:
- `SPRING_PROFILES_ACTIVE`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Health Check
The Docker health check monitors the `/actuator/health` endpoint every 30 seconds.