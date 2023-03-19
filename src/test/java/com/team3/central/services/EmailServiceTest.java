package com.team3.central.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

class EmailServiceTest {
    @Autowired
    EmailService emailService;

    @Test
    void sendEmailTest() {
        //emailService.sendSimpleMessage("io2testmail@gmail.com", "test", "test content");
    }

}
