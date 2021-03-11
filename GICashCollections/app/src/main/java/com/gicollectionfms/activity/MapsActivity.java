package com.gicollectionfms.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.google.android.gms.maps.model.Polyline;
import com.gicollectionfms.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import static com.akexorcist.googledirection.GoogleDirection.withServerKey;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap myMap;
    Polyline line;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = MapsActivity.this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        ((SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync(this);

       // myMap.setMyLocationEnabled(true);

// String serverKey = "AIzaSyBeigI5iExywf7LkBeLCkFBzjk-xFJYuTA";
        String serverKey = "AIzaSyC6x8K4gULHHTYNKDV2cEJR9dljIBbJTpU";
        LatLng origin = new LatLng(25.294371,77.871094);
        LatLng dest = new LatLng(16.098598,79.628906);
        withServerKey(serverKey)
                .from(origin)
                .to(dest)
                .execute(new DirectionCallback() {

                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        Log.e("direction", String.valueOf(direction));
                        Log.e("rawBody", rawBody);

                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                    }
                });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        // Add a marker in Sydney and move the camera
        //12.554625, 76.586547
       /* String address = "https://maps.googleapis.com/maps/api/geocode/json?address=" + "Guindy Bus Terminal, Guindy Industrial Estate, SIDCO Industrial Estate, Guindy, Chennai, Tamil Nadu" + "=AIzaSyDTEEiJbeuSVXtUg2V1sUTyODWAi3wPY18";
        Log.e("request", address);
        callapi(address);*/

      /*  Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(12.554625, 76.586547), new LatLng(11.019065, 76.954877))
                .width(5)
                .color(R.color.colorAccent));*/

       /* final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + 11.019065 + "," + 76.954877 + "&daddr=" + 12.554625 + "," + 76.586547));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);*/

       /* LatLng origin = new LatLng(12.554625, 76.586547);
        LatLng destination = new LatLng(11.019065, 76.954877);
        DrawRouteMaps.getInstance(this)
                .draw(origin, destination, mMap);
        DrawMarker.getInstance(this).draw(mMap, origin, R.drawable.mappin, "Origin Location");
        DrawMarker.getInstance(this).draw(mMap, destination, R.drawable.mappin, "Destination Location");

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origin)
                .include(destination).build();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));*/




    }

}

