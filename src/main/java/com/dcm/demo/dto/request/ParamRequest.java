package com.dcm.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ParamRequest {
    private Integer labOrderId;
    private String resultDetails;
    private String note;
    private String explanation;
    private String summary;
    private List<ParamDetail> paramDetails;
    private List<String> urls;
    @Data
    public static class ParamDetail {
        private Integer paramId;
        private String value;
    }
}
