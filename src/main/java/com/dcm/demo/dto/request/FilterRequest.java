package com.dcm.demo.dto.request;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FilterRequest {
    private String keyword;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String paymentStatus;
    private String method;
}
