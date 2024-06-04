package com.example.controller.admin;

import com.example.util.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatisticController {

    @FXML
    private TextField numOfCompletedRequestsTF;
    @FXML
    private TextField avgTimeTF;
    @FXML
    private TextField totalCostTF;
    @FXML
    private PieChart faultPieChart;
    private Database database;

    public void initialize() {
        database = Database.getInstance();
        setupCharts();
        updateCharts();
    }

    private void setupCharts() {
        faultPieChart.setTitle("Распределение типов неисправностей");
    }

    private void updateCharts() {
        updateStatsFields();
        updatePieChart();
    }

    private void updateStatsFields() {
        int completedRequestsCount = 0;
        double avgTime = 0;
        double totalCost = 0;

        try {
            completedRequestsCount = Integer.parseInt(database.singleValueQuery("SELECT COUNT(*) FROM reports"));
            avgTime = Double.parseDouble(database.singleValueQuery("SELECT AVG(time) FROM reports"));
            totalCost = Double.parseDouble(database.singleValueQuery("SELECT SUM(cost) FROM reports"));
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("Ошибка при получении данных");
        }

        numOfCompletedRequestsTF.setText(String.valueOf(completedRequestsCount));
        avgTimeTF.setText(String.format("%.2f", avgTime));
        totalCostTF.setText(String.format("%.2f", totalCost));
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

        // Добавить количество в подписи к сегментам
        for (PieChart.Data data : faultPieChart.getData()) {
            data.nameProperty().set(data.getName() + " (" + (int) data.getPieValue() + ")");
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