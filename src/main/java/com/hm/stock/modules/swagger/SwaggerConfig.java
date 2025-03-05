package com.hm.stock.modules.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class SwaggerConfig {
    private static final String SERVICE_URL = "http://127.0.0.1:7004/tj4/doc.html";
    private static final String API_INFO_TITLE = "软件接口文档";
    private static final String API_INFO_VERSION = "V1.0";
    private static final String API_INFO_DESCRIPTION = "Api接口列表";
    private static final String API_INFO_LICENSE = "2024年度内部文档,违拷必究.";

    @Bean
    public GroupedOpenApi app() {
        return GroupedOpenApi.builder()
                .group("前端接口")
                .displayName("前端接口")
                .pathsToMatch("/app/**")
                .build();
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("公共接口")
                .displayName("公共接口")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_INFO_TITLE)
                        .description(API_INFO_DESCRIPTION)
                        .version(API_INFO_VERSION)
                        .contact(new Contact().name("Keyidea").email("support@keyidea.cn"))
                        .license(new License().name(API_INFO_LICENSE).url(SERVICE_URL))
                );
    }


}
