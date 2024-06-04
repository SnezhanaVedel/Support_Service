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

public class ProfileController implements Initializable {
    public TextField userNameTF;
    public TextField phoneNumberTF;
    public TextField emailTF;
    public TextField loginTF;
    public TextField passwordTF;
    private Database database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        updateProfileInfo();
    }

    private void updateProfileInfo() {
        String query = "SELECT * FROM members WHERE id = " + MainViewController.userID;
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    userNameTF.setText(resultSet.getString("name"));
                    phoneNumberTF.setText(resultSet.getString("phone"));
                    emailTF.setText(resultSet.getString("email"));
                    loginTF.setText(resultSet.getString("login"));
                    passwordTF.setText(resultSet.getString("pass"));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при получении данных об оборудовании.");
        }
    }

    @FXML
    public void onActionAdd() {
        String insertRequestsSql = String.format(
                "UPDATE members SET name = '%s', phone = '%s', email = '%s', login = '%s', pass = '%s' WHERE id = %d",
                userNameTF.getText(), phoneNumberTF.getText(), emailTF.getText(), loginTF.getText(), passwordTF.getText(), MainViewController.userID);
        database.simpleQuery(insertRequestsSql);

        MyAlert.showInfoAlert("Запись добавлена успешно.");
    }
}