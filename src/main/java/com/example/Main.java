package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

public class Main extends Application {
    private static Stage primaryStage;
    private static final String DB_URL_FILE = "db_url.txt";
    private static final String ROOT_LOGIN = "postgres";
    private static final String ROOT_PASS = "root";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 10);
        Locale.setDefault(new Locale("ru"));

        if (new File(DB_URL_FILE).exists()) {
            String dbUrl = new String(Files.readAllBytes(Paths.get(DB_URL_FILE)));
            if (testDbConnection(dbUrl)) {
                showLoginWindow();
            } else {
                JOptionPane.showMessageDialog(null, "Не удалось подключиться к базе данных по сохраненному адресу. Пожалуйста, введите новый адрес.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                showStartupWindow();
            }
        } else {
            showStartupWindow();
        }
    }

    private void showStartupWindow() {
        String serverIp = JOptionPane.showInputDialog(null, "Введите IP базы данных:", "Настройка подключения к БД", JOptionPane.PLAIN_MESSAGE);
        if (serverIp != null && !serverIp.trim().isEmpty()) {
            serverIp = serverIp.trim();
            String dbUrl = "jdbc:postgresql://" + serverIp + ":8888/postgres";
            if (testDbConnection(dbUrl)) {
                try (FileWriter writer = new FileWriter(DB_URL_FILE)) {
                    writer.write(dbUrl);
                    writer.flush(); // Добавлено чтобы убедиться, что данные записаны
                    JOptionPane.showMessageDialog(null, "Подключение успешно. Перезапустите программу.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0); // Закрытие программы
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Ошибка записи в файл: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Не удалось подключиться к базе данных. Проверьте правильность IP адреса.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                showStartupWindow();
            }
        } else {
            JOptionPane.showMessageDialog(null, "IP базы данных не может быть пустым", "Ошибка", JOptionPane.ERROR_MESSAGE);
            showStartupWindow();
        }
    }

    private boolean testDbConnection(String dbUrl) {
        try (Connection connection = DriverManager.getConnection(dbUrl, ROOT_LOGIN, ROOT_PASS)) {
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("Connection timed out")) {
                JOptionPane.showMessageDialog(null, "Время подключения истекло. Проверьте правильность IP адреса и доступность базы данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Произошла ошибка при подключении к базе данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
            return false;
        }
    }

    public static void showLoginWindow() throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("/view/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Авторизация");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
