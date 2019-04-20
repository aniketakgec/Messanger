package com.example.android.messanger;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
private DatabaseReference mUserDatabase;
private FirebaseUser mCurrentUser;
    private CircleImageView mcircleImageView;
    private TextView account_status;
    private     TextView mDispalyname;
    private Button ImageBtn;
    private Button statusBtn;
    private StorageReference mImagetorage;
    private static final int GALLERY_PIC=1;
    private static final int MAX_LENGTH=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid=mCurrentUser.getUid();
        mImagetorage= FirebaseStorage.getInstance().getReference();
        mDispalyname=findViewById(R.id.displayname);
        ImageBtn=findViewById(R.id.change_image);
        ImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("images/*");
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PIC);*/
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);

            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PIC && resultCode==RESULT_OK)
        {
           Uri imageUri=data.getData();
           // Toast.makeText(this,imageUri,Toast.LENGTH_SHORT).show();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                StorageReference  filepath=mImagetorage.child("profile_images").child(random()+".jpg");
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(SettingsActivity.this,"YES PANDEY",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(SettingsActivity.this,"ERROR PANDEY",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


    public void changestatus(View view) {
        String status_value=account_status.getText().toString();

        Intent i=new Intent(SettingsActivity.this,StatusActivity.class);
        i.putExtra("status_value",status_value);
        startActivity(i);
    }
}
