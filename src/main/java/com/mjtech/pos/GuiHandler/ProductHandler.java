package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.*;
import com.mjtech.pos.service.ProductService;
import com.mjtech.pos.util.ActiveUser;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class ProductHandler {

    private final ProductRepository productRepository;

    private final ProductPhotoRepository productPhotoRepository;

    private final ProductCompanyRepository productCompanyRepository;

    private final ProductCategoryRepository productCategoryRepository;

    private final SupplierRepository supplierRepository;

    private final ProductService productService;

    public void saveProduct(TextField codeTextField,
                            TextField nameTextField,
                            int categoryId,
                            int productCompanyId,
                            int supplierId,
                            TextField buyPriceTextField,
                            TextField sellPriceTextField,
                            TextField quantityTextField,
                            boolean isService) {
        String code = codeTextField.getText();
        String name = nameTextField.getText();
        Double buyPrice = Double.parseDouble(buyPriceTextField.getText());
        Double sellPrice = Double.parseDouble(sellPriceTextField.getText());
        int quantity = Integer.parseInt(quantityTextField.getText());

        Optional<Product> byCode = productRepository.findByCode(code);
        if(byCode.isPresent()) {
            FxmlUtil.callErrorAlert(String.format("Product already exist with product code %s", code));
            return;
        }

        Optional<Product> byName = productRepository.findByName(name);
        if(byName.isPresent()) {
            FxmlUtil.callErrorAlert(String.format("Product already exist with product name %s", name));
            return;
        }

        ProductCompany productCompany = productCompanyRepository.findById(productCompanyId)
                .orElseThrow(() -> new RuntimeException("Product company not found with id " + productCompanyId));

        ProductCategory productCategory = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Product category not found with id " + categoryId));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Product supplier not found with id " + supplierId));

        Product product = Product.builder()
                .code(code)
                .name(name)
                .productCategory(productCategory)
                .productCompany(productCompany)
                .supplier(supplier)
                .buyPrice(buyPrice)
                .sellPrice(sellPrice)
                .quantity(quantity)
                .isService(isService)
                .createdBy(ActiveUser.getActiveUsername())
                .createdAt(new Date())
                .build();

        productRepository.save(product);

        FxmlUtil.callInformationAlert(String.format("Successfully created a new product with code %s and name %s",
                product.getCode(), product.getName()));
    }

    public void editProduct(int productId,
                            TextField codeTextField,
                            TextField nameTextField,
                            int categoryId,
                            int productCompanyId,
                            int supplierId,
                            TextField buyPriceTextField,
                            TextField sellPriceTextField,
                            TextField quantityTextField,
                            boolean isService) {
        String code = codeTextField.getText();
        String name = nameTextField.getText();
        Double buyPrice = Double.parseDouble(buyPriceTextField.getText());
        Double sellPrice = Double.parseDouble(sellPriceTextField.getText());
        int quantity = Integer.parseInt(quantityTextField.getText());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(String.format("Product not found with id %s", productId)));

        ProductCompany productCompany = productCompanyRepository.findById(productCompanyId)
                .orElseThrow(() -> new RuntimeException("Product company new found with id " + productCompanyId));

        ProductCategory productCategory = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Product category not found with id " + categoryId));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Product supplier not found with id " + supplierId));

        product.setCode(code);
        product.setName(name);
        product.setProductCategory(productCategory);
        product.setProductCompany(productCompany);
        product.setSupplier(supplier);
        product.setBuyPrice(buyPrice);
        product.setSellPrice(sellPrice);
        product.setQuantity(quantity);
        product.setUpdatedBy(ActiveUser.getActiveUsername());
        product.setUpdatedAt(new Date());
        product.setService(isService);

        productRepository.save(product);

        FxmlUtil.callInformationAlert(String.format("Successfully edited product with code %s and name %s",
                product.getCode(), product.getName()));
    }

    public void deleteProduct(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(String.format("Product not found with id %s", productId)));

        productRepository.deleteById(productId);

        FxmlUtil.callInformationAlert(String.format("Successfully deleted product with code %s and name %s",
                product.getCode(), product.getName()));
    }

    public boolean delete(ProductDto product) {
        boolean proceed = FxmlUtil.callConfirmationAlert("Are you sure you want to delete product with name " + product.getName());
        if(!proceed) {
            return true;
        }
        deleteProduct(product.getId());
        return true;
    }

    public void searchProduct(TextField codeTextField,
                              TextField nameTextField,
                              Integer categoryId,
                              Integer productCompanyId,
                              Integer supplierId,
                              TextField buyPriceTextField,
                              TextField sellPriceTextField,
                              TextField quantityTextField,
                              TableView<ProductDto> productTableView) {
        searchProduct(codeTextField, nameTextField, categoryId, productCompanyId, supplierId, buyPriceTextField,
                sellPriceTextField, quantityTextField, productTableView, null);
    }

    public void searchProduct(TextField codeTextField,
                              TextField nameTextField,
                              Integer categoryId,
                              Integer productCompanyId,
                              Integer supplierId,
                              TextField buyPriceTextField,
                              TextField sellPriceTextField,
                              TextField quantityTextField,
                              TableView<ProductDto> productTableView,
                              List<Integer> ignoreProductIds) {
        ignoreProductIds = ignoreProductIds == null ? new ArrayList<>() : ignoreProductIds;

        Double buyPrice = buyPriceTextField.getText().isEmpty() ? null :  Double.parseDouble(buyPriceTextField.getText());
        Double sellPrice = sellPriceTextField.getText().isEmpty() ? null :  Double.parseDouble(sellPriceTextField.getText());
        Integer quantity = quantityTextField.getText().isEmpty() ? null : Integer.parseInt(quantityTextField.getText());

        List<ProductDto> productDtos = productService.searchProducts(codeTextField.getText(), nameTextField.getText(), categoryId, productCompanyId,
                supplierId, quantity, buyPrice, sellPrice, ignoreProductIds);

        var columnMap = Map.of("Code", "code",
                "Name", "name",
                "Category", "categoryName",
                "Company", "productCompanyName",
                "Supplier", "supplierName",
                "Sell", "sellPrice",
                "Buy", "buyPrice",
                "Quantity", "quantity");

        FxmlUtil.populateTableView(productTableView, productDtos, columnMap, this::delete);
    }

    public void saveProductPhoto(int productId, byte[] picBytes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(String.format("Product not found with id %s", productId)));

        Optional<ProductPhoto> optionalProductPhoto = productPhotoRepository.findByProductId(product.getId());
        ProductPhoto productPhoto;
        if(optionalProductPhoto.isPresent()) {
            productPhoto = optionalProductPhoto.get();
            productPhoto.setImage(picBytes);
            productPhoto.setUpdatedBy(ActiveUser.getActiveUsername());
            productPhoto.setUpdatedAt(new Date());
        } else {
            productPhoto = new ProductPhoto();
            productPhoto.setProductId(productId);
            productPhoto.setImage(picBytes);
            productPhoto.setCreatedAt(new Date());
            productPhoto.setCreatedBy(ActiveUser.getActiveUsername());
        }
        productPhotoRepository.save(productPhoto);
    }
}
