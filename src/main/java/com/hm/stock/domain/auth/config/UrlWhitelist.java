package com.hm.stock.domain.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("url")
public class UrlWhitelist {
    private List<String> whitelist;

    private final AntPathMatcher matcher = new AntPathMatcher();

    public boolean contains(String requestUri) {
        for (String url : whitelist) {
            if (matcher.match(url, requestUri)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(String url, String requestUri) {
        return matcher.match(url, requestUri);
    }


}
