package com.example.controller.user;

import com.example.controller.dialogs.DialogAddMembersController;
import com.example.util.Database;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DialogAddMembersControllerIntegrationTest {

    private static Database database;
    private DialogAddMembersController controller;

    @BeforeAll
    static void setupDatabase() {
        database = Database.getInstance();
        try (Connection connection = database.getConnection();
             Statement statement = connection.createStatement()) {

            // Создание таблицы test_members
            statement.execute("CREATE TABLE test_members (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "phone VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "login VARCHAR(255), " +
                    "pass VARCHAR(255), " +
                    "role VARCHAR(50))");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Инициализация JavaFX Toolkit
        new JFXPanel(); // This will initialize the JavaFX environment
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    void setUp() {
        controller = new DialogAddMembersController();
        controller.nameTF = new TextField();
        controller.phoneTF = new TextField();
        controller.emailTF = new TextField();
        controller.loginTF = new TextField();
        controller.passTF = new TextField();
        controller.roleChoice = new ChoiceBox<>();
        controller.roleChoice.getItems().addAll("admin", "user");
        controller.idBottomAdd = new Button("Add");
        controller.valuesVbox = new VBox(controller.nameTF, controller.phoneTF, controller.emailTF, controller.loginTF, controller.passTF, controller.roleChoice, controller.idBottomAdd);
    }

    @Test
    void testAddMember_Success() {
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                stage.setScene(new Scene(controller.valuesVbox));
                stage.show();

                controller.nameTF.setText("John Doe");
                controller.phoneTF.setText("1234567890");
                controller.emailTF.setText("john.doe@example.com");
                controller.loginTF.setText("johndoe");
                controller.passTF.setText("password");
                controller.roleChoice.setValue("user");

                assertDoesNotThrow(() -> controller.onActionBottomAdd(null));

                // Проверка, что запись добавлена в базу данных
                try (Connection connection = database.getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery("SELECT * FROM test_members WHERE login = 'johndoe'")) {

                    assertTrue(resultSet.next());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail("Exception during test execution: " + e.getMessage());
            }
        });
        // Wait for the JavaFX thread to finish
        sleep(1000);
    }

    @Test
    void testUpdateMember_Success() {
        // Вставка записи для обновления
        try (Connection connection = database.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("INSERT INTO test_members (name, phone, email, login, pass, role) VALUES " +
                    "('Jane Doe', '0987654321', 'jane.doe@example.com', 'janedoe', 'password', 'admin')");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                stage.setScene(new Scene(controller.valuesVbox));
                stage.show();

                controller.initData(null, Arrays.asList("1", "Jane Doe", "0987654321", "jane.doe@example.com", "janedoe", "password", "admin"));
                controller.nameTF.setText("Jane Smith");
                controller.phoneTF.setText("1122334455");
                controller.emailTF.setText("jane.smith@example.com");
                controller.loginTF.setText("janesmith");
                controller.passTF.setText("newpassword");
                controller.roleChoice.setValue("user");

                assertDoesNotThrow(() -> controller.onActionBottomAdd(null));

                // Проверка, что запись обновлена в базе данных
                try (Connection connection = database.getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery("SELECT * FROM test_members WHERE login = 'janesmith'")) {

                    assertTrue(resultSet.next());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail("Exception during test execution: " + e.getMessage());
            }
        });
        // Wait for the JavaFX thread to finish
        sleep(1000);
    }

    @AfterAll
    static void tearDownDatabase() {
        try (Connection connection = database.getConnection();
             Statement statement = connection.createStatement()) {

            // Удаление таблиц после теста
            statement.execute("DROP TABLE IF EXISTS test_members");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
