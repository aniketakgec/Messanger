package com.example.android.messanger;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private String name,image,status,uid, download_url;
    private FirebaseUser mCurrentUser;
    private CircleImageView mcircleImageView;
    private TextView account_status;
    private TextView mDispalyname;
    private Button ImageBtn;
    private ImageView mDisplayImage;
    private Button statusBtn;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mUserDatabase;
    private StorageReference mImagetorage,filepath;

    private static final int GALLERY_PIC=1;
    private static final int MAX_LENGTH=10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
         uid=mCurrentUser.getUid();
        mDisplayImage=findViewById(R.id.profile_pic);

        mImagetorage= FirebaseStorage.getInstance().getReference();

        mDispalyname=findViewById(R.id.displayname);
        ImageBtn=findViewById(R.id.change_image);
        ImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);

            }
        });

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        statusBtn=findViewById(R.id.change_status);
        mcircleImageView=findViewById(R.id.profile_pic);
        account_status=findViewById(R.id.account_status);

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name=dataSnapshot.child("name").getValue().toString();
                status=dataSnapshot.child("status").getValue().toString();
                image=dataSnapshot.child("image").getValue().toString();
                mDispalyname.setText(name);
                account_status.setText(status);
                if(image!=null)
                Picasso.get().load(image).resize(500,550).into(mDisplayImage);
                else
                    Picasso.get().load(R.drawable.images).into(mDisplayImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

// End of On Create Method

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_PIC && resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mProgressDialog=new ProgressDialog(this);
                mProgressDialog.setTitle("Uploading Image");
                mProgressDialog.setMessage("Please wait while we upload the image...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();



                Uri resultUri = result.getUri();

                  filepath=mImagetorage.child("profile_images").child(uid+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {
                           Toast.makeText(SettingsActivity.this,"Profile Pic Updated",Toast.LENGTH_SHORT).show();
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                   final String url=uri.toString();
                                    mUserDatabase.child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                mProgressDialog.dismiss();


                                            }
                                        }
                                    });

                                }
                            });


                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this,"TUMSE NA HO PAYEGA ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }



    public void changestatus(View view) {
        String status_value=account_status.getText().toString();

        Intent i=new Intent(SettingsActivity.this,StatusActivity.class);
        i.putExtra("status_value",status_value);
        startActivity(i);
    }
}
