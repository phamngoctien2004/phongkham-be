package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.LabResultRequest;
import com.dcm.demo.dto.response.LabResultResponse;

public interface LabResultService {
    LabResultResponse createResult(LabResultRequest request);
}
