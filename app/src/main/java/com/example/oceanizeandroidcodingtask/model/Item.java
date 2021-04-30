package com.example.oceanizeandroidcodingtask.model;

public class Item {
    private int id;
    private String name;
    private String host;
    private int port;
    private String username;
    private String password;
    private String command;
    private String createdAt;
    private String updatedAt;
    private int status;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCommand() {
        return command;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int getStatus() {
        return status;
    }
}
