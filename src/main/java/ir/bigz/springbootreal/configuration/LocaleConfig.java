package ir.bigz.springbootreal.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Value("${spring.messages.basename}")
    private String basename;

    @Value("${spring.messages.encoding}")
    private String encoding;

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localResolver = new AcceptHeaderLocaleResolver();
        localResolver.setDefaultLocale(Locale.US);
        return localResolver;
    }

    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setDefaultEncoding(encoding);
        return messageSource;
    }
}
