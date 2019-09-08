package com.ifs4205.fingerprinttoken;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private TextView displayError;

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Digital Token Registration");

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameValue = username.getText().toString();
                String passwordValue = password.getText().toString();


                if(TextUtils.isEmpty(usernameValue) || TextUtils.isEmpty(passwordValue)){
                    Toast.makeText(SignUpActivity.this, "All input fields must be filled", Toast.LENGTH_LONG).show();
                }else{
                    Gson gson = ((CustomApplication)getApplication()).getGsonObject();
                    UserObject userData = new UserObject(usernameValue, passwordValue);
                    String userDataString = gson.toJson(userData);
                    CustomSharedPreference pref = ((CustomApplication)getApplication()).getShared();
                    pref.setUserData(userDataString);

                    username.setText("");
                    password.setText("");

                    Intent loginIntent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                }
            }
        });
    }
}
