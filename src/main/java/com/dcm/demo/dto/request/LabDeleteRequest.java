package com.dcm.demo.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class LabDeleteRequest {
    private List<Integer> ids;
    private Integer medicalRecordId;
}
