package com.josepacheco.tcc.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Documentação da API")
                        .version("1.0.0")
                        .description("Documentação interativa da API usando Swagger/OpenAPI"))
//                .tags(List.of(
//                        new Tag().name("1. Listas de Consulta Simples: a) Medidas Básicas"),
//                        new Tag().name("1. Listas de Consulta Simples: b) Medidas Farmacêuticas")
                ;

    }
}
