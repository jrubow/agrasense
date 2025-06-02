package com.asterlink.rest.service.impl;

import com.asterlink.rest.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.Struct;

/**
 * Email service implementation.
 * @author gl3bert
 */

@Service
public class EmailServiceImpl implements EmailService {
    /*
    @Autowired
    private JavaMailSender mailSender;

    // Sends emails from designated email address.
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("anda.noreply@gmail.com"); // TODO create new account
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

     */

    public void sendEmail(String to, String subject, String text) {
        return;
    }
}