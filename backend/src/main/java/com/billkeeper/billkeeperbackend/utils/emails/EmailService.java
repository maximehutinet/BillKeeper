package com.billkeeper.billkeeperbackend.utils.emails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;
    private final TemplateEngine templateEngine;
    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(EmailDetails details) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(details.getRecipient());
            message.setSubject("Bill Keeper - " + details.getSubject());
            message.setText(details.getBody());
            mailSender.send(message);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void sendEmailWithTemplate(EmailDetails details, Context templateContext, String template) {
        try {
            String htmlContent = templateEngine.process(template, templateContext);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setSubject("Bill Keeper - " + details.getSubject());
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error(e.getMessage());
        }
    }

}
