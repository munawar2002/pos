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

}
