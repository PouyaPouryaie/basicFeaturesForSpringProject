# BasicFeaturesForSpringProject

This project, implement instance of basic feature, <br> you maybe want use for App base on Spring-boot <br>

### Note for upgrade to 2.4.X 
1) for active profile config: <br>
for before 2.4.x you can activate profile in pom file and add <b>@spring.profiles.active@</b> in application.properties <br>
and then properties apply in project base on profile that active. <br>
for 2.4.x or above
    - if you want to use same as old solution you must add ```spring.config.use-legacy-processing=true``` in application.properties
    - if you want to migrate in new approach please check https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-Config-Data-Migration-Guide


## Features: 
- spring-web
- spring-jpa
    - with custom configuration for PersistenceContext
- Connection-pool
    - HikariCp with Possibility of customization
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
- TestUtils
  - generator data with javaFaker
  - unit and integration test with <b>Spock</b> Framework
  - SwaggerAPI for show detail of Rest endpoint
  - TestContainer use for run db as dockerImage for test, and after that image is stop
- decoupling layer
    - controller, service and repository layer for easy extend
- flexible search
    - use CriteriaQuery and CriteriaBuilder for search base on Entity
- jasypt
    - use for encrypt password and added to config file, then decrypted pass in runtime
- Jobs
    - use Spring TaskScheduler for run jobs 
- Message Source
    - use message-source to define message in one file and use in whole project
    - use locale for config language of app for show message from different source

## Run guide
### Run for develop and debug: <br>
1 - check active profile on dev <br>
2 - run postgre sql on 5432 and create database myapp <br>
3 - run app <br>
4 - app.generator.enabled properties set to true, when you want to generate fake data <br>

### Run as docker image: <br>
1 - check active profile on docker and package project <br>
2 - create image from app: <b>docker build -t springapp:latest . </b>
<br>
3 - run app: <b>docker compose run</b> <br>


## Features Explain: 
### exception feature

1 - use AppException for define your own error Exception <br>
2 - user HttpExceptionModel for define your own error message and show to client <br>
3 - use HttpErrorCode for unifying error code in project <br>

### validation feature

1 - create your own validation type <br>
2 - define your own validation process for entity <br>
3 - use "@validator" annotation on field of entity for validate <br>
4 - log for validation and save error-message in log file <br>

### search v2 base on NativeQuery feature
1 - query clause, order for result and pagination can define in client Request as queryString and handle in AbstractController <br>
2 - buildNativeQueryCondition method in Utils class use for create native query base on request queryString <br>
3 - dynamic order and direction for sort data handle in daoRepository <br>
4 - more detail about search feature, see searchEngineV2.drawio <br> 
5 - sample rest search request: <br>
```http://localhost:9090/api/v2/user/search?firstName=h&orderBy=firstName_asc, gender_desc&page=1&size=5```

### search v3 base on CriteriaBuilder feature
1 - query clause, order for result and pagination can define in client Request as queryString and handle in AbstractController <br>
2 - define Quartet from javaTuples lib that use for define name of field from client, name of field in model, sqlOperation and sqlCondition <br>
3 - define Sort.Order object of spring with getSortOrderFromPagedQuery method to use for order by Clause<br>
4 - then use getUserQueryWithCriteriaBuilder in userRepository for create query and get Result<br>
5 - sample rest search request: <br>
```http://localhost:9090/api/v2/user/search?firstName=h&orderBy=firstName_asc, gender_desc&page=1&size=5```

### test feature
1 - generate sample data baseOn model with javaFaker <br>
2 - Spock framework use for test <br>
3 - sample test define in test directory with groovy language <br>
4 - Swagger Api for show details of endpoints <br>
5 - In order to change swagger url, you must be change springdoc.swagger-ui.path property in application-dev.properties <br>
6 - TestContainer for run db as dockerImage, properties config exist in application-test.properties <br>

### dataSource feature
1 - choose between use simpleDataSource or HikariCp datasource <br>
2 - you can customize properties for dataSource in application.properties
3 - you can define batch config for use execute batch for update and insert to db

### database password generator
if you want to use encrypt password in config file for access database, you must follow below statement. <br>

1 - use jasypt for generate encrypted database password with secret-key
~~~
java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input={password} password={secret-key} algorithm=PBEWithMD5AndTripleDES
~~~
2 - add output into application.properties <br>
note: I use '<b>ENC()<b>' as convention to use check password for decrypt or not. <br>

normal datasource:
~~~
demo.datasource.password="ENC({output})"
~~~
hikari datasource:
~~~
hikari.dataSource.password="ENC({output})"
~~~
3 - run program and add in program environment secret-key:
~~~
--jasypt.encryptor.password={secret-key}
~~~

### Jobs Run Tool
1) define class implement Runnable in order to execute job base on schedule 
2) define bean (AlertServiceJob) in BeanConfig class for your jobs
3) define trigger time in properties ```(ex: app.jobs.alertServiceJob=0 */1 * ? * *)``` file
4) @EnableScheduling on SpringApp

notice: that name of bean in BeanConfig must be equal with name of key in properties file.

### Message Source
1) define LocaleConfig to implement  Base config to define messageSource
   1) how load message source (ex: loadMessageSource method in LocaleConfig class)
   2) how define bean for use message source for specific category (ex: errorCodeSourceDesc method in LocaleConfig class)
   3) how define config for resolve locale (ex: localeResolver method in LocaleConfig class)
2) add message key from message-source bundle into code (ex: SampleExceptionType enum class)
3) the code is written use locale by header, and you should set Accept-Language in request header
4) define message and messageContainer for use to send identical messageResponse for error or normal message
