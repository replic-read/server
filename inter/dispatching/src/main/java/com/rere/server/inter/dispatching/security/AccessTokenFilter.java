package com.rere.server.inter.dispatching.security;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * Access-token filter that gives every request that presents a sensible authentication token a spring-security compatible authorization.
 */
@Component
public class AccessTokenFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final AuthenticationService authService;

    @Autowired
    public AccessTokenFilter(AuthenticationService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader(AUTH_HEADER_NAME) == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = request.getHeader(AUTH_HEADER_NAME).replaceFirst(PREFIX, "");

        Optional<Account> authentication = authService.authenticateWithJwt(jwt);

        if (authentication.isPresent()) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authentication.get(), null, Collections.emptySet());
            token.setDetails(authentication.get());
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        filterChain.doFilter(request, response);
    }
}
