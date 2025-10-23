package com.dcm.demo.dto.request;

import lombok.Data;

@Data
public class EmailRequest {
    private String name;
    private String email;
    private String title;
    private String message;
}
