package com.example.obus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location currentLoc;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQ_CODE = 101;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference DriversAvailabilityRef,DriverWorkingRef, AssignedCusRef, AssignedCusPickupRef;
    private String driverID, cusID="";
    Marker pickUpMarker;

    CardView driverCard;
    TextView cusName,cusPhne;
    CircleImageView cusImg;
    ImageView call;

    private ValueEventListener AssignedCusPickupRefListener;

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
                Intent intent = new Intent(DriverActivity.this,DriversProfileActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        driverCard = findViewById(R.id.driverCard);
        cusName = findViewById(R.id.cus2driver_name);
        cusPhne = findViewById(R.id.cus2driver_phne);
        cusImg = findViewById(R.id.cus2driver_img);
        call = findViewById(R.id.call_img);

        if(currentUser != null) {
            driverID = currentUser.getUid();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        getAssignedCusReq();


    }

    private void getAssignedCusReq() {
        AssignedCusRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Drivers").child(driverID).child("cusRideID");

        AssignedCusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    cusID = snapshot.getValue().toString();

                    getAssignedCusPickupLoc();

                    driverCard.setVisibility(View.VISIBLE);
                    getAssignedCustomerInfo();
                }
                else{
                    cusID = "";
                    if (pickUpMarker != null){
                        pickUpMarker.remove();
                    }

                    if (AssignedCusPickupRefListener != null){
                        AssignedCusPickupRef.removeEventListener(AssignedCusPickupRefListener);
                    }

                    driverCard.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAssignedCusPickupLoc() {
        AssignedCusPickupRef = FirebaseDatabase.getInstance().getReference()
                .child("Customers Requests").child(cusID).child("l");

        AssignedCusPickupRefListener = AssignedCusPickupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    List<Object> cusLocMap = (List<Object>) snapshot.getValue();
                    double locLat = 0;
                    double locLng = 0;


                    if (cusLocMap.get(0) != null) {
                        locLat = Double.parseDouble(cusLocMap.get(0).toString());
                    }
                    if (cusLocMap.get(1) != null) {
                        locLng = Double.parseDouble(cusLocMap.get(1).toString());
                    }
                    LatLng driverLatLng =  new LatLng(locLat,locLng);
                    mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Customer Pickup Location"));
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
                if (getApplicationContext() != null) {
                if(location != null){
                    currentLoc = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(DriverActivity.this);

                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DriversAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
                        GeoFire geoFireAvailability = new GeoFire(DriversAvailabilityRef);

                        DriverWorkingRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");
                        GeoFire geoFireWorking = new GeoFire(DriverWorkingRef);

                    if (cusID.equals("")) {
                        geoFireWorking.removeLocation(userID);
                        geoFireAvailability.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    } else {
                        geoFireAvailability.removeLocation(userID);
                        geoFireWorking.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    }
                    }
                }





            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(getApplicationContext() != null){
            LatLng latLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My location");
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f));
            mMap.addMarker(markerOptions);
        }


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

    @Override
    protected void onStop() {
        super.onStop();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DriversAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(DriversAvailabilityRef);
        geoFire.removeLocation(userID);
    }

    private void getAssignedCustomerInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Customers").child(cusID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    String name = snapshot.child("name").getValue().toString();
                    String phone = snapshot.child("phone").getValue().toString();

                    cusName.setText(name);
                    cusPhne.setText(phone);

                    if (snapshot.hasChild("image")) {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(cusImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}