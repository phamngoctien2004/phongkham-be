package com.dcm.demo.service.impl;

import com.dcm.demo.dto.response.DoctorResponse;
import com.dcm.demo.dto.response.SlotResponse;
import com.dcm.demo.model.Department;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Leave;
import com.dcm.demo.model.Schedule;
import com.dcm.demo.repository.LeaveRepository;
import com.dcm.demo.repository.ScheduleRepository;
import com.dcm.demo.service.interfaces.AppointmentService;
import com.dcm.demo.service.interfaces.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleQueryService {
    private final ScheduleRepository repository;
    private final LeaveRepository seRepository;
    private final DepartmentService departmentService;
    private final AppointmentService appointmentService;

    @Cacheable(
            value = "scheduleSlotsByDay",
            key = "T(String).valueOf(#departmentId ?: 'dep-null')" +
                    " + '-' + T(String).valueOf(#doctorId ?: 'doc-null')" +
                    " + '-' + #date.toString()" +
                    " + '-' + T(String).valueOf(#shift ?: 'shift-null')"
    )
    public SlotResponse getSlotsOfDay(
            Integer departmentId,
            Integer doctorId,
            LocalDate date,
            Schedule.Shift shift
    ) {
        // đây chính là phần bạn đang làm trong while
        var schedules = repository.findByOption(
                doctorId,
                departmentId,
                date.getDayOfWeek(),
                shift
        );
        return buildSlotResponse(schedules, date);
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
            doctorResponse.setExaminationFee(doctor.getDegree().getExaminationFee());
            Department department = doctor.getDepartment();
            doctorResponse.setRoomName(departmentService.getRoomFromDepartment(department));
//          tra ve khung gio da duoc dat (danh cho dat lich)
            List<LocalTime> bookedTimes = appointmentService.getTimeBooked(doctor.getId(), currentDate, schedule.getShift());
            doctorResponse.setInvalidTimes(bookedTimes);
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
        List<Leave> leaves = seRepository.findByDateAndDoctorIdAndLeaveStatus(
                date, schedule.getDoctor().getId(), Leave.leaveStatus.DA_DUYET
        );

        for (Leave leave : leaves) {
            LocalTime startTime = leave.getStartTime();
            LocalTime endTime = leave.getEndTime();
            if (startTime.isBefore(schedule.getEndTime()) && endTime.isAfter(schedule.getStartTime())) {
                return false; // Slot is not available
            }
        }
        return true; // Slot is available
    }

}
