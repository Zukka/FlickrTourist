package com.zukkadev.it.flickrtourist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zukkadev.it.flickrtourist.data.AppDatabase;
import com.zukkadev.it.flickrtourist.model.Pin;
import com.zukkadev.it.flickrtourist.utils.Constants;

import static android.support.v7.app.AlertDialog.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private AppDatabase mDb;
    private GoogleMap mMap;
    private Pin pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDb = AppDatabase.getInstance(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_clear_pins:
                clearAllPins();
                return true;
        }
        return false;
    }

    private void clearAllPins() {
        Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new Builder(this);
        }
        builder.setTitle(getString(R.string.alert_remove_all_title))
                .setMessage(getString(R.string.alert_remove_all_body))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mMap.clear();
                        mDb.pinsDao().nukeTable();
                        mDb.flickrImagesDao().nukeTable();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void signOut() {
        MainActivity.mAuth.signOut();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Long timeStamp = System.currentTimeMillis()/1000;
                addPinToDatabase(latLng, timeStamp);
                addPinToMap(latLng,timeStamp);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this,
                        getString(R.string.long_press_information),
                        Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this,
                        String.valueOf(marker.getPosition().latitude) +
                        String.valueOf(marker.getPosition().longitude),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapsActivity.this, ImagesActivity.class);
                intent.putExtra (Constants.Pin,marker.getTitle());
                intent.putExtra (Constants.Latitude,marker.getPosition().latitude);
                intent.putExtra (Constants.Longitude,marker.getPosition().longitude);
                startActivity(intent);
                return false;
            }
        });
    }

    private void addPinToMap(LatLng latLng, Long timeStamp) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(String.valueOf(timeStamp));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);
    }

    private void addPinToDatabase(LatLng latLng, Long timeStamp) {
        pin = new Pin(timeStamp, latLng.latitude, latLng.longitude);
        new AddPinToDatabase().execute(pin);
    }


    public class AddPinToDatabase extends AsyncTask<Pin, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Pin... pins) {

            try {
                mDb.pinsDao().insertPin(pins[0]);
                return true;
            } catch (Exception e){
                return false;
            }
        }
    }
}
