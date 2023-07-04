package com.mjtech.pos.service;

import com.mjtech.pos.entity.ProductCompany;
import com.mjtech.pos.repository.ProductCompanyRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductCompanyService {

    private final ProductCompanyRepository productCompanyRepository;

    public List<ProductCompany> find(String name) {
        if(StringUtils.isNotEmpty(name) ) {
            return productCompanyRepository.findByNameContainingIgnoreCase(name);
        } else {
            return productCompanyRepository.findAll();
        }
    }

    public List<ProductCompany> findInAllColumns(String search) {
        if(StringUtils.isNotEmpty(search) ) {
            return productCompanyRepository.findByNameOrContactOrAddress(search);
        } else {
            return productCompanyRepository.findAll();
        }
    }

    public void saveProductCompany(Integer id, String name, String address, String contactNo, String contactPerson) {
        ProductCompany productCompany;
        if(id == null) {
            productCompany = ProductCompany.builder()
                    .active(true)
                    .name(name)
                    .address(address)
                    .contactNo(contactNo)
                    .contactPerson(contactPerson)
                    .build();
        } else {
            productCompany = productCompanyRepository.findById(id).orElseThrow(() -> new RuntimeException("ProductCompany not found with id "+ id));
            productCompany.setName(name);
            productCompany.setAddress(address);
            productCompany.setContactNo(contactNo);
            productCompany.setContactPerson(contactPerson);
        }

        productCompanyRepository.save(productCompany);
    }

    public void deleteById(int id) {
        productCompanyRepository.deleteById(id);
    }

}
