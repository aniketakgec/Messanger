package com.example.android.messanger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {
private String mchatUser,muserName;
private Toolbar mChatToolbar;
private DatabaseReference mRootRef;
private TextView mLastSeenView,mTitleView;
private CircleImageView mProfileImage;
private FirebaseAuth mAuth;
private String mCurrentUserId,push_id;
private MessageAdapter mAdapter;
private Button mChatAddBtn,mChatSendBtn;
private EditText mchatmessageView;
private SwipeRefreshLayout mRefreshLayout;
private RecyclerView mMessagesList;
private final List<Messages> messagesList=new ArrayList<>();
private LinearLayoutManager mLinearLayout;
private static final int TOTAL_ITEMS_TO_LOAD=5;
private static  int mCurrentPage=1;
private static  int itemPosition=0;
private String  mLastKey="";
    private String  mPrevKey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


      mChatAddBtn=findViewById(R.id.chat_add_button);
        mChatSendBtn=findViewById(R.id.chat_send_button);
        mchatmessageView=findViewById(R.id.chat_message_view);



        mChatToolbar=findViewById(R.id.chat_appBar);
        //----- custom action bar items ------------//

//

        setSupportActionBar(mChatToolbar);
        mchatUser=getIntent().getStringExtra("user_id");

        muserName=getIntent().getStringExtra("user_name");

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true) ;
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);
        mLastSeenView=action_bar_view.findViewById(R.id.custom_bar_seen);
        mTitleView= action_bar_view.findViewById(R.id.custom_bar_title);
        mProfileImage=action_bar_view.findViewById(R.id.custom_bar_image);
        mRefreshLayout=findViewById(R.id.swipe_message_swipeLayout);


        mchatUser=getIntent().getStringExtra("user_id");

        muserName=getIntent().getStringExtra("user_name");
        mTitleView.setText(muserName);




        mRootRef= FirebaseDatabase.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null)
        mCurrentUserId=mAuth.getCurrentUser().getUid();
        else
            Toast.makeText(this,"NO  ONE LOGGED IN",Toast.LENGTH_SHORT).show();
        //--------   RECYCLER VIEW CODE-----------------------------------------//
       // Toast.makeText(this,"ID WE WANT: "+mCurrentUserId,Toast.LENGTH_SHORT).show();

        mAdapter=new MessageAdapter(messagesList);
        mMessagesList=findViewById(R.id.messages_list);

        mLinearLayout=new LinearLayoutManager(this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        //---------recycler view code ends here--------------------------------//


        loadMessages();


        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendMessage();
            }

            private void sendMessage()
            {

                String message=mchatmessageView.getText().toString();
                if (!(TextUtils.isEmpty(message)))
                {
                    DatabaseReference user_message_push=mRootRef.child("messages").child(mCurrentUserId).child(mchatUser).push();
                    String currentUserRef="messages/"+mCurrentUserId+"/"+mchatUser;
                    String chat_user_ref="messages/"+mchatUser+"/"+mCurrentUserId;
                    push_id=user_message_push.getKey();
                    Map messageMap=new HashMap();
                    messageMap.put("message",message);
                    messageMap.put("time",ServerValue.TIMESTAMP);
                    messageMap.put("type","text");
                    messageMap.put("seen",false);
                    messageMap.put("from",mCurrentUserId);

                    Map messageUserMap=new HashMap();
                    messageUserMap.put(currentUserRef+"/"+push_id,messageMap);
                    messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);
                    mchatmessageView.setText("");
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

        //===============REFRESH RECYCLER VIEW=======================//
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
               // messagesList.clear();
                itemPosition=0;
                loadMoreMessages();
            }
        });

//================ HOW MUCH TIME SEEN AGO CODE============================//
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
        //================ HOW MUCH TIME SEEN AGO CODE END HERE   ============================//

//=========================CHAT DATABASE CODE=================================================//

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

        //=========================  CHAT DATABASE CODE ENDS HERE   =================================================//









    }


    private void loadMoreMessages() {


        DatabaseReference messageRef=mRootRef.child("messages").child(mCurrentUserId).child(mchatUser);

        Query messageQuery=messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                messagesList.add(itemPosition++,messages);
                String messageKey=dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey))
                {
                    messagesList.add(itemPosition++,messages);
                }
                else
                {
                    mPrevKey=mLastKey;
                }

                if (itemPosition==1)
                {

                    mLastKey=messageKey;
                }




                mAdapter.notifyDataSetChanged();
                mLinearLayout.scrollToPositionWithOffset(itemPosition,0);
                mRefreshLayout.setRefreshing(false);

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
        });

    }

    private void loadMessages() {
        DatabaseReference messageRef=mRootRef.child("messages").child(mCurrentUserId).child(mchatUser);

        Query messageQuery=messageRef.limitToLast(mCurrentPage*TOTAL_ITEMS_TO_LOAD);


       messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                String messageKey=dataSnapshot.getKey();



                if (itemPosition==1)
                {

                    mLastKey=messageKey;
                    mPrevKey=messageKey;
                }

                messagesList.add(messages);
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messagesList.size()-1);
                mRefreshLayout.setRefreshing(false);
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
        });
    }


}
