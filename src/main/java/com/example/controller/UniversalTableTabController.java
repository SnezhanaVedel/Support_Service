package com.example.controller;

import com.example.controller.dialogs.DialogAddMembersController;
import com.example.util.Database;
import com.example.util.MyAlert;
import com.example.util.UniversalAddDialog;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UniversalTableTabController implements Initializable {
    public TableView<List<String>> tableView;
    private Database database;
    private String selectedTable;
    private List<String> columnNames;
    public Button addNewBtn;

    public UniversalTableTabController(String table) {
        selectedTable = table;
    }

    public void updateTable() {
        cleaningTable();
        fillingTable();
        fillingColumnsTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        if (!selectedTable.isEmpty()) {
            updateTable();
            if (selectedTable.equals("members")) {
                addNewBtn.setText("Добавить пользователя");
            } else if (selectedTable.equals("equipment")) {
                addNewBtn.setText("Добавить оборудование");
            } else if (selectedTable.equals("orders")) {
                addNewBtn.setText("Добавить заказ");
            }
        }
    }

    private void cleaningTable() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
    }

    private void fillingTable() {
        String ORDER_BY = "id";
        if (selectedTable.equals("equipment")){
            ORDER_BY = "serial_num";
        }
        ResultSet resultSet = database.getTable(selectedTable, ORDER_BY);
        try {
            if (resultSet != null) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                columnNames = new ArrayList<>();

                if (selectedTable.equals("equipment")) {
                    columnNames.add("Серийный номер");
                    columnNames.add("Наименование оборудования");
                    columnNames.add("Тип оборудования");
                    columnNames.add("Состояние");
                    columnNames.add("Подробная информация");
                    columnNames.add("Местонахождение");
                } else if (selectedTable.equals("members")) {
                    columnNames.add("ID");
                    columnNames.add("ФИО");
                    columnNames.add("Номер телефона");
                    columnNames.add("E-mail");
                    columnNames.add("Логин");
                    columnNames.add("Пароль");
                    columnNames.add("Роль");
                } else if (selectedTable.equals("orders")) {
                    columnNames.add("ID");
                    columnNames.add("ID заявки");
                    columnNames.add("Тип ресурса");
                    columnNames.add("Наименование ресурса");
                    columnNames.add("Стоимость");
                }

                ObservableList<List<String>> data = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        row.add(resultSet.getString(i));
                    }
                    data.add(row);
                }

                tableView.setItems(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillingColumnsTable() {
        // Очистите существующие столбцы, если они есть
        tableView.getColumns().clear();

        for (int i = 0; i < columnNames.size(); ++i) {
            TableColumn<List<String>, String> column = new TableColumn<>(columnNames.get(i));
            final int finalI = i;

            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<List<String>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<List<String>, String> data) {
                    return new ReadOnlyStringWrapper(data.getValue().get(finalI));
                }
            });

            column.setCellFactory(TextFieldTableCell.forTableColumn());

            // Включите сортировку для столбца
            column.setSortable(true);

            tableView.getColumns().add(column);
        }

        // Сортируйте TableView
        tableView.sort();
    }


    // TODO: сделать провеку перед удалением
    public void onActionDelete() {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String columnSearch = ((List) tableView.getItems().get(selectedIndex)).get(0).toString();
            String columnSearchName = ((TableColumn) tableView.getColumns().get(0)).getText();
            tableView.getItems().remove(selectedIndex);
            if (selectedTable.equals("equipment")){
                database.deleteQuery(selectedTable, "serial_num", "'" + columnSearch + "'");
            } else {
                database.deleteQuery(selectedTable, columnSearchName, columnSearch);
            }
            updateTable();
            MyAlert.showInfoAlert("Запись успешно удалена");
        }
    }

    public void onActionAdd() {
        String path = "";
        String title = "";

        if (selectedTable.equals("equipment")) {
            path = "/view/dialogs/DialogAddEquipment.fxml";
            title = "Добавление нового оборудования";
        } else if (selectedTable.equals("members")) {
            path = "/view/dialogs/AddMembersDialog.fxml";
            title = "Добавление нового пользователя";
        } else if (selectedTable.equals("orders")) {
            path = "/view/dialogs/DialogAddOrders.fxml";
            title = "Добавление нового заказа";
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));

            // Передача текущего контроллера как userData
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));
            stage.setUserData(this); // передаем UniversalTableTabController

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

