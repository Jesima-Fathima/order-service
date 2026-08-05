# Enterprise Order Management Platform (EOMP)

> A production-inspired microservices application built incrementally to demonstrate modern backend engineering practices using Java, Spring Boot, and cloud-native technologies.

---

# Purpose

This repository is **not** a CRUD application.

The objective is to build an enterprise-grade backend system step by step, following software engineering best practices that are commonly used in product-based companies.

Every feature should be implemented only after understanding:

* Business Requirement
* API Design
* Domain Modeling
* Design Decisions
* Implementation
* Testing
* Documentation

The goal is to understand **why** a solution is built in a certain way, not just **how** to code it.

---

# Project Goals

This project will demonstrate:

* REST API Design
* SOAP Integration
* GraphQL
* gRPC
* Microservices
* Event Driven Architecture
* Cloud Native Development
* Secure APIs
* API Documentation
* Production Best Practices

---

# Guiding Principles

## 1. Build Small

Never build everything at once.

Each iteration should introduce only one or two new concepts.

---

## 2. Keep Everything Production Ready

Every implementation should be suitable for a real production system.

Avoid shortcuts that would not be acceptable in a professional environment.

---

## 3. Understand Before Coding

Before implementing any feature, answer:

* Why is this needed?
* What problem does it solve?
* What alternatives exist?
* Why was this approach selected?

---

## 4. Document Decisions

Whenever an important architectural decision is made, document it.

Examples:

* Why REST instead of GraphQL?
* Why Redis?
* Why Kafka?
* Why gRPC?
* Why JWT?
* Why Cursor Pagination?

---

# Technology Stack (Final Target)

## Backend

* Java 21
* Spring Boot 3

## Database

* PostgreSQL

## Cache

* Redis

## Messaging

* Kafka

## API

* REST
* GraphQL
* gRPC
* SOAP Adapter

## Security

* Spring Security
* JWT
* OAuth2

## Documentation

* OpenAPI / Swagger

## Cloud

* Docker
* Kubernetes

## Monitoring

* Spring Boot Actuator
* Prometheus
* Grafana
* OpenTelemetry

## Logging

* ELK Stack

## Testing

* JUnit 5
* Mockito
* Testcontainers
* Rest Assured

---

# High-Level Architecture (Final Vision)

Client

↓

API Gateway

↓

User Service

Product Service

Order Service

Inventory Service

Payment Service

Notification Service

↓

Kafka

↓

Analytics / Audit / Notification

Each service owns its own database.

---

# Repository Structure (Target)

enterprise-order-platform/

├── api-gateway/

├── user-service/

├── product-service/

├── order-service/

├── inventory-service/

├── payment-service/

├── notification-service/

├── common/

├── docs/

├── docker/

├── kubernetes/

├── grpc/

├── graphql/

├── postman/

└── README.md

---

# Development Strategy

The project will evolve in phases.

Do **not** skip phases.

Do **not** implement future phases early.

---

---

# Project Progress

The project is being implemented incrementally.

The current implementation focuses on building the **Order Service** as the first production-inspired backend service.

## Completed Milestones

### Milestone 1 — Spring Boot Foundation

Status: ✅ Completed

Implemented:

* Spring Boot 3
* Java 21
* Maven
* Health API
* H2 Database
* Spring Data JPA
* Global Exception Handling
* Standard API Response
* OpenAPI / Swagger
* Clean Layered Architecture
* Constructor Injection

---

### Milestone 2 — Create Order API

Status: ✅ Completed

Implemented:

* Order Entity
* Order Repository
* Order Service
* Order Controller
* Request DTO
* Response DTO
* Bean Validation
* Global Exception Handling
* H2 Persistence
* OpenAPI Documentation

---

### Milestone 3 — Complete Order CRUD

Status: ✅ Completed

Implemented:

* Create Order
* Get Order By ID
* Get All Orders
* Update Order
* Delete Order
* Pagination
* Sorting
* API Versioning
* Custom Exceptions
* Page Response
* OpenAPI Documentation

---

### Milestone 4 — Testing & Production Readiness

Status: ✅ Completed

Implemented:

* Service Unit Tests
* Controller Tests
* Integration Tests
* Validation Tests
* Exception Handling Tests
* Pagination Tests
* Sorting Tests

---

## Current Learning — Kafka & Event-Driven Architecture

Status: 📚 Learning

Before implementing Kafka, the following concepts are being studied:

