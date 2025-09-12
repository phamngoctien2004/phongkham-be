package com.dcm.demo.dto.request;

import lombok.Data;

import java.time.LocalTime;

@Data
public class TimeDto {
    private LocalTime startTime;
    private LocalTime endTime;
}
