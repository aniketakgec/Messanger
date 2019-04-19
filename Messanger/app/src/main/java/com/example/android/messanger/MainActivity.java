package com.example.android.messanger;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager viewPager;
    private SectionsPagerAdapter mSectionsPagerAdpapter;
    private TabLayout mTablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mtoolbar=findViewById(R.id.toolbar);
        mTablayout=findViewById(R.id.main_tabs);
        setSupportActionBar(mtoolbar);
        viewPager=findViewById(R.id.main_tab_pager);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Messanger </font>"));
        mSectionsPagerAdpapter=new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdpapter);
        mTablayout.setupWithViewPager(viewPager);

        mTablayout.setTabTextColors(getResources().getColor(R.color.colorHintTextLight),
                getResources().getColor(R.color.colorPrimaryTextLight));

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null) {
            sendToStart();

        }

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
         return true;
    }
}
