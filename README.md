# Web Chat
## Description
Web chat is a simple application create by Spring Framework. This web application enables: exchange text
messages and files, as well as browsing, Administrator (first registration user get Administrator role) can change
passwords for other users. Messages update automatically. Users can self registration.
## Technologies
- Java 11:
  - Spring Framework:
    - Spring MVC:
      - application-level on the basis on design pattern: model-view-controller
    - Spring Data:
      - using JPQL and ready-made methods from `JpaRepository` to creating, reading, updating and deleting data
      - implementation of native queries
    - Spring Security:
      - own login and registration form with authentication of users on the basis of database
    - Spring Boot:
      - automatic configuration and launching application 
  - JPA & Hibernate:
    - specifying relations between entities in database and parameters of columns in tables
  - Java 11 SE:
    - Optionals, LocalDateTime
- HSQLDB default embedded database you can use postgresql or ther database choose in application.properties and in pom.xml choose driver
- Flyway for control database
- HTML:
  - Thymeleaf
  - data validation in login form and registration form
- CSS:
  - BootStrap
- JavaScript
  - JQuery
