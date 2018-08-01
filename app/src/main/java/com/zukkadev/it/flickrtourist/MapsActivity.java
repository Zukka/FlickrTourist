package com.zukkadev.it.flickrtourist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.List;

import static android.support.v7.app.AlertDialog.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private AppDatabase mDb;
    private GoogleMap mMap;
    private Pin pin;
    private List<Pin> restoredListPins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDb = AppDatabase.getInstance(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupPinViewModel();
    }

    private void setupPinViewModel() {
        PinViewModel viewModel = ViewModelProviders.of(this).get(PinViewModel.class);
        viewModel.getRestoredPin().observe(this, new Observer<List<Pin>>() {
            @Override
            public void onChanged(@Nullable List<Pin> restoredPins) {
                restoredListPins = restoredPins;
            }
        });
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
                        new ResetAllData().execute();
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
                Intent intent = new Intent(MapsActivity.this, ImagesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString (Constants.Pin,marker.getTitle());
                bundle.putDouble (Constants.Latitude,marker.getPosition().latitude);
                bundle.putDouble (Constants.Longitude,marker.getPosition().longitude);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });
        if (restoredListPins != null && restoredListPins.size() > 0) {
            for (Pin restoredPin: restoredListPins) {
                LatLng pinLatLng = new LatLng(restoredPin.getPinLatitude(), restoredPin.getPinLongitude());
                addPinToMap(pinLatLng, restoredPin.getPinID());
            }
        }
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

    public class ResetAllData extends AsyncTask<Pin,Void, Boolean> {

        @Override
        protected Boolean doInBackground(Pin... pins) {
            try {
                mDb.pinsDao().nukeTable();
                mDb.flickrImagesDao().nukeTable();
                return true;
            } catch (Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isReset) {
            if(!isReset)
                Toast.makeText(MapsActivity.this,
                        getString(R.string.reset_error),
                        Toast.LENGTH_SHORT).show();
        }
    }
}
