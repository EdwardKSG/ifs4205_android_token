package com.ifs4205.fingerprinttoken;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import static com.ifs4205.fingerprinttoken.ComputationUtil.hash;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Digital Token Registration");

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://ifs4205team1-3.comp.nus.edu.sg/");

        Button copyInforButton = (Button)findViewById(R.id.copy_infor);
        copyInforButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);

            String [] idList = getIdList();

            // not supposed to be exposed. should neither be saved nor appear in any transfer
            String compositeKey = hash(idList[0], idList[1], idList[2]);

            // this will be saved in the database at server side upon first mobile app login
            String hashedCompositeKey = hash(compositeKey);

            String hashedCompositeKeyLastSix = hash(compositeKey.substring(compositeKey.length()-6));

            // Creates a new text clip to put on the clipboard
            ClipData clip = ClipData.newPlainText("device_infor",
                    hashedCompositeKey + hashedCompositeKeyLastSix);

            // Set the clipboard's primary clip.
            clipboard.setPrimaryClip(clip);

            Toast.makeText(SignUpActivity.this,
                    "Device infor copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String[] getIdList () {
        String [] id = new String[3];

        // android id is unique to each combination of app-signing key, user, and device
        id[0] = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


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
