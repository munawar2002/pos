package com.mjtech.pos.GuiHandler;

import com.mjtech.pos.entity.Customer;
import com.mjtech.pos.service.CustomerService;
import com.mjtech.pos.util.FxmlUtil;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustomerHandler {

    @Autowired
    private CustomerService customerService;

    public void searchCustomer(TextField nameTextField, ChoiceBox<String> genderChoiceBox,
                               TextField contactNoTextField, TextField addressTextField,
                               TableView<Customer> customerTableView) {

        String name = nameTextField.getText();
        String gender = genderChoiceBox.getSelectionModel().getSelectedItem();
        String genderStr = gender == null || gender.equalsIgnoreCase("SELECT") ? "" : gender;
        String contactNo = contactNoTextField.getText();
        String address = addressTextField.getText();

        List<Customer> customers = customerService.searchCustomer(name, genderStr, contactNo, address);

        var columnMap = Map.of(
                "First Name", "firstName",
                "Last Name", "lastName",
                "Gender", "gender",
                "Contact No", "contactNo",
                "Address", "address");

        FxmlUtil.populateTableView(customerTableView, customers, columnMap);
    }

}
