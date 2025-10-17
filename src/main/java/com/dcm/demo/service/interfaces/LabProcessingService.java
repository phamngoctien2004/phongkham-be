package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.ParamRequest;
import com.dcm.demo.model.LabOrder;

public interface LabProcessingService {
    void processUpdateStatusLab(Integer labOrderId, LabOrder.TestStatus status);
    void completeLabOrder(ParamRequest request);
}
