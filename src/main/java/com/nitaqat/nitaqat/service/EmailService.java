package com.nitaqat.nitaqat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void notifyAdminOfNewUser(String email , String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("am909962@gmail.com");
        message.setSubject("New User Signup");
        message.setText("A new user signed up with name " + name+ " and email in NITAQAT : " + email);
        mailSender.send(message);
    }
}
