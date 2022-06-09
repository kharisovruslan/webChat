# Web Chat

---

## Description
Web chat is a application for chatting all messages will be save on server in database. 
This application was created by Spring Framework. Application used for: exchange text messages and files, 
keep all messages in database on server and find written messages in history. 
Administrator (first user get Administrator role) can change users names, passwords, and tokens 
(used for fast users authorization), see statistics about users and remove self messages. Then application 
open in brower Messages update automatically. Users can self registration. In browser new message loading automatically 
with javascript from server. Every 15 seconds web page check if where a new messages and if find show them. 
Application is pageable on page show five message if there more messages then you can choose page. 
Use localization all you languages.

![Application window](./src/test/resources/)
## Features
 + Exchange messages
 + Save date last visited user if you cursor over username you can see date of last visit user
 + Color of frame active users is green for other red
 + Exchange files (file size limited **8M** if you need more you can change limite in file application.properties) 
files for name get uuid and they names saves in database
 + Save all messages in database for keeping and search (default use embedded *hsqldb* but you can use other)
 + Send messages for offline users (then user connect then messages will be showed)
 + Application has api for observe new message if new message will be find other program ***webChatNotification*** show them
 + Authorization on password and token you can make a bookmarks or links for automatic authorization
 + Support localization
 + Use actuator you can go to [http://localhost:8080/actuator](http://localhost:8080/actuator) and see information of application

## Warning
This application creates for local network and all users of network can connect and register here.

If you want protect your data you need config application to use flyway and use external database as Postgresql.
For config application you need make directory config and input file application.properties with you
settings in this directory. As example you can use application.properties from this project.

## Quick start

### Compile
You can make application from source you need for assembling JDK 11 or later, maven, git:

```bash
    git clone https://github.com/kharisovruslan/webChat
    cd webChat
    mvn package
    cd target
```

### Start application
To start start jar file. Application create directory database with embedded database and uploadfiles with files upload 
to server with messages.

```bash
    java -jar webChat-2.0.0-SNAPSHOT.jar
```

### Open application
After Application was run you can open in web browser page [http://localhost:8080/](http://localhost:8080/). 
You can register user with administrator role (have access to change username password and tokens). 
Next users got user role. Application listen all you network interfaces you can connect from your local network.

For it you need install [jdk 17](https://adoptium.net/) [maven](https://maven.apache.org/) [git](https://git-scm.com/) 
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
  - Java 11:
    - Optionals, LocalDateTime
- HSQLDB default embedded database you can use postgresql or ther database choose in application.properties and in pom.xml choose driver
- Flyway for control database
- Database
  - HSQLDB
- HTML:
  - Thymeleaf
  - data validation in login form and registration form
- CSS:
  - BootStrap
- JavaScript
  - JQuery