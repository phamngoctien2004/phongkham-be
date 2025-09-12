package com.dcm.demo.mapper;

import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.ScheduleResponse;
import com.dcm.demo.model.Schedule;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    Schedule toEntity(ScheduleRequest scheduleRequest);
    ScheduleResponse toResponse(Schedule schedule);
}
