package com.dcm.demo.service.interfaces;

import java.util.List;
import java.util.Map;

public interface FileService {
    byte[] render(String templateName, Map<String, Object> params, List<Map<String, Object>> items);
}
