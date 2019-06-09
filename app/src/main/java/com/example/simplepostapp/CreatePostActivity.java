package com.example.simplepostapp;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;
import com.example.simplepostapp.service.model.modle.UserData;
import com.example.simplepostapp.viewmodle.PostViewModle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class CreatePostActivity extends AppCompatActivity {
     TextView mSelectImageText,mUserNameText;
     Button mPostButton;
     ImageView imagePervView;
     ImageButton mClearImagePerview,profileImage;
     EditText mCreateTextPost;
    String FilePath=null;
    PostViewModle postViewModle;
    final int FILE_SELECT=33;
    final int MY_PERMISSIONS_REQUEST_READ_File=011;
    MainActivity mainActivity ;
    List<UserData> userData;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar=findViewById(R.id.app_bar2);
        userData=new ArrayList<>();
        mSelectImageText=findViewById(R.id.create_textview);
        mUserNameText=findViewById(R.id.user_name_textview);
        mPostButton=findViewById(R.id.button_post);
        imagePervView=findViewById(R.id.image_perview);
        mClearImagePerview=findViewById(R.id.clear_image_perview);
        profileImage=findViewById(R.id.profile_imageview);
        mCreateTextPost=findViewById(R.id.create_post_editText);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mSelectImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chackSelfPermi();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        postViewModle= ViewModelProviders.of(this).get(PostViewModle.class);
        mainActivity =MainActivity.getMainActivity();
        userId=mainActivity.readFromPerferences("UserId","false");
        postViewModle.getUserData(userId).observe(this, new Observer<List<UserData>>() {
            @Override
            public void onChanged(@Nullable List<UserData> userDataa) {
                userData=userDataa;
                // Toast.makeText(MainActivity.this, ""+userDataa.get(0).getUserIconLink(), Toast.LENGTH_SHORT).show();
                try {
                    Glide.with(CreatePostActivity.this /* context */).load(userDataa.get(0).getUserIconLink()).into(profileImage);
                    mUserNameText.setText(userData.get(0).getUserName());

                }catch (Exception e){}
            }
        });

    }
    public void chackSelfPermi(){
    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("We need read external storage permission to proceed")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                        }
                    });

            builder.create();



        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_SETTINGS  },
                    MY_PERMISSIONS_REQUEST_READ_File);
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }else {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent,"Photo"),FILE_SELECT);

        }catch (android.content.ActivityNotFoundException e){
            Toast.makeText(CreatePostActivity.this, "Please Install a FileManager", Toast.LENGTH_SHORT).show();
        }

    }}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==MY_PERMISSIONS_REQUEST_READ_File){
            if( grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent,"Photo"),FILE_SELECT);

                }catch (android.content.ActivityNotFoundException e){
                    Toast.makeText(CreatePostActivity.this, "Please Install a FileManager", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Restart App And give Permission for the Process", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         switch (requestCode){
            case FILE_SELECT:
                if(resultCode==RESULT_OK){
                    Uri uriImag=data.getData();
                     FilePath =  getRealPathFromURI(this,uriImag);
                    if (FilePath != null)
                    {
                        imagePervView.setImageBitmap(BitmapFactory.decodeFile(FilePath));
                       // Toast.makeText(this, ""+FilePath, Toast.LENGTH_SHORT).show();
                        mClearImagePerview.setVisibility(View.VISIBLE);

                           // String FileName = data.getData().getLastPathSegment();
                           // int lastPos = FilePath.length() - FileName.length();
                           // String Folder = FilePath.substring(0, lastPos);
                           // path = (new File(URI.create(path))).getAbsolutePath();
                        // Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());



                    }

                }

                break;

        }

    }
    public void setClearImagePerview(View v){
        FilePath=null;
        imagePervView.setImageResource(android.R.color.transparent);
        mClearImagePerview.setVisibility(View.INVISIBLE);

    }
    public void submitPost(View v){
        String s=mCreateTextPost.getText().toString();
        Toast.makeText(this, "Post", Toast.LENGTH_SHORT).show();
        s=s.trim();
        if(!s.matches("")&&s.length()>=1){

           if(FilePath!=null){
               File file=new File(FilePath);
               compressImage(file,s);

           }
           else {
               postViewModle.insertTextPost(s,userId,userData.get(0).getUserName(),userData.get(0).getUserIconLink());
               finish();
           }

        }
        else if(FilePath!=null){
            mCreateTextPost.setError("Please Add  Image Title  ");
        }
        else {
            mCreateTextPost.setError("Post is Empty ");
        }
    }
    public void compressImage(File mActualImage, final String postTitle) {
        if (mActualImage == null) {
            //showError("Please choose an image!");
        } else {
            // Compress image using RxJava in background thread
            new Compressor(CreatePostActivity.this)
                    .compressToFileAsFlowable(mActualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {

                            postViewModle.insertNewImagePost(file,postTitle,userId, userData.get(0).getUserName(),userData.get(0).getUserIconLink());
                            Toast.makeText(CreatePostActivity.this, "ImageCompress", Toast.LENGTH_SHORT).show();
                            finish();
                            //  fileisCopressed=true;
                            // setCompressedImage();===========
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            //showError(throwable.getMessage());
                        }
                    });
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
