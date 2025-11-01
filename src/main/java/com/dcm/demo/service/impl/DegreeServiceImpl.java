package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.DegreeRequest;
import com.dcm.demo.dto.response.DegreeResponse;
import com.dcm.demo.repository.DegreeRepository;
import com.dcm.demo.service.interfaces.DegreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DegreeServiceImpl implements DegreeService {
    private final DegreeRepository degreeRepository;
    @Override
    public List<DegreeResponse> getDegrees() {
        return degreeRepository.findAll().stream()
                .map(it -> {
                    DegreeResponse response = new DegreeResponse();
                    response.setDegreeId(it.getDegreeId());
                    response.setDegreeName(it.getDegreeName());
                    response.setExaminationFee(it.getExaminationFee());
                    return response;
                })
                .toList();
    }

    @Override
    public DegreeResponse findById(Integer id) {
        return null;
    }

    @Override
    public DegreeResponse create(DegreeRequest degreeRequest) {
        return null;
    }

    @Override
    public DegreeResponse update(DegreeRequest degreeRequest) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }
}
