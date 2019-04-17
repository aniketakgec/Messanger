package com.example.android.messanger;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView mcircleImageView;
    private TextInputLayout mdisplayname;
    private TextView mstatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mcircleImageView=findViewById(R.id.profile_pic);
        mstatus=findViewById(R.id.default_status);
        mdisplayname=findViewById(R.id.displayname);
    }
}
