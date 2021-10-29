package com.example.obus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class getOtpActivity extends AppCompatActivity {

    CountryCodePicker ccp;
    EditText phnNo;
    Button sendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_otp);

        ccp = findViewById(R.id.ccp);
        phnNo = findViewById(R.id.phneTxt);
        sendOtp = findViewById(R.id.sOtpBtn);
        ccp.registerCarrierNumberEditText(phnNo);

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getOtpActivity.this,LoginActivity.class);
                intent.putExtra("mobile",ccp.getFullNumberWithPlus().replace(" ",""));
                startActivity(intent);
                finish();
            }
        });
    }
}