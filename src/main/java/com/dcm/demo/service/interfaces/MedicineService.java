package com.dcm.demo.service.interfaces;

import com.dcm.demo.dto.response.MedicineResponse;

import java.util.List;

public interface MedicineService {
    List<MedicineResponse> getAllMedicines(String keyword);
}
