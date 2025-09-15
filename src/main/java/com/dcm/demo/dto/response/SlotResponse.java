package com.dcm.demo.dto.response;

import com.dcm.demo.model.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotResponse {
    private LocalDate date;
    private String dateName;
    private int totalSlot;
    private List<DoctorResponse> doctors;
}

