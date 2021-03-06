package com.capstone.icoffie.monitr;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.capstone.icoffie.monitr.model.SharedPrefManager;

public class SplashscreenActivity extends AppCompatActivity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SharedPrefManager.getClassinstance(getApplicationContext()).getUserPin() != null){
                    Intent intent=new Intent(SplashscreenActivity.this,AuthenticationActivity.class);
                    startActivity(intent);
                    finish();

                } else{
                    Intent intent=new Intent(SplashscreenActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },3000);
    }
}
