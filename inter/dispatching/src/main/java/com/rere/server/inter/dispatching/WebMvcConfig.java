package com.rere.server.inter.dispatching;

import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springdoc.webmvc.ui.SwaggerWelcomeWebMvc;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.function.Predicate;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

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
}
