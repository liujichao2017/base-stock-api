package com.hm.stock.modules.i18n;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Slf4j
@Component
public class I18nUtil {


    @Value("${spring.messages.basename}")
    private String basename;

    private final I18nResolver resolver;

    private static I18nResolver customResolver;

    private static String path;

    public I18nUtil(I18nResolver resolver) {
        this.resolver = resolver;
    }

    @PostConstruct
    public void init() {
        setBasename(basename);
        setResolver(resolver);
    }

    public static String getLang(){
        return customResolver.getLocale().toString();
    }

    public static String getMessage(String code) {
        Locale locale = customResolver.getLocale();
        return getMessage(code, null, "", locale);
    }

    public static String getMessage(String code, Object[] ages) {
        Locale locale = customResolver.getLocale();
        return getMessage(code, ages, "", locale);
    }

    public static String getMessage(String code, String local) {
        Locale locale = new Locale(local);
        return getMessage(code, null, "", locale);
    }

    public static String getMessage(String code, Locale locale) {
        return getMessage(code, null, "", locale);
    }

    public static String getMessage(String code, Object[] ages, String defaultMessage, Locale locale) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename(path);
        String content = null;
        try {
            content = messageSource.getMessage(code, ages, defaultMessage, locale);
        } catch (Exception e) {
            log.error("获取国际化内容失败: locale: {}, code: {}", locale, code, e);
            content = defaultMessage;
        }
        return content;
    }


    private void setBasename(String basename) {
        I18nUtil.path = basename;
    }

    private void setResolver(I18nResolver resolver) {
        I18nUtil.customResolver = resolver;
    }

}
