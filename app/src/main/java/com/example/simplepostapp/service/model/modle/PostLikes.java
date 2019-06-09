package com.example.simplepostapp.service.model.modle;

public class PostLikes {

    String userId;
    String userIcon;
    String userName;

    public PostLikes() {
    }

    public PostLikes(String userId, String userIcon, String userName) {
        this.userId = userId;
        this.userIcon = userIcon;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
