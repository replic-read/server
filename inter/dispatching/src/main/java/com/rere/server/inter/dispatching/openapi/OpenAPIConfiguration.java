package com.rere.server.inter.dispatching.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration that provides the open api metadata.
 */
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI openAPI(BuildProperties props) {
        return new OpenAPI()
                .info(new Info()
                        .title("Replic-Read REST-API")
                        .description("This is the open-api documentation for the REST-API exposed by the backend server of the Replic-Read system")
                        .version(props.getVersion())
                        .contact(new Contact()
                                .email("simon@bumiller.me")
                                .name("Simon Bumiller")
                                .url("https://github.com/SimonBumiller"))
                        .license(new License()
                                .name("Apache-2.0 license")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .components(new Components()
                        .addSecuritySchemes("jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                )
                .security(List.of(new SecurityRequirement().addList("jwt")));
    }

}
