package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.LeaveRequest;
import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.LeaveResponse;
import com.dcm.demo.dto.response.ScheduleResponse;
import com.dcm.demo.dto.response.SlotResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.ScheduleMapper;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Leave;
import com.dcm.demo.model.Schedule;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.LeaveRepository;
import com.dcm.demo.repository.ScheduleRepository;
import com.dcm.demo.service.interfaces.DoctorService;
import com.dcm.demo.service.interfaces.ScheduleService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final DoctorService doctorService;
    private final UserService userService;
    private final ScheduleRepository repository;
    private final LeaveRepository seRepository;

    private final ScheduleMapper mapper;
    private final ScheduleQueryService sqs;

    @Override
    public List<LeaveResponse> getLeaveByDoctor(LocalDate date, Leave.leaveStatus leaveStatus) {
        User user = userService.getCurrentUser();
        Doctor doctor = user.getDoctor();
        if (doctor == null) {
            throw new RuntimeException("Current user is not a doctor");
        }

        return seRepository.findByDateAndLeaveStatus(date, leaveStatus, doctor.getId()).stream()
                .map(this::buildLeaveResponse)
                .toList();
    }

    LeaveResponse buildLeaveResponse(Leave leave) {
        LeaveResponse response = new LeaveResponse();
        response.setId(leave.getId());
        response.setDate(leave.getDate());
        response.setDoctorName(leave.getDoctor().getFullName());
        response.setStartTime(leave.getStartTime());
        response.setEndTime(leave.getEndTime());
        response.setSubmitDate(leave.getSubmitDate());
        response.setLeaveStatus(leave.getLeaveStatus());
        if (leave.getLeaveStatus() != Leave.leaveStatus.CHO_DUYET) {
            response.setUserApprover("ADMIN");
        }
        response.setReason(leave.getReason());
        return response;
    }

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
    public void createLeave(LeaveRequest request) {
        User user = userService.getCurrentUser();
        Doctor currentDoctor = user.getDoctor();
//      truong hop bac si them
        if (currentDoctor != null) {
            buildLeave(request, currentDoctor);
            return;
        }
//      truong hop admin them
        if (request.getDoctorId() == null) {
            throw new RuntimeException("Doctor id is required");
        }
        Doctor doctor = doctorService.findById(request.getDoctorId());
        buildLeave(request, doctor);
    }

    @Override
    public void deleteLeave(Integer id) {
        Leave leave = seRepository.findById(id).orElseThrow(() -> new RuntimeException("Leave not found"));
        if (leave.getLeaveStatus() != Leave.leaveStatus.CHO_DUYET) {
            throw new RuntimeException("Cannot delete approved leave");
        }
        seRepository.delete(leave);
    }

    private void buildLeave(LeaveRequest request, Doctor currentDoctor) {
        for (Leave.type type : request.getShifts()) {
            Leave leave = new Leave();
            leave.setDoctor(currentDoctor);
            leave.setDate(request.getDay());
            leave.setReason(request.getReason());
            leave.setLeaveStatus(Leave.leaveStatus.CHO_DUYET);
            leave.setSubmitDate(LocalDate.now());
            setTimeByShift(leave, type);
            seRepository.save(leave);
        }
    }

    private void setTimeByShift(Leave leave, Leave.type type) {
        if (type == Leave.type.SANG) {
            leave.setStartTime(LocalTime.of(7, 0));
            leave.setEndTime(LocalTime.of(12, 0));
        } else if (type == Leave.type.CHIEU) {
            leave.setStartTime(LocalTime.of(12, 0));
            leave.setEndTime(LocalTime.of(17, 0));
        } else if (type == Leave.type.TOI) {
            leave.setStartTime(LocalTime.of(17, 0));
            leave.setEndTime(LocalTime.of(23, 0));
        }
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }


    @Override
    @Transactional
    @CacheEvict(value = "scheduleSlotsByDay", allEntries = true)
    public void updateLeave(LeaveRequest request) {
        var leave = seRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
        if (leave.getLeaveStatus() != Leave.leaveStatus.CHO_DUYET) {
            throw new RuntimeException("Cannot update approved leave");
        }
        if(request.getLeaveStatus() != null){
            leave.setLeaveStatus(request.getLeaveStatus());
        }
        if(request.getReason() != null){
            leave.setReason(request.getReason());
        }
        if(request.getDay() != null){
            leave.setDate(request.getDay());
        }
        if(request.getShifts() != null && !request.getShifts().isEmpty()){
            setTimeByShift(leave, request.getShifts().get(0));
        }

        seRepository.save(leave);

//        Doctor doctor = leave.getDoctor();
//        DayOfWeek day = leave.getDate().getDayOfWeek();
//        Schedule schedule = repository.findByDayAndDoctorIdAndStartTimeAndEndTime(
//                        day,
//                        leave.getDoctor().getId(),
//                        leave.getStartTime(),
//                        leave.getEndTime())
//                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
//        repository.save(schedule);
    }

    @Override
    public Schedule.Shift getShift(LocalTime time) {
        if (time.isAfter(LocalTime.of(6, 59)) && time.isBefore(LocalTime.of(12, 0))) {
            return Schedule.Shift.SANG;
        }
        if (time.isAfter(LocalTime.of(11, 59)) && time.isBefore(LocalTime.of(18, 0))) {
            return Schedule.Shift.CHIEU;
        }
        if (time.isAfter(LocalTime.of(17, 59)) && time.isBefore(LocalTime.of(22, 1))) {
            return Schedule.Shift.TOI;
        }
        return null;
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

            SlotResponse slotResponse = sqs.getSlotsOfDay(
                    departmentId,
                    doctorId,
                    currentDate,
                    shift
            );
            results.add(slotResponse);
            currentDate = currentDate.plusDays(1);
        }

        return results;
    }

}
