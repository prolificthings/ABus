package com.example.obus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CusActivity extends AppCompatActivity implements OnMapReadyCallback {

    BottomNavigationView bottomNavigationView;
    Button bookRide;

    private GoogleMap mMap;
    Location currentLoc;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQ_CODE = 101;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference cusDbRef, driverAvailableRef, driverRef, driverLocRef;
    private String cusID;
    LatLng cusPickupLoc;
    Marker driverMarker, pickupMarker;
    int radius=1;
    private Boolean driverFound = false, reqType = false;

    private String driverFoundID;
    GeoQuery geoQuery;
    private ValueEventListener driverLocRefListener;

    CardView cusCard;
    TextView driverName, driverPhne, driverBus;
    CircleImageView driverImg;
    ImageView call;

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
                Intent intent = new Intent(CusActivity.this,CusProfileActivity.class);
                startActivity(intent);
            case R.id.ord:
                Intent ordIntent = new Intent(CusActivity.this,MyOrdersActivity.class);
                startActivity(ordIntent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        cusCard = findViewById(R.id.cusCard);
        driverName = findViewById(R.id.driver2cus_name);
        driverPhne = findViewById(R.id.driver2cus_phne);
        driverBus = findViewById(R.id.driver2cus_bus);
        driverImg  = findViewById(R.id.driver2cus_img);
        call = findViewById(R.id.call_img);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:

                    case R.id.send_req:
                        startActivity(new Intent(getApplicationContext(),SendRequestActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return;

                    case R.id.acc_req:
                        startActivity(new Intent(getApplicationContext(),AcceptRequestActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return;


                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        cusDbRef = FirebaseDatabase.getInstance().getReference().child("Customers Requests");
        driverAvailableRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        driverLocRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");

        bookRide = findViewById(R.id.bookRideBtn);

        bookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (reqType) {
                    reqType = false;
                    //geoQuery.removeAllListeners();
                    driverAvailableRef.removeEventListener(driverLocRefListener);

                    if (driverFound != null) {
                        driverRef = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child("Drivers").child(driverFoundID).child("cusRideID");

                        driverRef.setValue(true);
                        driverFoundID = null;
                    }

                    driverFound = false;
                    radius = 1;

                    String cusID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    GeoFire geoFire = new GeoFire(cusDbRef);
                    geoFire.removeLocation(cusID);

                    if (pickupMarker != null) {
                        pickupMarker.remove();
                    }
                    if (driverMarker != null) {
                        driverMarker.remove();
                    }

                    bookRide.setText("Book a ride");
                    cusCard.setVisibility(View.GONE);

                } else {

                    reqType = true;
                    String cusID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    GeoFire geoFire = new GeoFire(cusDbRef);
                    geoFire.setLocation(cusID, new GeoLocation(currentLoc.getLatitude(), currentLoc.getLongitude()));

                    // LatLng cusPickupLoc;
                    cusPickupLoc = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());

                    pickupMarker = mMap.addMarker(new MarkerOptions().position(cusPickupLoc).title("Pickup location"));


                    bookRide.setText("Searching for Buses...");
                    getClosestBus();
                }
            }

        });
    }

    private void getClosestBus() {
        GeoFire geoFire = new GeoFire(driverAvailableRef);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(cusPickupLoc.latitude,cusPickupLoc.longitude),radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && reqType){
                    driverFound = true;
                    driverFoundID = key;

                    driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
                    HashMap<String, Object> driverMap = new HashMap<String, Object>();
                    driverMap.put("cusRideID",cusID);
                    driverRef.updateChildren(driverMap);

                    getDriverLoc();
                    bookRide.setText("Looking for Driver location...");
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound){
                    radius = radius+1;
                    getClosestBus();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void getDriverLoc() {
        driverLocRefListener = driverAvailableRef.child(driverFoundID).child("l").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && reqType){
                    List<Object> driverLocMap = (List<Object>) snapshot.getValue();
                    double locLat = 0;
                    double locLng = 0;
                    bookRide.setText("Driver found!");

                    cusCard.setVisibility(View.VISIBLE);
                    getAssignedDriverInfo();

                    if (driverLocMap.get(0) != null) {
                        locLat = Double.parseDouble(driverLocMap.get(0).toString());
                    }
                    if (driverLocMap.get(1) != null) {
                        locLng = Double.parseDouble(driverLocMap.get(1).toString());
                    }

                    LatLng driverLatLng =  new LatLng(locLat,locLng);
                    if (driverMarker != null){
                        driverMarker.remove();
                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(cusPickupLoc.latitude);
                    loc1.setLongitude(cusPickupLoc.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float Distance = loc1.distanceTo(loc2);

                    if (Distance < 90){
                        bookRide.setText("Bus reached!");
                    }
                    else{
                        bookRide.setText("Bus found at " + Distance+"m");
                    }

                    driverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your bus is here")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLoc = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.cus_map);
                    mapFragment.getMapAsync(CusActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(currentLoc.getLatitude(),currentLoc.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My location");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.0f));
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQ_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }

    }

    private void getAssignedDriverInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Drivers").child(driverFoundID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    String name = snapshot.child("name").getValue().toString();
                    String phone = snapshot.child("phone").getValue().toString();
                    String busNo = snapshot.child("bus no").getValue().toString();

                    driverName.setText(name);
                    driverPhne.setText(phone);
                    driverBus.setText(busNo);

                    if (snapshot.hasChild("image")) {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(driverImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}