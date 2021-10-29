package com.example.obus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CusProfileActivity extends AppCompatActivity {

CheckBox buyer, seller;
TextView status;
TextView updateImg;
ImageView close,save;
CircleImageView proImg;
EditText cusName, cusPhne, cusProfession;

String checker;
private Uri imgUri;
private String myUrl = "";
private StorageTask uploadTask;
private StorageReference storageProPicsRef;

DatabaseReference databaseRef;
FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_profile);

        buyer = findViewById(R.id.buyerCheck);
        seller = findViewById(R.id.sellerCheck);
        status  = findViewById(R.id.stsTxt);
        cusName = findViewById(R.id.namePro);
        cusPhne = findViewById(R.id.phonePro);
        cusProfession = findViewById(R.id.professionPro);
        proImg = findViewById(R.id.profile_image);
        updateImg = findViewById(R.id.chngImgTxt);
        close = findViewById(R.id.closePro);
        save = findViewById(R.id.savePro);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");
        storageProPicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CusProfileActivity.this, CusActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buyer.isChecked() && !seller.isChecked()){
                    status.setText("I am a Buyer");
                }
                else if (!buyer.isChecked() && !seller.isChecked()){
                    status.setText("Please choose from above");
                }
            }
        });

        seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seller.isChecked() && !buyer.isChecked()){
                    status.setText("I am a Seller");
                }
                else if (!buyer.isChecked() && !seller.isChecked()){
                    status.setText("Please choose from above");
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")){
                    validateControllers();
                }
                else{
                    validateAndSaveOnlyInfo();
                }
            }
        });

        updateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(CusProfileActivity.this);
            }
        });

        getUserInfo();
    }

    private void validateAndSaveOnlyInfo() {
        if (TextUtils.isEmpty(cusName.getText().toString())){
            cusName.setError("Please enter your Name");
            cusName.requestFocus();
        }
        if (TextUtils.isEmpty(cusPhne.getText().toString())){
            cusPhne.setError("Please enter your Phone number");
            cusPhne.requestFocus();
        }
        if (TextUtils.isEmpty(cusProfession.getText().toString())){
            cusProfession.setError("Please enter your Profession");
            cusProfession.requestFocus();
        }
        else{
            HashMap<String,Object> userMap = new HashMap<>();
            if (mAuth.getCurrentUser() != null) {
                userMap.put("uid", mAuth.getCurrentUser().getUid());
            }
            userMap.put("name",cusName.getText().toString());
            userMap.put("phone",cusPhne.getText().toString());
            userMap.put("profession",cusProfession.getText().toString());

            databaseRef.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

            startActivity(new Intent(CusProfileActivity.this,CusActivity.class));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUri = result.getUri();

            proImg.setImageURI(imgUri);
        }
        else{
            Toast.makeText(this, "Error updating profile picture", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateControllers(){
        if (TextUtils.isEmpty(cusName.getText().toString())){
            cusName.setError("Please enter your Name");
            cusName.requestFocus();
        }
        if (TextUtils.isEmpty(cusPhne.getText().toString())){
            cusPhne.setError("Please enter your Phone number");
            cusPhne.requestFocus();
        }
        if (TextUtils.isEmpty(cusProfession.getText().toString())){
            cusProfession.setError("Please enter your Profession");
            cusProfession.requestFocus();
        }
        else if (checker.equals("clicked")){
            uploadProfilePic();
        }
    }

    private void uploadProfilePic() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("User account Information");
        progressDialog.setMessage("Please wait while we are saving your data");
        progressDialog.show();

        if (imgUri != null){
            final StorageReference fileRef = storageProPicsRef
                    .child(mAuth.getCurrentUser().getUid() + ".jpg");

            uploadTask = fileRef.putFile(imgUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("uid",mAuth.getCurrentUser().getUid());
                        userMap.put("name",cusName.getText().toString());
                        userMap.put("phone",cusPhne.getText().toString());
                        userMap.put("profession",cusProfession.getText().toString());
                        userMap.put("image",myUrl);

                        databaseRef.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(CusProfileActivity.this,CusActivity.class));
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserInfo(){
        if (mAuth.getCurrentUser() != null) {
            databaseRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String profession = snapshot.child("profession").getValue().toString();

                        cusName.setText(name);
                        cusPhne.setText(phone);
                        cusProfession.setText(profession);

                        if (snapshot.hasChild("image")) {
                            String image = snapshot.child("image").getValue().toString();
                            Picasso.get().load(image).into(proImg);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}