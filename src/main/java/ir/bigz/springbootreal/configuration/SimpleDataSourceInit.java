package ir.bigz.springbootreal.configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

/**
 * {@link SimpleDataSourceInit} implement basicDataSource,
 * if you want use connection-pool use {@link HikariDataSourceInit}
 */
@Configuration
public class SimpleDataSourceInit {

    @Bean
    @ConfigurationProperties(prefix = "demo.datasource")
    public DataSourceProperties dataSourceProperties(){
        return new DataSourceProperties();
    }

//    @Bean(name = "SimpleDataSourceInit")
    public DataSource dataSource(){
        DataSourceProperties dataSourceProperties = dataSourceProperties();
        return DataSourceBuilder.create()
                .driverClassName(dataSourceProperties.getDriverClassName())
                .url(dataSourceProperties.getUrl())
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .build();
    }
}
