package com.dcm.demo.dto.response;

import lombok.Data;

@Data
public class PayosResponse {
    private String code;
    private String desc;
    private Data data;

    @lombok.Data
    public static class Data {
        private String status;
    }
}
