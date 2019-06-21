package com.example.android.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private TextView mProfileName,mProfileStatus,mProfileFriendCount;
    private ImageView mProfileImage;
    private Button mProfileSendRequest,declineRrequestBtn;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mProgressDialog;
    private String current_state;
    private DatabaseReference mFriendRequestDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //textView=findViewById(R.id.profileDisplayName);
       final String user_id= getIntent().getStringExtra("user_id");
       //textView.setText(user_id);
        mProfileImage=findViewById(R.id.profile_image);
        mProfileFriendCount=findViewById(R.id.friends_count);
        mProfileName=findViewById(R.id.profile_name);
        mProfileStatus=findViewById(R.id.profile_status);
        declineRrequestBtn=findViewById(R.id.decline_request_btn);
        current_state="not_friends";
        mProgressDialog=new ProgressDialog(this);
        mProfileSendRequest=findViewById(R.id.friend_request_btn);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the Data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("notifications");
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 String display_name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.images).into(mProfileImage);

                // -----------------Friend List/Request feature-------------->




                mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mProgressDialog.dismiss();
                        if (dataSnapshot.hasChild(user_id))
                        {
                            String request_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (request_type.equals("received"))
                            {
                                //mProfileSendRequest.setEnabled(true);
                                current_state="request_received";
                                mProfileSendRequest.setText("Accept Friend Request");
                                declineRrequestBtn.setVisibility(View.VISIBLE);
                                declineRrequestBtn.setEnabled(true);

                            }
                            else if(request_type.equals("sent"))
                            {
                                current_state="request_sent";
                                mProfileSendRequest.setText("Cancel Friend Request");
                                declineRrequestBtn.setVisibility(View.INVISIBLE);
                                declineRrequestBtn.setEnabled(false);
                            }
                          //  mProgressDialog.dismiss();
                           else if (request_type.equals("friends"))
                            {
                                //current_state="friends";
                               // mProfileSendRequest.setText("UnFriend this person");
                                mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(user_id))
                                        {   mProgressDialog.dismiss();
                                            current_state="friends";
                                             mProfileSendRequest.setText("UnFriend this person");
                                            declineRrequestBtn.setVisibility(View.INVISIBLE);
                                            declineRrequestBtn.setEnabled(false);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        mProgressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


               // mProgressDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mProfileSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileSendRequest.setEnabled(false);

                // -------------------NOT FRIENDS STATE--------------------->
                if (current_state.equals("not_friends"))
                {
                    mProfileSendRequest.setBackgroundColor(Color.TRANSPARENT);

                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                HashMap<String,String> notificationData=new HashMap<>();
                                notificationData.put("from",mCurrentUser.getUid());
                                notificationData.put("type","request");
                                mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProfileSendRequest.setEnabled(true);
                                        current_state="request_sent";
                                        mProfileSendRequest.setText("Cancel Friend Request");
                                        declineRrequestBtn.setVisibility(View.INVISIBLE);
                                        declineRrequestBtn.setEnabled(false);
                                        mProfileSendRequest.setBackgroundResource(R.drawable.friendr_equest_button);

                                    }
                                });




                                mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileActivity.this,"Request Sent ! yeah :)",Toast.LENGTH_SHORT).show();

                                    }
                                });


                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this,"Fail Sending Request",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


                // --------------Cancel request state ------------------>
                if (current_state.equals("request_sent"))
                {
                     mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid)
                         {
                             mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {
                                     mProfileSendRequest.setEnabled(true);
                                     current_state="request_sent";
                                     mProfileSendRequest.setText("Sent Friend Request");
                                     declineRrequestBtn.setVisibility(View.INVISIBLE);
                                     declineRrequestBtn.setEnabled(false);

                                 }
                             });
                         }
                     });
                }


                //-- Friend Request Accept State----------------------------->
                if (current_state.equals("request_received"))
                {
                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid)
                                        {
                                            mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProfileSendRequest.setEnabled(true);
                                                    current_state="friends";
                                                    mProfileSendRequest.setText("UnFriend this Person");
                                                    declineRrequestBtn.setVisibility(View.INVISIBLE);
                                                    declineRrequestBtn.setEnabled(false);

                                                }
                                            });
                                        }
                                    });


                                }
                            });

                        }
                    });


                }


                if (current_state.equals("friends"))
                {

                    mFriendDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            mFriendDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendRequest.setEnabled(true);
                                    current_state="not_friends";
                                    mProfileSendRequest.setText("Sent Friend Request");

                                }
                            });
                        }
                    });
                }

            }
        });



    }
}
