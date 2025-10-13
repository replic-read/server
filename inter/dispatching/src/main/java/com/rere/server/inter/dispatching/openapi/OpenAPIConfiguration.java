package com.rere.server.inter.dispatching.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.annotations.OpenAPI30;

@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        name = "jwt"
)
@OpenAPIDefinition(
        info = @Info(
                title = "Replic-Read REST-API",
                description = "This is the open-api documentation for the REST-API exposed by the backend server of the Replic-Read system",
                contact = @Contact(
                        email = "simon@bumiller.me",
                        name = "Simon Bumiller",
                        url = "https://github.com/SimonBumiller"
                ),
                license = @License(
                        name = "Apache-2.0 license",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                ),
                version = "1.0.0"
        ),
        security = @SecurityRequirement(name = "jwt")
)
@OpenAPI30
public class OpenAPIConfiguration {
}
