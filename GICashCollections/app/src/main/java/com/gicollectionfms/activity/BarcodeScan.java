package com.gicollectionfms.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.Util;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gicollectionfms.R;

import org.json.JSONException;
import org.json.JSONObject;


public class BarcodeScan extends AppCompatActivity {
    //https://www.spaceotechnologies.com/qr-code-android-using-zxing-library/

    AlertDialog.Builder alertDialogBuilder;

    String appid, waybillno, orderno;
    boolean scan = false;
    private GpsTracker gpsTracker;
    String latitude, longitude;
    IntentIntegrator qrScan;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // new IntentIntegrator(this).initiateScan();
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();
        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        alertDialogBuilder.setCancelable(false);
        Intent intent = getIntent();

        String bulkscan = intent.getStringExtra("frombulkscan");

        if (Util.getData("app", getApplicationContext()).equalsIgnoreCase("onemg")) {
            orderno = intent.getStringExtra("OrderNumber");
        } else {
            orderno = intent.getStringExtra("WaybillNumber");
        }

        if (bulkscan.equalsIgnoreCase("true")) {
            scan = true;
        } else {
            appid = intent.getStringExtra("AppointmentId");
            waybillno = intent.getStringExtra("WaybillNumber");
            Log.e("appid", appid + "\n" + waybillno);
        }

        gpsTracker = new GpsTracker(BarcodeScan.this);

        if (gpsTracker.canGetLocation()) {
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result.getContents() != null) {
            if (scan == true) {
                callbulkscan(result.getContents());
            } else {
                Log.e("orderno common", orderno);
                if (orderno.equalsIgnoreCase(result.getContents())) {
                    Log.e("orderno if", orderno);
                    callapi(result.getContents());
                } else {
                    Log.e("orderno else", orderno);
                    alertDialogBuilder.setMessage("Barcode Mismatch");
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    finish();

                                    //PickupListviewAdapter dad=new PickupListviewAdapter() ;
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
        } else {
            Log.e("finish", " Log.e(\"content\", result.getContents());");
            Util.pickupfresh = true;
            alertDialogBuilder.setMessage("Closing Scanning");
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                            //PickupListviewAdapter dad=new PickupListviewAdapter() ;
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            // finish();
            // super.onActivityResult(requestCode, resultCode, data);
        }
    }

  /*  @Override
    public void handleResult(Result result) {
        Log.e("content", result.getContents()); // Prints scan results
        Log.e("name", result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
        // If you would like to resume scanning, call this method below:

        if (scan == true) {
            callbulkscan(result.getContents());
        } else {

            if (orderno.equalsIgnoreCase(result.getContents())) {
                callapi(result.getContents());
            } else {
                alertDialogBuilder.setMessage("Barcode Mismatch");
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                finish();

                                //PickupListviewAdapter dad=new PickupListviewAdapter() ;
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        }
    }*/

    private void callbulkscan(String contents) {
//        Log.e("contents",contents);
        String data = "";
        JSONObject obj = new JSONObject();
        try {
            obj.put("Latitude", "");
            obj.put("Longitude", "");
            obj.put("ClientOrderNo", contents);
            obj.put("SAUserid", Util.getData("UserId", getApplicationContext()));
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(BarcodeScan.this, params.toString(), Util.SCAN_BULKUPLOAD, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                     Util.Logcat.e("onError"+ message);
                }

                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        String hscsai = response.getString("Postresponse");
                        String ff = Util.Decrypt(hscsai);
                        Log.e("OUTPUT:::", ff);
                        JSONObject resobject = new JSONObject(ff);

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
                            alertDialogBuilder.setPositiveButton("Continue",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            finish();
                                            startActivity(getIntent());
                                            //PickupListviewAdapter dad=new PickupListviewAdapter() ;
                                        }
                                    });
                            alertDialogBuilder.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Util.refresh = true;
                                            finish();
                                            //PickupListviewAdapter dad=new PickupListviewAdapter() ;
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {

                            alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
                            alertDialogBuilder.setPositiveButton("Continue",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                            finish();
                                            startActivity(getIntent());
                                            //PickupListviewAdapter dad=new PickupListviewAdapter() ;
                                        }
                                    });
                            alertDialogBuilder.setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Util.refresh = true;
                                            finish();
                                            //PickupListviewAdapter dad=new PickupListviewAdapter() ;
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

    private void callapi(String contents) {
        String data = "";
        JSONObject obj = new JSONObject();
        try {
            obj.put("AppointmentId", appid);
            obj.put("WaybillNo", waybillno);
            obj.put("Latitude", latitude);
            obj.put("Longitude", longitude);
            obj.put("Barcode", contents);
            obj.put("UploadImg", "");
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(BarcodeScan.this, params.toString(), Util.SCAN, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                     Util.Logcat.e("onError"+ message);
                }

                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        String hscsai = response.getString("Postresponse");
                        String ff = Util.Decrypt(hscsai);
                        Log.e("OUTPUT:::", ff);
                        JSONObject resobject = new JSONObject(ff);

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
                            alertDialogBuilder.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Util.refresh = true;
                                            finish();
                                            //PickupListviewAdapter dad=new PickupListviewAdapter() ;
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {

                            alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
                            alertDialogBuilder.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            Util.refresh = true;
                                            finish();

                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();

                        } else {
                            alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
