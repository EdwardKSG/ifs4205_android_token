package com.ifs4205.fingerprinttoken;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class EnterNonceActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private EditText nonceArea;
    private Button submitButton;

    private BarcodeDetector barcodeDetector;

    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonce);

        setTitle("Capture The Code");

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        surfaceView = (SurfaceView) findViewById(R.id.qr_scanner);
        nonceArea = (EditText)findViewById(R.id.nonce);
        submitButton = (Button)findViewById(R.id.submit_button);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 640).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if(qrCodes.size() != 0) {
                    nonceArea.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);
                            nonceArea.setText(qrCodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nonce = nonceArea.getText().toString();
                if (TextUtils.isEmpty(nonce)) {
                    Toast.makeText(view.getContext(), "Please scan the QR code or type the one-time code first", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent userIntent = new Intent(view.getContext(), ShowOtpActivity.class);
                    userIntent.putExtra("NONCE", nonce);
                    view.getContext().startActivity(userIntent);
                }
            }
        });

        r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent userIntent = new Intent(EnterNonceActivity.this, TimeoutActivity.class);
                EnterNonceActivity.this.startActivity(userIntent);
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent userIntent = new Intent(EnterNonceActivity.this, TransitActivity.class);
        EnterNonceActivity.this.startActivity(userIntent);
    }

}

