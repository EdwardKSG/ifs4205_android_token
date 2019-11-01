package com.ifs4205.fingerprinttoken;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import static com.ifs4205.fingerprinttoken.ComputationUtil.hash;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Digital Token Registration");

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("https://ifs4205team1-3.comp.nus.edu.sg/mobileregister/login/");

        Button copyInforButton = (Button)findViewById(R.id.copy_infor);
        Button clearClipboardButton = (Button)findViewById(R.id.clear_clipboard);

        copyInforButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

                String [] idList = getIdList();

                String uuid = ((CustomApplication)getApplication()).getShared().getUserData();

                // not supposed to be exposed. should neither be saved nor appear in any transfer
                String compositeKey = hash(idList[0], idList[1], idList[2], uuid);

                // this will be saved in the database at server side upon first mobile app login
                String hashedCompositeKey = hash(compositeKey);

                String hashedCompositeKeyLastSix = hash(compositeKey.substring(compositeKey.length()-6));

                // Creates a new text clip to put on the clipboard
                ClipData clip = ClipData.newPlainText("device_infor",
                        hashedCompositeKey + hashedCompositeKeyLastSix);

                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }

                Toast.makeText(SignUpActivity.this,
                        "Device infor copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });

        clearClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

                // Create an empty clip.
                ClipData clip = ClipData.newPlainText("","");

                // Use the empty clip to overwrite all entries in the clipboard.
                for (int i=0; i<50; i++) {
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                    }
                }

                Toast.makeText(SignUpActivity.this,
                        "Clipboard cleared", Toast.LENGTH_LONG).show();
            }
        });

        r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent userIntent = new Intent(SignUpActivity.this, TimeoutActivity.class);
                SignUpActivity.this.startActivity(userIntent);
            }
        };
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
