package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.ExamRequest;
import com.dcm.demo.dto.request.UserRequest;
import com.dcm.demo.dto.response.ExamResponse;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.model.ExaminationService;
import com.dcm.demo.model.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ExamMapper {
    ExaminationService toEntity(ExamRequest userRequest);
    ExamResponse toResponse(ExaminationService user);
}
