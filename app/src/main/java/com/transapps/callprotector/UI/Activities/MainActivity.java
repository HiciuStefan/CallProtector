package com.transapps.callprotector.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.transapps.callprotector.R;
import com.transapps.callprotector.Services.IncomingCalls1.BackgroundCallsService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_main_search);

        Intent intent = new Intent(this, BackgroundCallsService.class);
        startService(intent);



    }





}
