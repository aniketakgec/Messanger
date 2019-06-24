package com.example.android.messanger;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager viewPager;
    private SectionsPagerAdapter mSectionsPagerAdpapter;
    private TabLayout mTablayout;
    private DatabaseReference mUserRef;
    FirebaseUser current_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mtoolbar=findViewById(R.id.toolbar);
        mTablayout=findViewById(R.id.main_tabs);
        current_user=mAuth.getCurrentUser();
        setSupportActionBar(mtoolbar);
        viewPager=findViewById(R.id.main_tab_pager);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Messanger </font>"));
        mSectionsPagerAdpapter=new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdpapter);
//        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mTablayout.setupWithViewPager(viewPager);

        mTablayout.setTabTextColors(getResources().getColor(R.color.colorHintTextLight),
                getResources().getColor(R.color.colorPrimaryTextLight));

        if (mAuth.getCurrentUser() != null) {

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null) {
            sendToStart();

        }
        else
        {
            mUserRef.child("online").setValue("true");
           // mUserRef.child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (current_user!=null)
        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         if (item.getItemId()==R.id.logout)
         {
             FirebaseAuth.getInstance().signOut();
              sendToStart();
         }
         if(item.getItemId()==R.id.settings)
         {
             Intent i=new Intent(this,SettingsActivity.class);
             startActivity(i);
         }
        if(item.getItemId()==R.id.account)
        {
            Intent i=new Intent(this,UsersActivity.class);
            startActivity(i);
        }
         return true;
    }
}
