package com.mjtech.pos.service;

import com.mjtech.pos.entity.Supplier;
import com.mjtech.pos.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> find(String name) {
        if(StringUtils.isNotEmpty(name) ) {
            return supplierRepository.findByNameContainingIgnoreCase(name);
        } else {
            return supplierRepository.findAll();
        }
    }

}
