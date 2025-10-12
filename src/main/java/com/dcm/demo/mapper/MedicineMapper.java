package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.LabOrderRequest;
import com.dcm.demo.dto.request.MedicineRequest;
import com.dcm.demo.dto.response.LabOrderResponse;
import com.dcm.demo.dto.response.MedicineResponse;
import com.dcm.demo.model.LabOrder;
import com.dcm.demo.model.Medicine;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface MedicineMapper extends BaseMapper<Medicine, MedicineRequest, MedicineResponse> {
}
