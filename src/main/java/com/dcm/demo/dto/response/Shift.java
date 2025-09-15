package com.dcm.demo.dto.response;

import com.dcm.demo.model.Schedule;
import lombok.Data;

import java.util.List;

@Data
public class Shift {
    private Schedule.Shift shift;
    private List<DoctorResponse> doctors;
}
