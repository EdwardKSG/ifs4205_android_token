package com.ifs4205.fingerprinttoken;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import static android.provider.Settings.Secure;
import static com.ifs4205.fingerprinttoken.ComputationUtil.hash;

import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;
import com.ifs4205.fingerprinttoken.ComputationUtil;

public class ShowOtpActivity extends AppCompatActivity {

    Runnable r;

    //final static String MSG_SUCCESS = "Your authentication message has been sent. Please proceed to the web login page.";
    final static String MSG_SUCCESS = "Your One-Time-Password (OTP) is:";
    final static String MSG_FAIL = "Sorry. Failed to send authentication message. Please check the network connection.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        setTitle("One Time Password (OTP)");

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        String nonce = getIntent().getExtras().getString("NONCE");

        String [] idList = getIdList();

        // not supposed to be exposed. should neither be saved nor appear in any transfer
        String compositeKey = hash(idList[0], idList[1], idList[2]);

        // this will be saved in the database at server side upon first mobile app login
        String hashedCompositeKey = hash(compositeKey);

        /*
         * h: SHA256, ck: composite key
         * digest = h(h(ck)+n)
         * otp = digest XOR ck, this value will be transferred
         */
        String digest, otp;

        digest = hash(hashedCompositeKey + nonce);
        otp = HexStringXor.xorHex(digest, compositeKey);

        String bio = "n: " + nonce + "\n" +
                "random: " + ((CustomApplication)getApplication()).getShared().getUserData() + "\n" +
                "ck: " + compositeKey + "\n" +
                "h(ck): " + hashedCompositeKey + "\n" +
                "h(h(ck)+n): " + digest + "\n" +
                "h(h(ck)+n) XOR ck: " + otp + "\n" +
                "Verified h(ck): " + hash(HexStringXor.xorHex(otp, digest)) + "\n" +
                "Android ID: " + idList[0] + "\n" +
                "Device ID: " + idList[1] + "\n" +
                "Subscriber ID: " + idList[2] + "\n";

        TextView otpTextValue = (TextView)findViewById(R.id.user_otp);
        otpTextValue.setText(otp.substring(otp.length()-6));

        TextView userTextValue = (TextView)findViewById(R.id.user_bio);
        userTextValue.setText(bio);

        TextView resultValue = (TextView)findViewById(R.id.user_otp_title);
        resultValue.setText(MSG_SUCCESS);

        r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent userIntent = new Intent(ShowOtpActivity.this, TimeoutActivity.class);
                ShowOtpActivity.this.startActivity(userIntent);
            }
        };


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
