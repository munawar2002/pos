package com.mjtech.pos.repository;

import com.mjtech.pos.entity.ProductCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCompanyRepository extends JpaRepository<ProductCompany, Integer> {

    List<ProductCompany> findByNameContainingIgnoreCase(String name);

    @Query("SELECT u FROM ProductCompany u WHERE u.name LIKE %:search% OR u.address LIKE %:search% OR u.contactNo LIKE %:search% OR u.contactPerson LIKE %:search% OR u.contactPersonNo LIKE %:search%")
    List<ProductCompany> findByNameOrContactOrAddress(@Param("search") String search);

    boolean existsByName(String name);
}
