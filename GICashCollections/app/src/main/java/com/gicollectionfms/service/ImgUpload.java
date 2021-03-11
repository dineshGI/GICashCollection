package com.gicollectionfms.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.activity.GpsTracker;
import com.gicollectionfms.activity.LoginActivity;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.location.LocationManager.*;
import static com.gicollectionfms.utils.Util.UPLOAD;

public class ImgUpload extends Service {

    //https://deepshikhapuri.wordpress.com/2016/11/25/service-in-android/

    private Handler mHandler = new Handler();
    public static Timer mTimers = null;

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

        notify_interval= notify_interval*Long.parseLong(Util.getData("ImageUpload",getApplicationContext()));
        Log.e("ImageUpload",Util.getData("ImageUpload",getApplicationContext()));
        gpsTracker = new GpsTracker(this);
        mTimers = new Timer();
        mTimers.schedule(new TimerTaskToGetLocation(), 5, notify_interval);

    }

    private void uploadimage(String encodedImage, String waybillno, final String path, String filetype) {

        String data = "";
        JSONObject obj = new JSONObject();
        try {
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("WayBillNo", waybillno);
            obj.put("FileType", filetype);
            obj.put("Filepath", encodedImage);
            Util.Logcat.e("INPUT:::"+ obj.toString());
            data = Util.EncryptURL(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(this, params.toString(),UPLOAD, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    if (message.contains("TimeoutError")) {
                        //alert.build(getString(R.string.timeout_error));
                    } else {
                        //alert.build(getString(R.string.server_error));
                    }
                    Util.Logcat.e("onError"+ message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse"+ response);
                    try {
                        String hscsai = response.getString("Postresponse");
                        String ff = Util.Decrypt(hscsai);
                        Util.Logcat.e("OUTPUT:::"+ ff);
                        JSONObject resobject = new JSONObject(ff);

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            File target = new File(path);
                            if (target.exists() && target.isFile() && target.canWrite()) {
                                target.delete();
                                Log.d("file deleted", "" + target.getName());
                            }

                           // loadfilenames();

                        } else if(resobject.getString("Status").equalsIgnoreCase("1")){
                            //  alert.build(resobject.getString("StatusDesc"));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void loadfilenames() {
        ArrayList<String> filenames = new ArrayList<String>();
        File directory = new File(Environment.getExternalStorageDirectory()
                + File.separator + "/" + Util.getData("directory",getApplicationContext()));
        File[] files = directory.listFiles();
        Log.e("file lenght", String.valueOf(files.length));
        for (int i = 0; i < files.length; i++) {
            String file_name = files[i].getName();
            // you can store name to arraylist and use it later
            filenames.add(file_name);
        }

        Log.e("filenames", String.valueOf(filenames));

    }
    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    File path = new File(Environment.getExternalStorageDirectory()
                            + File.separator + "/" + Util.getData("directory",getApplicationContext()));
                    File[] files = path.listFiles();

                    for (int i = 0; i < files.length; i++) {

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap bitmap = BitmapFactory.decodeFile(path + "/" + files[i].getName());
                        String waybillno = files[i].getName().substring(2).replace(".jpg", "").trim();
                        String filetype;
                        if (files[i].getName().contains("P_")) {
                            filetype = "1";
                        } else {
                            filetype = "2";
                        }

                        Util.Logcat.e("waybillno:::"+ waybillno);
                        String filepath = path + "/" + files[i].getName();
                        Util.Logcat.e("Path:::"+ filepath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byte_arr = stream.toByteArray();
                        String encodedImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                        Util.Logcat.e("encodedImage"+ encodedImage);
                        ///Now set this bitmap on imageview
                        uploadimage(encodedImage, waybillno, filepath, filetype);

                    }

                }
            });
        }
    }
}
