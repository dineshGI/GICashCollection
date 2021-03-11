package com.gicollectionfms.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.R;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.gicollectionfms.utils.Util.BANK_PICKUP;

public class BankReason extends AppCompatActivity implements View.OnClickListener {
    RadioGroup radiogroup;
    EditText EditAmount;
    TextView cancel, submit, title,TxAmount;
    private GpsTracker gpsTracker;
    AlertDialog.Builder alertDialogBuilder;
    String appid;
    static String waybillno;
    LinearLayout hidelayout;
    CommonAlertDialog alert;
    LinearLayout LayoutAmount;
    String amount;

    //9840287157
    //307418

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_reason);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        radiogroup = findViewById(R.id.radiobtn);
        LayoutAmount = findViewById(R.id.lyAmount);
        EditAmount = findViewById(R.id.edit_collectamount);
        TxAmount = findViewById(R.id.showamount);
        cancel = findViewById(R.id.btn_cancel);
        hidelayout = findViewById(R.id.dialog_layout);
        cancel.setOnClickListener(this);
        title = findViewById(R.id.radiotitle);
//        title.setText("Choose Payment Option");
        submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(this);
        alert = new CommonAlertDialog(this);
        gpsTracker = new GpsTracker(this);
        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        alertDialogBuilder.setCancelable(false);
        Intent intent = getIntent();
        appid = intent.getStringExtra("AppointmentId");
        waybillno = intent.getStringExtra("WaybillNumber");
        String from = intent.getStringExtra("from");
        amount = intent.getStringExtra("amount");
        EditAmount.setText(amount);
        TxAmount.setText("Amount to be Collected "+"("+amount+")");
        Log.e("from:::", from);
        Util.saveData("radioselection", "nodata", getApplicationContext());

        if (from.equalsIgnoreCase("collect")) {
            Callradio();
        } else {
            LayoutAmount.setVisibility(View.GONE);
            Callradiobutton();
        }

    }

    private void Callradiobutton() {
        title.setText("Choose Not Collect Reason");
        String data = "";
        JSONObject obj = new JSONObject();
        try {

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
            CallApi.postResponse(this, params.toString(), Util.RADIOBUTTON, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(getString(R.string.timeout_error));
                    } else {
                        alert.build(getString(R.string.server_error));
                    }
                     Util.Logcat.e("onError"+ message);
                }

                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        JSONArray jsonArray = resobject.optJSONArray("_BankFailedModel");
                        // JSONArray jsonArray = resobject.optJSONArray("_BankFailedModel");
                        if (jsonArray != null && jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                RadioButton radioButton = new RadioButton(BankReason.this);
                                radioButton.setTextSize(15);
                                radioButton.setText(jsonObject.optString("PickupFailedReasonDesc"));
                                radioButton.setTextColor(getResources().getColor(R.color.text_secondary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
                                radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
                                radiogroup.addView(radioButton);
                                Log.e("DeliveryFailed", jsonObject.optString("DeliveryFailedReasonDesc"));
                            }

                            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {

                                    int checkedRadioButtonId = group.getCheckedRadioButtonId();
                                    View radioButton = radiogroup.findViewById(checkedRadioButtonId);
                                    int radioId = radiogroup.indexOfChild(radioButton);
                                    RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                                    String selection = (String) btn.getText();
                                    Util.saveData("radioselection", selection, getApplicationContext());

                                }
                            });
                        } else {
                            alertDialogBuilder.setMessage(getString(R.string.server_empty));
                            alertDialogBuilder.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialogBuilder.setCancelable(false);
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

    private void Callradio() {

        title.setText("Choose Payment Option");
        RadioButton CashradioButton = new RadioButton(BankReason.this);
        CashradioButton.setTextSize(15);
        CashradioButton.setText("Cash");
        CashradioButton.setTextColor(getResources().getColor(R.color.text_secondary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
        CashradioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
        radiogroup.addView(CashradioButton);
        RadioButton ChequeradioButton = new RadioButton(BankReason.this);
        ChequeradioButton.setTextSize(15);
        ChequeradioButton.setText("Cheque");
        ChequeradioButton.setTextColor(getResources().getColor(R.color.text_secondary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
        ChequeradioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
        radiogroup.addView(ChequeradioButton);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int checkedRadioButtonId = group.getCheckedRadioButtonId();
                View radioButton = radiogroup.findViewById(checkedRadioButtonId);
                int radioId = radiogroup.indexOfChild(radioButton);
                RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                String selection = (String) btn.getText();
                Util.saveData("radioselection", selection, getApplicationContext());

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:

                if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("nodata")) {
                    alert.build(title.getText().toString());
                } else if (!(Float.parseFloat(EditAmount.getEditableText().toString()) >= Float.parseFloat(amount))) {
                    alert.build("Amount Should not be less than Promised Amount");
                } else {
                    hidelayout.setVisibility(View.INVISIBLE);
                    //  callapiradio();
                    Log.e("data::::", Util.getData("radioselection", getApplicationContext()));

                    if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("Cash")) {
                        callapiradio();
                    } else if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("Cheque")) {
                        //camera module
                        Intent i = new Intent(BankReason.this, BankCamModule.class);
                        i.putExtra("AppointmentId", appid);
                        i.putExtra("WaybillNumber", waybillno);
                        i.putExtra("amount", EditAmount.getEditableText().toString());
                        startActivity(i);
                        finish();
                    } else {
                        CashCollectionFailed();
                    }
                }
                break;
            case R.id.btn_cancel:
                //hidelayout.setVisibility(View.INVISIBLE);
                finish();
                break;
            default:
                break;
        }
    }

    private void CashCollectionFailed() {

        {
            String data = "";
            JSONObject obj = new JSONObject();


           /* WaybillNo VARCHAR(50) ,
                @Latitude VARCHAR(20),
                @Longitude VARCHAR(20),
                @PODFilePath VARCHAR(MAX),
                @CollectedAmount DECIMAL(12,2),
                @SAUserid INT*/

            try {
                obj.put("UserId", Util.getData("UserId", getApplicationContext()));
                obj.put("AppointmentId", appid);
                obj.put("WaybillNo", waybillno);
                obj.put("PickupFailedUpdate", Util.getData("radioselection", getApplicationContext()));
                if (gpsTracker.canGetLocation()) {
                    obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                    obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
                }
                Util.Logcat.e("INPUT:::"+ obj.toString());
                // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
                data = Util.EncryptURL(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);
                // CallApi.postResponse(this, params.toString(), Util.RADIOBUTTON_UPDATE, new VolleyResponseListener() {
                CallApi.postResponse(this, params.toString(), Util.RADIOBUTTON_UPDATE, new VolleyResponseListener() {
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
                            }
                            Util.saveData("radioselection", "nodata", getApplicationContext());

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

    private void callapiradio() {
        String data = "";
        JSONObject obj = new JSONObject();


        try {
            // obj.put("AppointmentId", appid);
            obj.put("WaybillNo", waybillno);
            // obj.put("Barcode", "");
            obj.put("PODFilePath", "");
            if (gpsTracker.canGetLocation()) {
                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
            }
            obj.put("CollectedAmount", EditAmount.getEditableText().toString());
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
            //CallApi.postResponseNopgrss(BankReason.this, params.toString(), Util.SCAN, new VolleyResponseListener() {
            CallApi.postResponseNopgrss(BankReason.this, params.toString(), BANK_PICKUP, new VolleyResponseListener() {
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

                            Intent alert = new Intent(BankReason.this, AlertActivity.class);
                            alert.putExtra("status", resobject.getString("Status"));
                            alert.putExtra("StatusDesc", resobject.getString("StatusDesc"));
                            startActivity(alert);
                            finish();

                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {

                            alert.build(resobject.getString("StatusDesc"));
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

    private void callapiradio1() {


        String data = "";
        JSONObject obj = new JSONObject();
        String latitude = "";
        String longitude = "";
        if (gpsTracker.canGetLocation()) {
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());

        }
        try {
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("AppointmentId", appid);
            obj.put("WaybillNo", waybillno);
            obj.put("DeliveryFailedUpdate", Util.getData("radioselection", getApplicationContext()));
            obj.put("UploadImg", "");
            obj.put("Latitude", latitude);
            obj.put("Longitude", longitude);
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(this, params.toString(), Util.UPDATE_FAILED_REASON, new VolleyResponseListener() {
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
                        Util.saveData("radioselection", "nodata", getApplicationContext());

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

//https://stackoverflow.com/questions/32535235/how-to-add-a-radiobutton-dynamically-with-custom-width
//https://stackoverflow.com/questions/32520850/create-a-custom-dialog-with-radio-buttons-list

