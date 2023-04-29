package com.mjtech.pos.repository;

import com.mjtech.pos.entity.Product;
import com.mjtech.pos.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Integer>, JpaSpecificationExecutor<Product> {

    Optional<ProductPhoto> findByProductId(int productId);
}
