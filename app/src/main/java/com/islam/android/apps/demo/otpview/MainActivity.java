package com.islam.android.apps.demo.otpview;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.islam.android.libraries.otpview.OTPView;

public class MainActivity extends AppCompatActivity implements OTPView.OnCodeCompleteListener {

    private OTPView otpView1, otpView2, otpView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        otpView1 = findViewById(R.id.otp_view_1);
        otpView2 = findViewById(R.id.otp_view_2);
        otpView3 = findViewById(R.id.otp_view_3);

        otpView1.setOnCodeCompleteListener(this);
        otpView2.setOnCodeCompleteListener(this);
        otpView3.setOnCodeCompleteListener(this);
    }

    @Override
    public void onCodeComplete(String code) {
        Toast.makeText(this, "Code is: " + code, Toast.LENGTH_SHORT).show();
    }
}