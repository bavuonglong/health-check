# Health Check Service

Simple health check service Rest API, poller service using Spring Boot 2; web application using ReactJS 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

The project uses some libraries and implementations with reasons:

* Flyway: create schema and tables for the database instead of letting Hibernate do that part
(does not support every database-specific DDL structure like partial indexes, user-defined types, triggers, checks, etc),
we do it by writing sql script. Database is the most crucial part in the system, we should take full responsibilities
for creation and maintenance, especially on production environment. Also, Flyway lets you version control incremental
changes so that you can migrate it to a new version easily and confidently.

* MapStruct: mapping between DTO and DAO, reduce boilerplate code converting.

### Prerequisites


```
JDK 8

Docker

Gradle

NodeJS
```

### Installing

From the project directory, run below command in terminal to:

* start the MySQL Database:

```
docker-compose -f mysql-phpmyadmin.yml up -d
```

* start the rest-api up:

```
cd service-provider && gradle bootRun
```

* start the poller up:

```
cd poller && gradle bootRun
```

* start the web-app up:

```
cd web-app && npm install && npm start
```

After the project start up successfully, you can 

* access Swagger Rest API document from:  [http://localhost:8081/service-provider/swagger-ui/index.html](http://localhost:8081/service-provider/swagger-ui/index.html)
* access web application from:  [http://localhost:3000](http://localhost:3000)
* with MySQL database, you can view the database from phpMyAdmin by accessing: [http://localhost:8085](http://localhost:8085)


## Author

**Cuong Ngo**