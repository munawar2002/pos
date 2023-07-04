package com.mjtech.pos.repository;

import com.mjtech.pos.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findByNameContainingIgnoreCase(String name);

    @Query("SELECT u FROM Supplier u WHERE u.name LIKE %:search% OR u.address LIKE %:search% OR u.contactNo LIKE %:search% OR u.contactPerson LIKE %:search% OR u.contactPersonNo LIKE %:search%")
    List<Supplier> findByNameOrContactOrAddress(@Param("search") String search);

    boolean existsByName(String name);
}
