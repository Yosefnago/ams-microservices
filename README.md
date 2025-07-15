# 📊 AMS System – Accounting Management System

**AMS System** is a microservices-based Accounting Management System designed to provide comprehensive accounting and management functionalities. The system is built using Spring Boot and Spring Cloud technologies, following a microservices architecture pattern for scalability, maintainability, and resilience.

## 🏗️ System Architecture
The application is structured as a collection of microservices, each responsible for specific business functionality:

AMS System
- **API Gateway** (gateway-service)
- **Service Discovery** (eureka-server)
- **User Management** (user-service)
- **Client Management** (client-service)
- **UI Service** (ui-service)
- **Common Utilities** (common-utils)
- **Common Security** (common-security)

📦 Core Features
✅ Role-Based Document Workflow (Client ↔ Accountant)

📤 PDF/Image Upload with OCR Parsing for invoices

📑 Grid Views for Clients and Accountants with filtering, sorting

🔁 Status Management (Pending, Approved, Rejected)

✏️ Reject Reason Tracking

🔐 Secure Login using Spring Security and JWT

🔄 Microservice Communication via Gateway and Eureka



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
- **Maven**
  
📦 Module Descriptions

🛡️ Gateway Service (gateway-service)
The central API entry point. Handles request routing and token forwarding to downstream services.

🔍 Eureka Server (eureka-server)
Provides service discovery for all registered microservices in the system.

👤 User Service (user-service)
Manages user accounts, roles, authentication, and token generation using Spring Security and JWT.

👥 Client Service (client-service)
Business logic center of the system, responsible for:

Managing client profiles and business entities

Document management: upload, delete, status updates, rejection flow

Invoice processing: PDF/image upload, Tesseract OCR field extraction, status flow

💻 UI Service (ui-service)
Frontend application developed with Vaadin 24, providing responsive views for both clients and accountants.

🧰 Common Utils (common-utils)
Contains shared utility classes and constants used across all services.

🔐 Common Security (common-security)
Provides shared JWT token utilities, custom user detail service, and Spring Security configuration.



🛠️ Setup Instructions
✅ Prerequisites
Java 17+

Maven 3.6+

PostgreSQL (13+)

Tesseract OCR installed (if using invoice OCR)


### Contributors
**Yosef Nago** – Project architect and core developer


