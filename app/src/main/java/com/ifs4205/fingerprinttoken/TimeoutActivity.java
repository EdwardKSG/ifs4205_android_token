package com.ifs4205.fingerprinttoken;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import static com.ifs4205.fingerprinttoken.MainActivity.REQUEST_CAMERA_ACCESS;
import static com.ifs4205.fingerprinttoken.MainActivity.REQUEST_READ_PHONE_STATE;

public class TimeoutActivity extends AppCompatActivity {

    private static final String TAG = TransitActivity.class.getSimpleName();

    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        Button loginButton = (Button)findViewById(R.id.log_in);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(TimeoutActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        });


        r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent userIntent = new Intent(TimeoutActivity.this, TimeoutActivity.class);
                TimeoutActivity.this.startActivity(userIntent);
            }
        };

    }

    @Override
    public void onBackPressed() {
        Intent userIntent = new Intent(TimeoutActivity.this, MainActivity.class);
        TimeoutActivity.this.startActivity(userIntent);
    }

}
