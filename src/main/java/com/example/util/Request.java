package com.example.util;

import java.sql.*;

public class Request {
    int id;
    String serial_num;
    String equip_name;
    String equip_type;
    String condition;
    String detals;
    String location;
    String problem_desc;
    String request_comments;
    String status;
    String date_start;
    int member_id;
    String client_name;
    String client_phone;
    String email;
    String login;
    String pass;
    String role;
    Database database;


    public Request(int id) {
        this.id = id;
        database = Database.getInstance();
        loadInfoFromDB();
    }

    public void updateRequestInDB(
                                  String equip_type,
                                  String problem_desc,
                                  String request_comments,
                                  String status,
                                  String equip_name,
                                  String condition,
                                  String detals,
                                  String location
                                  ) {

//        this.serial_num = equip_num;
        this.problem_desc = problem_desc;
        this.request_comments = request_comments;
        this.status = status;
        this.equip_type = equip_type;
        this.equip_name = equip_name;
        this.condition = condition;
        this.detals = detals;
        this.location = location;

        database = Database.getInstance();

        // Обновляем запись в таблице requests
        String updateRequestsQuery = String.format("UPDATE requests " +
                "SET problem_desc = '%s', request_comments = '%s', status = '%s' " +
                "WHERE id = %d", problem_desc, request_comments, status, id);
        database.simpleQuery(updateRequestsQuery);


        // Обновляем запись в таблице requests
        String updateEquipQuery = String.format("UPDATE equipment " +
                "SET equip_type = '%s', equip_name = '%s', condition = '%s', detals = '%s', location = '%s' " +
                "WHERE serial_num = '%s'", equip_type, equip_name, condition, detals, location, serial_num);
        database.simpleQuery(updateEquipQuery);

        MyAlert.showInfoAlert("Информация по заявке обновлена успешно.");
    }

    public void loadInfoFromDB() {
        database = Database.getInstance();
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            String query = "SELECT r.id, r.serial_num, r.problem_desc, r.request_comments, r.status, " +
                    "r.date_start, r.member_id, e.equip_name, e.equip_type, e.condition, e.detals, e.location, " +
                    "m.name, m.phone, m.email, m.login, m.pass, m.role " +
                    "FROM requests r " +
                    "JOIN equipment e ON r.serial_num = e.serial_num " +
                    "JOIN members m ON r.member_id = m.id " +
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
                        date_start = resultSet.getString("date_start");
                        member_id = resultSet.getInt("member_id");
                        equip_name = resultSet.getString("equip_name");
                        equip_type = resultSet.getString("equip_type");
                        condition = resultSet.getString("condition");
                        detals = resultSet.getString("detals");
                        location = resultSet.getString("location");
                        client_name = resultSet.getString("name");
                        client_phone = resultSet.getString("phone");
                        email = resultSet.getString("email");
                        login = resultSet.getString("login");
                        pass = resultSet.getString("pass");
                        role = resultSet.getString("role");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при получении информации о заявке.");
        }
    }

    public int getId() {
        return id;
    }

    public String getEquip_name() {
        return equip_name;
    }

    public String getCondition() {
        return condition;
    }

    public String getDetals() {
        return detals;
    }

    public String getLocation() {
        return location;
    }

    public int getMember_id() {
        return member_id;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public String getRole() {
        return role;
    }

    public Database getDatabase() {
        return database;
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
}