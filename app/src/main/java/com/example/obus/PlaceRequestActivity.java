package com.example.obus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlaceRequestActivity extends AppCompatActivity {

    Spinner Rstate;
    EditText dist,qnt, cntct;
    ImageView itemImg;
    TextView itemName;
    Button plcOrdr;

    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference myOrderRef, requestRef;
    Uri PathUri;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.pro:
                Intent intent = new Intent(PlaceRequestActivity.this,CusProfileActivity.class);
                startActivity(intent);
            case R.id.ord:
                Intent ordIntent = new Intent(PlaceRequestActivity.this,MyOrdersActivity.class);
                startActivity(ordIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_request);

        Rstate = findViewById(R.id.OrderItemState);
        dist = findViewById(R.id.OrderItemDistrict);
        qnt = findViewById(R.id.OrderItemQuantity);
        itemImg = findViewById(R.id.OrderItemImg);
        itemName = findViewById(R.id.OrderItemName);
        plcOrdr = findViewById(R.id.placeOrderBtn);
        cntct = findViewById(R.id.OrderItemContact);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        requestRef = db.getReference().child("Order Requests");

        myOrderRef = db.getReference().child("Users").child("Customers");

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.states));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Rstate.setAdapter(myAdapter);

        itemImg.setImageURI(Uri.parse(getIntent().getStringExtra("image")));
        itemName.setText(getIntent().getStringExtra("name"));

        PathUri = Uri.parse(getIntent().getStringExtra("image"));

        DatabaseReference phneRef  = myOrderRef.child(mAuth.getCurrentUser().getUid());
        phneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phne = snapshot.child("phone").getValue().toString();
                cntct.setText(phne);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        plcOrdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertMyOrder();
                insertRequest();
            }
        });

    }

    private void insertRequest() {
        String state= Rstate.getSelectedItem().toString();
        String district = dist.getText().toString();
        String quantity = qnt.getText().toString();
        String item = itemName.getText().toString();
        String image = PathUri.toString();
        String number = cntct.getText().toString();

        Order orders = new Order(item,state,district,quantity,image,number);
        requestRef.push().setValue(orders);
    }

    private void insertMyOrder() {

        String quantity = qnt.getText().toString();
        String item = itemName.getText().toString();
        String image = PathUri.toString();

        Order order = new Order(item,quantity,image);
        if (mAuth.getCurrentUser() != null) {
            myOrderRef.child(mAuth.getCurrentUser().getUid()).child("My Requests").push().setValue(order);
        }
        Toast.makeText(PlaceRequestActivity.this, "Your Order request is successful!", Toast.LENGTH_SHORT).show();
    }
}