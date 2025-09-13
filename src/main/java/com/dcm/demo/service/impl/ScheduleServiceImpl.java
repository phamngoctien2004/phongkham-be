package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.ScheduleResponse;
import com.dcm.demo.dto.response.SlotResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.ScheduleMapper;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Schedule;
import com.dcm.demo.model.ScheduleException;
import com.dcm.demo.repository.ScheduleExceptionRepository;
import com.dcm.demo.repository.ScheduleRepository;
import com.dcm.demo.service.interfaces.DoctorService;
import com.dcm.demo.service.interfaces.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final DoctorService doctorService;
    private final ScheduleRepository repository;
    private final ScheduleExceptionRepository seRepository;
    private final ScheduleMapper mapper;

    @Override
    @Transactional
    public ScheduleResponse create(ScheduleRequest request) {
//      kiem tra neu khung gio khong hop le != 1h
        if (Duration.between(request.getStartTime(), request.getEndTime()).toMinutes() != 60) {
            throw new AppException(ErrorCode.SLOT_INVALID);
        }
        Schedule schedule = mapper.toEntity(request);
        schedule.setDay(request.getDay());
        Doctor doctor = doctorService.findById(request.getDoctorId());
        Doctor simpleDoctor = new Doctor();

        simpleDoctor.setId(doctor.getId());
        simpleDoctor.setPosition(doctor.getPosition());
        schedule.setDoctor(simpleDoctor);
        return mapper.toResponse(
                repository.save(schedule)
        );
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<SlotResponse> filterSlots(Integer doctorId, Integer departmentId, Schedule.DayOfWeek day) {
        return repository.findByOption(
                doctorId,
                departmentId,
                day
        ).stream().map(it -> {
            SlotResponse slot = new SlotResponse();
            slot.setStartTime(it.getStartTime());
            slot.setEndTime(it.getEndTime());
            return slot;
        }).toList();
    }

    @Override
    @Transactional
    public void acceptLeave(Integer id) {
        var scheduleException = seRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
        scheduleException.setLeaveStatus(ScheduleException.leaveStatus.DA_DUYET);
        seRepository.save(scheduleException);

        Doctor doctor =  scheduleException.getDoctor();
        var day = mapDayOfWeek(scheduleException.getDate().getDayOfWeek());
        Schedule schedule = repository.findByDayAndDoctorIdAndStartTimeAndEndTime(
                Schedule.DayOfWeek.valueOf(day),
                scheduleException.getDoctor().getId(),
                scheduleException.getStartTime(),
                scheduleException.getEndTime())
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
        schedule.setStatus(false);
        repository.save(schedule);
    }
    public  String mapDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY    -> "T2";
            case TUESDAY   -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY  -> "T5";
            case FRIDAY    -> "T6";
            case SATURDAY  -> "T7";
            case SUNDAY    -> "CN";
        };
    }

}
