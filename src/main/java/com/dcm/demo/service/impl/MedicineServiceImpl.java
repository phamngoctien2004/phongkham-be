package com.dcm.demo.service.impl;

import com.dcm.demo.Utils.ConvertUtil;
import com.dcm.demo.dto.response.MedicineResponse;
import com.dcm.demo.helpers.FilterHelper;
import com.dcm.demo.model.Medicine;
import com.dcm.demo.repository.MedicineRepository;
import com.dcm.demo.service.interfaces.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository medicineRepository;

    @Override
    public List<MedicineResponse> getAllMedicines(String keyword) {
        Specification<Medicine> specification = FilterHelper.contain(keyword, "name");
        return medicineRepository.findAll(specification).stream()
                .map(ConvertUtil::convert)
                .toList();
    }
}

