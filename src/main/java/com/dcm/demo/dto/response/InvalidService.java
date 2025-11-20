package com.dcm.demo.dto.response;

import java.time.LocalTime;
import java.util.List;

import lombok.Data;

@Data
public class InvalidService {
    private Integer id;
    private List<LocalTime> invalidTimes;
}
