package com.example.simplepostapp;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.simplepostapp.service.model.modle.PostComm;
import com.example.simplepostapp.service.model.modle.UserData;
import com.example.simplepostapp.viewmodle.PostViewModle;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    StorageReference mStorageRef;
    StorageReference riversRef;
    UploadTask    mUploadTask;
    EditText userName,userPassword;
    ImageView imageUserIcon;
    ImageButton imageSelect;
    Button signUp;
    String FilePath=null;
    final int FILE_SELECT=33;
    final int MY_PERMISSIONS_REQUEST_READ_File=011;
    PostViewModle postViewModle;
    MainActivity mainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName=findViewById(R.id.user_name);
        userPassword=findViewById(R.id.user_password);
        imageUserIcon=findViewById(R.id.user_icon_login);
        imageSelect=findViewById(R.id.icon_select_login);
        signUp=findViewById(R.id.signUp);
        mStorage= FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
          mainActivity=MainActivity.getMainActivity();
        postViewModle= ViewModelProviders.of(this).get(PostViewModle.class);
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Users");
        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chackSelfPermi();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=userName.getText().toString();
                String password=userPassword.getText().toString();
                name=name.trim();
                password=password.trim();
                if(name.length()<1){
                    userName.setError("User Name Empty");
                }
                if(password.length()<6){
                    userPassword.setError("Password Minmum Character 7");

                }
                if(name.length()>=1 &&password.length()>=6&&FilePath==null){
                    String key= mDatabaseRef.push().getKey();
                    UserData userData=new UserData(name,password,null);
                    mDatabaseRef.child(key).setValue(userData);

                    mainActivity.saveToPerferences("Login","true");
                    mainActivity.saveToPerferences("UserId",key);
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }
                else if(name.length()>=1 &&password.length()>=6&&FilePath!=null) {
                    File file=new File(FilePath);
                    String key= mDatabaseRef.push().getKey();

                   compressImage(file,name,password,key);
                }
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
                Toast.makeText(LoginActivity.this, "Please Install a FileManager", Toast.LENGTH_SHORT).show();
            }

        }}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==MY_PERMISSIONS_REQUEST_READ_File){
            if( grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent,"Photo"),FILE_SELECT);

                }catch (android.content.ActivityNotFoundException e){
                    Toast.makeText(LoginActivity.this, "Please Install a FileManager", Toast.LENGTH_SHORT).show();
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
                        Bitmap b=BitmapFactory.decodeFile(FilePath);
                        imageUserIcon.setImageBitmap(Bitmap.createScaledBitmap(b,120,120,false));
                        // Toast.makeText(this, ""+FilePath, Toast.LENGTH_SHORT).show();


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

    public void compressImage(File mActualImage, final String userName, final String pasword,final String key) {
        if (mActualImage == null) {
            //showError("Please choose an image!");
        } else {
            // Compress image using RxJava in background thread
            new Compressor(LoginActivity.this)
                    .compressToFileAsFlowable(mActualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                           // postViewModle.insertUserData(file,userName,pasword,key);
                            upLoadUserData(file,userName,pasword,key);
                            Toast.makeText(LoginActivity.this, "ImageCompress", Toast.LENGTH_SHORT).show();

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

    public void upLoadUserData(final File mCompressedImage,final String userName, final String userPassword,final String key){
        //mStorageRef = FirebaseStorage.getInstance().getReference();
        final Uri file = Uri.fromFile(mCompressedImage);
        riversRef = mStorageRef.child("userIcon/"+file.getLastPathSegment());
        mUploadTask = riversRef.putFile(file);
        final StorageReference ref = mStorageRef.child("userIcon/"+file.getLastPathSegment());
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


                        UserData userData=new UserData(userName,userPassword,mImageUri);

                        mDatabaseRef.child(key).setValue(userData);
                        mainActivity.saveToPerferences("Login","true");
                        mainActivity.saveToPerferences("UserId",key);
                        //getimageUri(file.getLastPathSegment());
                        //  mCompressedImage.delete();
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();


                    }

                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }


}
