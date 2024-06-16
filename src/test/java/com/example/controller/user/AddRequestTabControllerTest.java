package com.example.controller.user;

import com.example.controller.MainViewController;
import com.example.util.Database;
import com.example.util.MyAlert;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddRequestControllerTest {

    @Mock
    private Database database;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement insertRequestsStatement;

    @InjectMocks
    private AddRequestController controller;

    @BeforeAll
    static void initToolkit() {
        // Инициализация JavaFX Toolkit
        new JFXPanel(); // This will initialize the JavaFX environment
        Platform.setImplicitExit(false);
    }

    @BeforeEach
    void setUp() throws SQLException {
        controller = new AddRequestController();
        controller.database = database;

        // Настройка моков для SQL-запросов
        when(database.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("INSERT INTO requests (serial_num, problem_desc, request_comments, status, date_start, member_id) VALUES (?, ?, ?, ?, ?, ?)"))
                .thenReturn(insertRequestsStatement);
    }

    @Test
    void testAddRequestWithParams_Success() throws SQLException {
        // Настройка успешного выполнения executeUpdate
        when(insertRequestsStatement.executeUpdate()).thenReturn(1);

        // Мокирование статического метода MyAlert.showInfoAlert
        try (MockedStatic<MyAlert> mockedAlert = mockStatic(MyAlert.class)) {
            mockedAlert.when(() -> MyAlert.showInfoAlert(anyString())).thenAnswer(invocation -> null);

            // Вызов метода с передачей параметров
            controller.addRequestWithParams("SN12345", "Ноутбук не включается");

            // Проверка вызова executeUpdate
            verify(insertRequestsStatement).setString(1, "SN12345");
            verify(insertRequestsStatement).setString(2, "Ноутбук не включается");
            verify(insertRequestsStatement).setString(3, " ");
            verify(insertRequestsStatement).setString(4, "Новая");
            verify(insertRequestsStatement).setDate(5, Date.valueOf(LocalDate.now()));
            verify(insertRequestsStatement).setInt(6, MainViewController.userID);
            verify(insertRequestsStatement).executeUpdate();

            // Проверка вызова статического метода MyAlert.showInfoAlert
            mockedAlert.verify(() -> MyAlert.showInfoAlert("Запись добавлена успешно."), times(1));
        }
    }
}
