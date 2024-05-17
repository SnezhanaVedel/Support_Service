package com.example.controller.operator_tabs;

import com.example.util.Database;
import com.example.util.MyAlert;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.example.controller.MainViewController.userID;

public class AddRequestTabController implements Initializable {
    public TextField clientNameField;
    public TextField clientPhoneField;
    public TextField serialNumberField;
    public TextField equipTypeField;
    public TextArea descTextArea;
    public Button createRequestBtn;
    public Button clearFieldsBtn;
    private Database database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
    }

    private void clearFields() {
        serialNumberField.clear();
        descTextArea.clear();
    }

    public void onActionClear() {
        clearFields();
    }

    @FXML
    public void onActionAdd() {
        String insertRequestsSql = String.format(
                "INSERT INTO requests (serial_num, problem_desc, request_comments, status, date_start, member_id) VALUES ('%s', '%s', '%s', '%s', '%s', %d)",
                serialNumberField.getText(), descTextArea.getText(), "", "Новая", Date.valueOf(LocalDate.now()), userID);
        database.simpleQuery(insertRequestsSql);

        MyAlert.showInfoAlert("Запись добавлена успешно.");
    }
}