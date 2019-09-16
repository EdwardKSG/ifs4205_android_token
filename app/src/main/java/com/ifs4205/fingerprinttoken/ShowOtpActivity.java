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
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;

public class ShowOtpActivity extends AppCompatActivity {

    final static String MSG_SUCCESS = "Your authentication message has been sent. Please proceed to the web login page.";
    final static String MSG_FAIL = "Sorry. Failed to send authentication message. Please check the network connection.";

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

        digest = hash(hashedCompositeKey, nonce);
        otp = HexStringXor.xorHex(digest, compositeKey);

        String bio = "n: " + nonce + "\n" +
                "ck: " + compositeKey + "\n" +
                "h(ck): " + hashedCompositeKey + "\n" +
                "h(h(ck)+n): " + digest + "\n" +
                "h(h(ck)+n) XOR ck: " + otp + "\n" +
                "Verified h(ck): " + hash(HexStringXor.xorHex(otp, digest)) + "\n" +
                "Android ID: " + idList[0] + "\n" +
                "Device ID: " + idList[1] + "\n" +
                "Subscriber ID: " + idList[2] + "\n" +
                "User name: " + mUserObject.getUsername() + "\n" +
                "Password: " + mUserObject.getPassword();

        TextView otpTextValue = (TextView)findViewById(R.id.user_otp);
        otpTextValue.setText(otp);

        TextView userTextValue = (TextView)findViewById(R.id.user_bio);
        userTextValue.setText(bio);

        TextView resultValue = (TextView)findViewById(R.id.user_otp_title);
        resultValue.setText(MSG_SUCCESS);


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

    private String hash (String... keys) {
        String result = "";

        try {
            for (String key : keys) {
                result = bytesToHexString(getSHA(result + key));
            }
        } catch (NoSuchAlgorithmException e) {
            //temporarily do nothing, because the exception shouldn't happen
        }

        return result;
    }

    /**
     * Use SHA-256 to compute message digest
     *
     * @param input the input message
     *
     * @return a byte array containing the message digest
     */
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // calculate message digest of an input and return a byte array
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Convert a byte array to a hex string
     *
     * @param hash the message digest presented as byte array
     *
     * @return the message digest represented as a 32-byte hex string
     */
    public static String bytesToHexString(byte[] hash)
    {
        // Convert a byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

}
