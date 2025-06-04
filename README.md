# Forum Application (MVC, Spring, Hibernate)

_A simple web-based forum application where users can register, confirm their email, and participate in discussions. Administrators can manage content and moderate user interactions._

---

## Tech Stack

- **Language:** Java  
- **Build and Packaging:** Gradle (java, war)  
- **Framework:** Spring Framework 6 (core, MVC, transactions, ORM, security)  
- **ORM:** Hibernate 6 + Hibernate Validator + Hibernate JCache  
- **Caching:** JCache API + Ehcache 3  
- **Security:** Spring Security 6 + JJWT (JWT)  
- **Database:** MySQL (connector) in a Docker container  
- **Templating Engine:** Thymeleaf 3  
- **Application Server:** Apache Tomcat 11 (WAR deployment)  
- **Mail:** Jakarta Mail 2.0.1  
- **Lombok:** for simplifying Java code  
- **Logging:** SLF4J + Logback  
- **Testing:** JUnit 5 + Mockito 5  

---

## Project Overview

This project is a simple web-based forum.  
- Users can register, confirm their email, and log in.  
- After logging in, they can create new topics, post comments under topics, and archive their own topics.  
- Administrators have elevated privileges:  
  - Invite other admins by email  
  - Create or archive any topic  
  - Remove comments that violate rules  

---

## Architecture and Technologies

- **MVC (Model-View-Controller):**  
  Separates presentation, business logic, and data access layers for clear structure and maintainability.  

- **Spring Framework (without Spring Boot):**  
  - Dependency Injection (spring-context)  
  - MVC Controllers (spring-webmvc)  
  - Transaction Management (spring-tx)  
  - ORM Integration (spring-orm)  
  - Security (spring-security)  

- **Hibernate (ORM):**  
  - Core ORM (hibernate-core)  
  - Entity Validation (hibernate-validator)  
  - JCache Integration (hibernate-jcache)  

- **Caching:**  
  - JCache API (JSR-107)  
  - Ehcache 3 as the cache provider  

- **Security:**  
  - Spring Security 6 for session-based authentication and role-based access control  
  - JJWT (io.jsonwebtoken) for JWT handling  

- **Database:**  
  - MySQL (Connector/J) running in a Docker container  
  - Hibernate handles schema generation and data access  

- **Templating Engine:**  
  - Thymeleaf 3 to render server-side HTML views  

- **Application Server:**  
  - Packaged as a WAR file and deployed on Apache Tomcat 11  

- **Mail Service:**  
  - Jakarta Mail 2.0.1 to send account confirmation and admin invitation emails  

- **Utilities:**  
  - Lombok for boilerplate code reduction (getters, setters, constructors)  
  - SLF4J + Logback for flexible logging  

- **Testing:**  
  - JUnit 5 (Jupiter) for unit tests  
  - Mockito 5 for mocking and verifying behavior  

---
