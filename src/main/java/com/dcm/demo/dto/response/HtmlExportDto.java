package com.dcm.demo.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HtmlExportDto {
    private String name;
    private String unit;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal total;
}
