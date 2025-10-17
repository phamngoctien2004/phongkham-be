package com.dcm.demo.dto.response;

import com.dcm.demo.model.LabResult;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LabResultResponse {
    private Integer id;
    private LocalDateTime date;
    private String resultDetails;
    private String note;
    private String explanation;
    private String summary;
    private List<ResultDetailResponse> paramResults;
    private List<String> urls;
}
