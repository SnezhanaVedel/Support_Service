package com.example.controller;

import com.example.admin.UniversalRequestsController;
import com.example.user.UserRequestsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    public static String role;
    public static int userID;
    @FXML
    public TabPane mainPane;


    public MainViewController(String role, int userID) {
        MainViewController.role = role;
        MainViewController.userID = userID;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.getTabs().clear();

        if (role.equals("user")) {
            addTab("Создать заявку", "/view/user/AddRequestTab.fxml", null);
            addTab("Мои заявки", "/view/user/UserRequests.fxml", new UserRequestsController("user"));

        } else if (role.equals("admin")) {
            addTab("Новые заявки", "/view/admin/UniversalRequests.fxml", new UniversalRequestsController("admin_new"));
            addTab("Все заявки", "/view/admin/UniversalRequests.fxml", new UniversalRequestsController("admin"));
            addTab("Пользователи", "/view/admin/UniversalTableTab.fxml", new UniversalTableTabController("members"));
            addTab("Оборудование", "/view/admin/UniversalTableTab.fxml", new UniversalTableTabController("equipment"));
            addTab("Заказ запчастей", "/view/admin/UniversalTableTab.fxml", new UniversalTableTabController("orders"));
            addTab("Статистика", "/view/admin/Statistic.fxml", null);

        }
    }



    private void addTab(String title, String pathToFXML, Object controller) {
        Tab newRequestTab = new Tab(title);
        try {
            newRequestTab.setStyle("-fx-font-family: Arial; -fx-font-size: 14;");
            if (!pathToFXML.equals("")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(pathToFXML));
                if (controller != null) {
                    loader.setController(controller); // Установка контроллера
                }
                newRequestTab.setContent(loader.load());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML", e);
        }
        mainPane.getTabs().add(newRequestTab);
    }
}
