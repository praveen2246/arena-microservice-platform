# Arena Microservices Platform 🚀

Arena is a production-grade, distributed social media platform built using Spring Cloud, RabbitMQ, and React. It features a fully decoupled architecture with 9 specialized microservices, real-time messaging, and personalized content delivery.

## 🏗️ Architecture Overview

The system is designed with high scalability and resilience in mind:

- **Frontend**: Modern React + Vite Dashboard with a premium dark-themed UI.
- **API Gateway**: Entry point for all traffic, handling JWT security, routing, and identity propagation.
- **Service Discovery**: Eureka Server for dynamic service registration.
- **Centralized Config**: Spring Cloud Config Server managing externalized properties for all environments.
- **Messaging Bus**: RabbitMQ for asynchronous notifications and analytics tracking.
- **Tracing**: Zipkin integrated for distributed request tracing.

### Microservices Map
| Service | Port | Responsibility |
| :--- | :--- | :--- |
| `user-service` | 8081 | Auth, Profile Management, Social Graph (Followers) |
| `post-service` | 8082 | Content CRUD, Personalized Feed Generation |
| `notification-service` | 8083 | Event-driven alerts (Social & Content) |
| `media-service` | 8084 | Image Uploads and Asset Delivery |
| `chat-service` | 8085 | Real-time Private Messaging (WebSockets) |
| `analytics-service` | 8086 | Platform-wide Insights & Engagement Tracking |

## 🛠️ Key Features

- **Personalized Feed**: Content aggregation from followed users via inter-service Feign calls.
- **Identity Propagation**: Transparent user identification across services using Gateway headers.
- **Real-time Communication**: WebSocket-driven chat system with STOMP protocol.
- **Asynchronous Workflows**: Scalable notification system using RabbitMQ Topic Exchanges.
- **Unified Security**: Centralized JWT validation at the Gateway level.

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local development)
- Node.js 18+ (for frontend)

### One-Click Deployment (Recommended)
The entire platform can be launched using Docker Compose:

```bash
docker-compose up --build
```

This will automatically:
1. Initialize the **PostgreSQL** database and **RabbitMQ** broker.
2. Build and start all **9 microservices**.
3. Register services with **Eureka**.
4. Expose the **API Gateway** on port `8080`.

### Local Development
1. Start `discovery-server`.
2. Start `config-server`.
3. Start the infrastructure services (`user-service`, `post-service`, etc.) in any order.
4. Run the frontend:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## 📊 Monitoring & UI
- **Eureka Dashboard**: `http://localhost:8761`
- **Config Server**: `http://localhost:8888`
- **Zipkin Tracing**: `http://localhost:9411`
- **RabbitMQ Admin**: `http://localhost:15672` (guest/guest)
- **Frontend Dashboard**: `http://localhost:5173`

---
Developed by **Antigravity** for the Arena Project.
