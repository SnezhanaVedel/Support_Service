package com.example.controller.dialogs;

import com.example.controller.UniversalRequestsTabController;
import com.example.controller.UniversalTableTabController;
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

    public DialogAddMembersController() {
    }

    public void onCancelBtn(ActionEvent actionEvent) {
        ((Stage) idBottomAdd.getScene().getWindow()).close();
    }

    public void onActionBottomAdd(ActionEvent actionEvent) {
        ObservableList<Node> list = valuesVbox.getChildren();
        String sqlAdd = "INSERT INTO members (name, phone, email, login, pass, role) VALUES (";

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof TextField) {
                sqlAdd += "'" + ((TextField) list.get(i)).getText() + "'";
                sqlAdd += (i + 1 != list.size()) ? "," : "";

            } else if (list.get(i) instanceof ChoiceBox) {
                sqlAdd += "'" + ((ChoiceBox) list.get(i)).getValue() + "'";
                sqlAdd += (i + 1 != list.size()) ? "," : "";
            }
        }
        sqlAdd += ");";

        boolean success = Database.getInstance().simpleQuery(sqlAdd);
        if (success) {
            MyAlert.showInfoAlert("Запись добавлена успешно");
        } else {
            MyAlert.showErrorAlert("Произошла ошибка при добавлении записи");
        }

        // Получаем текущее окно и закрываем его
        Stage stage = (Stage) idBottomAdd.getScene().getWindow();
        stage.close();

        // Обновляем таблицу в UsersTabController
        UniversalTableTabController parentController = (UniversalTableTabController) stage.getUserData();
        parentController.updateTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleChoice.getItems().addAll("admin", "user");
    }
}
