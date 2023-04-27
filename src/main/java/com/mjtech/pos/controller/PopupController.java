package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.GenericFormHandler;
import com.mjtech.pos.dto.GenericFromDto;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
@Scope("prototype")
public class PopupController implements Initializable {

    @Autowired
    private GenericFormHandler genericFormHandler;

    @FXML
    private TextField nameTextField;

    @FXML
    private TableView<GenericFromDto> popupTable;

    @FXML
    private Label entityNameLabel;

    private String entityName;

    private String valueToSearch;

    private GenericFromDto selectedEntity;

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getValueToSearch() {
        return valueToSearch;
    }

    public void setValueToSearch(String valueToSearch) {
        this.valueToSearch = valueToSearch;
    }

    public GenericFromDto getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(GenericFromDto selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public TableView<GenericFromDto> getPopupTable() {
        return popupTable;
    }

    @FXML
    public void searchEntity() {
        genericFormHandler.search(entityName, nameTextField, new TextField(), popupTable);
    }

    public void textFieldAction() {
        genericFormHandler.search(entityName, nameTextField, new TextField(), popupTable);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameTextField.setText(valueToSearch);
        entityNameLabel.setText(this.entityName);
        genericFormHandler.search(entityName, nameTextField, new TextField(), popupTable);

        popupTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click detected
                selectItemAndCloseForm();
            }
        });

        popupTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                selectItemAndCloseForm();
            }
        });
    }

    private void selectItemAndCloseForm() {
        // Retrieve the selected value from the TableView
        this.selectedEntity = popupTable.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) entityNameLabel.getScene().getWindow();
        stage.close();
    }

}
