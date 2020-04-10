package com.paic.app.entity;

public class LoginWarning {
    private String userId;
    private String type;
    private String ip;

    public LoginWarning() {
    }

    public LoginWarning(String userId, String ip,  String type) {
        this.userId = userId;
        this.type = type;
        this.ip = ip;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "LoginWarning{" +
                "userId='" + userId + '\'' +
                ", ip='" + ip + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
