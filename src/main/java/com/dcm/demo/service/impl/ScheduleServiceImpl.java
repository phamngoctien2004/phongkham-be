package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.DoctorResponse;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    @Transactional
    public void acceptLeave(Integer id) {
        var scheduleException = seRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
        scheduleException.setLeaveStatus(ScheduleException.leaveStatus.DA_DUYET);
        seRepository.save(scheduleException);

        Doctor doctor = scheduleException.getDoctor();
        DayOfWeek day = scheduleException.getDate().getDayOfWeek();
        Schedule schedule = repository.findByDayAndDoctorIdAndStartTimeAndEndTime(
                        day,
                        scheduleException.getDoctor().getId(),
                        scheduleException.getStartTime(),
                        scheduleException.getEndTime())
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
        repository.save(schedule);
    }

    @Override
    public List<SlotResponse> filterSchedules(
            Integer departmentId,
            Integer doctorId,
            LocalDate startDate,
            LocalDate endDate,
            Schedule.Shift shift
    ) {
        List<SlotResponse> results = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            List<Schedule> schedules = repository.findByOption(
                    doctorId,
                    departmentId,
                    currentDate.getDayOfWeek(),
                    shift
            );

            SlotResponse slotResponse = buildSlotResponse(schedules, currentDate);
            results.add(slotResponse);
            currentDate = currentDate.plusDays(1);
        }

        return results;
    }

    private SlotResponse buildSlotResponse(List<Schedule> schedules, LocalDate currentDate) {
        List<DoctorResponse> doctorResponses = schedules.stream().map(schedule -> {
            Doctor doctor = schedule.getDoctor();
            DoctorResponse doctorResponse = new DoctorResponse();
            doctorResponse.setId(doctor.getId());
            doctorResponse.setFullName(doctor.getFullName());
            doctorResponse.setPosition(doctor.getPosition());
            doctorResponse.setShift(schedule.getShift());
            doctorResponse.setAvailable(isSlotAvailable(schedule, currentDate));
            return doctorResponse;
        }).toList();
        SlotResponse slotResponse = new SlotResponse();
        slotResponse.setDate(currentDate);
        slotResponse.setDateName(currentDate.getDayOfWeek().name());
        slotResponse.setDoctors(doctorResponses);
        slotResponse.setTotalSlot(doctorResponses.size());
        return slotResponse;
    }

    private boolean isSlotAvailable(Schedule schedule, LocalDate date) {
        List<ScheduleException> exceptions = seRepository.findByDateAndDoctorIdAndLeaveStatus(
                date, schedule.getDoctor().getId(), ScheduleException.leaveStatus.DA_DUYET
        );

        for (ScheduleException exception : exceptions) {
            LocalTime startTime = exception.getStartTime();
            LocalTime endTime = exception.getEndTime();
            if (startTime.isBefore(schedule.getEndTime()) && endTime.isAfter(schedule.getStartTime())) {
                return false; // Slot is not available
            }
        }
        return true; // Slot is available
    }

}
