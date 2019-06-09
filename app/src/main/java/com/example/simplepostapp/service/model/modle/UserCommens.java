package com.example.simplepostapp.service.model.modle;

public class UserCommens {
    String commentOnPost;
    String comeentTimeStemp;

    public UserCommens() {
    }

    public UserCommens(String commentOnPost, String comeentTimeStemp) {
        this.commentOnPost = commentOnPost;
        this.comeentTimeStemp = comeentTimeStemp;
    }

    public String getCommentOnPost() {
        return commentOnPost;
    }

    public void setCommentOnPost(String commentOnPost) {
        this.commentOnPost = commentOnPost;
    }

    public String getComeentTimeStemp() {
        return comeentTimeStemp;
    }

    public void setComeentTimeStemp(String comeentTimeStemp) {
        this.comeentTimeStemp = comeentTimeStemp;
    }
}
