package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.ParamDTO;
import com.dcm.demo.dto.response.ParamResponse;

import java.util.List;

public interface ParamService {
    List<ParamResponse> findAll(String keyword);

    void addParamInService(ParamDTO request);
    void deleteParamInService(ParamDTO request);
}
