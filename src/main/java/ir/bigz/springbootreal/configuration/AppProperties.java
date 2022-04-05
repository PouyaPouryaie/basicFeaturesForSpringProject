package ir.bigz.springbootreal.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties("app")
@Data
public class AppProperties {

    private Map<String, String> jobs = new HashMap<>();
}
