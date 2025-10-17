package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.ImageRequest;
import com.dcm.demo.dto.request.LabResultRequest;
import com.dcm.demo.dto.request.ResultDetailRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.dto.response.LabResultResponse;
import com.dcm.demo.dto.response.ResultDetailResponse;
import com.dcm.demo.model.ImageLab;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.model.LabResult;
import com.dcm.demo.model.LabResultDetail;
import com.dcm.demo.repository.ImageLabRepository;
import com.dcm.demo.repository.LabResultDetailRepository;
import com.dcm.demo.repository.LabResultRepository;
import com.dcm.demo.repository.ParamRepository;
import com.dcm.demo.service.interfaces.LabOrderService;
import com.dcm.demo.service.interfaces.LabResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class LabResultServiceImpl implements LabResultService {
    private final LabOrderService labOrderService;
    private final ParamRepository paramRepository;
    private final LabResultDetailRepository labResultDetailRepository;
    private final LabResultRepository repository;
    private final ImageLabRepository imageLabRepository;
    @Override
    public List<ResultDetailResponse> getParamResults(Integer labResultId) {
        LabResult labResult = repository.findById(labResultId)
                .orElseThrow(() -> new RuntimeException("Lab result not found with id: " + labResultId));
        List<LabResultDetail> labResultDetails = labResult.getLabResultDetails();

//      neu chua co ket qua
        if (labResultDetails == null || labResultDetails.isEmpty()) {
            throw new RuntimeException("Lab result details already exist for lab result id: " + labResultId);
        }

        return labResultDetails.stream()
                .map(it -> {
                    ResultDetailResponse response = new ResultDetailResponse();
                    response.setId(it.getId());
                    response.setName(it.getName());
                    response.setUnit(it.getUnit());
                    response.setRange(it.getRange());
                    response.setValue(it.getValue());
                    response.setRangeStatus(it.getRangeStatus());
                    return response;
                })
                .toList();
    }

    @Override
    public LabResultResponse createResult(LabResultRequest request) {
        LabResult labResult = new LabResult();

        LabOrder labOrder = labOrderService.findById(request.getLabOrderId());
        labResult.setLabOrder(labOrder);

        labResult.setExplanation(request.getExplanation());
        labResult.setNote(request.getNote());
        labResult.setResultDetails(request.getResultDetails());
        labResult.setSummary(request.getSummary());
        labResult.setSampleTime(LocalDateTime.now());
        return buildResponse(repository.save(labResult));
    }

    @Override
    public LabResultResponse updateResult(LabResultRequest request) {
        LabOrder labOrder = labOrderService.findById(request.getLabOrderId());
        LabResult labResult = labOrder.getLabResult();
        labResult.setExplanation(request.getExplanation());
        labResult.setNote(request.getNote());
        labResult.setResultDetails(request.getResultDetails());
        labResult.setSummary(request.getSummary());
        labResult.setStatus(LabResult.Status.HOAN_THANH);

        LabResultResponse response = buildResponse(repository.save(labResult));

        return buildResponse(repository.save(labResult));
    }
//
//    @Override
//    public void createParamResults(ParamRequest request) {
//        LabResult labResult = repository.findById(request.getLabResultId())
//                .orElseThrow(() -> new RuntimeException("Lab result not found with id: " + request.getLabResultId()));
//        List<LabResultDetail> labResultDetails = new ArrayList<>();
//

    /// /      tao chi tiet cho tung chi so
//        request.getParamDetails().forEach(paramDetail -> {
//            Param param = paramRepository.findById(paramDetail.getParamId())
//                    .orElseThrow(() -> new RuntimeException("Param not found with id: " + paramDetail.getParamId()));
//            LabResultDetail labResultDetail = new LabResultDetail();
//            labResultDetail.setLabResult(labResult);
//            labResultDetail.setParam(param);
//            labResultDetail.setValue(paramDetail.getValue());
//            labResultDetail.setName(param.getName());
//            labResultDetail.setUnit(param.getUnit());
//            labResultDetail.setRange(param.getRange());
//            labResultDetail.setRangeStatus(getRangeStatus(param.getRange(), paramDetail.getValue()));
//            labResultDetails.add(labResultDetail);
//        });
//        labResult.setLabResultDetails(labResultDetails);
//        repository.save(labResult);
//    }
    @Override
    public void updateResultDetails(ResultDetailRequest request) {
        LabResultDetail labResultDetail = labResultDetailRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Lab result detail not found with id: " + request.getId()));
        labResultDetail.setValue(request.getValue());
        labResultDetail.setRangeStatus(getRangeStatus(labResultDetail.getRange(), request.getValue()));
        labResultDetailRepository.save(labResultDetail);
    }

    private LabResultResponse buildResponse(LabResult labResult) {
        LabResultResponse response = new LabResultResponse();

        LabOrderResponse labOrderResponse = labOrderService.buildResponse(labResult.getLabOrder());
        response.setId(labResult.getId());
        response.setDate(labResult.getDate());
        response.setExplanation(labResult.getExplanation());
        response.setNote(labResult.getNote());
        response.setResultDetails(labResult.getResultDetails());
        return response;
    }

    @Override
    public LabResultDetail.RangeStatus getRangeStatus(String range, String value) {
        String[] parts = range.split("-");
        double lowerBound = Double.parseDouble(parts[0].trim());
        double upperBound = Double.parseDouble(parts[1].trim());
        double actualValue = Double.parseDouble(value.trim());

        if (actualValue < lowerBound) {
            return LabResultDetail.RangeStatus.THAP;
        }
        if (actualValue > upperBound) {
            return LabResultDetail.RangeStatus.CAO;
        }
        return LabResultDetail.RangeStatus.TRUNG_BINH;
    }

    @Override
    public void saveUrlImage(ImageRequest request) {
        LabOrder labOrder = labOrderService.findById(request.getLabOrderId());
        LabResult labResult = labOrder.getLabResult();
        List<ImageLab> images = request.getUrls().stream().map(url -> {
            ImageLab imageLab = new ImageLab();
            imageLab.setLabResult(labResult);
            imageLab.setUrl(url);
            return imageLab;
        }).toList();
        imageLabRepository.saveAll(images);
    }
}
