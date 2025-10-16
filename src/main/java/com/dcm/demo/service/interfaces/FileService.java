package com.dcm.demo.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {
    byte[] render(String templateName, Map<String, Object> params, List<Map<String, Object>> items);

    List<String> saveFile(MultipartFile[] file, String type);

    String upload(MultipartFile file, String folderName);
}
