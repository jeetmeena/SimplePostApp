package com.example.simplepostapp.service.model.modlerepositry;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.simplepostapp.MainActivity;
import com.example.simplepostapp.service.model.modle.PostComm;
import com.example.simplepostapp.service.model.modle.PostLikes;
import com.example.simplepostapp.service.model.modle.PostObj;
import com.example.simplepostapp.service.model.modle.UserCommens;
import com.example.simplepostapp.service.model.modle.UserData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;



public class FireRepositry {
    DatabaseReference mDatabaseRef;
    DatabaseReference mUserDatabaseRef;
    private FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference riversRef;
    UploadTask    mUploadTask;
    String lastGetPostKey;
    private MutableLiveData<List<PostObj>> mPostData;
    private MutableLiveData<List<PostObj>> mPostNextDate;
    private MutableLiveData<List<UserData>> mUserData;
    boolean islodContun=false;
    MainActivity mainActivity;
    public FireRepositry(Application application) {
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Post");
        mUserDatabaseRef= FirebaseDatabase.getInstance().getReference("Users");
        mStorage= FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mUserData=new MutableLiveData<List<UserData>>();
        mPostData=new MutableLiveData<List<PostObj>>();
        mPostNextDate=new MutableLiveData<List<PostObj>>();
        mainActivity=MainActivity.getMainActivity();
    }
    public LiveData<List<PostObj>> getCurentPost() {
       final List<PostObj> postObjs=new ArrayList<>();
        Query myTopPostsQuery = mDatabaseRef
                .orderByChild("last_time_stamp").limitToLast(5);
         ChildEventListener childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                PostObj postObj=dataSnapshot.getValue(PostObj.class );
                postObjs.add(postObj);
                Log.e("Tag","GetPostFromVM"+postObjs.get(0).getPostTitle());

                if(postObjs.size()==5){

                    lastGetPostKey=postObjs.get(0).getLast_time_stamp();
                    //islodmore=true;
                    Collections.reverse(postObjs);
                    postObjs.remove(postObjs.size()-1);
                    mPostData.setValue(postObjs);
                    islodContun=true;
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myTopPostsQuery.addChildEventListener(childEventListener);

        return mPostData;
    }
    public LiveData<List<PostObj>> getNextPost(boolean islodmore) {
     DatabaseReference   mDatabaseRef= FirebaseDatabase.getInstance().getReference("Post");

        final List<PostObj> nextPostostObjs=new ArrayList<>();
        if(islodmore && islodContun){
            Query myTopPostsQuery = mDatabaseRef.orderByChild("last_time_stamp").endAt(lastGetPostKey).limitToLast(5);
            myTopPostsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                  //  postObjs.clear();
                   // int i=0;
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        PostObj postObj=postSnapshot.getValue(PostObj.class );
                        nextPostostObjs.add(postObj);
                      //  Log.e("Tag","GetNextPostFromVM"+nextPostostObjs.get(i++).getPostTitle());
                        //mPostData.setValue(postObjs);

                    }


                    if(nextPostostObjs.size()==5){

                        lastGetPostKey=nextPostostObjs.get(0).getLast_time_stamp();
                        Collections.reverse(nextPostostObjs);
                        nextPostostObjs.remove(nextPostostObjs.size()-1);
                        mPostNextDate.setValue(nextPostostObjs);
                    }
                    else if(nextPostostObjs.size()>=1){
                        lastGetPostKey=nextPostostObjs.get(0).getLast_time_stamp();
                        islodContun=false;
                        Collections.reverse(nextPostostObjs);
                         mPostNextDate.setValue(nextPostostObjs);

                    }

                    Log.e("Tag","GetNextPostFromVM");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("Tag", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            });

        }

