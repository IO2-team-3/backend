package com.team3.central.services;


import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTest {
    JavaMailSender sender;
    EmailService service;

    @BeforeEach
    void setup() {
        sender = Mockito.mock(JavaMailSender.class);
        service = new EmailService(sender);
    }
    @Test
    void simpleTest() {
        // given
        String subject = "fSubject";
        String to = "to@mail.com";
        String message = "Some message.";
        SimpleMailMessage expectedMessage = createMessage(to, subject, message);
        // when
        service.sendSimpleMessage(to, subject, message);

        // then
        verify(sender).send(argThat(new MessageMatcher(expectedMessage)));
    }
    private SimpleMailMessage createMessage(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setText(text);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        return simpleMailMessage;
    }

    private static class MessageMatcher implements ArgumentMatcher<SimpleMailMessage> {
        private final SimpleMailMessage expected;
        public MessageMatcher(SimpleMailMessage expected) {this.expected = expected;}

        @Override
        public boolean matches(SimpleMailMessage simpleMailMessage) {
            boolean to = Arrays.equals(simpleMailMessage.getTo(), expected.getTo());
            boolean subject = simpleMailMessage.getSubject() == expected.getSubject();
            boolean text = simpleMailMessage.getText() == expected.getText();
            return to && subject && text;
        }
    }

}
