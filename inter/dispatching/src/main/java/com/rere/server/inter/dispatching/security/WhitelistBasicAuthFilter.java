package com.rere.server.inter.dispatching.security;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

/**
 * A {@link OncePerRequestFilter} that implements basic authentication with a username whitelist:
 * when a client tries to authenticate with a username that does not belong to an account, we simply
 * create an admin-account for that user with the username and password that were presented.
 * <br><br>
 * This is very insecure for a normal application, but eases authentication for the load tests.
 */
@Profile("load-test")
@Qualifier("rereAuthFilter")
@Component
public class WhitelistBasicAuthFilter extends OncePerRequestFilter {

    private static final String WHITELISTED_USERNAME_PREFIX = "load-test-user-";
    private static final String MOCK_EMAIL_FORMAT = "%s@example.com";

    private static final String HEADER_NAME = "Authorization";
    private static final String PREFIX = "Basic ";
    private static final String DELIMITER = ":";

    private final AuthenticationService authService;

    @Autowired
    private WhitelistBasicAuthFilter(AuthenticationService authService) {
        this.authService = authService;
    }

    private static String extractUsername(String basicContent) {
        if (basicContent.contains(DELIMITER)) {
            return basicContent.split(DELIMITER)[0];
        }
        return null;
    }

    private static String extractPassword(String basicContent) {
        if (basicContent.contains(DELIMITER)) {
            return basicContent.split(DELIMITER)[1];
        }
        return null;
    }

    private static String createEmailAddress(String username) {
        return MOCK_EMAIL_FORMAT.formatted(username);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_NAME) != null ? request.getHeader(HEADER_NAME) : "";
        String basicContent = authHeader.replace(PREFIX, "");
        String decodedContent;
        try {
            byte[] decodedArray = Base64.getDecoder().decode(basicContent.getBytes(StandardCharsets.UTF_8));
            decodedContent = new String(decodedArray, StandardCharsets.UTF_8);
        } catch (Exception e) {
            decodedContent = "";
        }

        String username = extractUsername(decodedContent);
        String password = extractPassword(decodedContent);

        if (username == null || password == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Account account = authService.authenticateWithCredentials(null, username, password)
                .orElse(null);

        if (account == null && username.startsWith(WHITELISTED_USERNAME_PREFIX)) {
            try {
                account = authService.createAccount(createEmailAddress(username), username, password, 0,
                        true, true, false, true);
            } catch (DomainException e) {
                // Nothing is done, account is still null.
            }
        }

        if (account != null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(account, null, Collections.emptySet());
            token.setDetails(account);
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        filterChain.doFilter(request, response);
    }
}
