package com.mjtech.pos.service;

import com.mjtech.pos.BaseTest;
import com.mjtech.pos.constant.GenericFormOperation;
import com.mjtech.pos.entity.ProductCategory;
import com.mjtech.pos.repository.ProductCategoryRepository;
import com.mjtech.pos.util.ActiveUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductCategoryServiceTest extends BaseTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductCategoryService productCategoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPerformOperation() {
        // Test save operation
        productCategoryService.performOperation(GenericFormOperation.SAVE, "Category 1", "Category 1 description", null);
        verify(productCategoryRepository, times(1)).save(any());

        // Test edit operation
        ProductCategory category = new ProductCategory();
        category.setId(1);
        category.setName("Category 1");
        category.setDescription("Category 1 description");
        category.setCreatedBy("admin");
        category.setCreatedAt(new Date());
        when(productCategoryRepository.findById(1)).thenReturn(Optional.of(category));

        productCategoryService.performOperation(GenericFormOperation.EDIT, "Category 1 Edited", "Category 1 description edited", 1);
        verify(productCategoryRepository, times(2)).save(any());

        assertEquals("Category 1 Edited", category.getName());
        assertEquals("Category 1 description edited", category.getDescription());
        assertEquals("admin", category.getCreatedBy());
        assertEquals(ActiveUser.getActiveUsername(), category.getUpdatedBy());

        // Test delete operation
        productCategoryService.performOperation(GenericFormOperation.DELETE, null, null, 1);
        verify(productCategoryRepository, times(1)).delete(any());
    }

    @Test
    void testGetProductCategory() {
        ProductCategory category = new ProductCategory();
        category.setId(1);
        when(productCategoryRepository.findById(1)).thenReturn(Optional.of(category));

        assertEquals(category, productCategoryService.getProductCategory(1));
    }

    @Test
    void testGetProductCategoryNotFoundException() {
        when(productCategoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productCategoryService.getProductCategory(1));
    }

    @Test
    void testSave() {
        productCategoryService.save("Category 1", "Category 1 description");
        verify(productCategoryRepository, times(1)).save(any());
    }

    @Test
    void testSearch() {
        // Test search by name and description
        List<ProductCategory> categories = new ArrayList<>();
        categories.add(new ProductCategory());
        when(productCategoryRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase("category", "description")).thenReturn(categories);

        assertEquals(categories, productCategoryService.search("category", "description"));

        // Test search by name only
        categories.clear();
        categories.add(new ProductCategory());
        when(productCategoryRepository.findByNameContainingIgnoreCase("category")).thenReturn(categories);

        assertEquals(categories, productCategoryService.search("category", null));

        // Test search by description only
        categories.clear();
        categories.add(new ProductCategory());
        when(productCategoryRepository.findByDescriptionContainingIgnoreCase("description")).thenReturn(categories);

        assertEquals(categories, productCategoryService.search(null, "description"));

        // Test search all
        categories.clear();
        categories.add(new ProductCategory());
        when(productCategoryRepository.findAll()).thenReturn(categories);
        assertEquals(categories, productCategoryService.search(null, null));
    }
}