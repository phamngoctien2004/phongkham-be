package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.model.LabOrder;

import java.util.List;

public interface LabOrderService {
    List<LabOrderResponse> createLabOrder(List<LabOrderRequest> request);
    LabOrderResponse buildResponse(LabOrder labOrder);
    LabOrder findById(Integer id);
}
