package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.GenericFormHandler;
import com.mjtech.pos.constant.GenericFormOperation;
import com.mjtech.pos.dto.GenericFromDto;
import com.mjtech.pos.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
@Scope("prototype")
public class GenericFormController implements Initializable {

    @FXML
    private Label entityNameLabel;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private TextField nameSearchField;

    @FXML
    private TextField descriptionSearchField;

    @FXML
    private TableView<GenericFromDto> genericTable;

    @Autowired
    private GenericFormHandler genericFormHandler;

    private String formName;

    public void setFormName(String name) {
        this.formName = name;
    }

    public void saveGeneric() {
        genericFormHandler.performOperation(GenericFormOperation.SAVE, nameTextField, descriptionTextField, formName, null, genericTable);
    }

    public void editGeneric() {
        int selectedId = getSelectedId("update");
        if(selectedId < 0 ) return;
        genericFormHandler.performOperation(GenericFormOperation.EDIT, nameTextField, descriptionTextField, formName, selectedId, genericTable);
    }

    public void clearGeneric() {
        nameTextField.clear();
        descriptionTextField.clear();
        nameSearchField.clear();
        descriptionSearchField.clear();
        genericTable.getItems().clear();
    }

    public void deleteGeneric() {
        int selectedId = getSelectedId("delete");
        if(selectedId < 0 ) return;
        boolean confirmedByUser = FxmlUtil.callConfirmationAlert(String.format("Are you sure you want to delete %s %s", formName, nameTextField.getText()));
        if(!confirmedByUser) return;
        genericFormHandler.performOperation(GenericFormOperation.DELETE, nameTextField, descriptionTextField, formName, selectedId, genericTable);
    }

    public void searchGeneric() {
        genericFormHandler.search(formName, nameSearchField, descriptionSearchField, genericTable);
    }

    public void tableMouseClick() {
        GenericFromDto selectedItem = genericTable.getSelectionModel().getSelectedItem();
        nameTextField.setText(selectedItem.getName());
        descriptionTextField.setText(selectedItem.getDescription());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        entityNameLabel.setText(this.formName);
    }

    private int getSelectedId(String operation) {
        GenericFromDto selectedItem = genericTable.getSelectionModel().getSelectedItem();
        if(selectedItem == null) {
            FxmlUtil.callWarningAlert(String.format("Please select the row in table to %s!", operation));
            return -1;
        }
        return selectedItem.getId();
    }
}
