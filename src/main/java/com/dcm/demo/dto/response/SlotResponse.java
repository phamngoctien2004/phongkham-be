package com.dcm.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotResponse implements Serializable {
    private LocalDate date;
    private String dateName;
    private int totalSlot;
    private List<DoctorResponse> doctors;

}

