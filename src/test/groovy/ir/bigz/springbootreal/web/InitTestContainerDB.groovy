package ir.bigz.springbootreal.web

import ir.bigz.springbootreal.configuration.DataSourceConfiguration
import ir.bigz.springbootreal.configuration.HikariDataSourceInit
import ir.bigz.springbootreal.configuration.SimpleDataSourceInit
import ir.bigz.springbootreal.configuration.WebConfiguration
import ir.bigz.springbootreal.dal.UserRepositoryImpl
import ir.bigz.springbootreal.dto.entity.User
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Specification

@Testcontainers
@ContextConfiguration(classes = [UserRepositoryImpl.class, User.class,
        DataSourceConfiguration.class, WebConfiguration.class, SimpleDataSourceInit.class, HikariDataSourceInit.class], initializers = InitTestContainerDB.class)
@SpringBootTest(properties = "spring.profiles.active:test")
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class])
@EnableTransactionManagement
class InitTestContainerDB extends Specification implements ApplicationContextInitializer<ConfigurableApplicationContext>{

    static PostgreSQLContainer postgreSQLContainer

    @Override
    void initialize(ConfigurableApplicationContext configurableApplicationContext) {

        final Properties properties = new Properties()
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader()
            String resolvedLocation = configurableApplicationContext.environment.resolveRequiredPlaceholders("classpath:application-test.properties");
            Resource resource = resourceLoader.getResource(resolvedLocation)
            properties.load(new FileInputStream(resource.file))
        }
        catch (IllegalArgumentException | FileNotFoundException | UnknownHostException | SocketException ex) {
            throw ex
        }

        DockerImageName myImage = DockerImageName.parse((String)properties.getProperty("demo.datasource.dockerImageName")).
                asCompatibleSubstituteFor("postgres")
        postgreSQLContainer = new PostgreSQLContainer<>(myImage)
                .withExposedPorts(Integer.parseInt(properties.getProperty("demo.datasource.imagePort")))
                .withPassword((String)properties.getProperty("demo.datasource.password"))
                .withDatabaseName((String)properties.getProperty("demo.datasource.dbName"))
        postgreSQLContainer.start()
    }
}
