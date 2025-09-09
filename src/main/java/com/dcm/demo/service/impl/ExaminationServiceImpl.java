package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.ExamResponse;
import com.dcm.demo.mapper.ExamMapper;
import com.dcm.demo.repository.ExaminationServiceRepository;
import com.dcm.demo.service.interfaces.ExaminationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ExaminationServiceImpl implements ExaminationService {
    private final ExaminationServiceRepository repository;
    private final ExamMapper mapper;
    @Override
    public List<ExamResponse> getAllExamService() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}
