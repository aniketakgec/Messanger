package com.example.android.messanger;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
private Toolbar mtoolbar;
private TextInputLayout mStatus;
private Button status_btn;
private DatabaseReference mDatabaseReference;
private FirebaseUser mCurrentUser;
private ProgressDialog mprogressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mtoolbar=findViewById(R.id.layout_statusBar);
        setSupportActionBar(mtoolbar);
        mStatus=findViewById(R.id.update_status);
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();
        mprogressDialog=new ProgressDialog(this);
        String Status_value=getIntent().getStringExtra("status_value");
        mStatus.getEditText().setText(Status_value);
        status_btn=findViewById(R.id.status_btn);
        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprogressDialog.setTitle("Saving Changes");
                mprogressDialog.setMessage("Please Wait...");
                mprogressDialog.setCanceledOnTouchOutside(false);
                mprogressDialog.show();
                String changed_status=mStatus.getEditText().getText().toString();
                mDatabaseReference.child("status").setValue(changed_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mprogressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Status Updated",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Error  Updating",Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'> Account Status</font>"));

    }
}
