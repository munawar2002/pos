package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.constant.GenericFormOperation;
import com.mjtech.pos.constant.GenericFormValue;
import com.mjtech.pos.dto.GenericFromDto;
import com.mjtech.pos.entity.ProductCategory;
import com.mjtech.pos.entity.ProductCompany;
import com.mjtech.pos.entity.Supplier;
import com.mjtech.pos.mapper.DtoMapper;
import com.mjtech.pos.service.ProductCategoryService;
import com.mjtech.pos.service.ProductCompanyService;
import com.mjtech.pos.service.SupplierService;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class GenericFormHandler {

    private final ApplicationContext applicationContext;

    public void performOperation(GenericFormOperation operation, TextField nameTextField, TextField descriptionTextField, String formName, Integer selectedId, TableView<GenericFromDto> tableView) {
        String genericName = nameTextField.getText();
        String genericDescription = descriptionTextField.getText();
        callService(operation, formName, genericName, genericDescription, selectedId);
        nameTextField.clear();
        descriptionTextField.clear();
        populateTable(formName, tableView , "", "");
    }

    private void callService(GenericFormOperation operation, String formName, String name, String description, Integer selectedId) {
        if (formName.equals(GenericFormValue.PRODUCT_CATEGORY.getValue())) {
            ProductCategoryService productCategoryService = applicationContext.getBean(ProductCategoryService.class);
            productCategoryService.performOperation(operation, name, description, selectedId);
        }
    }

    public List<GenericFromDto> searchEntity(String formName, String text) {
        if (formName.equals(GenericFormValue.PRODUCT_CATEGORY.getValue())) {
            ProductCategoryService productCategoryService = applicationContext.getBean(ProductCategoryService.class);
            List<ProductCategory> productCategories = productCategoryService.search(text, null);
            return DtoMapper.mapToGenericFormDtos(productCategories);
        } else if(formName.equals(GenericFormValue.SUPPLIER.getValue())) {
            SupplierService supplierService = applicationContext.getBean(SupplierService.class);
            List<Supplier> suppliers = supplierService.find(text);
            return DtoMapper.mapToGenericFormDtos(suppliers);
        } else if(formName.equals(GenericFormValue.PRODUCT_COMPANY.getValue())) {
            ProductCompanyService productCompanyService = applicationContext.getBean(ProductCompanyService.class);
            List<ProductCompany> suppliers = productCompanyService.find(text);
            return DtoMapper.mapToGenericFormDtos(suppliers);
        }

        return null;
    }

    public void search(String formName, TextField nameTextField, TextField descriptionTextField, TableView<GenericFromDto> table) {
        String name = nameTextField.getText();
        String description = descriptionTextField.getText();

        populateTable(formName, table, name, description);
        table.requestFocus();
    }

    private void populateTable(String formName, TableView<GenericFromDto> table, String name, String description) {
        List<?> objects;
        List<GenericFromDto> genericFromDtos = null;
        if (formName.equals(GenericFormValue.PRODUCT_CATEGORY.getValue())) {
            ProductCategoryService productCategoryService = applicationContext.getBean(ProductCategoryService.class);
            objects = productCategoryService.search(name, description);
            genericFromDtos = DtoMapper.mapToGenericFormDtos(objects);
        } else if (formName.equals(GenericFormValue.SUPPLIER.getValue())) {
            SupplierService supplierService = applicationContext.getBean(SupplierService.class);
            objects = supplierService.find(name);
            genericFromDtos = DtoMapper.mapToGenericFormDtos(objects);
        } else if (formName.equals(GenericFormValue.PRODUCT_COMPANY.getValue())) {
            ProductCompanyService productCompanyService = applicationContext.getBean(ProductCompanyService.class);
            objects = productCompanyService.find(name);
            genericFromDtos = DtoMapper.mapToGenericFormDtos(objects);
        }

        populateTable(genericFromDtos, table);
    }

    private void populateTable(List<GenericFromDto> genericFromDtos, TableView<GenericFromDto> table) {
        if (genericFromDtos == null) {
            return;
        }

        Map<String, String> columnsMap = new LinkedHashMap<>();
        columnsMap.put("Name", "name");
        columnsMap.put("Description", "description");


        for (Map.Entry<String, String> entry : columnsMap.entrySet()) {
            boolean columnAlreadyExist = table.getColumns().stream().anyMatch(col -> col.getText().equals(entry.getKey()));
            if(!columnAlreadyExist) {
                TableColumn<GenericFromDto, String> newColumn = new TableColumn<>(entry.getKey());
                newColumn.setCellValueFactory(new PropertyValueFactory<>(entry.getValue()));
                if(newColumn.getText().equals("Name")) {
                    newColumn.setPrefWidth(200);
                } else {
                    newColumn.setPrefWidth(400);
                }
                table.getColumns().add(newColumn);

            }
        }

        table.getColumns().forEach(col -> col.setCellValueFactory(new PropertyValueFactory<>(col.getText().toLowerCase())));

        table.setItems(FXCollections.observableArrayList(genericFromDtos));
    }

}
