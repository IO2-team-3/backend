package com.team3.central;

import com.team3.central.services.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
class EmailServiceTest {

    @Test
    void sendEmailTest() {
        EmailServiceImpl emailService =new EmailServiceImpl();
        emailService.sendSimpleMessage();
    }

}
