package com.dcm.demo.service.impl;

import com.dcm.demo.service.interfaces.FileService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService implements FileService {
    private final SpringTemplateEngine templateEngine;
    @Override
    public byte[] render(String templateName, Map<String, Object> params, List<Map<String, Object>> items) {

        Context context = new Context();
        context.setVariables(params);
        context.setVariable("items", items);
        String html = templateEngine.process(templateName, context);
        // Logic to convert HTML to PDF and return as byte array
        html = html.replace("&nbsp;", "");
        html = html.replace("<br>", "<br/>");
        // 2) HTML -> PDF
        try (var out = new ByteArrayOutputStream()) {
            var b = new PdfRendererBuilder();
            b.useFastMode();
            b.useFont(
                    () -> getClass().getResourceAsStream("/fonts/mono.ttf"),
                    "Liberation Serif", 400,
                    com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle.NORMAL,
                    true
            );
            // baseUrl để resolve relative path (ảnh/logo trong templates)
            b.withHtmlContent(html, "classpath:/templates/");
            b.toStream(out);
            b.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PDF", e);
        }
    }
}
