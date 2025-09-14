package com.dcm.demo.repository;

import com.dcm.demo.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    List<Doctor> findAllByOrderByDepartmentIdAscDegreeExaminationFeeDesc();

}
