package com.example.controller;

import com.example.util.Database;
import com.example.util.MyAlert;
import com.example.util.Request;
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
    public TextField equipTypeField;
    public TextArea descriptionTextArea;
    public Label clientNameLabel;
    public Label clientPhoneLabel;
    public TextField equipSerialField;
    public TextArea commentsTextArea;
    public ScrollPane moreInfoScrollPane;
    public BorderPane moreInfoPane;
    public VBox infoVBox;
    public VBox stateVBox;
    public ChoiceBox<String> stateChoice;
    public ChoiceBox<String> priorityChoice;
    public TextField registerDateTF;
    public TextField finishDateTF;
    public Button refreshListBtn;
    public Button createOrCheckReportBtn;



    @FXML
    private TextField serialNumField;
    @FXML
    private TextField equipNameField;
    @FXML
    private ChoiceBox<String> conditionChoiceBox;
    @FXML
    private TextArea detalsTextArea;
    @FXML
    private TextField locationField;
    @FXML
    private TextArea problemDescTextArea;
    @FXML
    private ChoiceBox<String> statusChoice;
    @FXML
    private TextField dateStartField;
    @FXML
    private TextField clientNameField;
    @FXML
    private TextField clientPhoneField;
    @FXML
    private TextField emailField;



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

        loadRepairRequests();
        //TODO: убрать статус ЗАКРЫТА в остальных частях кода

        stateChoice.getItems().addAll("В работе", "Выполнено", "В ожидании");
        //TODO: перечень состояний возможно поменять
        conditionChoiceBox.getItems().addAll("Исправно","Не исправно","Требуются запчасти");

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

    //TODO: фильтры

//
//     @FXML
//    public void applyFilters(ActionEvent event) {
//        repairRequestListView.getItems().clear(); // Очищаем ListView перед загрузкой новых данных
//
//        StringBuilder queryBuilder = new StringBuilder("SELECT r.id FROM requests r " +
//                "JOIN request_processes rp ON r.id = rp.request_id " +
//                "WHERE r.status != 'Новая' AND ");
//
//        List<String> conditions = new ArrayList<>();
//
//        // Фильтры по состояниям (используем оператор OR между выбранными состояниями)
//        List<String> stateConditions = new ArrayList<>();
//        for (Node node : statesVBox.getChildren()) {
//            if (node instanceof CheckBox) {
//                CheckBox checkbox = (CheckBox) node;
//                if (checkbox.isSelected()) {
//                    stateConditions.add("r.status = '" + checkbox.getText() + "'");
//                }
//            }
//        }
//        if (!stateConditions.isEmpty()) {
//            conditions.add("(" + String.join(" OR ", stateConditions) + ")");
//        }
//
//        // Фильтры по приоритетам (используем оператор OR между выбранными приоритетами)
//        List<String> priorityConditions = new ArrayList<>();
//        for (Node node : priorityVBox.getChildren()) {
//            if (node instanceof CheckBox) {
//                CheckBox checkbox = (CheckBox) node;
//                if (checkbox.isSelected()) {
//                    priorityConditions.add("rp.priority = '" + checkbox.getText() + "'");
//                }
//            }
//        }
//        if (!priorityConditions.isEmpty()) {
//            conditions.add("(" + String.join(" OR ", priorityConditions) + ")");
//        }
//
//        // Фильтр по дате создания заявки
//        String selectedDate = dateFilterTF.getText();
//        if (selectedDate != null && !selectedDate.trim().isEmpty()) {
//            conditions.add("r.id IN (SELECT request_id FROM request_regs WHERE date_start = '" + selectedDate + "')");
//        }
//
//        // Фильтр по номеру заявки
//        String requestNumber = idFilterTF.getText().trim();
//        if (!requestNumber.isEmpty()) {
//            try {
//                int requestId = Integer.parseInt(requestNumber);
//                conditions.add("r.id = " + requestId);
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//                MyAlert.showErrorAlert("Неверный формат номера заявки.");
//                return;
//            }
//        }
//
//        // Строим окончательное условие WHERE
//        if (!conditions.isEmpty()) {
//            queryBuilder.append(String.join(" AND ", conditions));
//        } else {
//            queryBuilder.append("1=1"); // Если фильтры не выбраны, выбираем все заявки
//        }
//
//        queryBuilder.append(" ORDER BY r.id");
//
//        // Выполняем SQL-запрос и заполняем ListView
//        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
//            String query = queryBuilder.toString();
//            loadRepairRequests(connection, query);
//
//            // Проверяем, видима ли подробная информация после применения фильтров
//            if (!isRequestIdInFilteredResults(currentRequestNumber)) {
//                moreInfoPane.setVisible(false);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            MyAlert.showErrorAlert("Ошибка при соединении с базой данных.");
//        }
//
//        MyAlert.showInfoAlert("Фильтры успешно применены");
//    }
//
//    private boolean isRequestIdInFilteredResults(int requestId) {
//        ObservableList<String> filteredItems = repairRequestListView.getItems();
//        for (String item : filteredItems) {
//            if (Integer.parseInt(item) == requestId) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//
//








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

        } else if (role.equals("admin_new")) {
            return "SELECT id FROM requests WHERE status = 'Новая' ORDER BY id";

        } else if (role.equals("user")) {
            return "SELECT r.id " +
                    "FROM requests r " +
                    "WHERE r.member_id = " + MainViewController.userID + " " +
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
        if (role.equals("admin_new")) {
            stateValue = "В работе";
        } else {
            stateValue = stateChoice.getValue();
        }

        requestMap.get(currentRequestNumber).updateRequestInDB(
                equipTypeField.getText(),
                problemDescTextArea.getText(),
                commentsTextArea.getText(),
                stateChoice.getValue(),
                equipNameField.getText(),
                conditionChoiceBox.getValue(),
                detalsTextArea.getText(),
                locationField.getText()
        );


        if (role.equals("admin_new")) {
            moreInfoPane.setVisible(false);
            loadRepairRequests();
        } else {
            showMoreInfo(currentRequestNumber);
        }
    }

//TODO: поля поменять для отчета
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
        equipTypeField.setText(request.getEquip_type());
//        descriptionTextArea.setText(request.getProblem_desc());
        commentsTextArea.setText(request.getRequest_comments());
//        registerDateTF.setText(request.getDate_start());

        //TODO: отдельные для пользователей

        serialNumField.setText(request.getSerial_num());
        equipNameField.setText(request.getEquip_name());
        conditionChoiceBox.setValue(request.getCondition());
        detalsTextArea.setText(request.getDetals());
        locationField.setText(request.getLocation());
        problemDescTextArea.setText(request.getProblem_desc());
        stateChoice.setValue(request.getStatus());
        dateStartField.setText(request.getDate_start());
        clientNameField.setText(request.getClient_name());
        clientPhoneField.setText(request.getClient_phone());
        emailField.setText(request.getEmail());

        if (!role.equals("admin_new")) {
            stateChoice.setValue(request.getStatus());
        }

        // Отображение кнопки для проверки отчета в зависимости от состояния
        String currentState = request.getStatus();
        if (currentState.equals("Выполнено")) {

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
