package com.example.controller.admin;

import com.example.controller.ListItemController;
import com.example.util.Database;
import com.example.util.MyAlert;
import com.example.util.Request;
import com.example.util.UniversalAddDialog;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.postgresql.PGNotification;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

public class AdminRequestsController implements Initializable {
    public VBox statesVBox;
    public VBox priorityVBox;
    public TextField dateFilterTF;
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

    private LinkedHashMap<Integer, Request> requestMap;
    private boolean filterApplied = false;
    private String query = null;
    private Thread notificationThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        requestMap = new LinkedHashMap<>();

        moreInfoPane.setVisible(false);
        createOrCheckReportBtn.setVisible(false);

        query = getQueryForRole();

        database.listenForNotifications("request_created_admin");
        database.listenForNotifications("request_updated_admin");

        startNotificationListener();

        loadRepairRequests();

        stateChoice.getItems().addAll("В работе", "Выполнено", "В ожидании");
        conditionChoiceBox.getItems().addAll("Исправно", "Не исправно", "Требуются запчасти");

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

    public void startNotificationListener() {
        stopNotificationListener(); // Остановить предыдущий поток, если он есть
        notificationThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                PGNotification[] notifications = database.getNotifications();
                if (notifications != null) {
                    for (PGNotification notification : notifications) {
                        if ("request_created_admin".equals(notification.getName()) || "request_updated_admin".equals(notification.getName())) {
                            Platform.runLater(() -> {
                                loadRepairRequests();
                                if (moreInfoPane.isVisible()) {
                                    showMoreInfo(currentRequestNumber);
                                }
                            });
                        }
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        notificationThread.setDaemon(true);
        notificationThread.start();
    }

    public void stopNotificationListener() {
        if (notificationThread != null && notificationThread.isAlive()) {
            notificationThread.interrupt();
        }
    }

    @FXML
    public void applyFilters() {
        repairRequestListView.getItems().clear();

        StringBuilder queryBuilder = new StringBuilder("SELECT id FROM requests WHERE status != 'Новая' AND ");
        List<String> conditions = new ArrayList<>();

        List<String> stateConditions = new ArrayList<>();
        for (Node node : statesVBox.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkbox = (CheckBox) node;
                if (checkbox.isSelected()) {
                    stateConditions.add("status = '" + checkbox.getText() + "'");
                }
            }
        }
        if (!stateConditions.isEmpty()) {
            conditions.add("(" + String.join(" OR ", stateConditions) + ")");
        }

        String selectedDate = dateFilterTF.getText();
        if (selectedDate != null && !selectedDate.trim().isEmpty()) {
            conditions.add("date_start = '" + selectedDate + "'");
        }

        String requestNumber = idFilterTF.getText().trim();
        if (!requestNumber.isEmpty()) {
            try {
                int requestId = Integer.parseInt(requestNumber);
                conditions.add("id = " + requestId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                MyAlert.showErrorAlert("Неверный формат номера заявки.");
                return;
            }
        }

        if (!conditions.isEmpty()) {
            queryBuilder.append(String.join(" AND ", conditions));
        } else {
            queryBuilder.append("1=1");
        }

        queryBuilder.append(" ORDER BY id");

        query = queryBuilder.toString();
        System.out.println(query);
        loadRepairRequests();
        MyAlert.showInfoAlert("Фильтры успешно применены");
    }

    private boolean isRequestIdInFilteredResults(int requestId) {
        ObservableList<String> filteredItems = repairRequestListView.getItems();
        for (String item : filteredItems) {
            if (Integer.parseInt(item) == requestId) {
                return true;
            }
        }
        return false;
    }

    public void loadRepairRequests() {
        repairRequestListView.getItems().clear();
        requestMap.clear();

        if (query == null) {
            query = getQueryForRole();
        }
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

            if (!isRequestIdInFilteredResults(currentRequestNumber)) {
                moreInfoPane.setVisible(false);
            }

            repairRequestListView.setCellFactory(param -> new ListCell<String>() {
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
            });
        } else {
            MyAlert.showErrorAlert("Ошибка: роль admin не найдена");
        }
    }

    private String getQueryForRole() {
        return "SELECT id FROM requests WHERE status != 'Новая' ORDER BY id";
    }

    @FXML
    public void clearFilters(ActionEvent event) {
        for (Node node : statesVBox.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }

        dateFilterTF.clear();
        idFilterTF.clear();
        query = null;
        loadRepairRequests();
    }

    @FXML
    public void onActionRefresh() {
        loadRepairRequests();
    }

    @FXML
    public void onActionSave() {
        String stateValue = stateChoice.getValue();

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

        loadRepairRequests();
        showMoreInfo(currentRequestNumber);
    }

    @FXML
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
        commentsTextArea.setText(request.getRequest_comments());
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

        if (request.getStatus().equals("Выполнено")) {
            ArrayList<String> reportValues = database.executeQueryAndGetColumnValues(
                    "SELECT * FROM reports WHERE request_id = " + currentRequestNumber);

            if (reportValues != null && reportValues.size() > 0) {
                createOrCheckReportBtn.setText("Посмотреть отчёт");
            } else {
                createOrCheckReportBtn.setText("Создать отчёт");
            }
            createOrCheckReportBtn.setVisible(true);
        } else {
            createOrCheckReportBtn.setVisible(false);
        }
        moreInfoPane.setVisible(true);
    }
}
