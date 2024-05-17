package com.example.controller;

import com.example.util.Request;
import com.example.util.Database;
import com.example.util.MyAlert;
import com.example.util.UniversalAddDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

public class UniversalRequestsTabController implements Initializable {
    public TextField idFilterTF;

    @FXML
    private ListView<String> repairRequestListView;
    public Label requestNumberLabel;
    public TextField equipSerialField;
    public TextField equipTypeField;
    public TextField equipNameField;
    public TextField equipConditionField;
    public TextArea equipDetailsArea;
    public TextField equipLocationField;
    public TextArea descriptionTextArea;
    public Label clientNameLabel;
    public Label clientPhoneLabel;
    public Label clientEmailLabel;
    public TextArea commentsTextArea;
    public BorderPane moreInfoPane;
    public VBox infoVBox;
    public VBox stateVBox;
    public ChoiceBox<String> stateChoice;
    public TextField registerDateTF;

    public Button refreshListBtn;

    public Button createOrCheckReportBtn;

    private Database database;
    private int currentRequestNumber = -1;
    private String role;


    private LinkedHashMap<Integer, Request> requestMap;
    private boolean filterApplied = false;

    public UniversalRequestsTabController(String role) {
        this.role = role;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        requestMap = new LinkedHashMap<>();

        moreInfoPane.setVisible(false);
        createOrCheckReportBtn.setVisible(false);

        setUnavailableFields();
        loadRepairRequests();

        stateChoice.getItems().addAll("В работе", "Выполнено", "В ожидании", "Закрыта");

        repairRequestListView.setOnMouseClicked(event -> {
            int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedItem = repairRequestListView.getItems().get(selectedIndex);
                currentRequestNumber = Integer.parseInt(selectedItem);
                System.out.println("Выбрана заявка №" + currentRequestNumber);
                showMoreInfo(currentRequestNumber);
            }
        });
    }

    private void setUnavailableFields() {
        if (role.equals("manager_new")) {
            infoVBox.getChildren().remove(stateVBox);
        }
    }

    @FXML
    public void applyFilters() {
        if (!idFilterTF.getText().trim().equals("")) {
            int requestId = Integer.parseInt(idFilterTF.getText());

            if (requestMap.containsKey(requestId)) {
                repairRequestListView.getItems().clear();
                repairRequestListView.getItems().add(String.valueOf(requestId));
                filterApplied = true;
                showMoreInfo(requestId);

            } else {
                filterApplied = false;
                moreInfoPane.setVisible(false);
                clearFilters();
                MyAlert.showErrorAlert(String.format("Заявка с номером %d не найдена", requestId));
            }
        } else {
            filterApplied = false;
        }
    }


//TODO: метод для item

