package com.example.android.messanger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
private DatabaseReference mUserDatabase;
private FirebaseUser mCurrentUser;
    private CircleImageView mcircleImageView;
    private TextView account_status;
    private     TextView mDispalyname;
    private Button statusBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=mCurrentUser.getUid();
        mDispalyname=findViewById(R.id.displayname);
        statusBtn=findViewById(R.id.change_status);
        mcircleImageView=findViewById(R.id.profile_pic);
        account_status=findViewById(R.id.account_status);
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // Toast.makeText(SettingsActivity.this,dataSnapshot.toString(),Toast.LENGTH_SHORT).show();
                String name=dataSnapshot.child("name").getValue().toString();
                String  status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
              ///  String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
                mDispalyname.setText(name);
                account_status.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void changestatus(View view) {
        String status_value=account_status.getText().toString();

        Intent i=new Intent(SettingsActivity.this,StatusActivity.class);
        i.putExtra("status_value",status_value);
        startActivity(i);
    }
}
