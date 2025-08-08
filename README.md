# Boot-MS: Spring Boot Microservices Example

This repository contains a multi-module microservices application built with Java 17 and Spring Boot 3.x. It demonstrates best practices for building, securing, and running distributed microservices using modern Spring technologies.

## Modules
- **customer-service**: Manages customer data and operations.
- **product-service**: Handles product catalog and related operations.
- **menu-service**: (Optional) For menu or catalog aggregation.
- **reactive-client**: Demonstrates reactive programming and aggregation of data from other services.
- **service-registry**: Eureka server for service discovery.

## Key Features
- Java 17, Spring Boot 3.x, Spring Cloud 2022.x
- Spring Security enabled in all services
- Scheduler and ExecutorService configuration for background tasks
- Eureka-based service discovery
- DTOs and clean architecture
- Maven multi-module structure

## Prerequisites
- Java 17+
- Maven 3.8+
- Docker (optional, for running with docker-compose)

## How to Build
```sh
mvn clean install
```

## How to Run
### 1. Start the Service Registry (Eureka)
```sh
cd service-registry
mvn spring-boot:run
```

### 2. Start Other Services (in separate terminals)
```sh
cd customer-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd menu-service && mvn spring-boot:run
cd reactive-client && mvn spring-boot:run
```

### 3. Using Docker Compose (optional)
```sh
docker-compose up --build
```

## API Testing
- Use Postman or the included `api-tester.html` to test endpoints.
- Each service exposes REST APIs on its configured port (see `application.yml`).

## Security
- All services are secured with Spring Security (basic auth by default).
- Update credentials in the respective `application.yml` files.

## Scheduler & Executor
- Background jobs and async tasks are configured in each service via `SchedulerConfig` and `ExecutorServiceConfig`.

## Contributing
1. Fork the repo and create a feature branch.
2. Commit your changes and push to your branch.
3. Open a pull request to `main`.

## License
MIT

---

**Author:** Mithlesh Kumar (mitkumar1)
