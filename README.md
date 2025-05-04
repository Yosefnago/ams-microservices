# 📊 AMS System – Accounting Management System

**AMS System** is a microservices-based Accounting Management System designed to provide comprehensive accounting and management functionalities. The system is built using Spring Boot and Spring Cloud technologies, following a microservices architecture pattern for scalability, maintainability, and resilience.

## 🏗️ System Architecture
The application is structured as a collection of microservices, each responsible for specific business functionality:

AMS System
├── API Gateway (gateway-service)
├── Service Discovery (eureka-server)
├── User Management (user-service)
├── Client Management (client-service)
├── UI Service (ui-service)
├── Common Utilities (common-utils)
└── Common Security (common-security)

## ⚙️ Technologies Used
- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Cloud 2023.0.1**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **Vaadin 24.6.6**
- **Netflix Eureka**
- **Spring Cloud Gateway**
- **OpenFeign**
- **Maven**
  
## 📦 Module Descriptions

### 🛡️ Gateway Service
The API Gateway that routes all client requests to the appropriate microservices. It handles authentication, request routing, and load balancing.

### 🔍 Eureka Server
Service discovery server that allows microservices to find and communicate with each other without hardcoding hostname and port.

### 👤 User Service
Manages user accounts, authentication, and authorization. Handles user registration, login, and profile management.

### 👥 Client Service
Manages client information and related operations for the accounting system.

### 💻 UI Service
Provides the web-based user interface for the application using Vaadin framework.

### 🧰 Common Utils
Shared utility classes and functions used across multiple microservices.

### 🔐 Common Security
Shared security components, including JWT authentication utilities, used across microservices.

## 🛠️ Setup and Installation

### ✅ Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL (any version 13+)


### Contributors
**Yosef Nago** – Project architect and core developer

## 📄 License

This project is not licensed for reuse. All rights reserved © Yosef Nago.
