package com.mjtech.pos.mapper;

import com.mjtech.pos.dto.GenericFromDto;
import com.mjtech.pos.entity.ProductCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;

public class DtoMapperTest {

    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    public void testMapToGenericFormDtos() {
        // Arrange
        ProductCategory category1 = ProductCategory.builder().name("name1").description("desc1").id(1).build();
        ProductCategory category2 = ProductCategory.builder().name("name2").description("desc2").id(2).build();
        ProductCategory category3 = ProductCategory.builder().name("name3").description("desc3").id(3).build();

        List<ProductCategory> productCategories = Arrays.asList(category1, category2, category3);

        // Act
        List<GenericFromDto> dtoList = DtoMapper.mapToGenericFormDtos(productCategories);

        // Assert
        Assertions.assertEquals(productCategories.size(), dtoList.size());
        for (int i = 0; i < productCategories.size(); i++) {
            ProductCategory productCategory = productCategories.get(i);
            GenericFromDto dto = dtoList.get(i);
            Assertions.assertEquals(productCategory.getName(), dto.getName());
        }
    }

}
