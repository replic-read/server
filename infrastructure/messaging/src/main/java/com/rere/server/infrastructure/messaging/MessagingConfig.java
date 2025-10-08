package com.rere.server.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Contains required configuration and beans for the messaging.
 */
@Configuration
public class MessagingConfig {

    /**
     * Provides the mail sender.
     */
    @Bean
    public JavaMailSender javaMailSender(
            @Value(("${rere.mail.username}")) String username,
            @Value(("${rere.mail.password}")) String password,
            @Value(("${rere.mail.smtpHost}")) String host,
            @Value(("${rere.mail.smtpPort}")) int port,
            @Value(("${rere.mail.ssl}")) boolean ssl
    ) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);


        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", ssl ? "true" : "false");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
