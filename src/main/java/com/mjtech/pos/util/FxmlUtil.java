package com.mjtech.pos.util;

import com.mjtech.pos.GuiHandler.GenericFormHandler;
import com.mjtech.pos.controller.ControllerInterface;
import com.mjtech.pos.controller.GenericFormController;
import com.mjtech.pos.controller.PopupController;
import com.mjtech.pos.dto.GenericFromDto;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class FxmlUtil {

    public void callMainForm(Stage primaryStage, String formName, ConfigurableApplicationContext context) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FxmlUtil.class.getResource(formName));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void callForm(Stage stage, String formName, ConfigurableApplicationContext context) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FxmlUtil.class.getResource(formName));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void callGenericForm(Stage stage, String formName, GenericFormController controller, ConfigurableApplicationContext context) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FxmlUtil.class.getResource(formName));
        loader.setControllerFactory(context::getBean);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void callPopupForm(GenericFormHandler handler, PopupController controller, ConfigurableApplicationContext context) {
        TextField field = new TextField();
        field.setText(controller.getValueToSearch());
        List<GenericFromDto> genericFromDtos = handler.searchEntity(controller.getEntityName(), controller.getValueToSearch());
        if(genericFromDtos.size() == 1) {
            controller.setSelectedEntity(genericFromDtos.get(0));
            return;
        }
        callPopupForm(controller, context);
    }

    private void callPopupForm(PopupController controller, ConfigurableApplicationContext context) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FxmlUtil.class.getResource("/fxml/entityPopup.fxml"));
            loader.setControllerFactory(context::getBean);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed while opening popup form for %s", controller.getEntityName()), e);
        }
    }

    public void callPopupForm(String formName, ControllerInterface controller, ConfigurableApplicationContext context) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FxmlUtil.class.getResource(formName));
            loader.setControllerFactory(context::getBean);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed while opening popup form for %s", formName), e);
        }
    }

    public void callErrorAlert(String message) {
        callAlert(Alert.AlertType.ERROR, "Error", message);
    }

    public void callWarningAlert(String message) {
        callAlert(Alert.AlertType.WARNING, "Warning", message);
    }

    public void callInformationAlert(String message) {
        callAlert(Alert.AlertType.INFORMATION, "Information", message);
    }

    public void callAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean callConfirmationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public String validateEmptyTextFields(Map<TextField, String> map) {
        for (Map.Entry<TextField,String> entry : map.entrySet()) {
            if(StringUtils.isEmpty(entry.getKey().getText())) {
                return String.format("Validation Failed! Please fill the required field %s", entry.getValue());
            }
        }
        return null;
    }

    public String validateEmptyIds(Map<String, Integer> map) {
        for (Map.Entry<String,Integer> entry : map.entrySet()) {
            if(entry.getValue() == null) {
                return String.format("Validation Failed! Please fill the required field %s", entry.getKey());
            }
        }
        return null;
    }

    public String validateDouble(Map<TextField, String> map) {
        for (Map.Entry<TextField,String> entry : map.entrySet()) {
            try {
                Double.parseDouble(entry.getKey().getText());
            } catch (NumberFormatException e) {
                return String.format("Validation Failed! Provided input is not a number for %s", entry.getValue());
            }
        }
        return null;
    }

    public <T> void populateTableView(TableView<T> tableView, List<T> items, Map<String, String> columnMap,
                                      Function<T, Boolean> deleteHandler) {
        for (Map.Entry<String,String> entry : columnMap.entrySet()) {
            tableView.getColumns().forEach(col -> {
                if(col.getText().equalsIgnoreCase(entry.getKey())) {
                    col.setCellValueFactory(new PropertyValueFactory<>(entry.getValue()));
                }
            });
        }

        addActionButtonsInTable(tableView, deleteHandler);

        tableView.setItems(FXCollections.observableList(items));
    }

    private static <T> void addActionButtonsInTable(TableView<T> tableView, Function<T, Boolean> deleteHandler) {
        if (deleteHandler != null) {
            TableColumn<T, Void> actionColumn = new TableColumn<>("Action");
            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        // Get the item associated with the clicked delete button
                        T record = getTableView().getItems().get(getIndex());
                        Boolean result = deleteHandler.apply(record);
                        if (result) {
                            getTableView().getItems().remove(getIndex());
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setGraphic(deleteButton);
                    } else {
                        setGraphic(null);
                    }
                }
            });

            boolean alreadyAdded = tableView.getColumns().stream().anyMatch(c -> c.getText().contains("Action"));
            if(!alreadyAdded) {
                tableView.getColumns().add(actionColumn);
            }
        }
    }

}
