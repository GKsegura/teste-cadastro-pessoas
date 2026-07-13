package br.com.gksegura.cadastropessoas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI apiInfo() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API de Cadastro de Pessoas")
                                                .description("Teste técnico - cadastro, listagem e exclusão de pessoas. "
                                                                + "Desenvolvido por José Segura (github.com/GKsegura).")
                                                .version("1.0")
                                                .contact(new Contact()
                                                                .name("José Segura - Portfólio")
                                                                .url("https://gksegura.netlify.app")))
                                .externalDocs(new ExternalDocumentation()
                                                .description("Repositório do projeto no GitHub")
                                                .url("https://github.com/GKsegura/teste-cadastro-pessoas"));
        }
}