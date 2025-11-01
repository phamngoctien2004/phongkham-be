package com.dcm.demo.dto.request;

import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Room;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentRequest {

    private Integer id;

    private String name;

    private String phone;

    private String description;

    private List<Integer> roomIds;
}
