package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.dto.ProductDto;
import com.mjtech.pos.entity.Product;
import com.mjtech.pos.entity.ProductPhoto;
import com.mjtech.pos.repository.ProductPhotoRepository;
import com.mjtech.pos.repository.ProductRepository;
import com.mjtech.pos.service.ProductService;
import com.mjtech.pos.util.ActiveUser;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ProductHandler {

    private final ProductRepository productRepository;

    private final ProductPhotoRepository productPhotoRepository;

    private final ProductService productService;

    public void saveProduct(TextField codeTextField,
                            TextField nameTextField,
                            int categoryId,
                            int productCompanyId,
                            int supplierId,
                            TextField buyPriceTextField,
                            TextField sellPriceTextField,
                            TextField quantityTextField) {
        String code = codeTextField.getText();
        String name = nameTextField.getText();
        Double buyPrice = Double.parseDouble(buyPriceTextField.getText());
        Double sellPrice = Double.parseDouble(sellPriceTextField.getText());
        int quantity = Integer.parseInt(quantityTextField.getText());

        Product product = Product.builder()
                .code(code)
                .name(name)
                .categoryId(categoryId)
                .productCompanyId(productCompanyId)
                .supplierId(supplierId)
                .buyPrice(buyPrice)
                .sellPrice(sellPrice)
                .quantity(quantity)
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
                            TextField quantityTextField) {
        String code = codeTextField.getText();
        String name = nameTextField.getText();
        Double buyPrice = Double.parseDouble(buyPriceTextField.getText());
        Double sellPrice = Double.parseDouble(sellPriceTextField.getText());
        int quantity = Integer.parseInt(quantityTextField.getText());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(String.format("Product not found with id %s", productId)));

        product.setCode(code);
        product.setName(name);
        product.setCategoryId(categoryId);
        product.setProductCompanyId(productCompanyId);
        product.setSupplierId(supplierId);
        product.setBuyPrice(buyPrice);
        product.setSellPrice(sellPrice);
        product.setQuantity(quantity);
        product.setUpdatedBy(ActiveUser.getActiveUsername());
        product.setUpdatedAt(new Date());

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

    public void searchProduct(TextField codeTextField,
                              TextField nameTextField,
                              Integer categoryId,
                              Integer productCompanyId,
                              Integer supplierId,
                              TextField buyPriceTextField,
                              TextField sellPriceTextField,
                              TextField quantityTextField,
                              TableView<ProductDto> productTableView) {
        Double buyPrice = buyPriceTextField.getText().isEmpty() ? null :  Double.parseDouble(buyPriceTextField.getText());
        Double sellPrice = sellPriceTextField.getText().isEmpty() ? null :  Double.parseDouble(sellPriceTextField.getText());
        Integer quantity = quantityTextField.getText().isEmpty() ? null : Integer.parseInt(quantityTextField.getText());

        List<ProductDto> productDtos = productService.searchProducts(codeTextField.getText(), nameTextField.getText(), categoryId, productCompanyId,
                supplierId, quantity, buyPrice, sellPrice);

        var columnMap = Map.of("Code", "code",
                "Name", "name",
                "Category", "categoryName",
                "Company", "productCompanyName",
                "Supplier", "supplierName",
                "Sell", "sellPrice",
                "Buy", "buyPrice",
                "Quantity", "quantity");

        FxmlUtil.populateTableView(productTableView, productDtos, columnMap);
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
