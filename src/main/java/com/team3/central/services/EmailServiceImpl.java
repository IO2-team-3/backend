package com.team3.central.services;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

//import  com.team3.central.configuration.EmailConfiguration;
/**
 * Created by Olga on 7/15/2016.
 */
@Service("EmailService")
public class EmailServiceImpl //implements EmailService {
{
    private static final String NOREPLY_ADDRESS = "noreply@baeldung.com";

//    @Autowired
//    private JavaMailSender emailSender;

    @Value("classpath:/mail-logo.png")
    private Resource resourceFile;

    @Value("smtp.gmail.com")
    private String mailServerHost;

    @Value("587")
    private Integer mailServerPort;

    @Value("username")
    private String mailServerUsername;

    @Value("password")
    private String mailServerPassword;

    @Value("true")
    private String mailServerAuth;

    @Value("false")
    private String mailServerStartTls;

    public void sendSimpleMessage(/*String to, String subject, String text*/) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(NOREPLY_ADDRESS);
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(text);
//            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        mailSender.setHost(mailServerHost);
//        mailSender.setPort(mailServerPort);
//
//        mailSender.setUsername(mailServerUsername);
//        mailSender.setPassword(mailServerPassword);
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", mailServerAuth);
//        props.put("mail.smtp.starttls.enable", mailServerStartTls);
//        props.put("mail.debug", "true");
//            mailSender.send(message);
//        } catch (MailException exception) {
//            exception.printStackTrace();
//        }
        String to = "recipient@example.com"; // recipient email address
        String from = "sender@example.com"; // sender email address
        String host = "smtp.gmail.com"; // SMTP server host
        String username = "your_email_username"; // email account username
        String password = "your_email_password"; // email account password

        // Create a JavaMailSenderImpl object
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // Create a SimpleMailMessage object
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Subject of your email");
        message.setText("This is a test message");

        // Send message
        mailSender.send(message);
        System.out.println("Sent message successfully....");
    }

}