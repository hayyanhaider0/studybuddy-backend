package com.studybuddy.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String code) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("'" + code + "' is your Study Buddy Verification Code.");
            msg.setText("Your verification code for Study Buddy is: " + code + ". This code will expire in 5 minutes.");
            msg.setFrom("kaijugami@gmail.com");

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email.");
        }
    }
}
