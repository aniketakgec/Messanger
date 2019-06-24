package com.example.android.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {
private String mchatUser,muserName;
private Toolbar mChatToolbar;
private DatabaseReference mRootRef;
private TextView mLastSeenView,mTitleView;
private CircleImageView mProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mchatUser=getIntent().getStringExtra("user_id");
        muserName=getIntent().getStringExtra("user_name");
        mChatToolbar=findViewById(R.id.chat_appBar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true) ;
        actionBar.setDisplayShowCustomEnabled(true);
       // getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+mchatUser +"</font>"));

LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
View action_bar_view=inflater.inflate(R.layout.chat_custom_bar,null);
actionBar.setCustomView(action_bar_view);
        mRootRef= FirebaseDatabase.getInstance().getReference();

        //----- custom action bar items ------------//
        mLastSeenView=findViewById(R.id.custom_bar_seen);
        mTitleView=findViewById(R.id.custom_bar_title);
        mProfileImage=findViewById(R.id.custom_bar_image);
        mTitleView.setText(muserName);
        mRootRef.child("Users").child(mchatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online=dataSnapshot.child("online").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                if (online.equals("true"))
                {
                    mLastSeenView.setText("online");
                }
                else {
                    mLastSeenView.setText(online);
                }
                Picasso.get().load(image).resize(45,45).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.images).into(   mProfileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
