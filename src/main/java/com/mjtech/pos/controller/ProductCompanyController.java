package com.mjtech.pos.controller;

import com.mjtech.pos.entity.ProductCompany;
import com.mjtech.pos.repository.ProductCompanyRepository;
import com.mjtech.pos.service.ProductCompanyService;
import com.mjtech.pos.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class ProductCompanyController implements ControllerInterface, Initializable {

    @FXML
    private TextField nameTextField, addressTextField, contactNoTextField, contactPersonTextField, searchTextField;

    @FXML
    private TableView<ProductCompany> productCompanyTable;

    @Autowired
    private ProductCompanyService productCompanyService;

    @Autowired
    private ProductCompanyRepository productCompanyRepository;

    private ProductCompany selectedProductCompany;

    private Node lastSelectedNode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchProductCompany();

        productCompanyTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                this.selectedProductCompany = productCompanyTable.getSelectionModel().getSelectedItem();
                if(selectedProductCompany == null) {
                    return;
                }
                nameTextField.setText(selectedProductCompany.getName());
                addressTextField.setText(selectedProductCompany.getAddress());
                contactNoTextField.setText(selectedProductCompany.getContactNo());
                contactPersonTextField.setText(selectedProductCompany.getContactPerson());
            }
        });
    }

    public void searchProductCompany() {
        List<ProductCompany> productCompanies = productCompanyService.findInAllColumns(searchTextField.getText());
        populateTable(productCompanies);
    }

    private void populateTable(List<ProductCompany> productCompanies) {
        var columnMap = Map.of(
                "Name", "name",
                "Address", "address",
                "Contact No.", "contactNo",
                "Contact Person", "contactPerson");

        FxmlUtil.populateTableView(productCompanyTable, productCompanies, columnMap, this::deleteProductCompany);
    }

    public void searchBtn() {
        searchProductCompany();
    }

    public void saveBtn() {
        try {
            selectedProductCompany = null;
            if (StringUtils.isEmpty(nameTextField.getText())) {
                FxmlUtil.callErrorAlert("ProductCompany Name can't be empty.");
                nameTextField.requestFocus();
                return;
            }
            if(productCompanyRepository.existsByName(nameTextField.getText())) {
                FxmlUtil.callErrorAlert("Product Company already exist with name " + nameTextField.getText());
                return;
            }
            productCompanyService.saveProductCompany(
                    null,
                    nameTextField.getText(),
                    addressTextField.getText(),
                    contactNoTextField.getText(),
                    contactPersonTextField.getText());

            searchProductCompany();
        } catch (Exception e) {
            FxmlUtil.callErrorAlert("Failed while saving ProductCompany. Please contact administrator!");
        }
    }

    public void editBtn() {
        if(selectedProductCompany == null) {
            FxmlUtil.callErrorAlert("Please select (click) ProductCompany in table");
            return;
        }

        productCompanyService.saveProductCompany(
                selectedProductCompany.getId(),
                nameTextField.getText(),
                addressTextField.getText(),
                contactNoTextField.getText(),
                contactPersonTextField.getText());

        selectedProductCompany = null;
        searchProductCompany();
    }

    public void clearBtn() {
        nameTextField.clear();
        addressTextField.clear();
        contactPersonTextField.clear();
        contactNoTextField.clear();
        productCompanyTable.getItems().clear();
        selectedProductCompany = null;
    }

    public boolean deleteProductCompany(ProductCompany object) {
//        if(!ActiveUser.isSuperAdmin()) {
//            FxmlUtil.callErrorAlert("You don't have access to delete this entry. Please contact admin!");
//            return;
//        }

        boolean confirm = FxmlUtil.callConfirmationAlert("Are you sure you want to delete Product Company with name " +
                object.getName());
        if (!confirm) {
            return false;
        }

        productCompanyService.deleteById(object.getId());
        clearBtn();
        searchProductCompany();
        return false;
    }
}
