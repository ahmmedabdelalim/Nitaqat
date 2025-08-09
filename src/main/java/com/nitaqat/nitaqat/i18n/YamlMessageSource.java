package com.nitaqat.nitaqat.i18n;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.Optional;

public class YamlMessageSource extends AbstractMessageSource implements InitializingBean {

    private final Map<String, Properties> propertiesPerLocale = new HashMap<>();
    private final String basename; // e.g. "classpath:/i18n/messages"
    private Locale defaultLocale = Locale.ENGLISH;

    public YamlMessageSource(String basename) {
        this.basename = basename;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadYamlFiles();
    }

    private void loadYamlFiles() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(basename + "_*.yml"); // messages_en.yml etc.
        for (Resource res : resources) {
            YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
            yamlFactory.setResources(res);
            yamlFactory.afterPropertiesSet();
            Properties props = yamlFactory.getObject();

            String filename = Optional.ofNullable(res.getFilename()).orElse("");
            String localeKey = extractLocaleFromFilename(filename); // "en" or "en_US"
            if (localeKey == null) {
                localeKey = defaultLocale.getLanguage();
            }
            propertiesPerLocale.put(localeKey, props);
            // also register short language (e.g. "en" from "en_US") for easier lookup
            String shortLang = localeKey.split("[-_]")[0];
            propertiesPerLocale.putIfAbsent(shortLang, props);
        }
    }

    private String extractLocaleFromFilename(String filename) {
        int underscore = filename.lastIndexOf('_');
        int dot = filename.lastIndexOf('.');
        if (underscore == -1 || dot == -1 || underscore >= dot) return null;
        return filename.substring(underscore + 1, dot); // e.g. "en" or "en_US"
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String msg = findMessage(code, locale);
        if (msg == null) return null;
        return new MessageFormat(msg, locale);
    }

    private String findMessage(String code, Locale locale) {
        if (locale != null) {
            // try multiple fallbacks: en-US, en_US, en
            String tag = locale.toLanguageTag(); // e.g. "en-US"
            Properties p = propertiesPerLocale.get(tag);
            if (p != null && p.getProperty(code) != null) return p.getProperty(code);

            String langCountry = locale.getLanguage();
            if (!locale.getCountry().isEmpty()) langCountry += "_" + locale.getCountry();
            p = propertiesPerLocale.get(langCountry);
            if (p != null && p.getProperty(code) != null) return p.getProperty(code);

            p = propertiesPerLocale.get(locale.getLanguage());
            if (p != null && p.getProperty(code) != null) return p.getProperty(code);
        }
        // fallback to default locale
        Properties p = propertiesPerLocale.get(defaultLocale.getLanguage());
        if (p != null) return p.getProperty(code);
        return null;
    }
}