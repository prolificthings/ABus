package com.example.obus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText otp;
    Button login;
    Dialog dialog;
    String phoneNumber;
    FirebaseAuth mAuth;
    String otpId,currentUserID;
    FirebaseUser currentUser;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        otp = findViewById(R.id.otpTxt);
        login = findViewById(R.id.lgnBtn);



        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                otpId = s;
            }
        };

        phoneNumber = getIntent().getStringExtra("mobile").toString();

        initiateOtp();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter the OTP", Toast.LENGTH_SHORT).show();
                }
                else if(otp.getText().toString().length()!=6) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show();
                } else{
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId,otp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.login_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button drvrLgn = dialog.findViewById(R.id.driLgnBtn);
        Button cusLgn = dialog.findViewById(R.id.cusLgnBtn);

        if (currentUser != null) {
            currentUserID = currentUser.getUid();
        }


        cusLgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(currentUserID).setValue(true);
                Intent cusIntent= new Intent(LoginActivity.this, CusActivity.class);
                startActivity(cusIntent);
                dialog.dismiss();
                finish();
            }
        });

        drvrLgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(currentUserID).setValue(true);
                Intent dvrIntent= new Intent(LoginActivity.this, DriverActivity.class);
                startActivity(dvrIntent);
                dialog.dismiss();
                finish();
            }
        });
    }

    private void initiateOtp() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();


        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseDatabase.getInstance().getReference().child("Users");
                            dialog.show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Unable to Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}