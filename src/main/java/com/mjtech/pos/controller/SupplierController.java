package com.mjtech.pos.controller;

import com.mjtech.pos.entity.Supplier;
import com.mjtech.pos.repository.SupplierRepository;
import com.mjtech.pos.service.SupplierService;
import com.mjtech.pos.util.ActiveUser;
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
public class SupplierController implements ControllerInterface, Initializable {

    @FXML
    private TextField nameTextField, addressTextField, contactNoTextField, contactPersonTextField, searchTextField;

    @FXML
    private TableView<Supplier> supplierTable;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierRepository supplierRepository;

    private Supplier selectedSupplier;

    private Node lastSelectedNode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchSupplier();

        supplierTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                this.selectedSupplier = supplierTable.getSelectionModel().getSelectedItem();
                if(selectedSupplier == null) {
                    return;
                }
                nameTextField.setText(selectedSupplier.getName());
                addressTextField.setText(selectedSupplier.getAddress());
                contactNoTextField.setText(selectedSupplier.getContactNo());
                contactPersonTextField.setText(selectedSupplier.getContactPerson());
            }
        });
    }

    public void searchSupplier() {
        List<Supplier> productCompanies = supplierService.findInAllColumns(searchTextField.getText());
        populateTable(productCompanies);
    }

    private void populateTable(List<Supplier> suppliers) {
        var columnMap = Map.of(
                "Name", "name",
                "Address", "address",
                "Contact No.", "contactNo",
                "Contact Person", "contactPerson");

        FxmlUtil.populateTableView(supplierTable, suppliers, columnMap);
    }

    public void searchBtn() {
        searchSupplier();
    }

    public void saveBtn() {
        try {
            selectedSupplier = null;
            if (StringUtils.isEmpty(nameTextField.getText())) {
                FxmlUtil.callErrorAlert("Supplier Name can't be empty.");
                nameTextField.requestFocus();
                return;
            }
            if(supplierRepository.existsByName(nameTextField.getText())) {
                FxmlUtil.callErrorAlert("Supplier already exist with name " + nameTextField.getText());
                return;
            }
            supplierService.saveSupplier(
                    null,
                    nameTextField.getText(),
                    addressTextField.getText(),
                    contactNoTextField.getText(),
                    contactPersonTextField.getText());

            searchSupplier();
        } catch (Exception e) {
            FxmlUtil.callErrorAlert("Failed while saving Supplier. Please contact administrator!");
        }
    }

    @FXML
    public void editBtn() {
        if(selectedSupplier == null) {
            FxmlUtil.callErrorAlert("Please select (click) Supplier in table");
            return;
        }

        supplierService.saveSupplier(
                selectedSupplier.getId(),
                nameTextField.getText(),
                addressTextField.getText(),
                contactNoTextField.getText(),
                contactPersonTextField.getText());

        selectedSupplier = null;
        searchSupplier();
    }

    public void clearBtn() {
        nameTextField.clear();
        addressTextField.clear();
        contactPersonTextField.clear();
        contactNoTextField.clear();
        supplierTable.getItems().clear();
        selectedSupplier = null;
    }

    public void deleteBtn() {
//        if(!ActiveUser.isSuperAdmin()) {
//            FxmlUtil.callErrorAlert("You don't have access to delete this entry. Please contact admin!");
//            return;
//        }
        supplierTable.getScene().focusOwnerProperty().addListener((observable, oldFocusOwner, newFocusOwner) -> {
            lastSelectedNode = oldFocusOwner;
        });

        if (!(lastSelectedNode instanceof TableView<?>) || selectedSupplier == null) {
            FxmlUtil.callErrorAlert("Please select supplier from table to delete.");
            return;
        }

        boolean proceed = FxmlUtil.callConfirmationAlert("Are you sure you want to delete Supplier with name " +
                selectedSupplier.getName());
        if (!proceed) {
            return;
        }
        supplierService.deleteById(selectedSupplier.getId());
        clearBtn();
        searchSupplier();
    }
}
