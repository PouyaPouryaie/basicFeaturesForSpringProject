package ir.bigz.springbootreal.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * {@link HikariDataSourceInit} use for build connection-pool base on HikariCp with customize properties,
 * and then added to {@link DataSourceConfiguration} class as datasource for use in app.
 * also if you want use for production uncomment line 32 and comment line 33 and 34
 * and if you want test with testContainer uncomment line 33 and 34 and comment line 32
 *
 * DataBase password encryption with JASYPT:
 * first of all use jasypt to encrypted db-password and added into application.properties file,
 * then uncomment line 34 (encryptorPassword)
 * at the end add --jasypt.encryptor.password={secret-key} in program environment
 */

//todo i must be find better solution for use hikariDataSource between production and test with testContainer

@Configuration
public class HikariDataSourceInit{

    @Autowired
    private Environment env;

    @Value("${jasypt.encryptor.password}")
    String encryptorPassword;

    @Bean(name = "HikariDataSourceInit")
    public DataSource dataSource(){
       HikariConfig hikariConfig = new HikariConfig(hikariProperties()); // use for production
        // HikariConfig hikariConfig = new HikariConfig();
        // hikariConfig.setDataSource(InitDataSource());
        int cpuCores = Runtime.getRuntime().availableProcessors();
        hikariConfig.setMaximumPoolSize(cpuCores * 4);
        hikariConfig.setConnectionTimeout(Long.parseLong(env.getProperty("hikari.connectionTimeout")));
        hikariConfig.setIdleTimeout(Long.parseLong(env.getProperty("hikari.idleTimeout")));
        hikariConfig.setMaxLifetime(Long.parseLong(env.getProperty("hikari.maxLifetime")));
        return new HikariDataSource(hikariConfig);
    }

    protected DataSource InitDataSource(){
        String passwordProperty = env.getProperty("demo.datasource.password");
        String plainPassword = "";
        if(passwordProperty.contains("ENC(")){
            plainPassword = decryptPassword(passwordProperty);
        }else{
            plainPassword = passwordProperty;
        }
        return DataSourceBuilder.create()
                .driverClassName(env.getProperty("demo.datasource.driver-class-name"))
                .url(env.getProperty("demo.datasource.url"))
                .username(env.getProperty("demo.datasource.username"))
                .password(plainPassword)
                .build();
    }

    protected Properties hikariProperties(){
        String passwordProperty = env.getProperty("hikari.dataSource.password");
        String plainPassword = "";
        if(passwordProperty.contains("ENC(")){
            plainPassword = decryptPassword(passwordProperty);
        }else{
            plainPassword = passwordProperty;
        }
        Properties hikariProps = new Properties();
        hikariProps.setProperty("dataSourceClassName", env.getProperty("hikari.dataSourceClassName"));
        hikariProps.setProperty("dataSource.user", env.getProperty("hikari.dataSource.user"));
        hikariProps.setProperty("dataSource.password", plainPassword);
        hikariProps.setProperty("dataSource.databaseName", env.getProperty("hikari.dataSource.databaseName"));
        return hikariProps;
    }

    @Bean
    protected String decryptPassword(String value) {
        var textEncryptor = new StrongTextEncryptor();
        textEncryptor.setPassword(encryptorPassword);
        return textEncryptor.decrypt(value.substring(4, value.length() - 1));
    }

}
