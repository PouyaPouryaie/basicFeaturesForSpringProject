package ir.bigz.springbootreal.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * {@link HikariDataSourceInit} use for build connection-pool base on HikariCp with customize properties,
 * and then added to {@link DataSourceConfiguration} class as datasource for use in app.
 */

@Configuration
public class HikariDataSourceInit{

    @Autowired
    private Environment env;

    @Bean(name = "HikariDataSourceInit")
    public DataSource dataSource(){
        HikariConfig hikariConfig = new HikariConfig(hikariProperties());
        int cpuCores = Runtime.getRuntime().availableProcessors();
        hikariConfig.setMaximumPoolSize(cpuCores * 4);
        hikariConfig.setConnectionTimeout(Long.parseLong(env.getProperty("hikari.connectionTimeout")));
        hikariConfig.setIdleTimeout(Long.parseLong(env.getProperty("hikari.idleTimeout")));
        hikariConfig.setMaxLifetime(Long.parseLong(env.getProperty("hikari.maxLifetime")));
        return new HikariDataSource(hikariConfig);
    }

    protected Properties hikariProperties(){
        Properties hikariProps = new Properties();
        hikariProps.setProperty("dataSourceClassName", env.getProperty("hikari.dataSourceClassName"));
        hikariProps.setProperty("dataSource.user", env.getProperty("hikari.dataSource.user"));
        hikariProps.setProperty("dataSource.password", env.getProperty("hikari.dataSource.password"));
        hikariProps.setProperty("dataSource.databaseName", env.getProperty("hikari.dataSource.databaseName"));
        return hikariProps;
    }

}
