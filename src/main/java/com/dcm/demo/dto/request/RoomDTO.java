package com.dcm.demo.dto.request;

import com.dcm.demo.model.Department;
import lombok.Data;

@Data
public class RoomDTO {

    private Integer roomId;

    private String roomName;

    private String roomNumber;

    private Integer departmentId;

    private String departmentName;
}
