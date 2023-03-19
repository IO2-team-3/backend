package com.team3.central;

import com.team3.central.services.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailServiceTest {
    @Autowired
    EmailServiceImpl emailService;

    @Test
    void sendEmailTest() {
        emailService.sendSimpleMessage("io2testmail@gmail.com", "test", "test content");
    }

}
