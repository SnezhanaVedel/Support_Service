package com.example.controller.user;

import com.example.controller.MainViewController;
import com.example.util.Database;
import com.example.util.MyAlert;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddRequestController implements Initializable {
    public TextField serialNumberField;
    public TextField equipmentTypeTF;
    public TextField equipmentNameTF;
    public TextArea descTextArea;
    public Button createRequestBtn;
    public Button clearFieldsBtn;
    private Database database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();

        String serialsQuery = "SELECT serial_num FROM equipment";
        ArrayList<String> serialNumList = database.stringListQuery("serial_num", serialsQuery);

        TextFields.bindAutoCompletion(serialNumberField, serialNumList);

        // Добавление слушателя к полю serialNumberField
        serialNumberField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty()) {
                    updateEquipmentDetails(newValue);
                } else {
                    clearEquipmentDetails();
                }
            }
        });
    }

    private void updateEquipmentDetails(String serialNumber) {
        String query = "SELECT equip_name, equip_type FROM equipment WHERE serial_num = ?";
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, serialNumber);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    equipmentNameTF.setText(resultSet.getString("equip_name"));
                    equipmentTypeTF.setText(resultSet.getString("equip_type"));
                } else {
                    clearEquipmentDetails();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при получении данных об оборудовании.");
        }
    }

    private void clearEquipmentDetails() {
        equipmentNameTF.clear();
        equipmentTypeTF.clear();
    }

    private void clearFields() {
        serialNumberField.clear();
        descTextArea.clear();
        clearEquipmentDetails();
    }

    public void onActionClear() {
        clearFields();
    }

    @FXML
    public void onActionAdd() {
        String insertRequestsSql = String.format(
                "INSERT INTO requests (serial_num, problem_desc, request_comments, status, date_start, member_id) VALUES ('%s', '%s', '%s', '%s', '%s', %d)",
                serialNumberField.getText(), descTextArea.getText(), " ", "Новая", Date.valueOf(LocalDate.now()), MainViewController.userID);
        database.simpleQuery(insertRequestsSql);

        MyAlert.showInfoAlert("Запись добавлена успешно.");
    }
}