package com.dcm.demo.service.interfaces;

import java.util.Map;

public interface EmailService {
    void sendHtml(String to, String subject, String text);
    void sendTemplate(String to, String subject, String template, Map<String, Object> dataContext);
}
