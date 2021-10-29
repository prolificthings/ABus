package com.example.obus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AcceptRequestActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    RecyclerView recyclerView;
    AcceptRequestAdapter adapter;
    FirebaseDatabase db;
    DatabaseReference requestRef;

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
                Intent intent = new Intent(AcceptRequestActivity.this,CusProfileActivity.class);
                startActivity(intent);
            case R.id.ord:
                Intent ordIntent = new Intent(AcceptRequestActivity.this,MyOrdersActivity.class);
                startActivity(ordIntent);

            case R.id.search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        processSearch(s);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        processSearch(s);
                        return false;
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }

    private void processSearch(String s) {
        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(requestRef.orderByChild("itm_state").startAt(s).endAt(s+"/uf8ff"),Order.class)
                        .build();

        adapter = new AcceptRequestAdapter(options,this);
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_request);


        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.acc_req);

        recyclerView = findViewById(R.id.acceptReqRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


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
                        startActivity(new Intent(getApplicationContext(),SendRequestActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return;

                    case R.id.acc_req:



                }
            }
        });

        db = FirebaseDatabase.getInstance();
        requestRef = db.getReference().child("Order Requests");

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(requestRef,Order.class)
                        .build();

        adapter = new AcceptRequestAdapter(options,this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}