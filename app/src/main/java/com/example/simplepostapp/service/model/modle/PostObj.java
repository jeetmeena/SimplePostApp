package com.example.simplepostapp.service.model.modle;

import java.util.List;

public class PostObj {
    int listPostLike;
    List<PostComm> listPostComm;
    List<PostLikes> postLikes;
    String imageLink;
    String postTitle;
    String postId;
    String userId;
    String userName;
    String iconLink;
    String last_time_stamp;


    PostObj() {
    }

    public PostObj(int listPostLike, List<PostComm> listPostComm, List<PostLikes> postLikes, String imageLink, String postTitle, String postId, String userId, String userName, String iconLink, String last_time_stamp) {
        this.listPostLike = listPostLike;
        this.listPostComm = listPostComm;
        this.postLikes = postLikes;
        this.imageLink = imageLink;
        this.postTitle = postTitle;
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.iconLink = iconLink;
        this.last_time_stamp = last_time_stamp;
    }

    public List<PostLikes> getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(List<PostLikes> postLikes) {
        this.postLikes = postLikes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIconLink() {
        return iconLink;
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    public int getListPostLike() {
        return listPostLike;
    }

    public void setListPostLike(int listPostLike) {
        this.listPostLike = listPostLike;
    }

    public String getLast_time_stamp() {
        return last_time_stamp;
    }

    public void setLast_time_stamp(String last_time_stamp) {
        this.last_time_stamp = last_time_stamp;
    }



    public List<PostComm> getListPostComm() {
        return listPostComm;
    }

    public void setListPostComm(List<PostComm> listPostComm) {
        this.listPostComm = listPostComm;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
