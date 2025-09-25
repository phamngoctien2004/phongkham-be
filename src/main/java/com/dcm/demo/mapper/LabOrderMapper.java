package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.model.LabOrder;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface LabOrderMapper extends BaseMapper<LabOrder, LabOrderRequest, LabOrderResponse> {
}
