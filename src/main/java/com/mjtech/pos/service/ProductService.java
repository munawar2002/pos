package com.mjtech.pos.service;

import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductCompanyRepository productCompanyRepository;
    private final ProductCategoryRepository productCategoryRepository;

    private final ProductPhotoRepository productPhotoRepository;

    private final static ModelMapper modelMapper = new ModelMapper();

    public List<ProductDto> searchProducts(String code,
                                        String name,
                                        Integer categoryId,
                                        Integer companyId,
                                        Integer supplierId,
                                        Integer quantity,
                                        Double buyPrice,
                                        Double sellPrice,
                                        List<Integer> ignoreProductIds) {

        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(code)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")),
                        "%" + code.toLowerCase() + "%"));
            }

            if (StringUtils.isNotBlank(name)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"));
            }

            if (quantity != null) {
                predicates.add(criteriaBuilder.equal(root.get("quantity"), quantity));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("productCategory").get("id"), categoryId));
            }

            if (companyId != null) {
                predicates.add(criteriaBuilder.equal(root.get("productCompany").get("id"), companyId));
            }

            if (supplierId != null) {
                predicates.add(criteriaBuilder.equal(root.get("supplier").get("id"), supplierId));
            }

            if (buyPrice != null) {
                predicates.add(criteriaBuilder.equal(root.get("buyPrice"), buyPrice));
            }

            if (sellPrice != null) {
                predicates.add(criteriaBuilder.equal(root.get("sellPrice"), sellPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<Product> products = productRepository.findAll(specification, Pageable.unpaged()).getContent();

        List<ProductDto> productDtos = new ArrayList<>();
        products.stream().filter(product -> !ignoreProductIds.contains(product.getId())).forEach(product -> {
            Supplier supplier = product.getSupplier();

            ProductCategory productCategory = product.getProductCategory();

            ProductCompany productCompany = product.getProductCompany();

            ProductPhoto productPhoto = productPhotoRepository.findByProductId(product.getId()).orElse(null);

            ProductDto dto = modelMapper.map(product, ProductDto.class);
            dto.setProductCompanyName(productCompany.getName());
            dto.setSupplierName(supplier.getName());
            dto.setCategoryName(productCategory.getName());
            dto.setImage(productPhoto == null ? null : productPhoto.getImage());
            dto.setService(product.isService());
            productDtos.add(dto);
        });

        return productDtos;
    }

    public void updateProductQuantity(Product product, int quantity) {
        int updatedQuantity = product.getQuantity() + quantity;
        product.setQuantity(updatedQuantity);
        productRepository.save(product);
    }
}
