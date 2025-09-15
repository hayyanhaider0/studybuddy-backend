package com.studybuddy.backend.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${STUDY_BUDDY_USERNAME}")
    private String senderEmail;

    public void sendCodeEmail(String email, String code, String purpose) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            String subject = "'" + code + "' is your Study Buddy " + purpose + " Code.";
            String text = "Your " + purpose.toLowerCase() + " code for Study Buddy is: " + code +
                    ". This code will expire in "
                    + (purpose.equalsIgnoreCase("Verification") ? "5 minutes." : "1 hour.");
            msg.setSubject(subject);
            msg.setText(text);
            msg.setFrom(senderEmail);

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email.", e);
        }
    }
}
