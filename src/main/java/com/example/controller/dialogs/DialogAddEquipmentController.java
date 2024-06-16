package com.example.controller.dialogs;

import com.example.controller.admin.UniversalTableController;
import com.example.util.Database;
import com.example.util.MyAlert;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DialogAddEquipmentController implements Initializable {
    public VBox valuesVbox;
    public Button idBottomAdd;
    public TextField serialNumTF;
    public TextField equipNameTF;
    public TextField equipTypeTF;
    public ChoiceBox<String> conditionCB;
    public TextField detailsTF;
    public TextField locationTF;

    private UniversalTableController parentController;
    private List<String> rowData;
    private boolean isEditMode = false;

    public DialogAddEquipmentController() {
    }

    public void initData(UniversalTableController parentController, List<String> rowData) {
        this.parentController = parentController;
        this.rowData = rowData;
        this.isEditMode = true;

        // Заполнение полей данными для редактирования
        serialNumTF.setText(rowData.get(0));
        equipNameTF.setText(rowData.get(1));
        equipTypeTF.setText(rowData.get(2));
        conditionCB.setValue(rowData.get(3));
        detailsTF.setText(rowData.get(4));
        locationTF.setText(rowData.get(5));

        serialNumTF.setEditable(false);  // serial_num не редактируется
    }

    public void onCancelBtn(ActionEvent actionEvent) {
        ((Stage) idBottomAdd.getScene().getWindow()).close();
    }

    public void onActionBottomAdd(ActionEvent actionEvent) {
        ObservableList<Node> list = valuesVbox.getChildren();
        String sqlQuery;

        if (isEditMode) {
            // Обновление записи
            sqlQuery = "UPDATE equipment SET " +
                    "equip_name = '" + equipNameTF.getText() + "', " +
                    "equip_type = '" + equipTypeTF.getText() + "', " +
                    "condition = '" + conditionCB.getValue() + "', " +
                    "detals = '" + detailsTF.getText() + "', " +
                    "location = '" + locationTF.getText() + "' " +
                    "WHERE serial_num = '" + serialNumTF.getText() + "'";
        } else {
            // Добавление новой записи
            sqlQuery = "INSERT INTO equipment (serial_num, equip_name, equip_type, condition, detals, location) VALUES (";

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof TextField) {
                    sqlQuery += "'" + ((TextField) list.get(i)).getText() + "'";
                    sqlQuery += (i + 1 != list.size()) ? "," : "";
                } else if (list.get(i) instanceof ChoiceBox) {
                    sqlQuery += "'" + ((ChoiceBox) list.get(i)).getValue() + "'";
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
        conditionCB.getItems().addAll("Исправно", "Не исправно", "Требуются запчасти");
    }
}
