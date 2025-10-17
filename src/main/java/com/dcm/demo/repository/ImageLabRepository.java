package com.dcm.demo.repository;

import com.dcm.demo.model.ImageLab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageLabRepository extends JpaRepository<ImageLab, Integer> {
    List<ImageLab> findByLabResultId(Integer labResultId);
}
