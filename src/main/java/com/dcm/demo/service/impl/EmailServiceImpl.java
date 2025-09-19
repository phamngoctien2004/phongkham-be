package com.dcm.demo.service.impl;

import com.dcm.demo.service.interfaces.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendHtml(String to, String subject, String text) {

    }

    @Override
    public void sendTemplate(String to, String subject, String template, Map<String, Object> dataContext) {
        String html = this.processTemplate(template, dataContext);
        this.send(to, subject, html);
    }

    //  Convert model into context of thymleaf
    private String processTemplate(String template, Map<String, Object> model) {
        Context context = new Context();
        if (model != null) model.forEach(context::setVariable);
        return templateEngine.process(template, context);
    }
    //send email
    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("your_email@gmail.com");
            helper.setText(htmlBody, true);
            mailSender.send(mime);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
