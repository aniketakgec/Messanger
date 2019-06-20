package com.example.android.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class UsersActivity extends AppCompatActivity {
private Toolbar mtoolbar;
private RecyclerView mUsersList;
private DatabaseReference mUsersDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mtoolbar=findViewById(R.id.users_appBar);
        setSupportActionBar(mtoolbar);
        mUsersList=findViewById(R.id.users_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>All Users</font>"));
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mUsersDatabase, Users.class)
                        .build();
        super.onStart();
       FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i, @NonNull Users users) {
                    usersViewHolder.setName(users.getName());
                    usersViewHolder.setStatus(users.getStatus());
                    usersViewHolder.setUserImage(users.getThumb_image());
                    final String user_id=getRef(i).getKey();

                    usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profileIntent=new Intent(UsersActivity.this,ProfileActivity.class);
                            profileIntent.putExtra("user_id",user_id);
                            startActivity(profileIntent);
                        }
                    });
             //  Toast.makeText(UsersActivity.this,users.getName(),Toast.LENGTH_SHORT).show();
             //  usersViewHolder.setImage(R.drawable.photo);

           }

           @NonNull
           @Override
           public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext())
                       .inflate(R.layout.users_single_layout, parent, false);

               return new UsersViewHolder(view);
           }
       };

       firebaseRecyclerAdapter.startListening();
        mUsersList.setAdapter(firebaseRecyclerAdapter);

    }

    public static  class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String name) {
            TextView userNameView=mView.findViewById(R.id.users_single_name);
            userNameView.setText(name);
        }


        public void setImage(int imageId) {
             CircleImageView userNameView=mView.findViewById(R.id.users_single_name);
            userNameView.setImageResource(imageId);
        }

        public void setStatus(String status) {
            TextView usersStatusView=mView.findViewById(R.id.users_single_status);
            usersStatusView.setText(status);
        }

        public void setUserImage(String thumb_image) {
            CircleImageView thumb_imageView=mView.findViewById(R.id.circleImageView);
            Picasso.get().load(thumb_image).placeholder(R.drawable.images).resize(250,250).into(thumb_imageView);
        }
    }


}
