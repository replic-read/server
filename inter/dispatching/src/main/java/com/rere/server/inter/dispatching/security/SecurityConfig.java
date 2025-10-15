package com.rere.server.inter.dispatching.security;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.service.AccountService;
import com.rere.server.inter.execution.AuthPrincipalSupplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static com.rere.server.inter.dispatching.WebMvcConfig.BASE_PATH_PREFIX;

/**
 * Security configuration.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Endpoints that <b>may</b> not require authentication, depending on the config.
     * <br>
     * If they do, we check in by manual authorization.
     */
    private static final Endpoint[] UNAUTHENTICATED_REST_ENDPOINTS = {
            new Endpoint("/accounts/partial/", HttpMethod.GET),
            new Endpoint("/replics/", HttpMethod.GET),
            new Endpoint("/replics/", HttpMethod.POST),
            new Endpoint("/replics/{id}/content/", HttpMethod.GET),
            new Endpoint("/reports/", HttpMethod.POST),
            new Endpoint("/server-config/", HttpMethod.GET),
            new Endpoint("/swagger-ui/**", HttpMethod.GET),
            new Endpoint("/swagger-ui.html", HttpMethod.GET),
            new Endpoint("/auth/submit-email-verification/", HttpMethod.POST),
            new Endpoint("/auth/refresh/", HttpMethod.POST),
            new Endpoint("/auth/login/", HttpMethod.POST),
            new Endpoint("/auth/signup/", HttpMethod.POST)
    };

    /**
     * Endpoints that <b>may</b> not require authentication, depending on the config.
     * <br>
     * If they do, we check in by manual authorization.
     */
    private static final Endpoint[] UNAUTHENTICATED_ENDPOINTS = {
            new Endpoint("/v3/api-docs", HttpMethod.GET),
            new Endpoint("/v3/api-docs.yaml", HttpMethod.GET),
            new Endpoint("/v3/api-docs/**", HttpMethod.GET),
            new Endpoint("/swagger-ui.html", HttpMethod.GET),
            new Endpoint("/swagger-ui/**", HttpMethod.GET),
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider provider, @Qualifier("rereAuthFilter") Set<OncePerRequestFilter> authFilters) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Safe to disable because we use JWT's.
                .authorizeHttpRequests(config -> {
                    for (Endpoint endpoint : UNAUTHENTICATED_REST_ENDPOINTS) {
                        config
                                .requestMatchers(endpoint.method(), BASE_PATH_PREFIX + endpoint.endpoint())
                                .permitAll();
                    }
                    for (Endpoint endpoint : UNAUTHENTICATED_ENDPOINTS) {
                        config
                                .requestMatchers(endpoint.method(), endpoint.endpoint())
                                .permitAll();
                    }

                    config
                            .anyRequest()
                            .authenticated();
                        }
                )
                .sessionManagement(context -> context
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(provider);

        authFilters.forEach(filter -> http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class));

        return http.build();
    }

    /**
     * Provides the user details service that delegates to our own account service.
     */
    @Bean
    public UserDetailsService userDetailsService(AccountService accountService) {
        return email -> accountService
                .getAccounts(null, null, null, null)
                .stream()
                .filter(account -> account.getEmail().equals(email))
                .findFirst()
                .map(account -> new UserDetailsImpl(account.getPasswordHash(), account.getEmail()))
                .orElseThrow(() -> new UsernameNotFoundException("Did not find a user with email '" + email + "'"));
    }

    /**
     * Creates an AuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder encoder, UserDetailsService service) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(service);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public AuthPrincipalSupplier authPrincipalSupplier() {
        return () -> {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return principal instanceof Account account ? account : null;
        };
    }

    /**
     * Simple implementation of the UserDetails interface.
     */
    @Getter
    @AllArgsConstructor
    private static class UserDetailsImpl implements UserDetails {

        /**
         * The authorities.
         */
        private final Collection<? extends GrantedAuthority> authorities = Collections.emptySet();

        /**
         * The encrypted password.
         */
        private final String password;

        /**
         * The username.
         */
        private final String username;
    }

    /**
     * A type-static 2-tuple because arrays can't be generic.
     * @param endpoint The endpoint value.
     * @param method The method value.
     */
    public record Endpoint(String endpoint, HttpMethod method) {
    }

}
