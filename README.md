# 🏟️ Arena - Microservice Social Media Platform

A modern, scalable social media platform built with Spring Boot microservices, React frontend, and containerized deployment using Docker.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Project](#running-the-project)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Microservices](#microservices)
- [Development](#development)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

Arena is a comprehensive microservice-based social media platform that demonstrates enterprise-grade application architecture. It showcases distributed system patterns, inter-service communication, and scalable design principles.

**Key Highlights:**
- 8+ independent microservices with clear separation of concerns
- Centralized configuration management with Spring Cloud Config Server
- Service discovery using Netflix Eureka
- API Gateway for unified entry point
- Real-time messaging with WebSocket support
- Message-driven architecture with RabbitMQ
- Comprehensive monitoring with Zipkin
- Containerized deployment with Docker Compose

## ✨ Features

### User Management
- ✅ User registration and authentication
- ✅ JWT-based secure authentication
- ✅ User profiles and information
- ✅ Follow/Unfollow functionality
- ✅ Social connections tracking

### Posts & Feed
- ✅ Create and publish posts with optional images
- ✅ Real-time feed aggregation
- ✅ Like and comment on posts
- ✅ Share functionality
- ✅ Post history and analytics

### Messaging & Chat
- ✅ Real-time chat between users
- ✅ Message persistence
- ✅ Conversation history
- ✅ WebSocket-based communication

### Media Management
- ✅ Image upload and storage
- ✅ Media library for users
- ✅ Drag-and-drop file upload
- ✅ Support for JPG, PNG formats up to 10MB

### Analytics & Monitoring
- ✅ Platform statistics and metrics
- ✅ User engagement tracking
- ✅ Activity monitoring
- ✅ Distributed tracing with Zipkin
- ✅ Health checks for all services

### Notifications
- ✅ Event-driven notifications
- ✅ Message queue-based delivery
- ✅ Real-time updates

## 🏗️ Architecture

### System Design

```
┌─────────────────────────────────────────────────────────────────┐
│                         Frontend (React)                        │
│                    http://localhost:5174                        │
└──────────────────────┬──────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway (Port 8080)                      │
│          Spring Cloud Gateway + JWT Authentication             │
│                    CORS Enabled                                 │
└──────────────────────┬──────────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┬─────────────┬──────────┐
        ▼              ▼              ▼             ▼          ▼
    ┌────────┐    ┌────────┐    ┌────────┐    ┌────────┐ ┌────────┐
    │  User  │    │  Post  │    │  Chat  │    │ Notify │ │ Media  │
    │Service │    │Service │    │Service │    │Service │ │Service │
    │(8081)  │    │(8082)  │    │(8083)  │    │(8084)  │ │(8085)  │
    └────────┘    └────────┘    └────────┘    └────────┘ └────────┘
        │              │              │             │          │
        └──────────────┼──────────────┴─────────────┴──────────┘
                       │
            ┌──────────┴──────────┐
            ▼                     ▼
        ┌─────────┐          ┌──────────┐
        │PostgreSQL│         │ RabbitMQ │
        │(Port5433)│         │(Port5672)│
        └─────────┘          └──────────┘
            │
    ┌───────┴─────────┐
    │  Service Config │
    │  Database       │
    └─────────────────┘

Supporting Services:
- Config Server (8888): Centralized configuration
- Discovery Server (8761): Eureka service registry
- Zipkin (9411): Distributed tracing
```

## 💻 Tech Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2.5** - Application framework
- **Spring Cloud 2023.0.1** - Microservices framework
- **Spring Cloud Gateway** - API Gateway
- **Spring Cloud Config Server** - Centralized configuration
- **Netflix Eureka** - Service discovery
- **RabbitMQ** - Message broker
- **PostgreSQL** - Database
- **JWT** - Authentication
- **Zipkin** - Distributed tracing

### Frontend
- **React 18.2.0** - UI framework
- **Vite 5.2.0** - Build tool
- **Axios** - HTTP client
- **Tailwind CSS** - Styling
- **Lucide React** - Icons

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Orchestration
- **Maven 3.9.6** - Build tool
- **Git** - Version control

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Docker & Docker Compose** - Latest version
  - [Install Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Git** - Version 2.0 or higher
- **Node.js** - v18.0 or higher (for frontend development)
- **Java** - Optional for local development (Java 17+)
- **Maven** - Optional for local builds

### System Requirements
- **RAM**: Minimum 4GB (8GB recommended)
- **Disk Space**: 5GB free space for Docker images
- **OS**: Windows 10+, macOS, or Linux

## 🚀 Installation

### Step 1: Clone the Repository

```bash
git clone https://github.com/praveen2246/arena-microservice-platform.git
cd arena-microservice-platform
```

### Step 2: Setup Environment Variables

Copy the example environment file and configure:

```bash
cp .env.example .env
```

Edit `.env` file and update if needed:
```env
# Database Configuration
POSTGRES_USER=admin
POSTGRES_PASSWORD=password
POSTGRES_DB=social_media

# RabbitMQ Configuration
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest

# JWT Configuration
JWT_SECRET=your-secret-key-here

# Service Ports
API_GATEWAY_PORT=8080
USER_SERVICE_PORT=8081
POST_SERVICE_PORT=8082

# Spring Profile
SPRING_PROFILES_ACTIVE=docker

# Timezone
TZ=UTC
```

## ⚙️ Configuration

### 1. API Gateway Configuration

Located in `api-gateway/src/main/resources/application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/v1/users/**
        - id: post-service
          uri: lb://post-service
          predicates:
            - Path=/api/v1/posts/**
        # ... more routes
```

### 2. Service Discovery

Eureka is automatically configured. Services register on startup:
- **Eureka Dashboard**: http://localhost:8761

### 3. Config Server

Configuration files are located in `config-repo/`:
- `*-docker.yml` - Docker environment configurations
- `*.yml` - Local development configurations

### 4. Database Configuration

PostgreSQL is configured with:
- **Host**: `social-media-db` (Docker) or `localhost` (Local)
- **Port**: `5433`
- **Database**: `social_media`
- **User**: `admin`
- **Password**: `password`

## 🏃 Running the Project

### Option 1: Run with Docker Compose (Recommended)

```bash
# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Option 2: Run Individual Services

```bash
# Start only specific services
docker-compose up -d --build api-gateway user-service post-service

# View specific service logs
docker-compose logs -f user-service
```

### Option 3: Local Development (Without Docker)

**Start PostgreSQL**:
```bash
# Using Docker
docker run --name arena-db -e POSTGRES_PASSWORD=password -p 5433:5432 -d postgres:15
```

**Start RabbitMQ**:
```bash
docker run --name arena-rabbitmq -p 5672:5672 -p 15672:15672 -d rabbitmq:3-management
```

**Start Services**:
```bash
# Terminal 1: Config Server
cd config-server
mvn spring-boot:run

# Terminal 2: Discovery Server
cd discovery-server
mvn spring-boot:run

# Terminal 3: API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 4: User Service
cd user-service
mvn spring-boot:run

# Terminal 5: Post Service
cd post-service
mvn spring-boot:run

# Terminal 6: Frontend
cd frontend
npm install
npm run dev
```

## 📁 Project Structure

```
arena-microservice-platform/
├── analytics-service/           # Analytics and metrics service
│   ├── src/
│   │   └── main/
│   │       ├── java/            # Analytics business logic
│   │       └── resources/       # Configuration files
│   ├── pom.xml                  # Maven configuration
│   └── Dockerfile               # Container image
│
├── api-gateway/                 # API Gateway service
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   ├── config/      # Gateway configuration
│   │       │   └── filter/      # Authentication filters
│   │       └── resources/
│   ├── pom.xml
│   └── Dockerfile
│
├── chat-service/                # Real-time chat service
│   ├── src/main/java/
│   │   ├── config/              # WebSocket configuration
│   │   ├── controller/          # REST endpoints
│   │   └── model/               # Data models
│   ├── pom.xml
│   └── Dockerfile
│
├── config-server/               # Configuration management server
├── discovery-server/            # Service discovery (Eureka)
├── media-service/               # Media upload and management
├── notification-service/        # Event notifications
├── post-service/                # Post creation and management
├── user-service/                # User management and auth
│
├── config-repo/                 # Centralized configuration files
│   ├── user-service.yml
│   ├── post-service.yml
│   ├── chat-service.yml
│   ├── *-docker.yml             # Docker-specific overrides
│   └── ...
│
├── frontend/                    # React frontend application
│   ├── src/
│   │   ├── App.jsx              # Main component
│   │   ├── App.css              # Styling
│   │   └── main.jsx             # Entry point
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
│
├── docker-compose.yml           # Docker Compose orchestration
├── .env.example                 # Environment variables template
├── .gitignore                   # Git ignore rules
├── ENV_SETUP.md                 # Detailed environment setup
└── README.md                    # This file
```

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Authentication
All protected endpoints require JWT token in header:
```
Authorization: Bearer <token>
```

### User Service APIs

**Register User**
```
POST /users/auth/signup
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123"
}
```

**Login**
```
POST /users/auth/signin
Content-Type: application/json

{
  "username": "user123",
  "password": "password123"
}
```

**Get User Following**
```
GET /users/social/following/{userId}
Authorization: Bearer <token>
```

**Follow User**
```
POST /users/social/follow/{userId}
Authorization: Bearer <token>
```

### Post Service APIs

**Create Post**
```
POST /posts?userId=1
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "username": "user123",
  "content": "Hello Arena!",
  "image": "base64-encoded-image"
}
```

**Get Feed**
```
GET /posts/feed?userId=1
Authorization: Bearer <token>
```

### Chat Service APIs

**Send Message**
```
POST /chat/send
Authorization: Bearer <token>
Content-Type: application/json

{
  "senderId": 1,
  "receiverId": 2,
  "message": "Hello!"
}
```

**Get Messages**
```
GET /chat/messages/{userId}
Authorization: Bearer <token>
```

### Analytics APIs

**Get Stats**
```
GET /analytics/stats
Authorization: Bearer <token>
```

### Media Service APIs

**Upload Media**
```
POST /media/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

Form Data:
- file: <image file>
- userId: 1
```

**Get Media**
```
GET /media/{mediaId}
Authorization: Bearer <token>
```

## 🔧 Microservices

### 1. **User Service** (Port 8081)
- User registration and authentication
- Social connection management
- User profile management
- Follow/Unfollow logic

**Database**: PostgreSQL `user_db`

### 2. **Post Service** (Port 8082)
- Post creation and retrieval
- Feed generation
- Like and comment functionality
- Post analytics

**Database**: PostgreSQL `post_db`

### 3. **Chat Service** (Port 8083)
- Real-time messaging
- Message storage
- WebSocket communication
- Conversation history

**Database**: PostgreSQL `chat_db`

### 4. **Notification Service** (Port 8084)
- Event-driven notifications
- RabbitMQ message consumption
- Notification delivery
- Alert management

**Message Queue**: RabbitMQ

### 5. **Media Service** (Port 8085)
- File upload handling
- Image storage
- Media metadata management
- File retrieval

**Database**: PostgreSQL `media_db`

### 6. **Analytics Service**
- Metrics collection
- Statistics aggregation
- Platform analytics
- User engagement tracking

**Message Queue**: RabbitMQ

### 7. **API Gateway** (Port 8080)
- Request routing
- JWT authentication
- CORS handling
- Rate limiting
- Load balancing

### 8. **Config Server** (Port 8888)
- Centralized configuration management
- Environment-specific configs
- Service configuration updates
- Property encryption

### 9. **Discovery Server** (Port 8761)
- Service registration
- Service discovery
- Health checking
- Eureka Dashboard

## 🧑‍💻 Development

### Adding a New Microservice

1. **Create new service directory**:
```bash
mkdir new-service
cd new-service
```

2. **Copy pom.xml** from existing service and update:
```xml
<artifactId>new-service</artifactId>
```

3. **Create Spring Boot Application class**

4. **Add to docker-compose.yml**

5. **Create configuration file** in `config-repo/`

### Running Tests

```bash
# Run all tests
mvn test

# Run specific service tests
cd user-service
mvn test

# Run with coverage
mvn test jacoco:report
```

### Building for Production

```bash
# Build all services
mvn clean package -DskipTests

# Build Docker images
docker-compose build --no-cache

# Push to registry (if configured)
docker tag arena-api-gateway:latest myregistry/api-gateway:v1.0
docker push myregistry/api-gateway:v1.0
```

## 🔍 Monitoring & Debugging

### 1. View Service Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f user-service

# Last 100 lines
docker-compose logs -f --tail=100 post-service
```

### 2. Access Dashboards

- **Eureka Service Registry**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Zipkin Tracing**: http://localhost:9411
- **Frontend App**: http://localhost:5174

### 3. Check Database

```bash
# Connect to PostgreSQL
docker exec -it social-media-db psql -U admin -d social_media

# View tables
\dt

# Query data
SELECT * FROM users;
```

### 4. Monitor Services

```bash
# Check running containers
docker ps

# View resource usage
docker stats

# Inspect service logs with filtering
docker-compose logs user-service | grep ERROR
```

## 🐛 Troubleshooting

### Issue: Services won't start

**Solution**:
```bash
# Clean up Docker
docker-compose down -v
docker system prune -f

# Rebuild images
docker-compose up -d --build
```

### Issue: Database connection refused

**Solution**:
```bash
# Check if database is running
docker ps | grep postgres

# Restart database
docker-compose restart social-media-db

# Verify connection
docker-compose logs social-media-db
```

### Issue: CORS errors in browser

**Solution**:
- Check API Gateway CORS configuration in `application.yml`
- Verify allowed origins include `http://localhost:5174`
- Clear browser cache

### Issue: RabbitMQ connection failed

**Solution**:
```bash
# Check RabbitMQ status
docker-compose ps social-media-rabbitmq

# Restart RabbitMQ
docker-compose restart social-media-rabbitmq

# Verify connection
docker-compose logs social-media-rabbitmq | tail -20
```

### Issue: Port already in use

**Solution**:
```bash
# Find process using port
netstat -ano | findstr :8080

# Or change port in docker-compose.yml
# api-gateway:
#   ports:
#     - "8090:8080"
```

## 📝 Environment Setup Details

For detailed environment setup instructions, refer to [ENV_SETUP.md](ENV_SETUP.md).

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Code Style
- Follow Java naming conventions
- Use meaningful variable names
- Add comments for complex logic
- Format code with IDE formatter

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💼 Author

**Praveen Kumar**
- GitHub: [@praveen2246](https://github.com/praveen2246)
- Repository: [arena-microservice-platform](https://github.com/praveen2246/arena-microservice-platform)

## 📞 Support

For issues and questions:
1. Check [Troubleshooting](#troubleshooting) section
2. Review [ENV_SETUP.md](ENV_SETUP.md) for configuration details
3. Open an issue on GitHub
4. Review logs: `docker-compose logs -f`

## 🎓 Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Docker Documentation](https://docs.docker.com/)
- [React Documentation](https://react.dev)
- [Microservices Patterns](https://microservices.io/patterns/index.html)

---

**Happy coding! 🚀**

*Last Updated: May 8, 2026*
*Version: 1.0.0*
