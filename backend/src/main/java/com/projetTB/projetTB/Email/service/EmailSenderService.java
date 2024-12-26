package com.projetTB.projetTB.Email.service;

import com.projetTB.projetTB.Email.DTO.EmailRequest;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Value;
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

    public void sendEmail(EmailRequest emailRequest) throws Exception {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(465); // Ensure this is the correct port for your SMTP server
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.debug", "false");

        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailRequest.getRecipient()));
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.setSubject(emailRequest.getSubject());
            try {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setText(emailRequest.getMessage(), true);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to set email content", ex);
            }
        };

        try {
            mailSender.send(preparator);
        } catch (Exception ex) {
            throw new Exception("Failed to send email to " + emailRequest.getRecipient(), ex);
        }
    }
}
