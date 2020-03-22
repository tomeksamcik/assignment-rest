package com.roche.assignment;

import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.builders.RequestHandlerSelectors.any;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metadata())
                .select()
                .apis(any())
                .paths(regex("/products.*").or(regex("/orders.*")))
                .build();
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Roche Assignment API")
                .description("Documentation has been generated automatically by Spring Boot.")
                .version("0.0.1-SNAPSHOT")
                .build();
    }

}
