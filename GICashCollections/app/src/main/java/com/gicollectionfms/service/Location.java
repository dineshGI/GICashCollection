package com.gicollectionfms.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.activity.GpsTracker;
import com.gicollectionfms.activity.LoginActivity;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.location.LocationManager.*;

public class Location extends Service {

    //https://deepshikhapuri.wordpress.com/2016/11/25/service-in-android/

    private Handler mHandler = new Handler();
    public static Timer mTimer = null;

   // long notify_interval = 1000 * 60 * 10;
    long notify_interval = 1000 * 60;

    GpsTracker gpsTracker;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notify_interval= notify_interval*Long.parseLong(Util.getData("TrackTiming",getApplicationContext()));
        Log.e("TrackTiming",Util.getData("TrackTiming",getApplicationContext()));
        gpsTracker = new GpsTracker(this);
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);

    }

    private void callapi(String lat, String lng) {

        if (Util.isOnline(getApplicationContext())) {

            try {
                JSONObject obj = new JSONObject();
                obj.put("LoginId", Util.getData("LoginId", getApplicationContext()));
                obj.put("DeviceInfo", Util.getData("DeviceInfo", getApplicationContext()));
                obj.put("Lat", lat);
                obj.put("Long", lng);
               // Log.e("SERVICE CALL", obj.toString());
                String data = Util.EncryptURL(obj.toString());
                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);

                CallApi.postResponseNopgrss(getApplicationContext(), params.toString(), Util.MOBILE_TRACK, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        Util.Logcat.e("onError" + message);
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        Util.Logcat.e("onResponse" + response);
                        try {
                            String ff = Util.Decrypt(response.getString("Postresponse"));
                            JSONObject hai = new JSONObject(ff);
                            //Util.Logcat.e("OUTPUT" + hai);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.app_name) + "\nPlease check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    gpsTracker = new GpsTracker(getApplicationContext());
                    if (gpsTracker.canGetLocation()) {
                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(GPS_PROVIDER) || String.valueOf(gpsTracker.getLatitude()).equalsIgnoreCase("0.0")) {
                            Intent hai = new Intent(Location.this, LoginActivity.class);
                            hai.setFlags(FLAG_ACTIVITY_CLEAR_TASK);
                            hai.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(hai);
                        } else {
                            callapi(String.valueOf(gpsTracker.getLatitude()), String.valueOf(gpsTracker.getLongitude()));
                        }
                    }
                }
            });
        }
    }
}
