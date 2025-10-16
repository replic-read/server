package com.rere.server.inter.dispatching.security;

import lombok.extern.slf4j.Slf4j;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springdoc.webmvc.ui.SwaggerWelcomeWebMvc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.URI;
import java.net.URL;
import java.util.function.Predicate;

@Slf4j
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

    @Value("${rere.baseurl}")
    private String clientBaseurl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String clientOrigin = "";
        try {
            URL url = URI.create(clientBaseurl).toURL();
            clientOrigin += url.getProtocol()
                            + "://"
                            + url.getHost();
            if (url.getPort() != -1) {
                clientOrigin += ":"
                                + url.getPort();
            }
        } catch (Exception e) {
            log.error("The provided client url '{}' is not a valid URL. No cors configuration could be set up.", clientBaseurl);
        }
        registry
                .addMapping("/**")
                .allowedOrigins(clientOrigin)
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