* Kafka Producer
* Kafka Consumer
* Broker
* Topic
* Partition
* Offset
* Consumer Group
* REST vs Kafka
* Asynchronous Event-Driven Communication
* At-Most-Once Delivery
* At-Least-Once Delivery
* Exactly-Once Semantics
* Idempotency
* Consumer Lag
* Retry Strategy
* Dead Letter Topic
* Dual-Write Problem
* Transactional Outbox Pattern

Kafka implementation will begin only after the concepts and design decisions are understood.

---

## Upcoming Order Service Milestones

* [ ] Milestone 5 — Kafka Event Publishing
* [ ] Milestone 6 — Kafka Consumer, Retry & Dead Letter Topic
* [ ] Milestone 7 — Webhook Management & Delivery
* [ ] Milestone 8 — Docker
* [ ] Milestone 9 — GitHub Actions CI/CD
* [ ] Milestone 10 — Kubernetes
* [ ] Milestone 11 — Redis, Caching & Rate Limiting
* [ ] Milestone 12 — Observability & Monitoring

# Phase 1 — Foundation

Goal:

Understand the business domain and establish project standards.

Deliverables:

* Business requirements
* High-level architecture
* Domain modeling
* API-first design
* Repository structure
* Development conventions

Status:

🟢 In Progress

---

# Phase 2 — Product Service

Goal:

Build a production-quality REST API.

Topics:

* Resource Modeling
* DTO Design
* Validation
* Exception Handling
* CRUD APIs
* Search
* Filtering
* Sorting
* Pagination
* API Versioning
* OpenAPI Documentation

Status:

⬜ Not Started

---

# Phase 3 — User Service

Topics:

* Registration
* Login
* Password Encryption
* JWT
* OAuth2
* Role Based Access Control
* Refresh Tokens
* Method Security

Status:

⬜ Not Started

---

# Phase 4 — Order Service

Topics:

* Business Workflows
* Transactions
* Idempotency
* Optimistic Locking
* Domain Events

Status:

⬜ Not Started

---

# Phase 5 — Inventory Service

Topics:

* gRPC
* Internal Communication
* Stock Reservation
* Stock Release

Status:

⬜ Not Started

---

# Phase 6 — Event Driven Architecture

Topics:

* Kafka
* Event Publishing
* Event Consumption
* Dead Letter Queue
* Retry Strategy

Status:

⬜ Not Started

---

# Phase 7 — Real-Time Communication

Topics:

* Server-Sent Events
* WebSocket
* Long Polling
* Short Polling

Status:

⬜ Not Started

---

# Phase 8 — GraphQL

Topics:

* GraphQL Schema
* Queries
* Mutations
* Comparison with REST

Status:

⬜ Not Started

---

# Phase 9 — Cloud Native

Topics:

* Docker
* Docker Compose
* Kubernetes
* ConfigMaps
* Secrets
* Health Checks

Status:

⬜ Not Started

---

# Phase 10 — Production Readiness

Topics:

* Redis
* Rate Limiting
* Caching
* Circuit Breaker
* Retry
* Monitoring
* Logging
* Distributed Tracing
* GitHub Actions
* Integration Testing

Status:

⬜ Not Started

---

# Engineering Standards

Every service should eventually include:

* Controller
* Service
* Repository
* DTOs
* Entity
* Mapper
* Validation
* Exception Handling
* Logging
* OpenAPI
* Tests

---

# API Standards

Use:

* Resource-based URLs
* Proper HTTP methods
* Proper HTTP status codes
* Request DTOs
* Response DTOs
* RFC 7807 Problem Details for errors
* API Versioning
* Pagination
* Sorting
* Filtering
* Search

---

# Coding Standards

* Follow SOLID principles.
* Keep classes focused on a single responsibility.
* Do not expose JPA entities through REST APIs.
* Use constructor injection.
* Avoid duplicated logic.
* Keep business logic inside services.
* Validate input at the API boundary.

---

# Rules for AI Coding Assistants (GitHub Copilot / ChatGPT)

When generating code:

1. Implement **only the current phase**.
2. Do **not** introduce technologies from future phases.
3. Prefer clarity over cleverness.
4. Follow Spring Boot best practices.
5. Explain design decisions where appropriate.
6. Keep code modular and easy to extend.
7. Do not add unnecessary dependencies.
8. If multiple implementation options exist, choose the simplest production-ready approach.
9. Preserve backward compatibility with previous iterations unless explicitly instructed otherwise.

---

# Current Iteration

Current Phase:

**Phase 1 – Foundation**

Current Objective:

* Finalize business requirements.
* Finalize API design for the Product Service.
* No coding until the design is reviewed and approved.

Next Milestone:

Design the Product Service REST API before creating the Spring Boot project.
