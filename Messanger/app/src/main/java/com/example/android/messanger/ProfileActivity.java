package com.example.android.messanger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private TextView mProfileName,mProfileStatus,mProfileFriendCount;
    private ImageView mProfileImage;
    private Button mProfileSendRequest,declineRrequestBtn;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mProgressDialog;
    private String current_state;
    private DatabaseReference mFriendRequestDatabase,mRootRef;
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
        mRootRef=FirebaseDatabase.getInstance().getReference();
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
                {   mProfileSendRequest.setBackgroundColor(Color.TRANSPARENT);

                    DatabaseReference newNotificationref=mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId=newNotificationref.getKey();

                    HashMap<String,String> notificationData=new HashMap<>();
                    notificationData.put("from",mCurrentUser.getUid());
                    notificationData.put("type","request");

                    Map requestMap=new HashMap();
                    requestMap.put("Friend_request/"+mCurrentUser.getUid()+"/"+user_id+"/request_type","Sent");
                    requestMap.put("Friend_request/"+user_id+"/"+mCurrentUser.getUid()+"/request_type","received");
                    requestMap.put("notifications/"+user_id+"/"+newNotificationId,notificationData);
                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError!=null)
                            {

                                Toast.makeText(ProfileActivity.this,"Erroe sending request",Toast.LENGTH_SHORT).show();
                            }
else
                            {
                                mProfileSendRequest.setEnabled(true);
                                current_state="request_sent";
                                mProfileSendRequest.setText("Cancel Friend Request");
                                declineRrequestBtn.setVisibility(View.INVISIBLE);
                                declineRrequestBtn.setEnabled(false);
                                mProfileSendRequest.setBackgroundResource(R.drawable.friendr_equest_button);
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
                    final String currentDate=DateFormat.getDateTimeInstance().format(new Date());
                    Map friendsMap=new HashMap();
                    friendsMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id+"/date",currentDate);
                    friendsMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid()+"/date",currentDate);

                    friendsMap.put("Friend_request/"+mCurrentUser.getUid()+"/"+user_id,null);
                    friendsMap.put("Friend_request/"+user_id+"/"+mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                           if (databaseError==null) {
                               mProfileSendRequest.setEnabled(true);

                               current_state = "friends";
                               mProfileSendRequest.setText("UnFriend this Person");
                               declineRrequestBtn.setVisibility(View.INVISIBLE);
                               declineRrequestBtn.setEnabled(false);
                           }
                           else
                           {
                               String error=databaseError.getMessage();
                               Toast.makeText(ProfileActivity.this,"Error deleting Friend Request"+error,Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                }

                //-----------UNFRIENDS--------->


                if (current_state.equals("friends"))
                {
                        Map unfriendMap=new HashMap();
                        unfriendMap.put("Friends/"+mCurrentUser.getUid()+"/"+user_id,null);
                        unfriendMap.put("Friends/"+user_id+"/"+mCurrentUser.getUid(),null);
                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError==null) {
                                mProfileSendRequest.setEnabled(true);

                                current_state = "not_friends";
                                mProfileSendRequest.setText("Sent Friend Request");
                                declineRrequestBtn.setVisibility(View.INVISIBLE);
                                declineRrequestBtn.setEnabled(false);
                            }
                            else
                            {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,"Error deleting Friend Request"+error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        });



    }
}
