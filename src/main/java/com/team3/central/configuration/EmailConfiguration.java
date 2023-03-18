//package com.team3.central.configuration;
//
//import java.util.Properties;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.context.support.ResourceBundleMessageSource;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
////@Configuration
//@ComponentScan(basePackages = { "com.team3.central.services" })
////@PropertySource(value={"classpath:application.properties"})
//public class EmailConfiguration {
//
//    @Value("smtp.gmail.com")
//    private String mailServerHost;
//
//    @Value("587")
//    private Integer mailServerPort;
//
//    @Value("username")
//    private String mailServerUsername;
//
//    @Value("password")
//    private String mailServerPassword;
//
//    @Value("true")
//    private String mailServerAuth;
//
//    @Value("false")
//    private String mailServerStartTls;
//
//    @Bean
//    public JavaMailSender getJavaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
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
//
//        return mailSender;
//    }
//
//
//
//}