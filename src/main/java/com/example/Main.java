package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf"), 10);

        Locale.setDefault(new Locale("ru"));
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Авторизация");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}