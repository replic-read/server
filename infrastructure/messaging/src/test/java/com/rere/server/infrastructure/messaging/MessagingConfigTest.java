package com.rere.server.infrastructure.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = MessagingConfig.class,
        properties = {
                "rere.mail.username=test@rere.com",
                "rere.mail.password=secret",
                "rere.mail.smtpHost=smtp.rere.com",
                "rere.mail.smtpPort=587",
                "rere.mail.ssl=false"
        })
class MessagingConfigTest {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Test
    void javaMailSenderIsConfigured() {
        assertNotNull(javaMailSender);
    }

    @Test
    void templateEngineIsConfigured() {
        assertNotNull(springTemplateEngine);
    }

}
