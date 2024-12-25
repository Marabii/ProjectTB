package com.projetTB.projetTB.Email.service;

import com.projetTB.projetTB.Email.DTO.EmailRequest;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import java.util.Properties;

@Service
public class EmailSenderService {
    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.host}")
    private String host;

    public void sendEmail(EmailRequest emailRequest) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(465);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.Auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.debug", "false");

        JavaMailSender javaMailSender = mailSender;
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailRequest.getRecipient()));
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.setSubject(emailRequest.getSubject());
            try {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setText(emailRequest.getMessage(), true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        };
        javaMailSender.send(preparator);
    }
}
