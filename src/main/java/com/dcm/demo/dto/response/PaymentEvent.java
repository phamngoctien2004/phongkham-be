package com.dcm.demo.dto.response;

import com.dcm.demo.enums.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentEvent {
    private Event event;
    private String message;
    private Integer appointmentId;
}
