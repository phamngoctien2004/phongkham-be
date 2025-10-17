package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.ImageRequest;
import com.dcm.demo.dto.request.LabResultRequest;
import com.dcm.demo.dto.request.ParamRequest;
import com.dcm.demo.dto.request.ResultDetailRequest;
import com.dcm.demo.dto.response.LabResultResponse;
import com.dcm.demo.dto.response.ParamResponse;
import com.dcm.demo.dto.response.ResultDetailResponse;
import com.dcm.demo.model.LabResultDetail;

import java.util.List;

public interface LabResultService {
    List<ResultDetailResponse> getParamResults(Integer labResultId);
    LabResultResponse createResult(LabResultRequest request);
    LabResultResponse updateResult(LabResultRequest request);
//    void createParamResults(ParamRequest request);
    void updateResultDetails(ResultDetailRequest request);
    void saveUrlImage(ImageRequest request);
    LabResultDetail.RangeStatus getRangeStatus(String range, String value);
}
