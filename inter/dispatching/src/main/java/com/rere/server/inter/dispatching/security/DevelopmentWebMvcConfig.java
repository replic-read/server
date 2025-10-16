package com.rere.server.inter.dispatching.security;

import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springdoc.webmvc.ui.SwaggerWelcomeWebMvc;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.function.Predicate;

@Profile("dev")
@Configuration
public class DevelopmentWebMvcConfig implements WebMvcConfigurer {

    /**
     * The base rest path for our controllers.
     */
    public static final String BASE_PATH_PREFIX = "/api/v1";
    private static final Predicate<Class<?>> predicate = clazz ->
            clazz.isAnnotationPresent(RestController.class) &&
            !OpenApiWebMvcResource.class.equals(clazz) &&
            !SwaggerConfigResource.class.equals(clazz) &&
            !SwaggerWelcomeWebMvc.class.equals(clazz);

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(BASE_PATH_PREFIX, predicate);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
