package com.example.model;

import com.example.util.Database;
import com.example.util.Request;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.HashMap;
import java.util.Map;

public class RequestsModel implements Observable {

    private Database database;
    private Map<Integer, Request> requests;
    private InvalidationListener listener;

    public RequestsModel() {
        this.database = Database.getInstance();
        this.requests = new HashMap<>();
        loadRequests();
    }

    public void loadRequests() {
        requests.clear();
        String query = "SELECT id FROM requests WHERE status != 'Новая' ORDER BY id";
        database.stringListQuery("id", query).forEach(idStr -> {
            int id = Integer.parseInt(idStr);
            requests.put(id, new Request(id));
        });
        if (listener != null) {
            listener.invalidated(this);
        }
    }

    public Request getRequestById(int id) {
        return requests.get(id);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        this.listener = null;
    }
}
