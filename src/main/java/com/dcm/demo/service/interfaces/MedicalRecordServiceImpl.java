package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.request.CreateMedicalRecord;
import com.dcm.demo.dto.request.UserRequest;
import com.dcm.demo.model.MedicalRecord;
import com.dcm.demo.model.Patient;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordRepository repository;
    private final UserService userService;
    private final AppointmentService appointmentService;
    private DoctorService doctorService;
    @Override
    @Transactional
    public void createMedicalRecord(CreateMedicalRecord request) {
        MedicalRecord medicalRecord = new MedicalRecord();

        UserRequest userRequest = request.getPatient();
        User user = userService.findByPhone(userRequest.getPhone());
        if (user == null) {
//           tao user
            user = userService.createUserEntity(userRequest);
        }else{
            medicalRecord.setPatient(user.getPatient());
        }

        if (request.getAppointmentId() != null) {
            medicalRecord.setAppointment(appointmentService.findById(request.getAppointmentId()));
        }
        if(request.getDoctorId() != null) {
            medicalRecord.setDoctor(doctorService.findById(request.getDoctorId()));
        }
        medicalRecord.setSymptoms(request.getSymptoms());
    }
}
