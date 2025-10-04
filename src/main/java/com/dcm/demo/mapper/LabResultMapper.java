package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.request.LabResultRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.dto.response.LabResultResponse;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.model.LabResult;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface LabResultMapper extends BaseMapper<LabResult, LabResultRequest, LabResultResponse> {
}
