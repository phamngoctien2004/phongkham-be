package com.dcm.demo.repository;

import com.dcm.demo.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer>, JpaSpecificationExecutor<Doctor> {
    List<Doctor> findAllByOrderByDepartmentIdAscDegreeExaminationFeeDesc();

    @Query("""
        SELECT d FROM Doctor d
        JOIN FETCH Department dep on dep.id = d.department.id
        JOIN FETCH Degree deg on deg.degreeId = d.degree.degreeId
        WHERE
        (:keyword IS NULL OR d.fullName LIKE %:keyword% OR d.phone LIKE %:keyword%) AND
        (:departmentId IS NULL OR dep.id = :departmentId)
        AND (:degreeId IS NULL OR deg.degreeId = :degreeId)

""")
    Page<Doctor> findAll(Pageable pageable,
                         String keyword,
                         Integer departmentId,
                         Integer degreeId);
}
