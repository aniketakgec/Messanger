package com.example.android.messanger;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputLayout memail;
    private ProgressDialog progressDialog;
    private TextInputLayout mdisplayname;
    private TextInputLayout mpassword;
    private Button btn;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
   private DatabaseReference mdatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        memail=findViewById(R.id.email);

        mpassword=findViewById(R.id.password);
        btn=findViewById(R.id.angry_btn);
        mtoolbar=findViewById(R.id.include);
        mdisplayname=findViewById(R.id.displayname);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Messanger </font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

          progressDialog=new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name=mdisplayname.getEditText().getText().toString();
                String email=memail.getEditText().getText().toString();
                String password=mpassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    progressDialog.setTitle("Registering user");
                    progressDialog.setMessage("please wait while we create your account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register_user(display_name, email, password);
                }
            }

            private void register_user(final String display_name, String email, String password) {
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=currentUser.getUid();
                            mdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,String> userMap=new HashMap<>();
                            userMap.put("name",display_name);
                            userMap.put("status","Hi there I am using Whatsapp");
                            userMap.put("image","default");
                         //   userMap.put("thumb_image","default");
                              mdatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      if(task.isSuccessful())
                                      {
                                          progressDialog.dismiss();
                                          Intent mainIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                          startActivity(mainIntent);
                                          finish();

                                      }
                                      else
                                      {
                                          Toast.makeText(RegistrationActivity.this,"YOU GOT DATABASE ERROR",Toast.LENGTH_SHORT).show();
                                      }

                                  }
                              });



                        }
                        else {
                            progressDialog.hide();
                            Toast.makeText(RegistrationActivity.this,"you got error",Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });
    }
}
