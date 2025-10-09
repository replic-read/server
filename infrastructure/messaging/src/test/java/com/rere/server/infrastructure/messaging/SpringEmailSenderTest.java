package com.rere.server.infrastructure.messaging;

import com.rere.server.domain.messaging.EmailSender;
import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AuthToken;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.domain.model.impl.AuthTokenImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * tests for the {@link SpringEmailSender} class.
 */
@ExtendWith(MockitoExtension.class)
class SpringEmailSenderTest {

    private static final String BASE_URL = "https://example.com";
    private static final String EMAIL_USERNAME = "noreply@example.com";
    private JavaMailSender mailSender;
    private SpringTemplateEngine templateEngine;
    private EmailSender subject;

    @BeforeEach
    void setUp() {
        mailSender = Mockito.mock(JavaMailSender.class);
        templateEngine = Mockito.mock(SpringTemplateEngine.class);
        subject = new SpringEmailSender(mailSender, templateEngine, BASE_URL, EMAIL_USERNAME);
    }

    @Test
    void sendVerificationTokenSetsVariablesAndRequestsRightTemplate() {
        Account account = AccountImpl.builder()
                .email("user@example.com")
                .username("username").build();
        AuthToken authToken = AuthTokenImpl.builder().build();

        when(templateEngine.process(anyString(), any())).thenReturn("<html-file>");
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);

        subject.sendVerificationToken(account, authToken, true);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.captor();
        ArgumentCaptor<String> templateCaptor = ArgumentCaptor.captor();
        verify(templateEngine, times(1)).process(templateCaptor.capture(), contextCaptor.capture());

        assertEquals("email-verification", templateCaptor.getValue());
        assertEquals(account.getEmail(), contextCaptor.getValue().getVariable("target_email"));
        assertEquals(account.getUsername(), contextCaptor.getValue().getVariable("username"));
        assertEquals(BASE_URL + "/email-verification/" + authToken.getToken(), contextCaptor.getValue().getVariable("verification_link"));
        assertEquals(BASE_URL, contextCaptor.getValue().getVariable("baseUrl"));
    }

    @Test
    void sendVerificationTokenSetsCorrectEmailOptions() throws MessagingException {
        Account account = AccountImpl.builder()
                .email("user@example.com")
                .username("username").build();
        AuthToken authToken = AuthTokenImpl.builder().build();

        when(templateEngine.process(anyString(), any())).thenReturn("<html-file>");
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);

        subject.sendVerificationToken(account, authToken, true);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        assertEquals("Verify your email-address", messageCaptor.getValue().getSubject());
        assertEquals(account.getEmail(), messageCaptor.getValue().getAllRecipients()[0].toString());
        assertEquals("Replic-Read <" + EMAIL_USERNAME + ">", messageCaptor.getValue().getFrom()[0].toString());
    }

    @Test
    void sendVerificationTokenSetsCorrectContentHtml() throws MessagingException, IOException {
        Account account = AccountImpl.builder()
                .email("user@example.com")
                .username("username").build();
        AuthToken authToken = AuthTokenImpl.builder().build();

        when(templateEngine.process(anyString(), any())).thenReturn("<html-template>");
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);

        subject.sendVerificationToken(account, authToken, true);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(messageCaptor.getValue().getInputStream(), StandardCharsets.UTF_8);
        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
            out.append(buffer, 0, numRead);
        }
        String content = out.toString();

        assertEquals("<html-template>", content);
    }

    @Test
    void sendVerificationTokenSetsCorrectContentNoHtml() throws MessagingException, IOException {
        Account account = AccountImpl.builder()
                .email("user@example.com")
                .username("username").build();
        AuthToken authToken = AuthTokenImpl.builder().build();

        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);

        subject.sendVerificationToken(account, authToken, false);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(messageCaptor.getValue().getInputStream(), StandardCharsets.UTF_8);
        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
            out.append(buffer, 0, numRead);
        }
        String content = out.toString();

        assertEquals(authToken.getToken().toString(), content);
    }

}
