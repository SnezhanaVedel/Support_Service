package com.example.controller;

import com.example.controller.admin.AdminRequestsController;
import com.example.controller.admin.NewAdminRequestsController;
import com.example.controller.admin.UniversalTableController;
import com.example.controller.user.UserRequestsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
            addTab("Создать заявку", "/view/user/AddRequest.fxml", null);
            addTab("Мои заявки", "/view/user/UserRequests.fxml", new UserRequestsController("user"));
            addTab("Профиль", "/view/user/Profile.fxml", null);
        } else if (role.equals("admin")) {
            addTab("Новые заявки", "/view/admin/UniversalRequests.fxml", new NewAdminRequestsController());
            addTab("Все заявки", "/view/admin/UniversalRequests.fxml", new AdminRequestsController());
            addTab("Пользователи", "/view/admin/UniversalTableTab.fxml", new UniversalTableController("members"));
            addTab("Оборудование", "/view/admin/UniversalTableTab.fxml", new UniversalTableController("equipment"));
            addTab("Заказ запчастей", "/view/admin/UniversalTableTab.fxml", new UniversalTableController("orders"));
            addTab("Статистика", "/view/admin/Statistic.fxml", null);
        }

        mainPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (oldTab != null && oldTab.getContent().getUserData() instanceof Initializable) {
                Initializable controller = (Initializable) oldTab.getContent().getUserData();
                if (controller instanceof AdminRequestsController) {
                    ((AdminRequestsController) controller).stopNotificationListener();
                } else if (controller instanceof NewAdminRequestsController) {
                    ((NewAdminRequestsController) controller).stopNotificationListener();
                }
            }
            if (newTab != null && newTab.getContent().getUserData() instanceof Initializable) {
                Initializable controller = (Initializable) newTab.getContent().getUserData();
                if (controller instanceof AdminRequestsController) {
                    ((AdminRequestsController) controller).startNotificationListener();
                } else if (controller instanceof NewAdminRequestsController) {
                    ((NewAdminRequestsController) controller).startNotificationListener();
                }
            }
        });
    }

    private void addTab(String title, String pathToFXML, Object controller) {
        Tab newRequestTab = new Tab(title);
        try {
            newRequestTab.setStyle("-fx-font-family: ROBOTO; -fx-font-size: 14;");
            if (!pathToFXML.equals("")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(pathToFXML));
                if (controller != null) {
                    loader.setController(controller);
                }
                Parent content = loader.load();
                newRequestTab.setContent(content);
                content.setUserData(loader.getController());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML", e);
        }
        mainPane.getTabs().add(newRequestTab);
    }
}