//    public void loadRepairRequests() {
//        repairRequestListView.getItems().clear(); // Очищаем ListView перед загрузкой новых данных
//        requestMap.clear();
//
//        String query = getQueryForRole();
//        if (query != null) {
//            ArrayList<String> idList = database.stringListQuery("id", query);
//
//            for (String idStr : idList) {
//                int id = Integer.parseInt(idStr);
//                requestMap.put(id, new Request(id));
//                repairRequestListView.getItems().add(idStr);
//            }
//
//            if (filterApplied) {
//                applyFilters();
//            }
//
//        } else {
//            MyAlert.showErrorAlert("Ошибка: роль " + role + " не найдена");
//        }
//    }


    public void loadRepairRequests() {
        repairRequestListView.getItems().clear(); // Очищаем ListView перед загрузкой новых данных
        requestMap.clear();

        String query = getQueryForRole();
        if (query != null) {
            ArrayList<String> idList = database.stringListQuery("id", query);

            for (String idStr : idList) {
                int id = Integer.parseInt(idStr);
                requestMap.put(id, new Request(id));
                repairRequestListView.getItems().add(idStr);
            }

            if (filterApplied) {
                applyFilters();
            }

            // Вот код, который вы хотите добавить, чтобы установить фабрику ячеек для repairRequestListView
            repairRequestListView.setCellFactory(param -> {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ListItem.fxml"));
                            try {
                                Parent root = loader.load();
                                ListItemController controller = loader.getController();
                                controller.setRequestNumber(Integer.parseInt(item));
                                setGraphic(root);
                            } catch (IOException e) {
                                e.printStackTrace();
                                setGraphic(null);
                            }
                        }
                    }
                };
            });
        } else {
            MyAlert.showErrorAlert("Ошибка: роль " + role + " не найдена");
        }
    }


    private String getQueryForRole() {
        if (role.equals("admin")) {
            return "SELECT id FROM requests WHERE status != 'Новая' ORDER BY id";

        } else if (role.equals("manager_new")) {
            return "SELECT id FROM requests WHERE status = 'Новая' ORDER BY id";

        } else if (role.equals("resp_repairer")) {
            return "SELECT r.id " +
                    "FROM requests r " +
                    "JOIN assignments a ON r.id = a.id_request " +
                    "WHERE r.status != 'Новая' " +
                    "AND a.member_id = " + MainViewController.userID + " " +
                    "AND a.is_responsible = true " +
                    "ORDER BY r.id";

        } else if (role.equals("addit_repairer")) {
            return "SELECT r.id " +
                    "FROM requests r " +
                    "JOIN assignments a ON r.id = a.id_request " +
                    "WHERE r.status != 'Новая' " +
                    "AND a.member_id = " + MainViewController.userID + " " +
                    "AND a.is_responsible = false " +
                    "ORDER BY r.id";
        } else {
            return null;
        }
    }


    @FXML
    public void clearFilters() {
        idFilterTF.clear();
        loadRepairRequests();
    }

    @FXML
    public void onActionRefresh() {
        loadRepairRequests();
    }


    public void onActionSave() {

        String stateValue;
        if (role.equals("manager_new")) {
            stateValue = "В работе";
        } else {
            stateValue = stateChoice.getValue();
        }

        requestMap.get(currentRequestNumber).updateRequestInDB(
                equipDetailsArea.getText(),
                equipLocationField.getText(),
                equipConditionField.getText(),
                descriptionTextArea.getText(),
                commentsTextArea.getText(),
                stateValue);

        if (role.equals("manager_new")) {
            moreInfoPane.setVisible(false);
            loadRepairRequests();
        } else {
            showMoreInfo(currentRequestNumber);
        }
    }


    public void onActionCreateOrCheckReport() {

        if (createOrCheckReportBtn.getText().equals("Посмотреть отчёт")) {

            ArrayList<String> reportValues = database.executeQueryAndGetColumnValues(
                    "SELECT * FROM reports WHERE request_id = " + currentRequestNumber);

            if (reportValues != null && reportValues.size() > 0) {
                String report =
                        "Номер заявки: " + reportValues.get(0) + "\n\n" +
                                "Тип ремонта: " + reportValues.get(1) + "\n\n" +
                                "Время выполнения, дней: " + reportValues.get(2) + "\n\n" +
                                "Стоимость: " + reportValues.get(3) + "\n\n" +
                                "Ресурсы: " + reportValues.get(4) + "\n\n" +
                                "Причина неисправности: " + reportValues.get(5) + "\n\n" +
                                "Оказанная помощь: " + reportValues.get(6);
                MyAlert.showInfoAlert(report);
            } else {
                MyAlert.showInfoAlert("Отчёт для заявки №" + currentRequestNumber + " отсутствует");
            }

        } else {
            new UniversalAddDialog("reports", database.getAllTableColumnNames("reports"));
            showMoreInfo(currentRequestNumber);
        }

    }

    public void showMoreInfo(int requestId) {
        Request request = requestMap.get(requestId);

        requestNumberLabel.setText("Заявка №" + requestId);
        equipSerialField.setText(request.getSerial_num());
        equipTypeField.setText(request.getEquip_type());
        equipNameField.setText(request.getEquip_name());
        equipConditionField.setText(request.getEquip_condition());
        equipDetailsArea.setText(request.getEquip_details());
        equipLocationField.setText(request.getEquip_location());
        descriptionTextArea.setText(request.getProblem_desc());
        clientNameLabel.setText("ФИО: " + request.getClient_name());
        clientPhoneLabel.setText("Телефон: " + request.getClient_phone());
        clientEmailLabel.setText("Телефон: " + request.getClient_email());
        commentsTextArea.setText(request.getRequest_comments());
        registerDateTF.setText(request.getDate_start());

        if (!role.equals("manager_new")) {
            stateChoice.setValue(request.getStatus());
        }

        // Отображение кнопки для проверки отчета в зависимости от состояния
        String currentState = request.getStatus();
        if (currentState.equals("Выполнено") || currentState.equals("Закрыта")) {

            if (role.equals("admin")) {
                ArrayList<String> reportValues = database.executeQueryAndGetColumnValues(
                        "SELECT * FROM reports WHERE request_id = " + currentRequestNumber);

                if (reportValues != null && reportValues.size() > 0) {
                    createOrCheckReportBtn.setText("Посмотреть отчёт");
                } else {
                    createOrCheckReportBtn.setText("Создать отчёт");
                }
            }

            createOrCheckReportBtn.setVisible(true);
        } else {
            createOrCheckReportBtn.setVisible(false);
        }

        moreInfoPane.setVisible(true);
    }
}
