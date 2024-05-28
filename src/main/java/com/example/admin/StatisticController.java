package com.example.admin;

import com.example.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatisticController {

    @FXML
    private BarChart<Number, String> statisticsBarChart;

    @FXML
    private PieChart faultPieChart;

    private Database database;

    public void initialize() {
        database = Database.getInstance();
        setupCharts();
        updateCharts();
    }

    private void setupCharts() {
        ((CategoryAxis) statisticsBarChart.getYAxis()).setTickLabelRotation(0);

        faultPieChart.setTitle("Распределение типов неисправностей");
    }

    private void updateCharts() {
        updateBarChart();
        updatePieChart();
    }

    private void updateBarChart() {
        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.setName("Статистика");

        int completedRequestsCount = 0;
        try {
            completedRequestsCount = Integer.parseInt(database.singleValueQuery("SELECT COUNT(*) FROM reports"));
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("Ошибка при получении количества выполненных заявок");
        }

        double avgTime = 0;
        try {
            avgTime = Double.parseDouble(database.singleValueQuery("SELECT AVG(time) FROM reports"));
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("Ошибка при получении среднего времени выполнения заявок");
        }

        series.getData().add(new XYChart.Data<>(completedRequestsCount, "Количество выполненных заявок"));
        series.getData().add(new XYChart.Data<>(avgTime, "Среднее время выполнения заявки, дн."));

        statisticsBarChart.setData(FXCollections.observableArrayList(series));

        // Установить цвета и ширину для столбцов
        for (XYChart.Data<Number, String> data : series.getData()) {
            data.getNode().setStyle("-fx-bar-fill: " + (data.getYValue().equals("Количество выполненных заявок") ? "#1f77b4;" : "#ff7f0e;") +
                    "-fx-bar-width: 10;"); // Устанавливаем ширину столбцов
        }
    }

    private void updatePieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT repair_type, COUNT(*) AS count FROM reports GROUP BY repair_type")) {
                    while (resultSet.next()) {
                        String repairType = resultSet.getString("repair_type");
                        int count = resultSet.getInt("count");
                        PieChart.Data data = new PieChart.Data(repairType, count);
                        pieChartData.add(data);

                        // Добавляем Tooltip
                        Tooltip tooltip = new Tooltip(repairType + ": " + count);
                        Tooltip.install(data.getNode(), tooltip);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        faultPieChart.setData(pieChartData);

        // Установить случайные цвета для каждого сегмента круговой диаграммы
        for (PieChart.Data data : pieChartData) {
            data.getNode().setStyle("-fx-pie-color: " + generateRandomColor() + ";");
        }
    }

    private String generateRandomColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    @FXML
    private void onRefresh() {
        updateCharts();
    }
}
