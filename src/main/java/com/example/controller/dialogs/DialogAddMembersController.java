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

public class DialogAddMembersController implements Initializable {
    public VBox valuesVbox;
    public Button idBottomAdd;
    public ChoiceBox<String> roleChoice;
    public TextField nameTF;
    public TextField phoneTF;
    public TextField emailTF;
    public TextField loginTF;
    public TextField passTF;

    private UniversalTableController parentController;
    private List<String> rowData;
    private boolean isEditMode = false;

    public DialogAddMembersController() {
    }

    public void initData(UniversalTableController parentController, List<String> rowData) {
        this.parentController = parentController;
        this.rowData = rowData;
        this.isEditMode = true;

        // Заполнение полей данными для редактирования
        nameTF.setText(rowData.get(1));
        phoneTF.setText(rowData.get(2));
        emailTF.setText(rowData.get(3));
        loginTF.setText(rowData.get(4));
        passTF.setText(rowData.get(5));
        roleChoice.setValue(rowData.get(6));
    }

    public void onCancelBtn(ActionEvent actionEvent) {
        ((Stage) idBottomAdd.getScene().getWindow()).close();
    }

    public void onActionBottomAdd(ActionEvent actionEvent) {
        ObservableList<Node> list = valuesVbox.getChildren();
        String sqlQuery;

        if (isEditMode) {
            // Обновление записи
            sqlQuery = "UPDATE test_members SET " +
                    "name = '" + nameTF.getText() + "', " +
                    "phone = '" + phoneTF.getText() + "', " +
                    "email = '" + emailTF.getText() + "', " +
                    "login = '" + loginTF.getText() + "', " +
                    "pass = '" + passTF.getText() + "', " +
                    "role = '" + roleChoice.getValue() + "' " +
                    "WHERE id = '" + rowData.get(0) + "'";
        } else {
            // Добавление новой записи
            sqlQuery = "INSERT INTO test_members (name, phone, email, login, pass, role) VALUES (";

            boolean first = true;
            for (Node node : list) {
                if (node instanceof TextField) {
                    if (!first) {
                        sqlQuery += ", ";
                    }
                    sqlQuery += "'" + ((TextField) node).getText() + "'";
                    first = false;
                } else if (node instanceof ChoiceBox) {
                    if (!first) {
                        sqlQuery += ", ";
                    }
                    sqlQuery += "'" + ((ChoiceBox) node).getValue() + "'";
                    first = false;
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
        roleChoice.getItems().addAll("admin", "user");
    }
}
