package com.nitaqat.nitaqat.locale;


import com.nitaqat.nitaqat.i18n.YamlMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Configuration
public class LocalizationConfig {

    @Bean
    public MessageSource messageSource() {
        YamlMessageSource ms = new YamlMessageSource("classpath:/i18n/messages");
        ms.setDefaultLocale(Locale.ENGLISH);
        return ms;
    }

    // optional: use Accept-Language header by default
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
}