        return mPostNextDate;
    }

    public void removePostOfUser(String postId){
        mDatabaseRef.child(postId).removeValue();
    }
    public void upDatePostComment(String postId,String userId,String userIconLink,String userName,String titel,List<PostComm> postComms){
        List<PostComm> postComent1=new ArrayList<>();
        List<UserCommens> userCommens =new ArrayList<>();
        if (postComms==null){
            UserCommens userCommens1=new UserCommens(titel,getCurentTimeStamp());
            userCommens.add(userCommens1);
            PostComm postComm=new PostComm(userCommens,userId,userIconLink,userName);
            postComent1.add(postComm);
            mDatabaseRef.child(postId).child("listPostComm").setValue(postComent1);
        }
        else {
            for (int i=0;i<postComms.size();i++){
            if(postComms.get(i).getUserId().equals(userId)){
                userCommens=postComms.get(i).getComment();
                UserCommens userCommens1=new UserCommens(titel,getCurentTimeStamp());
                userCommens.add(userCommens1);
                PostComm postComm=new PostComm(userCommens,userId,userIconLink,userName);
                postComms.add(i,postComm);
                mDatabaseRef.child(postId).child("listPostComm").setValue(postComms);
                break;
            }
            else if(!postComms.get(i).getUserId().equals(userId)){

                UserCommens userCommens1=new UserCommens(titel,getCurentTimeStamp());
                userCommens.add(userCommens1);
                PostComm postComm=new PostComm(userCommens,userId,userIconLink,userName);
            }
        }}





    }
    public void upDatePostLikes(String postId,String userId,String userName,String userIconLink,List<PostLikes> likes){
        List<PostLikes> l=new ArrayList<>();
        if(likes==null){
            PostLikes postLikes=new PostLikes(userId,userIconLink,userName);
            l.add(postLikes);
            mDatabaseRef.child(postId).child("postLikes").setValue(l);
            Log.e("Tag","liketonull");

        }else {
            for (int i=0;i<likes.size();i++){

                if(!likes.get(i).getUserId().equals(userId) &&likes.size()-1==i){
                    l=likes;
                    PostLikes postLikes=new PostLikes(userId,userIconLink,userName);
                    l.add(postLikes);
                    mDatabaseRef.child(postId).child("postLikes").setValue(l);
                    Log.e("Tag","newLike");

                    break;

                }
                else if(likes.get(i).getUserId().equals(userId) ){
                    l=likes;
                    l.remove(i);
                    mDatabaseRef.child(postId).child("postLikes").setValue(l);
                    Log.e("Tag","Removelike");

                    break;
                }

            }
        }


    }
    public void upDateTextPost(String postId,String newTitle){
            mDatabaseRef.child(postId).child("postTitle").setValue(newTitle);
    }

    public LiveData<List<UserData>> getUserData(String userId) {
        final List<UserData> userData1=new ArrayList<>();
         mUserDatabaseRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    UserData userData=dataSnapshot.getValue(UserData.class);
                    userData1.add(userData);
                    mUserData.setValue(userData1);
                }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

        return mUserData;
    }


    public void insertNewPost(PostObj postObj){
         new insertAsyncTask().execute(postObj);
    }

    private class insertAsyncTask extends AsyncTask< PostObj,Void,Void> {




        @Override
        protected Void doInBackground(PostObj... words) {

          String key=  mDatabaseRef.push().getKey();

          mDatabaseRef.child(key).setValue(words[0]);
            return null;
        }
    }
    private class upLoadImAgeAsyncTask extends AsyncTask< PostObj,Void,Void> {




        @Override
        protected Void doInBackground(PostObj... words) {


            return null;
        }
    }

    //Upload Visitor Compressed image to FireBase Store
    public void upLoadImagePost(final File mCompressedImage, final String postTitle, final String userId, final String userName, final String iconLink){
        //mStorageRef = FirebaseStorage.getInstance().getReference();
        final Uri file = Uri.fromFile(mCompressedImage);
        riversRef = mStorageRef.child("images/"+file.getLastPathSegment());
        mUploadTask = riversRef.putFile(file);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                //  Toast.makeText(MainActivity.this, "uploadImageFailed"+exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Tag","ImageUpload");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                //
                // Toast.makeText(MainActivity.this, "upLoadImageSuccess//", Toast.LENGTH_SHORT).show();


            }
        });
        final StorageReference ref = mStorageRef.child("images/"+file.getLastPathSegment());
        // Get Uri of uploaded Image
        Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mImageUri=task.getResult().toString();
                    if(mImageUri!=null){
                        //Toast.makeText(MainActivity.this, "getUri", Toast.LENGTH_SHORT).show();

                        List<PostComm> postComms=new ArrayList<>();
                        List<PostLikes> postLikes1 =new ArrayList<>();
                        PostComm postComm=new PostComm(null,null,null,null);
                        PostLikes postLikes =new PostLikes(null,null,null) ;
                        postComms.add(postComm);
                        postLikes1.add(postLikes);
                        Log.e("Tag","ImagePost");
                        String key=  mDatabaseRef.push().getKey();
                       // int listPostLike, List<PostComm> listPostComm, String imageLink, String postTitle, String postId, String userId, String userName, String iconLink, String last_time_stamp
                        PostObj postObj=new PostObj(0,postComms,postLikes1,mImageUri,postTitle,key,userId,userName,iconLink,getCurentTimeStamp());


                        mDatabaseRef.child(key).setValue(postObj);
                            //getimageUri(file.getLastPathSegment());
                          //  mCompressedImage.delete();



                    }

                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }
    public void upLoadTextPost(String postTitle, String userId,  String userName,  String iconLink ){

        List<PostComm> postComms1=new ArrayList<>();
        List<PostLikes> postLikes1 =new ArrayList<>();
        PostComm postComm=new PostComm(null,null,null,null);
        PostLikes postLikes =new PostLikes(null,null,null) ;
        postComms1.add(postComm);
        postLikes1.add(postLikes);
        String key=  mDatabaseRef.push().getKey();
        PostObj postObj=new PostObj(0,postComms1,postLikes1,"null",postTitle,key,userId,userName,iconLink,getCurentTimeStamp());

        Log.e("Tag","TextPost");
        mDatabaseRef.child(key).setValue(postObj);
    }

    private String getCurentTimeStamp() {
        Date date= new Date();

        long time = date.getTime();
        System.out.println("Time in Milliseconds: " + time);

        Timestamp ts = new Timestamp(time);
        return String.valueOf(ts);
    }

}
