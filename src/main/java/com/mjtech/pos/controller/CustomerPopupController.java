package com.mjtech.pos.controller;

import com.mjtech.pos.GuiHandler.CustomerHandler;
import com.mjtech.pos.constant.Gender;
import com.mjtech.pos.entity.Customer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
@Scope("prototype")
public class CustomerPopupController implements ControllerInterface, Initializable {

    @FXML
    public TextField nameTextField;

    @FXML
    private ChoiceBox<String> genderChoiceBox;

    @FXML
    private TextField contactTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TableView<Customer> customerPopupTable;

    @Autowired
    private CustomerHandler customerHandler;

    @FXML
    private Button searchBtn;

    private Customer selectedCustomer;

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    @FXML
    public void searchBtn() {
        customerHandler.searchCustomer(nameTextField, genderChoiceBox,
                contactTextField, addressTextField, customerPopupTable);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genderChoiceBox.getItems().add("SELECT");
        genderChoiceBox.getItems().addAll(Gender.getGenderStrings());
        genderChoiceBox.getSelectionModel().select(0);

        customerHandler.searchCustomer(nameTextField, genderChoiceBox,
                contactTextField, addressTextField, customerPopupTable);

        nameTextField.setOnAction(event -> {
            customerHandler.searchCustomer(nameTextField, genderChoiceBox,
                    contactTextField, addressTextField, customerPopupTable);
        });

        contactTextField.setOnAction(event -> {
            customerHandler.searchCustomer(nameTextField, genderChoiceBox,
                    contactTextField, addressTextField, customerPopupTable);
        });

        addressTextField.setOnAction(event -> {
            customerHandler.searchCustomer(nameTextField, genderChoiceBox,
                    contactTextField, addressTextField, customerPopupTable);
        });

        genderChoiceBox.setOnAction(event -> {
            customerHandler.searchCustomer(nameTextField, genderChoiceBox,
                    contactTextField, addressTextField, customerPopupTable);
        });

        searchBtn.setOnAction(event -> {
            customerHandler.searchCustomer(nameTextField, genderChoiceBox,
                    contactTextField, addressTextField, customerPopupTable);
        });

        customerPopupTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click detected
                this.selectedCustomer = customerPopupTable.getSelectionModel().getSelectedItem();
                Stage stage = (Stage) nameTextField.getScene().getWindow();
                stage.close();
            }
        });

        customerPopupTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) { // Double-click detected
                this.selectedCustomer = customerPopupTable.getSelectionModel().getSelectedItem();
                Stage stage = (Stage) nameTextField.getScene().getWindow();
                stage.close();
            }
        });
    }
}
