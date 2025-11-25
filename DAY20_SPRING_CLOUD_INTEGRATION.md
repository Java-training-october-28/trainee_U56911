# Day 20: Spring Cloud Integration - Implementation Guide

## Overview
This implementation demonstrates the integration of three Spring Cloud components into the Task Management System:
1. **Spring Cloud Config Server** - Centralized configuration management
2. **Spring Cloud LoadBalancer** - Client-side load balancing
3. **Netflix Eureka** - Service discovery and registration

## Project Structure
```
trainee_U56911/
├── config-server/          # Spring Cloud Config Server
├── eureka-server/          # Netflix Eureka Server
├── src/                    # Main Task Management System (Config Client + Eureka Client)
└── DAY20_SPRING_CLOUD_INTEGRATION.md
```

## Components Implemented

### 1. Spring Cloud Config Server
**Location**: `config-server/`

**Features**:
- Centralized configuration management using Git backend
- Environment-specific properties (dev, test, prod)
- Dynamic configuration refresh with `@RefreshScope`

**Configuration**:
- Port: 8888
- Git repository: https://github.com/UST-Java-training-october-28/trainee_U56911
- Search path: `config-repo`

**Key Files**:
- `ConfigServerApplication.java` - Main application class with `@EnableConfigServer`
- `application.yml` - Server configuration

### 2. Netflix Eureka Server
**Location**: `eureka-server/`

**Features**:
- Service registry and discovery
- Health monitoring of registered services
- Service instance management

**Configuration**:
- Port: 8761
- Self-preservation disabled for development
- Fast eviction (5 seconds)

**Key Files**:
- `EurekaServerApplication.java` - Main application class with `@EnableEurekaServer`
- `application.yml` - Server configuration

### 3. Task Management System (Config Client + Eureka Client)
**Location**: `src/`

**Features**:
- Config Client for centralized configuration
- Eureka Client for service registration and discovery
- LoadBalancer for client-side load balancing
- Dynamic configuration refresh

**Key Files**:
- `CloudConfig.java` - LoadBalancer configuration
- `NotificationClientService.java` - Service discovery and inter-service communication
- `CloudIntegrationController.java` - Test endpoints for Spring Cloud features
- Updated `application.yaml` - Client configuration

## Running the System

### Prerequisites
- Java 21
- Maven
- Git

### Step 1: Start Config Server
```bash
cd config-server
mvn spring-boot:run
```
**Verify**: http://localhost:8888

### Step 2: Start Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
**Verify**: http://localhost:8761

### Step 3: Start Task Management System
```bash
mvn spring-boot:run
```
**Verify**: http://localhost:8080

## Testing the Integration

### 1. Configuration Management
**Endpoint**: `GET /api/cloud/config`

**Purpose**: Verify configuration is loaded from Config Server
```bash
curl http://localhost:8080/api/cloud/config
```

### 2. Service Discovery
**Endpoint**: `GET /api/cloud/discovery`

**Purpose**: Check service registration in Eureka
```bash
curl http://localhost:8080/api/cloud/discovery
```

### 3. Load Balancing & Service Communication
**Endpoint**: `POST /api/cloud/notify`

**Purpose**: Test load-balanced service-to-service communication
```bash
curl -X POST http://localhost:8080/api/cloud/notify \
  -H "Content-Type: application/json" \
  -d '{"message": "Test notification"}'
```

### 4. Health Check
**Endpoint**: `GET /api/cloud/health`

**Purpose**: Comprehensive health check of Spring Cloud components
```bash
curl http://localhost:8080/api/cloud/health
```

### 5. Configuration Refresh
**Endpoint**: `GET /api/cloud/refresh-info`

**Purpose**: Check current configuration and refresh instructions
```bash
curl http://localhost:8080/api/cloud/refresh-info
```

## Dynamic Configuration Refresh

### How it Works
1. Configuration changes are pushed to Git repository
2. Config Server picks up changes automatically
3. Clients can refresh configuration without restart using Actuator

### Refresh Configuration
```bash
curl -X POST http://localhost:8080/actuator/refresh \
  -H "Content-Type: application/json"
```

## Load Balancing Features

### Client-Side Load Balancing
- Uses Spring Cloud LoadBalancer
- Service discovery through Eureka
- Automatic failover between service instances

### LoadBalanced Components
- `RestTemplate` - Synchronous HTTP client
- `WebClient` - Reactive HTTP client

## Service Discovery Features

### Automatic Registration
- Services automatically register with Eureka
- Health checks ensure only healthy instances are available
- Instance metadata for advanced routing

### Manual Service Discovery
```java
// Using DiscoveryClient
List<ServiceInstance> instances = discoveryClient.getInstances("service-name");
```

## Configuration Properties

### Config Server Properties
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/UST-Java-training-october-28/trainee_U56911
          search-paths: config-repo
```

### Eureka Client Properties
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
```

### LoadBalancer Properties
```yaml
spring:
  cloud:
    loadbalancer:
      enabled: true
```

## Monitoring and Health Checks

### Actuator Endpoints
- `/actuator/health` - Application health
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/env` - Environment properties
- `/actuator/refresh` - Configuration refresh

### Eureka Dashboard
- Access: http://localhost:8761
- View registered services
- Monitor service health
- Instance details

## Troubleshooting

### Common Issues

1. **Config Server Connection Failed**
   - Verify Config Server is running on port 8888
   - Check Git repository accessibility
   - Verify network connectivity

2. **Eureka Registration Failed**
   - Verify Eureka Server is running on port 8761
   - Check service name configuration
   - Verify network connectivity

3. **Configuration Not Refreshing**
   - Ensure `@RefreshScope` is used on beans
   - Verify Actuator refresh endpoint is enabled
   - Check Git repository for configuration changes

### Logs to Monitor
- Config Server logs for configuration loading
- Eureka Server logs for service registration
- Application logs for configuration refresh events

## Next Steps

### Production Considerations
1. **High Availability**
   - Deploy multiple Config Server instances
   - Use Eureka clusters for service discovery
   - Implement circuit breakers for resilience

2. **Security**
   - Secure Config Server with authentication
   - Encrypt sensitive configuration properties
   - Secure Eureka Server endpoints

3. **Monitoring**
   - Integrate with monitoring systems
   - Set up alerts for service failures
   - Monitor configuration refresh events

### Advanced Features
1. **Configuration Encryption**
2. **Service Mesh Integration**
3. **Distributed Tracing**
4. **Circuit Breaker Patterns**

## Conclusion

This implementation successfully demonstrates the integration of Spring Cloud components into the Task Management System, providing:
- Centralized configuration management
- Service discovery and registration
- Client-side load balancing
- Dynamic configuration updates

The system is ready for microservices architecture and can be extended with additional Spring Cloud features as needed.
