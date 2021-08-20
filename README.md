# BasicFeaturesForSpringProject

this project, implement instance of basic feature, <br> you maybe want use for App base on Spring-boot <br>

## basicFeature
- spring-web
- spring-jpa
    - with custom configuration for PersistenceContext 
- Ehcache 
    - customize cache config and implement log for cache
- Log4j2 
    - logging and config log-message
- Exception management
- MapStruct
    - mapper tools for map view to entity and vice-versa
- Validation handler
    - use annotation for validation
    - use log for validation error
    - define your validation and use for model
- decoupling layer
    - controller, service and repository layer for easy extend

## Run guide
### Run for develop and debug: <br>
1 - check active profile on dev <br>
2 - run postgre sql on 5432 and create database myapp <br>
3 - run app <br>

### Run as docker image: <br>
1 - check active profile on docker and package project <br>
2 - run docker build command for create app image : docker build -t springapp:latest .
<br>
3 - run docker compose run command for running app <br>


## Features
### exception feature

1 - use AppException for define your own error Exception <br>
2 - user HttpExceptionModel for define your own error message and show to client <br>
3 - use HttpErrorCode for unifying error code in project <br>

### validation feature

1 - create your own validation type <br>
2 - define your own validation process for entity <br>
3 - use "@validator" annotation on field of entity for validate <br>
4 - log for validation and save error-message in log file <br>