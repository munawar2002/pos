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

    public List<Supplier> findInAllColumns(String search) {
        if(StringUtils.isNotEmpty(search) ) {
            return supplierRepository.findByNameOrContactOrAddress(search);
        } else {
            return supplierRepository.findAll();
        }
    }

    public void saveSupplier(Integer id, String name, String address, String contactNo, String contactPerson) {
        Supplier supplier;
        if(id == null) {
            supplier = Supplier.builder()
                    .active(true)
                    .name(name)
                    .address(address)
                    .contactNo(contactNo)
                    .contactPerson(contactPerson)
                    .build();
        } else {
            supplier = supplierRepository.findById(id).orElseThrow(() -> new RuntimeException("Supplier not found with id "+ id));
            supplier.setName(name);
            supplier.setAddress(address);
            supplier.setContactNo(contactNo);
            supplier.setContactPerson(contactPerson);
        }

        supplierRepository.save(supplier);
    }

    public void deleteById(int id) {
        supplierRepository.deleteById(id);
    }
}
