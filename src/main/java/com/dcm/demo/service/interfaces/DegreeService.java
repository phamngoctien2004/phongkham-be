package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.DegreeRequest;
import com.dcm.demo.dto.response.DegreeResponse;

import java.util.List;

public interface DegreeService {
    List<DegreeResponse> getDegrees();
    DegreeResponse findById(Integer id);
    DegreeResponse create(DegreeRequest degreeRequest);
    DegreeResponse update(DegreeRequest degreeRequest);
    void delete(Integer id);
}
