package com.example.android.messanger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;/////


// video:28  13:46
public class ChatActivity extends AppCompatActivity {
private String mchatUser,muserName;
private Toolbar mChatToolbar;
private DatabaseReference mRootRef;
private TextView mLastSeenView,mTitleView;
private CircleImageView mProfileImage;
private FirebaseAuth mAuth;
private String mCurrentUserId;
private Button mChatAddBtn,mChatSendBtn;
private EditText mchatmessageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mchatUser=getIntent().getStringExtra("user_id");
        muserName=getIntent().getStringExtra("user_name");
        mChatToolbar=findViewById(R.id.chat_appBar);
        setSupportActionBar(mChatToolbar);
        mAuth= FirebaseAuth.getInstance();
        mCurrentUserId=mAuth.getCurrentUser().getUid();
        mChatAddBtn=findViewById(R.id.chat_add_button);
        mChatSendBtn=findViewById(R.id.chat_send_button);
        mchatmessageView=findViewById(R.id.chat_message_view);



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
                    GetTimeAgo getimeAgo=new GetTimeAgo();
                    long LastTime=Long.parseLong(online);
                    String lastSeenTime=getimeAgo.getTimeAgo(LastTime,getApplicationContext());
                    mLastSeenView.setText(lastSeenTime);
                }
                Picasso.get().load(image).resize(45,45).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.images).into(   mProfileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mchatUser))
                {}
                else
                {
                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map chatUserMap=new HashMap();
                    chatUserMap.put("Chat/"+mCurrentUserId+"/"+mchatUser,chatAddMap);
                    chatUserMap.put("Chat/"+mchatUser+"/"+mCurrentUserId,chatAddMap);
                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError!=null)
                            {
                                Log.d("Chat Log",databaseError.getMessage());
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // send message

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }

            private void sendMessage() {

                String message=mchatmessageView.getText().toString();
                if (!(TextUtils.isEmpty(message)))
                {
                    DatabaseReference user_message_push=mRootRef.child("messages").child(mCurrentUserId).child(mchatUser).push();
                    String currentUserRef="messages/"+mCurrentUserId+"/"+mchatUser;
                    String chat_user_ref="messages/"+mchatUser+"/"+mCurrentUserId;
                    String push_id=user_message_push.getKey();
                    Map messageMap=new HashMap();
                    messageMap.put("message",message);
                    messageMap.put("seen","false");
                    messageMap.put("type","text");
                    messageMap.put("time",ServerValue.TIMESTAMP);

                    Map messageUserMap=new HashMap();
                    messageUserMap.put(currentUserRef+"/"+push_id,messageMap);
                    messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);
                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError!=null)
                                Log.d("CHAT_LOG",databaseError.getMessage());
                        }
                    });
                }
            }
        });

    }
}
