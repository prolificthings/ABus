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

public class DriversProfileActivity extends AppCompatActivity {

    CircleImageView proImg;
    TextView updateImg;
    ImageView close,save;
    EditText driName, driPhne, driBusName;

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
        setContentView(R.layout.activity_drivers_profile);

        proImg = findViewById(R.id.profile_image);
        updateImg = findViewById(R.id.chngImgTxt);
        driName = findViewById(R.id.namePro);
        driPhne = findViewById(R.id.phonePro);
        driBusName = findViewById(R.id.busNamePro);
        close = findViewById(R.id.closePro);
        save = findViewById(R.id.savePro);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
        storageProPicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriversProfileActivity.this, DriverActivity.class);
                startActivity(intent);
                finish();
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
                        .start(DriversProfileActivity.this);
            }
        });

        getUserInfo();
    }

    private void validateAndSaveOnlyInfo() {
        if (TextUtils.isEmpty(driName.getText().toString())){
            driName.setError("Please enter your Name");
            driName.requestFocus();
        }
        if (TextUtils.isEmpty(driPhne.getText().toString())){
            driPhne.setError("Please enter your Phone number");
            driPhne.requestFocus();
        }
        if (TextUtils.isEmpty(driBusName.getText().toString())){
            driBusName.setError("Please enter your Profession");
            driBusName.requestFocus();
        }
        else{
            HashMap<String,Object> userMap = new HashMap<>();
            if (mAuth.getCurrentUser() != null) {
                userMap.put("uid", mAuth.getCurrentUser().getUid());
            }
            userMap.put("name",driName.getText().toString());
            userMap.put("phone",driPhne.getText().toString());
            userMap.put("bus no.",driBusName.getText().toString());

            databaseRef.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

            startActivity(new Intent(DriversProfileActivity.this,DriverActivity.class));
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

    private void validateControllers() {
        if (TextUtils.isEmpty(driName.getText().toString())){
            driName.setError("Please enter your Name");
            driName.requestFocus();
        }
        if (TextUtils.isEmpty(driPhne.getText().toString())){
            driPhne.setError("Please enter your Phone number");
            driPhne.requestFocus();
        }
        if (TextUtils.isEmpty(driBusName.getText().toString())){
            driBusName.setError("Please enter your Profession");
            driBusName.requestFocus();
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
                        userMap.put("name",driName.getText().toString());
                        userMap.put("phone",driPhne.getText().toString());
                        userMap.put("bus no",driBusName.getText().toString());
                        userMap.put("image",myUrl);

                        databaseRef.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(DriversProfileActivity.this,DriverActivity.class));
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
                        String busNo = snapshot.child("bus no").getValue().toString();

                        driName.setText(name);
                        driPhne.setText(phone);
                        driBusName.setText(busNo);

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