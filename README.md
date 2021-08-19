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
- decoupling layer
    - controller, service and repository layer for easy extend

## Run guide
### Run for develop and debug: <br>
1 - check active profile on dev <br>
2 - run postgre sql on 5432 and create database myapp <br>
3 - run app <br>

### Run as docker image: <br>
1 - check active profile on docker and package project <br>
2 - run docker build for create app image <br>
3 - run docker compose run for running app <br>


## exception feature

1 - use AppException for define your own error Exception <br>
2 - user HttpExceptionModel for define your own error message and show to client <br>
3 - use HttpErrorCode for unifying error code in project <br>