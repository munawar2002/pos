package com.mjtech.pos.service;

import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.CustomerRepository;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> searchCustomer(String name, String gender, String contactNo, String address) {
        Specification<Customer> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(name)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")),
                        "%" + name.toLowerCase() + "%"));
            }

            if (StringUtils.isNotBlank(gender)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("gender")), gender));
            }

            if (StringUtils.isNotBlank(contactNo)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("contactNo")),
                        "%" + contactNo + "%"));
            }

            if (StringUtils.isNotBlank(address)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")),
                        "%" + address + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return customerRepository.findAll(specification, Pageable.unpaged()).getContent();
    }


}
