package com.mjtech.pos.repository;

import com.mjtech.pos.entity.ProductCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCompanyRepository extends JpaRepository<ProductCompany, Integer> {

    List<ProductCompany> findByNameContainingIgnoreCase(String name);
}
