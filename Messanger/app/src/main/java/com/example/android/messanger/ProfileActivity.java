package com.example.android.messanger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

public class ProfileActivity extends AppCompatActivity {
    private TextView mProfileName,mProfileStatus,mProfileFriendCount;
    private ImageView mProfileImage;
    private Button mProfileSendRequest;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mProgressDialog;
    private String current_state;
    private DatabaseReference mFriendRequestDatabase;
    private FirebaseUser mCurrentUser;


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
        current_state="not_friends";
        mProgressDialog=new ProgressDialog(this);
        mProfileSendRequest=findViewById(R.id.friend_request_btn);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the Data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_request");
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
                mProgressDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mProfileSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_state.equals("not_friends"))
                {
                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
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

            }
        });



    }
}
