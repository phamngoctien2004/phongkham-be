package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.ImageRequest;
import com.dcm.demo.dto.request.ParamRequest;
import com.dcm.demo.model.*;
import com.dcm.demo.service.interfaces.LabOrderService;
import com.dcm.demo.service.interfaces.LabProcessingService;
import com.dcm.demo.service.interfaces.LabResultService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class LabProcessingServiceImpl implements LabProcessingService {
    private final LabOrderService labOrderService;
    private final LabResultService labResultService;

    @Override
    public void processUpdateStatusLab(Integer labOrderId, LabOrder.TestStatus status) {
        if (!status.equals(LabOrder.TestStatus.CHO_KET_QUA)) {
            labOrderService.updateStatus(labOrderId, status);
            return;
        }

        LabOrder labOrder = labOrderService.findById(labOrderId);
        HealthPlan healthPlan = labOrder.getHealthPlan();

        LabResult labResult = new LabResult();
        labResult.setLabOrder(labOrder);
        labResult.setDate(LocalDateTime.now());

//      danh sach chi so can do cho dich vu
        List<LabResultDetail> labResultDetails = getLabResultDetails(healthPlan, labResult);
        labResult.setLabResultDetails(labResultDetails);
        labOrder.setLabResult(labResult);

        labOrder.setStatus(LabOrder.TestStatus.CHO_KET_QUA);
        labOrderService.save(labOrder);
    }

    @Override
    public void completeLabOrder(ParamRequest request) {
        LabOrder labOrder = labOrderService.findById(request.getLabOrderId());
        LabResult labResult = labOrder.getLabResult();
        labResult.setExplanation(request.getExplanation());
        labResult.setNote(request.getNote());
        labResult.setResultDetails(request.getResultDetails());
        labResult.setSummary(request.getSummary());

        List<LabResultDetail> labResultDetails = labResult.getLabResultDetails();

        Map<Integer, String> map = request.getParamDetails().stream()
                .collect(Collectors.toMap(ParamRequest.ParamDetail::getParamId, ParamRequest.ParamDetail::getValue));
        labResultDetails.forEach(it -> {
            String value = map.get(it.getId());
            it.setValue(value);
            it.setRangeStatus(labResultService.getRangeStatus(it.getRange(), value));
        });
        if(request.getUrls() != null && !request.getUrls().isEmpty()) {
            ImageRequest imageRequest = new ImageRequest();
            imageRequest.setUrls(request.getUrls());
            imageRequest.setLabOrderId(labOrder.getId());
            labResultService.saveUrlImage(imageRequest);
        }
        labOrder.setStatus(LabOrder.TestStatus.HOAN_THANH);
        labOrderService.save(labOrder);
    }

    @NotNull
    private static List<LabResultDetail> getLabResultDetails(HealthPlan healthPlan, LabResult labResult) {
        List<HealthPlanParam> healthPlanParams = healthPlan.getHealthPlanParams();
        List<LabResultDetail> labResultDetails = new ArrayList<>();
        healthPlanParams.forEach(it -> {
            Param param = it.getParam();
            LabResultDetail labResultDetail = new LabResultDetail();
            labResultDetail.setLabResult(labResult);
            labResultDetail.setName(param.getName());
            labResultDetail.setUnit(param.getUnit());
            labResultDetail.setRange(param.getRange());
            labResultDetail.setParam(param);
            labResultDetails.add(labResultDetail);
        });
        return labResultDetails;
    }
}
