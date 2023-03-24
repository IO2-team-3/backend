package com.team3.central.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
    void sendEmailTest() {
        // given
        String subject = "Subject";
        String to = "to@mail.com";
        String message = "Some message";
        SimpleMailMessage expected = createSimpleMessage(subject, to, message);

        // when
        service.sendSimpleMessage(to, subject, message);

        // then
        verify(sender).send(argThat(new MessageMatcher(expected)));
    }

    private SimpleMailMessage createSimpleMessage(String subject, String to, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        return simpleMailMessage;
    }

    private static class MessageMatcher implements org.mockito.ArgumentMatcher<SimpleMailMessage> {

        private final SimpleMailMessage expected;

        public MessageMatcher(SimpleMailMessage expected) {this.expected = expected;}
        @Override
        public boolean matches(SimpleMailMessage message) {
            boolean to = Arrays.equals(message.getTo(), expected.getTo());
            boolean subject = Objects.equals(message.getSubject(), expected.getSubject());
            boolean text = Objects.equals(message.getText(), expected.getText());
            return to && subject && text;
        }
    }
}
