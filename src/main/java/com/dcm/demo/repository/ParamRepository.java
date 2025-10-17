package com.dcm.demo.repository;

import com.dcm.demo.model.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ParamRepository extends JpaRepository<Param, Integer>, JpaSpecificationExecutor<Param> {
}
