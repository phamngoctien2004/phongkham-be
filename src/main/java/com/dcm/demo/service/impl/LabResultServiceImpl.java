package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.LabResultRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.dto.response.LabResultResponse;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.model.LabResult;
import com.dcm.demo.repository.LabOrderRepository;
import com.dcm.demo.repository.LabResultRepository;
import com.dcm.demo.service.interfaces.LabOrderService;
import com.dcm.demo.service.interfaces.LabResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LabResultServiceImpl implements LabResultService {
    private final LabOrderService labOrderService;
    private final LabResultRepository repository;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LabResultResponse createResult(LabResultRequest request) {
        LabResult labResult = new LabResult();

        LabOrder labOrder = labOrderService.findById(request.getLabOrderId());
        labOrder.setStatus(LabOrder.TestStatus.HOAN_THANH);
        labResult.setLabOrder(labOrder);
        labResult.setPerformingDoctor(labOrder.getPerformingDoctor());
        labResult.setExplanation(request.getExplanation());
        labResult.setNote(request.getNote());
        labResult.setResultDetails(request.getResultDetails());
        labResult.setSummary(request.getSummary());
        labResult.setStatus(LabResult.Status.HOAN_THANH);
        return buildResponse(repository.save(labResult));
    }

    private LabResultResponse buildResponse(LabResult labResult) {
        LabResultResponse response= new LabResultResponse();

        LabOrderResponse labOrderResponse = labOrderService.buildResponse(labResult.getLabOrder());
        response.setId(labResult.getId());
        response.setDate(labResult.getDate());
        response.setExplanation(labResult.getExplanation());
        response.setNote(labResult.getNote());
        response.setResultDetails(labResult.getResultDetails());
        response.setStatus(labResult.getStatus());
        return response;
    }
}
