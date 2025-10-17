package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.request.ParamRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.dto.response.ParamResponse;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.model.Param;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ParamMapper extends BaseMapper<Param, ParamRequest, ParamResponse> {
}
