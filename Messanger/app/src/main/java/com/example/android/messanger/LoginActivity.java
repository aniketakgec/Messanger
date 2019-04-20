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

public class LoginActivity extends AppCompatActivity {

   private TextInputLayout mloginEmail,mloginPassword;
   private Button login;
    private String Email, pass;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=findViewById(R.id.login_btn);
        mloginEmail=findViewById(R.id.loginemail);
        mloginPassword=findViewById(R.id.loginpassword);
        mAuth = FirebaseAuth.getInstance();
        mtoolbar=findViewById(R.id.include);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Messanger </font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog=new ProgressDialog(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = mloginEmail.getEditText().getText().toString();
                pass = mloginPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(pass)) {
                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("please wait while we verify  your credentials");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    login_user(Email, pass);
                }
                else
                {   mloginPassword.setError("Password Incorrect");
                mloginEmail.setError("Enter correct email");
                    //Toast.makeText(LoginActivity.this,"Field Empty",Toast.LENGTH_LONG).show();

                }








            }

            private void login_user(String email, String pass) {
mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful())
        {  progressDialog.dismiss();
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();

        }
        else {
            progressDialog.hide();
            Toast.makeText(LoginActivity.this,"Cannot Sign in.Please check the form and try again",Toast.LENGTH_LONG).show();
        }

    }
});

            }
        });



    }







}
