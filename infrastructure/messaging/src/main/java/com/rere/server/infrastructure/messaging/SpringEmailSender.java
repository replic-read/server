package com.rere.server.infrastructure.messaging;

import com.rere.server.domain.messaging.EmailSender;
import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AuthToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the email-sender.
 */
@Component
public class SpringEmailSender implements EmailSender {

    private static final String VERIFICATION_LINK_FORMAT = "%s/email-verification/%s";

    private static final String VAR_USERNAME = "username";
    private static final String VAR_BASEURL = "baseUrl";
    private static final String VAR_VERIFICATION_LINK = "verification_link";
    private static final String VAR_TARGET_EMAIL = "target_email";

    private static final String TEMPLATE_EMAIL_VERIFY = "email-verification";

    private static final String DISPLAY_FROM = "Replic-Read";

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private final String baseUrl;
    private final String emailFrom;

    @Autowired
    public SpringEmailSender(JavaMailSender mailSender, SpringTemplateEngine templateEngine, @Value("${rere.baseurl}") String baseUrl, @Value("${rere.mail.username}") String emailFrom) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.baseUrl = baseUrl;
        this.emailFrom = emailFrom;
    }

    private String createVerificationLink(UUID token) {
        return VERIFICATION_LINK_FORMAT.formatted(baseUrl, token.toString());
    }

    private String getTemplate(String template, Map<String, Object> model, String alt, boolean doHtml) {
        if (doHtml) {
            Context context = new Context();
            context.setVariables(model);
            return templateEngine.process(template, context);
        } else {
            return alt;
        }
    }

    @Override
    public boolean sendVerificationToken(Account account, AuthToken token, boolean htmlEmail) {
        Map<String, Object> model = Map.of(
                VAR_USERNAME, account.getUsername(),
                VAR_BASEURL, baseUrl,
                VAR_VERIFICATION_LINK, createVerificationLink(token.getToken()),
                VAR_TARGET_EMAIL, account.getEmail()
        );
        String content = getTemplate(TEMPLATE_EMAIL_VERIFY, model, token.getToken().toString(), htmlEmail);

        return sendMail(content, account.getEmail(), "Verify your email-address", htmlEmail);
    }

    private boolean sendMail(String content, String target, String subject, boolean isHtml) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setText(content, isHtml);
            helper.setTo(target);
            helper.setFrom(emailFrom, DISPLAY_FROM);
            helper.setSubject(subject);
        } catch (MessagingException e) {
            throw new IllegalStateException("An email could not be sent.", e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("An error occurred when creating an InternetAddress.", e);
        }

        mailSender.send(message);
        return true;
    }

}
