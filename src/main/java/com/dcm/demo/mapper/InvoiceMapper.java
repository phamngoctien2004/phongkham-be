package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.InvoiceRequest;
import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.InvoiceResponse;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Invoice;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    Invoice toEntity(InvoiceRequest request);
    InvoiceResponse toResponse(Invoice invoice);
}
