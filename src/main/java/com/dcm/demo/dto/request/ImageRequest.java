package com.dcm.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ImageRequest {
    private Integer labOrderId;
    private List<String> urls;
}
