package com.gicollectionfms.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.Util;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.gicollectionfms.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class Signature extends AppCompatActivity implements View.OnClickListener {


    Button BtnClear, BtnDone;
    SignaturePad mSignaturePad;
    Boolean event = false;
    // Creating Separate Directory for saving Generated Images
    String DIRECTORY;
    File file;
    String appid;
    static String waybillno, cod;
    private GpsTracker gpsTracker;
    String StoredPath;
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        alertDialogBuilder.setCancelable(false);
        gpsTracker = new GpsTracker(this);
        Intent intent = getIntent();
        appid = intent.getStringExtra("AppointmentId");
        waybillno = intent.getStringExtra("WaybillNumber");
        cod = intent.getStringExtra("COD");
        DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/" + Util.getData("directory",getApplicationContext())+"/";
        Log.e("DIRECTORY",DIRECTORY);
        file = new File(DIRECTORY);
        Log.e("file", String.valueOf(file));
        if (!file.exists()) {
            file.mkdir();
        }
        BtnClear = findViewById(R.id.clear);
        BtnClear.setOnClickListener(this);
        BtnDone = findViewById(R.id.done);
        BtnDone.setOnClickListener(this);
        mSignaturePad = findViewById(R.id.signature_pad);
        mSignaturePad.clear();
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                event = true;
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                event = false;
            }
        });
    }

    private void callapi() {
        String data = "";
        JSONObject obj = new JSONObject();
        String latitude = "";
        String longitude = "";
        if (gpsTracker.canGetLocation()) {
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());
        }
        try {
            obj.put("AppointmentId", appid);
            obj.put("WaybillNo", waybillno);
            obj.put("CollectedAmount", cod);
            obj.put("UploadImg", "");
            obj.put("Latitude", latitude);
            obj.put("Longitude", longitude);
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            Util.Logcat.e("INPUT:::"+ obj.toString());
            data = Util.EncryptURL(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(Signature.this, params.toString(),Util.DELIVERY_SUCCESS, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                     Util.Logcat.e("onError"+ message);
                }

                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            try {
                                alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
                                alertDialogBuilder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                save();
                                              //  Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                                                Util.DeliveryRefresh = true;
                                                finish();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                            alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
                            alertDialogBuilder.setPositiveButton("Ok",
                                     new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Util.DeliveryRefresh = true;
                                            finish();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
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

    private void save() {

        String root = Environment.getExternalStorageDirectory().toString();

        // the directory where the signature will be saved
        File myDir = new File(root + "/" + Util.getData("directory",getApplicationContext()));

        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        // set the file name of your choice
        String fname = "D_" + waybillno + ".jpg";

        // in our case, we delete the previous file, you can remove this
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }

        try {
            // Output the file
            FileOutputStream mFileOutStream = new FileOutputStream(file);
            // Convert the output file to Image such as .png
            mSignaturePad.getSignatureBitmap().compress(Bitmap.CompressFormat.JPEG, 90, mFileOutStream);
            mFileOutStream.flush();
            mFileOutStream.close();

        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }

        mSignaturePad.clear();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done:
                if (event) {
                    StoredPath = DIRECTORY + "D_" + waybillno + ".png";
                    // save();
                    callapi();
                    Log.e("data", "available");
                } else {

                    Log.e("data", "NO data");
                    alertDialogBuilder.setMessage("Enter Signature");
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
                break;
            case R.id.clear:
                event = false;
                mSignaturePad.clear();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
