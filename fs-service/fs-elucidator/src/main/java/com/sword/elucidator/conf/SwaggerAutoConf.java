package com.sword.elucidator.conf;

import io.swagger.annotations.Api;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger 自动配置
 *
 * @author Tan
 * @version 1.0 2020-11-02
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(prefix = "base", name = "swagger.enabled", matchIfMissing = true)
public class SwaggerAutoConf {

    @Bean
    @ConditionalOnMissingBean
    public SwaggerProperties swaggerProperties() {
        return new SwaggerProperties();
    }


    @Bean
    public Docket api(SwaggerProperties swaggerProperties) {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo(swaggerProperties)).select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .build()
                .pathMapping(swaggerProperties.getBasePath());
    }


    private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail()))
                .version(swaggerProperties.getVersion())
                .build();
    }
}
