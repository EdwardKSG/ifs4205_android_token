package com.ifs4205.fingerprinttoken;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterNonceActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private EditText nonceArea;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonce);

        setTitle("Enter Nonce");
        nonceArea = (EditText)findViewById(R.id.nonce);
        submitButton = (Button)findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nonce = nonceArea.getText().toString();
                if (TextUtils.isEmpty(nonce)) {
                    Toast.makeText(view.getContext(), "Please type the nonce", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent userIntent = new Intent(view.getContext(), ShowOtpActivity.class);
                    userIntent.putExtra("NONCE", nonce);
                    String userBio = getIntent().getExtras().getString("USER_BIO");
                    userIntent.putExtra("USER_BIO", userBio);
                    view.getContext().startActivity(userIntent);
                }
            }
        });
    }

}

