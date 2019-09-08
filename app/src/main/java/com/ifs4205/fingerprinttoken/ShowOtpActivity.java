package com.ifs4205.fingerprinttoken;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import static android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

public class ShowOtpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        setTitle("One Time Password (OTP)");

        String nonce = getIntent().getExtras().getString("NONCE");
        String userBio = getIntent().getExtras().getString("USER_BIO");
        Gson gson = ((CustomApplication)getApplication()).getGsonObject();

        UserObject mUserObject = gson.fromJson(userBio, UserObject.class);

        String [] idList = getIdList();
        String bio = "Nonce: " + nonce + "\n" +
                "Android ID: " + idList[0] + "\n" +
                "Device ID: " + idList[1] + "\n" +
                "Subscriber ID: " + idList[2] + "\n" +
                "User name: " + mUserObject.getUsername() + "\n" +
                "Password: " + mUserObject.getPassword();

        TextView userTextValue = (TextView)findViewById(R.id.user_bio);
        userTextValue.setText(bio);
    }

    private String[] getIdList () {
        String [] id = new String[3];

        // android id is unique to each combination of app-signing key, user, and device
        id[0] = Secure.getString(getContentResolver(), Secure.ANDROID_ID);


        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            /*
             * getDeviceId() returns the unique device ID.
             * For example,the IMEI for GSM and the MEID or ESN for CDMA phones.
             */
            id[1] = telephonyManager.getDeviceId();
            /*
             * getSubscriberId() returns the unique subscriber ID,
             * For example, the IMSI for a GSM phone.
             */
            id[2] = telephonyManager.getSubscriberId();
        } else {
            id[1] = "null";
            id[2] = "null";
        }

        return id;


    }
}
