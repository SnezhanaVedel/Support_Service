package com.example.controller.dialogs;

import com.example.controller.admin.UniversalTableController;
import com.example.util.Database;
import com.example.util.MyAlert;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DialogAddOrdersController implements Initializable {
    public VBox valuesVbox;
    public Button idBottomAdd;
    public TextField requestIdTF;
    public TextField resourceTypeTF;
    public TextField resourceNameTF;
    public TextField costTF;

    private UniversalTableController parentController;
    private List<String> rowData;
    private boolean isEditMode = false;

    public DialogAddOrdersController() {
    }

    public void initData(UniversalTableController parentController, List<String> rowData) {
        this.parentController = parentController;
        this.rowData = rowData;
        this.isEditMode = true;

        // Заполнение полей данными для редактирования
        requestIdTF.setText(rowData.get(1));
        resourceTypeTF.setText(rowData.get(2));
        resourceNameTF.setText(rowData.get(3));
        costTF.setText(rowData.get(4));
    }

    public void onCancelBtn(ActionEvent actionEvent) {
        ((Stage) idBottomAdd.getScene().getWindow()).close();
    }

    public void onActionBottomAdd(ActionEvent actionEvent) {
        ObservableList<Node> list = valuesVbox.getChildren();
        String sqlQuery;

        if (isEditMode) {
            // Обновление записи
            sqlQuery = "UPDATE orders SET " +
                    "request_id = '" + requestIdTF.getText() + "', " +
                    "resource_type = '" + resourceTypeTF.getText() + "', " +
                    "resource_name = '" + resourceNameTF.getText() + "', " +
                    "cost = '" + costTF.getText() + "' " +
                    "WHERE id = '" + rowData.get(0) + "'";
        } else {
            // Добавление новой записи
            sqlQuery = "INSERT INTO orders (request_id, resource_type, resource_name, cost) VALUES (";

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof TextField) {
                    sqlQuery += "'" + ((TextField) list.get(i)).getText() + "'";
                    sqlQuery += (i + 1 != list.size()) ? "," : "";
                }
            }
            sqlQuery += ");";
        }

        boolean success = Database.getInstance().simpleQuery(sqlQuery);
        if (success) {
            MyAlert.showInfoAlert("Запись " + (isEditMode ? "обновлена" : "добавлена") + " успешно");
        } else {
            MyAlert.showErrorAlert("Произошла ошибка при " + (isEditMode ? "обновлении" : "добавлении") + " записи");
        }

        // Закрываем текущее окно
        Stage stage = (Stage) idBottomAdd.getScene().getWindow();
        stage.close();

        // Обновляем таблицу в UniversalTableController
        if (parentController != null) {
            parentController.updateTable();
        } else {
            // Обновляем таблицу в UniversalTableController
            UniversalTableController parentController1 = (UniversalTableController) stage.getUserData();
            parentController1.updateTable();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
