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

public class TransitActivity extends AppCompatActivity {

    private static final String TAG = TransitActivity.class.getSimpleName();

    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transit);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        Button signInButton = (Button)findViewById(R.id.sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(TransitActivity.this, EnterNonceActivity.class);
                startActivity(loginIntent);
            }
        });

        Button signUpButton = (Button)findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(TransitActivity.this, SignUpActivity.class);
                startActivity(signInIntent);
            }
        });

        r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(TransitActivity.this, "2 minutes time out. You are logged out.", Toast.LENGTH_SHORT).show();
                Intent userIntent = new Intent(TransitActivity.this, MainActivity.class);
                TransitActivity.this.startActivity(userIntent);
            }
        };

        checkPerm();
    }

    private void checkPerm() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.INTERNET};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private boolean hasPermissions (@NonNull Context context, @NonNull String[] permissions) {
        boolean granted = true;

        for( String perm : permissions) {
            if (ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                return granted;
            }
        }

        return granted;

    }

}
