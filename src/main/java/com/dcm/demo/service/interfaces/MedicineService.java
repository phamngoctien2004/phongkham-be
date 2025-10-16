package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.response.MedicineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicineService {
    Page<MedicineResponse> getAllMedicines(String keyword, Pageable pageable);
}
