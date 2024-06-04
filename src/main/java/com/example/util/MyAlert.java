package com.example.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class MyAlert {

    public static void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showWarningConfirmation(String title, String message) {
        // Создаем Alert с типом WARNING и добавляем стандартные кнопки OK и Cancel
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null); // Убираем заголовок внутри окна, если не нужен

        // Отображаем Alert и ждем решения пользователя
        Optional<ButtonType> result = alert.showAndWait();
        // Возвращаем true, если нажата кнопка OK
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}