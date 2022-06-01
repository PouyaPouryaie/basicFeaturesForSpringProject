package ir.bigz.springbootreal.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;
import java.util.Objects;

/**
 * you can define bean for message resource that it's read from specific path and inject bean in every class you want
 * or use default messageResource bean that read from spring config define in application.properties file
 */

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

    @Bean("loadErrorMessageSource")
    public ReloadableResourceBundleMessageSource errorCodeSourceDesc(){
        return loadMessageSource("classpath:lang/error/code", 20, null);
    }

    public ReloadableResourceBundleMessageSource loadMessageSource(String path, Integer timeToCache, String defaultEncoding){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(path);
        messageSource.setDefaultEncoding(Objects.isNull(defaultEncoding) ? encoding: defaultEncoding);
        messageSource.setCacheSeconds(Objects.isNull(timeToCache) ? 10 : timeToCache);
        return messageSource;
    }

    @Bean
    @Primary
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setDefaultEncoding(encoding);
        return messageSource;
    }
}
