package com.example.simplepostapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simplepostapp.service.model.modle.PostComm;
import com.example.simplepostapp.service.model.modle.PostObj;
import com.example.simplepostapp.service.model.modle.UserData;
import com.example.simplepostapp.viewmodle.PostViewModle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity  {
    RecyclerView mPostRecyclerView;
     LinearLayoutManager layoutManager;
    EditText mPostEditText;
    ImageButton mUserIcon;
    public  static String PREF_FILE_NAME="signFirst";
    static MainActivity  mainActivity;
    DatabaseReference mDatabaseRef;
    PostViewModle postViewModle;
    List<UserData> userData;
    List<PostObj> postObjs;
    DividerItemDecoration dividerItemDecoration;
    boolean isItemLoad =false;
    int count=0;
    String[] mDataSet= new String[450];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPostEditText=findViewById(R.id.post_edit_text);
        mUserIcon=findViewById(R.id.user_icon);
        Toolbar toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mainActivity=this;
        postObjs=new ArrayList<>();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Users");
       if(!readFromPerferences("Login","false").equals("true")){
           Intent intent=new Intent(MainActivity.this,LoginActivity.class);
           startActivity(intent);
           finish();
       }
        mPostEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,CreatePostActivity.class);
                startActivity(intent);

            }
        });
       postViewModle= ViewModelProviders.of(this).get(PostViewModle.class);
        Drawable dvider= ContextCompat.getDrawable(this,R.drawable.divider);
        dividerItemDecoration=new DividerItemDecoration(this ,VERTICAL);
        dividerItemDecoration.setDrawable(dvider);
        mPostRecyclerView=findViewById(R.id.recyclerView_post);
        layoutManager=new LinearLayoutManager(this);
        mPostRecyclerView.setLayoutManager(layoutManager);
        mPostRecyclerView.addItemDecoration(dividerItemDecoration);

        final PostAdpter postAdpter=new PostAdpter();
        mPostRecyclerView.setAdapter(postAdpter);
        mPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibalItemCount=layoutManager.getChildCount();
                int totelItemCount=layoutManager.getItemCount();
                int firstVisibalItemPostion=layoutManager.findFirstVisibleItemPosition();
                //Toast.makeText(MainActivity.this, "is Scrolled", Toast.LENGTH_SHORT).show();

                if ((visibalItemCount + firstVisibalItemPostion) >= totelItemCount
                        && firstVisibalItemPostion >= 0 &&!isItemLoad&&count>=2) {
                  //  setObserverForNextPost();
                    isItemLoad=true;
                    postViewModle.getNextPost(true);
                    Handler handler =new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isItemLoad=false;
                        }
                    },3000);


                }
                if(count<2){
                    count++;
                }


            }
        });
        postViewModle.getUserData(readFromPerferences("UserId","false")).observe(this, new Observer<List<UserData>>() {
            @Override
            public void onChanged(@Nullable List<UserData> userDataa) {
                userData=userDataa;
               // Toast.makeText(MainActivity.this, ""+userDataa.get(0).getUserIconLink(), Toast.LENGTH_SHORT).show();
               try {
                   Glide.with(MainActivity.this /* context */).load(userDataa.get(0).getUserIconLink()).into(mUserIcon);

               }catch (Exception e){}
            }
        });
        postViewModle.getCurentPost().observe(this, new Observer<List<PostObj>>() {
            @Override
            public void onChanged(@Nullable List<PostObj> postObjs) {

                //Toast.makeText(MainActivity.this, ""+postObjs.size(), Toast.LENGTH_SHORT).show();
                   postAdpter.getUpDateList(postObjs);
                  // isLoadMore=true;

            }
        });

        postViewModle.getNextPost(false).observe(this, new Observer<List<PostObj>>() {
            @Override
            public void onChanged(@Nullable List<PostObj> postObjs) {


                if(postObjs!=null ){
                    for(int i=0;i<postObjs.size();i++){
                         postAdpter.addItemToPostAdp(postObjs.get(i));
                        //Toast.makeText(MainActivity.this, "jgvhjk"+postObjs.get(i).getPostTitle(), Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }

   public void setObserverForNextPost(){

   }



    class PostAdpter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.recyclerview_item,null);
            ViewHolder viewHolder=new ViewHolder(view,new GetEditTextText());
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            try{
                Glide.with(MainActivity.this /* context */).load(userData.get(0).getUserIconLink()).into(viewHolder.userIcon2);
                Glide.with(MainActivity.this /* context */).load(postObjs.get(i).getIconLink()).into(viewHolder.userIcon1);
                if(postObjs.get(i).getImageLink()!=null){
                   // viewHolder.imagePostitem.setVisibility(View.VISIBLE);
                    Glide.with(MainActivity.this /* context */).load(postObjs.get(i).getImageLink()).into(viewHolder.imagePostitem);

                }
                else {
                  //  viewHolder.imagePostitem.setVisibility(View.GONE);
                }


            }catch (Exception e){}
            if(postObjs.get(i).getPostLikes()==null){

                viewHolder.nubmerOfLike.setText("Be the first one to like");
            }
            else {
                viewHolder.nubmerOfLike.setText(String.valueOf(postObjs.get(i).getPostLikes().size()));
                // Toast.makeText(MainActivity.this, "ReclerUp"+, Toast.LENGTH_SHORT).show();

            }

            viewHolder.userNameitem.setText(postObjs.get(i).getUserName());
            viewHolder.postTitleItem.setText(postObjs.get(i).getPostTitle());
            viewHolder.postDate.setText(postObjs.get(i).getLast_time_stamp());
            viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            PostCommentItemAdpter postCommentItemAdpter=new PostCommentItemAdpter();
            viewHolder.recyclerView.setAdapter(postCommentItemAdpter);

            //viewHolder.recyclerView.addItemDecoration(dividerItemDecoration);
            postCommentItemAdpter.setPostComms(postObjs.get(i).getListPostComm());
            viewHolder.getEditTextText.updatePosition(viewHolder.getAdapterPosition());

        }
        void getUpDateList(List<PostObj> postObjsa){
             postObjs=postObjsa;
             notifyDataSetChanged();
           // Toast.makeText(MainActivity.this, "ReclerUp", Toast.LENGTH_SHORT).show();

        }
        void addItemToPostAdp(PostObj postObj){
            postObjs.add(postObj);
            notifyItemInserted(postObjs.size()-1);
        }
        @Override
        public int getItemCount() {
            if(postObjs!=null) return postObjs.size();
            return 0;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,PopupMenu.OnMenuItemClickListener{
        RecyclerView recyclerView;
        ImageView imagePostitem;
        ImageView userIcon1,userIcon2;
        ImageButton actionImage,likeImage,sendComeent;
        TextView userNameitem,postDate,postTitleItem,nubmerOfLike;
        EditText postComment;
        GetEditTextText getEditTextText;

        public ViewHolder(@NonNull View itemView, GetEditTextText getEditTextText) {

            super(itemView);
            recyclerView=itemView.findViewById(R.id.recyclerview_r_item);
            imagePostitem=itemView.findViewById(R.id.image_post_r_item);
            userIcon1=itemView.findViewById(R.id.user_icon1_r_item);
            userIcon2=itemView.findViewById(R.id.user_icon2_r_item);
            userNameitem=itemView.findViewById(R.id.user_name_r_item);
            postDate=itemView.findViewById(R.id.date_textview_r_item);
            postTitleItem=itemView.findViewById(R.id.post_title_r_item);
            nubmerOfLike=itemView.findViewById(R.id.like_count_r_item);
            actionImage=itemView.findViewById(R.id.action_r_item);
            actionImage.setTag(R.id.action_r_item,itemView);
            postComment=itemView.findViewById(R.id.edit_comment_r_item);
            actionImage.setOnClickListener(this);
            likeImage=itemView.findViewById(R.id.like_image_r_item);
            likeImage.setTag(R.id.like_image_r_item,itemView);
            likeImage.setOnClickListener(this);
            sendComeent=itemView.findViewById(R.id.send_comment_r_item);
            sendComeent.setTag(R.id.send_comment_r_item);
            sendComeent.setOnClickListener(this);
            this.getEditTextText=getEditTextText;
            postComment.addTextChangedListener(getEditTextText);



        }

        @Override
        public void onClick(final View v) {
            final int pos=getAdapterPosition();
            if(v.getId()==actionImage.getId()){

                //Toast.makeText(MainActivity.this, "Action On Post", Toast.LENGTH_SHORT).show();
                if(postObjs.get(pos).getUserId().equals(readFromPerferences("UserId","false"))){
                    showMenu(v);

                }
                else {
                    Toast.makeText(MainActivity.this, "You can update/remove Only your post", Toast.LENGTH_SHORT).show();
                }

            }
            else if(v.getId()==likeImage.getId()){
                if(postObjs.get(pos).getPostLikes()!=null){
                    postViewModle.upDataPostLike(postObjs.get(pos).getPostId(),readFromPerferences("UserId","false"),userData.get(0).getUserName(),userData.get(0).getUserIconLink(),postObjs.get(pos).getPostLikes());

                }else {
                    postViewModle.upDataPostLike(postObjs.get(pos).getPostId(),readFromPerferences("UserId","false"),userData.get(0).getUserName(),userData.get(0).getUserIconLink(),null);
                }

            }
            else  if(v.getId()==sendComeent.getId()){

            }


        }
        public void showMenu(View v) {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);

            // This activity implements OnMenuItemClickListener
            popup.setOnMenuItemClickListener(MainActivity.ViewHolder.this);
            popup.inflate(R.menu.action);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            final int pos=getAdapterPosition();
            switch (menuItem.getItemId()) {
                case R.id.remove_post:
                    //archive(item);
                    postViewModle.removePostOfUser(postObjs.get(pos).getPostId());
                    postObjs.remove(pos);
                    Toast.makeText(MainActivity.this, "Action On Post"+pos, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.edit_post:
                    final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    final View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_update_text,null);
                    final EditText editText=view.findViewById(R.id.edit_text_dialog);
                    editText.setText(postObjs.get(pos).getPostTitle());
                    builder.setView(view)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String s=editText.getText().toString();
                                    s=s.trim();
                                    if(!s.equals("")){
                                        postViewModle.upDateTextPost(postObjs.get(pos).getPostId(),s);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    builder.show();
                   // delete(item);
                    Toast.makeText(MainActivity.this, "Action On Post"+pos, Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
        }
    }
    public class GetEditTextText implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           mDataSet[position]=s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public void saveToPerferences(String perferencesName, String perferencesValue){
        SharedPreferences sharedPreferences =this.getSharedPreferences(PREF_FILE_NAME,this.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putString(perferencesName,perferencesValue);
        editor.apply();
        Toast toast1 = Toast.makeText(this, "hii Test", Toast.LENGTH_SHORT);
        toast1.show();

    }

    public String readFromPerferences(String perferencesName, String defaultValue) {

        SharedPreferences sharedPreferences = this.getSharedPreferences(PREF_FILE_NAME, this.MODE_PRIVATE);
        return sharedPreferences.getString(perferencesName,defaultValue);
    }

    private class PostCommentItemAdpter extends RecyclerView.Adapter<CommViewHolder>{
        List<PostComm> postComms;
        @NonNull
        @Override
        public CommViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.comment_recyc_item,null);
            CommViewHolder commViewHolder=new CommViewHolder(view);
            return commViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CommViewHolder commViewHolder, int i) {
              commViewHolder.showComentText.setText(postComms.get(i).getComment().get(0).getCommentOnPost());
        }
        void setPostComms(List<PostComm> postComms){
            this.postComms=postComms;
            notifyDataSetChanged();
        }
        @Override
        public int getItemCount() {
            if (postComms!=null)return postComms.size();
            return 0;
        }
    }

    private class CommViewHolder extends RecyclerView.ViewHolder {
        TextView showComentText;
        public CommViewHolder(@NonNull View itemView) {
            super(itemView);
            showComentText=itemView.findViewById(R.id.text_comment);
        }
    }
}
