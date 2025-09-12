package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.ScheduleRequest;
import com.dcm.demo.dto.response.ScheduleResponse;
import com.dcm.demo.exception.AppException;
import com.dcm.demo.exception.ErrorCode;
import com.dcm.demo.mapper.ScheduleMapper;
import com.dcm.demo.model.Doctor;
import com.dcm.demo.model.Schedule;
import com.dcm.demo.model.ScheduleException;
import com.dcm.demo.model.ScheduleOfficial;
import com.dcm.demo.repository.ScheduleExceptionRepository;
import com.dcm.demo.repository.ScheduleOfficialRepository;
import com.dcm.demo.repository.ScheduleRepository;
import com.dcm.demo.service.interfaces.DoctorService;
import com.dcm.demo.service.interfaces.ScheduleService;
import lombok.RequiredArgsConstructor;
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
    private final ScheduleRepository repository;
    private final ScheduleExceptionRepository seRepository;
    private final ScheduleOfficialRepository soRepository;
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
    public void generateScheduleOfficial(LocalDate day) {
        String dayOfWeek = mapDayOfWeek(day);
        List<Schedule> schedules = repository.findByDayAndDoctorId(Schedule.DayOfWeek.valueOf(dayOfWeek), 1);
        List<ScheduleException> scheduleExceptions = seRepository
                .findByDateAndLeaveStatusAndDoctorId(day, ScheduleException.leaveStatus.DA_DUYET, 1);
        List<ScheduleOfficial> result = new ArrayList<>();

//      tao moi cac khung gio trong ngay dua tren lich lam viec cua bac si
        for (Schedule schedule : schedules) {
            LocalTime s = schedule.getStartTime();
            LocalTime e = schedule.getEndTime();

            while (!s.plusMinutes(60).isAfter(e)) {
                ScheduleOfficial scheduleOfficial = new ScheduleOfficial();
                scheduleOfficial.setDoctor(schedule.getDoctor());
                scheduleOfficial.setDate(day);
                scheduleOfficial.setStartTime(s);

                LocalTime slotEnd = s.plusMinutes(60);
                scheduleOfficial.setEndTime(slotEnd);
                result.add(scheduleOfficial);
                s = s.plusMinutes(60);
            }
        }

//        danh dau ngay nghi tren lich chinh thuc
        for (ScheduleOfficial it : result) {
            for (ScheduleException ex : scheduleExceptions) {
//              vi du nghi 7-10 va lich 6-7 = 6 < 10 and 7 = 7 => khong hop le , 7-8 = 7 < 10 and 8 > 10 => hop le
                if (it.getStartTime().isBefore(ex.getEndTime()) && it.getEndTime().isAfter(ex.getStartTime())) {
                    it.setStatus(false);
                    break;
                }
            }
            soRepository.save(it);
        }
    }

    @Override
    @Transactional
    public void updateScheduleOfficial(Integer scheduleExceptionId) {
        ScheduleException scheduleException = seRepository.findById(scheduleExceptionId)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOTFOUND));
        Integer doctorId = scheduleException.getDoctor().getId();

        List<ScheduleOfficial> scheduleOfficials = soRepository
                .findByDateAndDoctorId(scheduleException.getDate(), doctorId);
        for (ScheduleOfficial it : scheduleOfficials) {
            if (it.getStartTime().isBefore(scheduleException.getEndTime())
                    && it.getEndTime().isAfter(scheduleException.getStartTime())) {
                it.setStatus(false);
                soRepository.save(it);
            }
        }
    }

    private String mapDayOfWeek(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            case SUNDAY -> "CN";
        };
    }
}
