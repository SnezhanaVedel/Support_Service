package com.example.util;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static Database instance;
    public static final String URL = "jdbc:postgresql://localhost:8888/postgres";
    public static final String ROOT_LOGIN = "postgres";
    public static final String ROOT_PASS = "root";
    private Connection externalConnection = null;
    private Connection notificationConnection = null;
    private PGConnection pgConnection;
    private Thread notificationThread;
    private volatile boolean listening = true;
    private List<NotificationListener> listeners = new ArrayList<>();

    private Database() {
        try {
            notificationConnection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            pgConnection = (PGConnection) notificationConnection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public boolean accessToDB(String login, String password) {
        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS)) {
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        externalConnection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
        return externalConnection;
    }

    public void closeConnection() throws SQLException {
        if (externalConnection != null) {
            externalConnection.close();
        }
    }

    public void listenForNotifications(String channel) {
        try (Statement stmt = notificationConnection.createStatement()) {
            stmt.execute("LISTEN " + channel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PGNotification[] getNotifications() {
        try {
            return pgConnection.getNotifications();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void startNotificationListener() {
        notificationThread = new Thread(() -> {
            while (listening) {
                PGNotification[] notifications = getNotifications();
                if (notifications != null) {
                    for (PGNotification notification : notifications) {
                        handleNotification(notification);
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

    private void handleNotification(PGNotification notification) {
        System.out.println("Received notification: " + notification.getParameter());
        for (NotificationListener listener : listeners) {
            listener.onNotification(notification.getName(), notification.getParameter());
        }
    }

    public void stopNotificationListener() {
        listening = false;
        if (notificationThread != null) {
            notificationThread.interrupt();
        }
    }

    public void addNotificationListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeNotificationListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    public ResultSet getTable(String selectedTable, String orderBy) {
        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS)) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM " + selectedTable + " ORDER BY " + orderBy + " ASC");
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getAllTableColumnNames(String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    columnNames.add(columnName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnNames;
    }

    public ArrayList<String> stringListQuery(String neededColumn, String table, String where, String orderBy) {
        ArrayList<String> finalList = new ArrayList<>();
        String finalQuery = "SELECT " + neededColumn + " FROM " + table;
        if (where != null) {
            finalQuery += " WHERE " + where;
        }
        if (orderBy != null) {
            finalQuery += " ORDER BY " + orderBy;
        }
        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
             PreparedStatement statement = connection.prepareStatement(finalQuery.trim());
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                finalList.add(resultSet.getString(neededColumn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return finalList;
    }

    public ArrayList<String> stringListQuery(String neededColumn, String sql) {
        ArrayList<String> finalList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
             PreparedStatement statement = connection.prepareStatement(sql.trim());
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                finalList.add(resultSet.getString(neededColumn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return finalList;
    }

    public String singleValueQuery(String sql) {
        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public ArrayList<String> executeQueryAndGetColumnValues(String query) {
        ArrayList<String> columnValues = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    columnValues.add(resultSet.getString(i));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения запроса к базе данных", e);
        }
        return columnValues;
    }

    public boolean updateTable(String selectedTable, String columnChangeName, String newRecord, String columnSearchName, String columnSearch) {
        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE " + selectedTable +
                             " SET " + columnChangeName + " = ?" +
                             " WHERE " + columnSearchName + " = " + columnSearch)) {
            statement.setObject(1, convertStringToInteger(newRecord));
            return statement.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Object convertStringToInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return str;
        }
    }

    public boolean deleteQuery(String selectedTable, String columnSearchName, String columnSearch) {
        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM " + selectedTable + " WHERE " + columnSearchName + " = " + columnSearch)) {
            return statement.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateQuery(String table, String set, String where) {
        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE " + table + " SET " + set + " WHERE " + where)) {
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean simpleQuery(String sql) {
        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface NotificationListener {
        void onNotification(String channel, String payload);
    }
}
