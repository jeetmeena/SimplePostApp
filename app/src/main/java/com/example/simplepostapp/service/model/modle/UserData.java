package com.example.simplepostapp.service.model.modle;

public class UserData {
    String userName;
    String userPassword;
    String userIconLink;

    public UserData() {
    }

    public UserData(String userName, String userPassword, String userIconLink) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userIconLink = userIconLink;
    }

    public String getUserIconLink() {
        return userIconLink;
    }

    public void setUserIconLink(String userIconLink) {
        this.userIconLink = userIconLink;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
