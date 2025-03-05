package com.hm.stock.modules.i18n;

import com.hm.stock.modules.common.LogicUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Configuration
public class I18nResolver implements LocaleResolver {

    @Autowired
    private HttpServletRequest request;

    public  Locale getLocale() {
        return resolveLocale(request);
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String language = request.getHeader("lang");
        Locale locale = new Locale("");
        if (LogicUtils.isNotBlank(language)) {
            if (language.contains("_")) {
                String[] split = language.split("_");
                if (split.length == 2) {
                    locale = new Locale(split[0], split[1]);
                }
            }else{
                locale = new Locale(language);
            }
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}
