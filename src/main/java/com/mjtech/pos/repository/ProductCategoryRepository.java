package com.mjtech.pos.repository;

import com.mjtech.pos.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

    Optional<ProductCategory> findByName(String name);

    List<ProductCategory> findByNameContainingIgnoreCase(String name);

    List<ProductCategory> findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String name, String description);

    List<ProductCategory> findByDescriptionContainingIgnoreCase(String description);
}
