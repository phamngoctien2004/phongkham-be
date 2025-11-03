package com.dcm.demo.dto.request;

import com.dcm.demo.dto.response.ParamResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParamDTO {
    private Integer healthPlanId;
    private List<Integer> requestIds;
}
