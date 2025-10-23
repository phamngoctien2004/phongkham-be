package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.EmailRequest;

import java.util.Map;

public interface EmailService {
    void sendHtml(String to, String subject, String text);
    void sendTemplate(String to, String subject, String template, Map<String, Object> dataContext);
    void sendFormMessage(EmailRequest emailRequest);
}
