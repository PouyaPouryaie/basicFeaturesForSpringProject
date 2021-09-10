package ir.bigz.springbootreal;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class})
@EnableTransactionManagement
public class SpringbootRealApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootRealApplication.class, args);
	}

	@Autowired
	ObjectMapper mapper;

    @Bean
    public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption, @Value("${application-version}") String appVersion) {
     return new OpenAPI()
          .info(new Info()
          .title("Sample Application API")
          .version(appVersion)
          .description(appDesciption)
          .termsOfService("http://swagger.io/terms/")
          .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }



}
