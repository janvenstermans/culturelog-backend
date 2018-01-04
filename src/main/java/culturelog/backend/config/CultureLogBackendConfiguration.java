package culturelog.backend.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * @author Jan Venstermans
 */
@Configuration
public class CultureLogBackendConfiguration {

    /**
     * Determine which locale is currently being used, based 1) on the session, 2) cookies, the Accept-Language header, or a fixed value.
     * @return
     * @see http://www.baeldung.com/spring-boot-internationalization
     */
    @Bean
    public LocaleResolver localeResolver() {
        CultureLogSessionLocaleResolver slr = new CultureLogSessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    @Bean
    public MessageSource cultureLogControllerMessages() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/cultureLogControllerMessages");
        return messageSource;
    }

    @Bean
    public MessageSource cultureLogMessages() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/cultureLogMessages");
        return messageSource;
    }
}
