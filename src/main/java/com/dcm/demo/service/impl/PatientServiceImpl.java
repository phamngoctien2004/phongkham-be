package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.PatientRequest;
import com.dcm.demo.dto.request.VerifyOtpRequest;
import com.dcm.demo.dto.response.PatientResponse;
import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.mapper.PatientMapper;
import com.dcm.demo.mapper.UserMapper;
import com.dcm.demo.model.Patient;
import com.dcm.demo.model.Relationship;
import com.dcm.demo.model.User;
import com.dcm.demo.repository.PatientRepository;
import com.dcm.demo.service.interfaces.PatientService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PatientMapper patientMapper;
    private final SendMessage sendMessage;

    @Override
    @Transactional
    public PatientResponse create(PatientRequest request) {
        return createPatientAndAccount(request);
    }

    @Override
    @Transactional
    public PatientResponse update(PatientRequest request) {
        Patient patient = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        toPatient(request, patient);

        if (request.getPhone() != null && !request.getPhone().isEmpty() && !request.getPhone().equals(patient.getPhone())) {
            patient.setPhone(request.getPhone());
            User user = userService.findByPhone(request.getPhone())
                    .orElse(new User());

//          neu chua co tai khoan thi tao moi, nguoc lai thi them quan he
            Relationship relationship = buildRelationship(patient, user, request.getRelationshipName());
            patient.getRelationships().add(relationship);

            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            userService.save(user);
        }

        return patientMapper.toResponse(
                repository.save(patient)
        );
    }

    private void toPatient(PatientRequest request, Patient patient) {
        patient.setFullName(request.getFullName());
        patient.setAddress(request.getAddress());
        patient.setGender(request.getGender());
        patient.setBirth(request.getBirth());
        patient.setBloodType(request.getBloodType());
        patient.setWeight(request.getWeight());
        patient.setHeight(request.getHeight());
        patient.setCccd(request.getCccd());
    }

    @Override
    public PatientResponse updateForBooking(PatientRequest request) {
        Patient patient = repository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Patient existingByCccd = repository.findByCccd(request.getCccd())
                .orElse(null);
//      benh nhan kham lan dau
        if (existingByCccd == null) {
            patient.setPhone(request.getPhone());
            toPatient(request, patient);

//          benh nhan chua tung kham -> chua co tai khoan -> tao tai khoan neu co so dien thoai
            if (request.getPhone() != null && !request.getPhone().isEmpty()) {
//              tim tai khoan theo so dien thoai
                User user = userService.findByPhone(patient.getPhone())
                        .orElse(new User());

//          neu chua co tai khoan thi tao moi, nguoc lai thi them quan he
                Relationship relationship = buildRelationship(patient, user, null);
                patient.getRelationships().add(relationship);


                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                userService.save(user);
            }
            return patientMapper.toResponse(
                    repository.save(patient)
            );
        }

//      benh nhan da tung kham, dong bo thong tin benh da co
        Relationship relationship = patient.getRelationships().get(0);
        relationship.setPatient(existingByCccd);

//      tao tai khoan moi neu cap nhat so dien thoai
//      cap nhat so dien thoai (da co tai khoan)
        if (existingByCccd.getPhone() != null && !request.getPhone().equals(existingByCccd.getPhone())) {
            patient.setPhone(request.getPhone());
            User user = userService.findByPhone(patient.getPhone())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPhone(request.getPhone());
            user.setEmail(request.getEmail());
            userService.save(user);

            return patientMapper.toResponse(
                    repository.save(existingByCccd)
            );
        }

//      tao moi tai khoan
        if (existingByCccd.getPhone() == null && request.getPhone() != null) {
            existingByCccd.setPhone(request.getPhone());

            User user = new User();
            user.setPhone(request.getPhone());
            user.setEmail(request.getEmail());
            Relationship relationship1 = buildRelationship(existingByCccd, user, null);
            patient.getRelationships().add(relationship1);
            userService.save(user);
        }

        return patientMapper.toResponse(
                repository.save(existingByCccd)
        );
    }

    //  benh nhan chua tung kham
    private void updateOrCreateUser(PatientRequest request, Patient patient) {

    }

    //    tao new user co lien ket voi tai khoan
    @Override
    public PatientResponse createPatient(PatientRequest request, User user) {
        Patient patient = buildPatient(request);
        Relationship relationship = this.buildRelationship(patient, user, null);
        relationship.setVerified(true);
        patient.getRelationships().add(relationship);
        return patientMapper.toResponse(
                repository.save(patient)
        );
    }

    @Override
    public void delete(Integer id) {
        Patient patient = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        repository.delete(patient);
    }

    //    tao user va account
    @Override
    public PatientResponse createPatientAndAccount(PatientRequest request) {
        Patient patient = buildPatient(request);

        if (request.getPhone() != null) {
            User newUser = userService.createAccountByPhone(request.getPhone());

            Relationship relationship = this.buildRelationship(patient, newUser, "BAN_THAN");
            relationship.setVerified(true);

            patient.getRelationships().add(relationship);
        }

        if (request.getPhoneLink() != null) {
            User userLink = userService.createAccountByPhone(request.getPhoneLink());

            Relationship relationship = this.buildRelationship(patient, userLink, "NGUOI_THAN");
            relationship.setVerified(true);

            patient.getRelationships().add(relationship);
        }

        return patientMapper.toResponse(
                repository.save(patient)
        );
    }


    @Override
    public Patient findByPhone(String phone) {
        return repository.findByPhone(phone).orElse(null);
    }

    @Override
    public Patient findByCccd(String cccd) {
        return repository.findByCccd(cccd).orElseThrow(() -> new RuntimeException("Patient not found"));
    }


    @Override
    public PatientResponse findById(Integer id) {
        return patientMapper.toResponse(
                repository.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"))
        );
    }

    @Override
    public Patient findEntityById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    @Override
    public List<PatientResponse> findAll(String keyword) {
        Specification<Patient> spec = FilterHelper.contain(keyword, List.of(
                "phone", "cccd", "fullName"
        ));
        return repository.findAll(spec).stream()
                .map(patientMapper::toResponse)
                .toList();
    }

    @Override
    public Page<PatientResponse> findAll(String keyword, Pageable pageable) {
        Specification<Patient> spec = FilterHelper.contain(keyword, List.of(
                "phone", "cccd", "fullName"
        ));
        return repository.findAll(spec, pageable)
                .map(patientMapper::toResponse);
    }

    @Override
    public PatientResponse me() {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Patient patient = repository.findByPhone(user.getPhone()).orElseThrow(() -> new RuntimeException("Patient not found"));
        PatientResponse response = patientMapper.toResponse(patient);
        response.setEmail(user.getEmail());
        return response;
    }

    @Override
    public Patient save(Patient patient) {
        return repository.save(patient);
    }

    @Override
    public List<PatientResponse> all() {
        User user = userService.getCurrentUser();
        if (user != null) {
            List<Relationship> relationships = user.getRelationships();

            List<PatientResponse> patientResponses = new ArrayList<>();

            for (Relationship relationship : relationships) {
                Patient patient = relationship.getPatient();
                PatientResponse response = patientMapper.toResponse(patient);
                response.setRelationship(relationship.getRelational());
                response.setVerified(relationship.getVerified());
                patientResponses.add(response);
            }
            return patientResponses;
        }
        return null;
    }

    @Override
    public PatientResponse addRelationship(PatientRequest request) {
//      tao benh nhan tam
        User user = userService.getCurrentUser();
        Patient patient = this.buildPatient(request);
        Relationship relationship = this.buildRelationship(patient, user, request.getRelationshipName());

        patient.getRelationships().add(relationship);
        return patientMapper.toResponse(
                repository.save(patient)
        );
    }

    @Override
    public void syncRelationship(VerifyOtpRequest request) {
//      kiem tra otp
        sendMessage.checkOtp(request.getPhone(), request.getOtp());

//      gan id benh nhan da tim thay vao tai khoan
        User user = userService.getCurrentUser();
        Patient patient = repository.findBySync(request.getPhone()).orElseThrow(() -> new RuntimeException("Patient not found"));
        Patient existPatient = repository.findByPhone(request.getPhone()).orElse(null);

//      dong bo voi benh nhan da ton tai
        if (existPatient != null) {
            String relational = patient.getRelationships().get(0).getRelational();
            Relationship relationship = this.buildRelationship(existPatient, user, relational);
            relationship.setVerified(true);
            existPatient.getRelationships().add(relationship);
            repository.save(existPatient);
            repository.delete(patient);
            return;
        }

//      cap nhat benh nhan moi tao voi so chinh thuc
        patient.getRelationships().get(0).setVerified(true);
        patient.setPhone(request.getPhone());
        repository.save(patient);
    }

    @Override
    public void deleteRelationship(Integer patientId) {
        User user = userService.getCurrentUser();
        user.getRelationships().removeIf(r -> r.getPatient().getId().equals(patientId));
        userService.save(user);
    }

    @Override
    public Relationship buildRelationship(Patient patient, User user, String relational) {
        Relationship relationship = new Relationship();
        relationship.setUser(user);
        relationship.setPatient(patient);
        if (relational != null && !relational.isEmpty()) {
            relationship.setRelational(relational);
        }
        return relationship;
    }

    private Patient buildPatient(PatientRequest request) {
        Patient patient = patientMapper.toEntity(request);
        patient.setCode("BN" + System.currentTimeMillis());
        return patient;
    }


}
