package com.example.obus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class SendRequestActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    sendRequestAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<Items> itemList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_recycle,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.pro:
                Intent intent = new Intent(SendRequestActivity.this,CusProfileActivity.class);
                startActivity(intent);

            case R.id.ord:
                Intent ordIntent = new Intent(SendRequestActivity.this,MyOrdersActivity.class);
                startActivity(ordIntent);

            case R.id.search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        adapter.getFilter().filter(s);
                        return false;
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);


        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.send_req);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),CusActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return;

                    case R.id.send_req:


                    case R.id.acc_req:
                        startActivity(new Intent(getApplicationContext(),AcceptRequestActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return;


                }
            }
        });

        recyclerView = findViewById(R.id.itemRecycle);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        itemList = new ArrayList<>();
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/potato","Potato"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/onion","Onion"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/beans","Beans"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/beetroot","Beetroot"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/brinjal","Brinjal"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/cauliflower","Cauliflower"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/cabbage","Cabbage"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/broccoli","Broccoli"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/carrot","Carrot"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/chilli","Chilli"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/cucumber","Cucumber"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/garlic","Garlic"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/ginger","Ginger"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/corn","Corn"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/jahni","Ridge gourd"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/potala","Potala"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/kalara","Bitter gourd"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/peas","Peas"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/pumpkin","Pumpkin"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/raddish","Raddish"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/tomato","Tomato"));
//
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/banana","Banana"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/apple","Apple"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/orange","Orange"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/watermelon","Watermelon"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/lichi","Lichi"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/lemon","Lemon"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/tamarind","Tamarind"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/guava","Guava"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/pomegranates","Pomeogranate"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/sitafal","Custard Apple"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/dates","Dry dates"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/grapes","Grapes"));
//        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/papaya","Papaya"));

        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/chana","Chana Dal"));
        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/masoor","Masoor Dal"));
        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/moong","Moong Dal"));
        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/toor","Toor Dal"));
        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/urad","Urad Dal"));
        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/urad_split","Urad Dal (split)"));
        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/rajma","Rajma"));
        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/wheat","Wheat"));
        itemList.add(new Items("android.resource://" + getPackageName() + "/drawable/matar","Matar"));





        adapter = new sendRequestAdapter(itemList,this);
        recyclerView.setAdapter(adapter);


    }
}