package com.dcm.demo.service.impl;

import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.service.interfaces.FileService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PdfService implements FileService {
    private final SpringTemplateEngine templateEngine;
    private final S3Client s3;
    private final S3Presigner presigner;
    @Value("${r2.bucket}")
    String bucket;


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

    @Override
    public List<String> saveFile(MultipartFile[] file, String type) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile f : file) {
                urls.add(upload(f, type));
        }

        return urls;
    }

    @Override
    public String upload(MultipartFile file, String folderName) {
        if (file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

//           check dinh dang file
        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(fn -> fn.contains("."))
                .map(fn -> fn.substring(fn.lastIndexOf(".") + 1))
                .orElseThrow(() -> new AppException(ErrorCode.FILE_INVALID_FORMAT));

//          tao key
        String key = "%s/%s.%s".formatted(folderName, UUID.randomUUID(), extension);
        try (var in = file.getInputStream()) {
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(in, file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload", e);
        }
        return "https://files.tienpndev.id.vn/phongkham/" + folderName + "/"+ key;
    }
}
