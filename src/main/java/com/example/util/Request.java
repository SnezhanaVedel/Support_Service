package com.example.util;

import java.sql.*;

public class Request {
    int id;
    String serial_num;
    String equip_type;
    String equip_name;
    String equip_details;
    String equip_location;
    String equip_condition;
    String problem_desc;
    String client_name;
    String client_phone;
    String client_email;
    String status;
    String date_start;
    String request_comments;

    Database database;


    public Request(int id) {
        this.id = id;
        database = Database.getInstance();
        loadInfoFromDB();
    }

    public void updateRequestInDB(String equip_details,
                                  String equip_location,
                                  String equip_condition,
                                  String problem_desc,
                                  String request_comments,
                                  String status) {

        this.equip_details = equip_details;
        this.equip_location = equip_location;
        this.equip_condition = equip_condition;
        this.problem_desc = problem_desc;
        this.request_comments = request_comments;
        this.status = status;

        database = Database.getInstance();

        // Обновление таблицы equipment
        String updateEquipmentQuery = String.format("UPDATE equipment " +
                "SET details = '%s', location = '%s', condition = '%s' " +
                "WHERE serial_num = (SELECT serial_num FROM requests WHERE id = %d)", equip_details, equip_location, equip_condition, id);
        database.simpleQuery(updateEquipmentQuery);

// Обновление таблицы requests
        String updateRequestsQuery = String.format("UPDATE requests " +
                "SET problem_desc = '%s', request_comments = '%s', status = '%s' " +
                "WHERE id = %d", problem_desc, request_comments, status, id);
        database.simpleQuery(updateRequestsQuery);


        MyAlert.showInfoAlert("Информация по заявке обновлена успешно.");
    }

    public void loadInfoFromDB() {
        database = Database.getInstance();
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            String query = "SELECT r.id, r.serial_num, r.problem_desc, r.request_comments, r.status, r.date_start, " +
                    "m.name, m.phone, m.e_mail, " +
                    "e.equip_name, e.equip_type, e.condition, e.details, e.location " +
                    "FROM requests r " +
                    "JOIN members m ON r.member_id = m.id " +
                    "JOIN equipment e ON r.serial_num = e.serial_num " +
                    "WHERE r.id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt("id");
                        serial_num = resultSet.getString("serial_num");
                        problem_desc = resultSet.getString("problem_desc");
                        request_comments = resultSet.getString("request_comments");
                        status = resultSet.getString("status");
                        client_name = resultSet.getString("name");
                        client_phone = resultSet.getString("phone");
                        client_email = resultSet.getString("e_mail");
                        equip_name = resultSet.getString("equip_name");
                        equip_type = resultSet.getString("equip_type");
                        equip_condition = resultSet.getString("condition");
                        equip_details = resultSet.getString("details");
                        equip_location = resultSet.getString("location");

                        // Используется getString вместо getDate, чтобы избежать NullPointerException в случае,
                        // когда эти данные у заявки ещё отсутствуют
                        date_start = resultSet.getString("date_start");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при получении информации о заявке.");
        }
    }


//    public boolean deleteRequestInDB() {
//        String idStr = String.valueOf(id);
//        database.deleteQuery("assignments", "id_request", idStr);
//        database.deleteQuery("request_processes", "request_id", idStr);
//        database.deleteQuery("request_regs", "request_id", idStr);
//        boolean deletedSuccessfully = database.deleteQuery("requests", "id", idStr);
//
//        // TODO: мб проверку условий доделать
//        return deletedSuccessfully;
//    }


    public int getId() {
        return id;
    }

    public String getEquip_type() {
        return equip_type;
    }

    public String getProblem_desc() {
        return problem_desc;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public String getSerial_num() {
        return serial_num;
    }

    public String getStatus() {
        return status;
    }

    public String getDate_start() {
        return date_start;
    }

    public String getRequest_comments() {
        return request_comments;
    }

    public String getEquip_name() {
        return equip_name;
    }

    public String getEquip_details() {
        return equip_details;
    }

    public String getEquip_location() {
        return equip_location;
    }

    public String getEquip_condition() {
        return equip_condition;
    }

    public String getClient_email() {
        return client_email;
    }

    public Database getDatabase() {
        return database;
    }
}
