package com.example.simplepostapp.viewmodle;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.simplepostapp.MainActivity;
import com.example.simplepostapp.service.model.modle.PostComm;
import com.example.simplepostapp.service.model.modle.PostLikes;
import com.example.simplepostapp.service.model.modle.PostObj;
import com.example.simplepostapp.service.model.modle.UserData;
import com.example.simplepostapp.service.model.modlerepositry.FireRepositry;

import java.io.File;
import java.util.List;

public class PostViewModle extends AndroidViewModel {
    FireRepositry mFireRepositry;
    LiveData<List<PostObj>> curentPost;
    LiveData<List<PostObj>> nextPost;
    LiveData<List<UserData>> userData;

    public PostViewModle(@NonNull Application application) {
        super(application);
        mFireRepositry=new FireRepositry(application);
       // mainActivity=MainActivity.getMainActivity();
        curentPost=mFireRepositry.getCurentPost();
      //  userData=mFireRepositry.getUserData(mainActivity.readFromPerferences("UserId","false"));

    }
    public LiveData<List<PostObj>> getCurentPost() {
        return curentPost ;
    }
    public LiveData<List<UserData>> getUserData(String userID) {
        return userData=mFireRepositry.getUserData(userID) ;
    }
    public LiveData<List<PostObj>> getNextPost(boolean isLoadMore){
        return nextPost=mFireRepositry.getNextPost(isLoadMore);
    }
    public void insertNewPost(PostObj postObj){
        mFireRepositry.insertNewPost(postObj);
    }
    public void insertTextPost(String postTitle, String userId,String userName,String userIcon ){
        mFireRepositry.upLoadTextPost( postTitle,  userId,userName,userIcon);
    }
    public void insertNewImagePost(File file, String postTitle, String userId,String userName,String userIcon ){
        mFireRepositry.upLoadImagePost(file,postTitle,userId,userName,userIcon);
    }
    public void removePostOfUser(String postId){
        mFireRepositry.removePostOfUser(postId);
    }
    public void upDateTextPost(String postId,String newText){
        mFireRepositry.upDateTextPost(postId,newText);
    }
    public void upDataPostLike(String postId,String userId,String userName,String userIconLink,List<PostLikes> likes){
        mFireRepositry.upDatePostLikes(postId,userId,userName,userIconLink,likes);

    }
    public void upDataPostComents(String postId,String userId,String userIconLink,String userName,String titel,List<PostComm> postComms){
        mFireRepositry.upDatePostComment(postId,userId,userIconLink,userName,titel,postComms);
    }
    public void insertUserData(File file, String userName, String userPassword,String key ){
       // mFireRepositry.upLoadUserData(file,userName,userPassword,key);
    }
}
