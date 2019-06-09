package com.example.simplepostapp.service.model.modle;

import java.util.List;

public class PostComm {
    List<UserCommens> comment;
    String userId;
    String userIcon;
    String userName;
    public PostComm() {
    }

    public PostComm(List<UserCommens> comment, String userId, String userIcon, String userName) {
        this.comment = comment;
        this.userId = userId;
        this.userIcon = userIcon;
        this.userName = userName;
    }

    public List<UserCommens> getComment() {
        return comment;
    }

    public void setComment(List<UserCommens> comment) {
        this.comment = comment;
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
