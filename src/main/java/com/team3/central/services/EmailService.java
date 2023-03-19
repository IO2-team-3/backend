package com.team3.central.services;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}