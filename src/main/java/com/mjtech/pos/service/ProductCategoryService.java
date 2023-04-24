package com.mjtech.pos.service;

import com.mjtech.pos.constant.GenericFormOperation;
import com.mjtech.pos.entity.ProductCategory;
import com.mjtech.pos.repository.ProductCategoryRepository;
import com.mjtech.pos.util.ActiveUser;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public void performOperation(GenericFormOperation operation, String name, String description, Integer selectedId) {
        switch (operation) {
            case SAVE -> save(name, description);
            case EDIT -> edit(name, description, selectedId);
            case DELETE -> delete(selectedId);
            default -> throw new RuntimeException(String.format("Operation [%s] does not support in this method!", operation));
        }
    }

    private void delete(int id) {
        ProductCategory category = getProductCategory(id);
        productCategoryRepository.delete(category);
    }

    private ProductCategory getProductCategory(int id) {
        Optional<ProductCategory> optionalProductCategory = productCategoryRepository.findById(id);
        if(optionalProductCategory.isEmpty()) {
            throw new RuntimeException(String.format("Product category does not exist with id [%s] ", id));
        }
        return optionalProductCategory.get();
    }

    private void edit(String name, String description, int selectedId) {
        ProductCategory category = getProductCategory(selectedId);
        category.setName(name);
        category.setDescription(description);
        category.setUpdatedBy(ActiveUser.getActiveUsername());
        category.setUpdatedAt(new Date());
        productCategoryRepository.save(category);
    }

    public void save(String name, String description) {
        ProductCategory category = ProductCategory.builder()
                .name(name)
                .description(description)
                .createdBy(ActiveUser.getActiveUsername())
                .createdAt(new Date())
                .build();

        productCategoryRepository.save(category);
    }

    public List<ProductCategory> search(String name, String description) {
        if(StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(description)) {
            return productCategoryRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(name, description);
        } else if(StringUtils.isNotEmpty(name)) {
            return productCategoryRepository.findByNameContainingIgnoreCase(name);
        } else if(StringUtils.isNotEmpty(description)) {
            return productCategoryRepository.findByDescriptionContainingIgnoreCase(description);
        } else {
            return productCategoryRepository.findAll();
        }
    }
}
