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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RadioButtonActivity extends AppCompatActivity implements View.OnClickListener {
    RadioGroup radiogroup;
    TextView cancel, submit, title;
    private GpsTracker gpsTracker;
    AlertDialog.Builder alertDialogBuilder;
    String appid;
    static String waybillno;
    LinearLayout hidelayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        radiogroup = findViewById(R.id.radiobtn);
        cancel = findViewById(R.id.btn_cancel);
        title = findViewById(R.id.radiotitle);
        title.setText("Select Reason");
        hidelayout = findViewById(R.id.dialog_layout);
        cancel.setOnClickListener(this);
        submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(this);
        gpsTracker = new GpsTracker(this);
        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        Intent intent = getIntent();
        appid = intent.getStringExtra("AppointmentId");
        waybillno = intent.getStringExtra("WaybillNumber");
        Util.saveData("radioselection", "nodata", getApplicationContext());
        Callradiobutton();
    }

    private void Callradiobutton() {
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
                    if (message.equalsIgnoreCase("TimeoutError")) {
                        alertDialogBuilder.setMessage(getString(R.string.timeout_error));
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    } else {
                        alertDialogBuilder.setMessage(getString(R.string.server_error));
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
                }

                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        JSONArray jsonArray = resobject.optJSONArray("_RRFailedModel");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            jsonObject.optString("PickupFailedReasonDesc");
                            RadioButton radioButton = new RadioButton(RadioButtonActivity.this);
                            radioButton.setTextSize(15);
                            radioButton.setText(jsonObject.optString("PickupFailedReasonDesc"));
                            radioButton.setTextColor(getResources().getColor(R.color.text_secondary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
                            radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
                            radiogroup.addView(radioButton);
                            Log.e("PickupFailedReasonDesc", jsonObject.optString("PickupFailedReasonDesc"));
                        }
                        // now that the dialog is set up, it's time to show it
                        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {

                                String text = group.toString();
                                int checkedRadioButtonId = group.getCheckedRadioButtonId();
                                View radioButton = radiogroup.findViewById(checkedRadioButtonId);
                                int radioId = radiogroup.indexOfChild(radioButton);
                                RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                                String selection = (String) btn.getText();
                                Toast.makeText(getApplicationContext(), selection, Toast.LENGTH_SHORT).show();

                            }
                        });
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:

                if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("nodata")) {
                    Toast.makeText(this, getResources().getString(R.string.select_reason), Toast.LENGTH_SHORT).show();
                    alertDialogBuilder.setMessage(title.getText().toString());
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } else {
                    hidelayout.setVisibility(View.INVISIBLE);
                    callapiradio();
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

    private void callapiradio() {

        {
            String data = "";
            JSONObject obj = new JSONObject();

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

    //https://stackoverflow.com/questions/32535235/how-to-add-a-radiobutton-dynamically-with-custom-width
    //https://stackoverflow.com/questions/32520850/create-a-custom-dialog-with-radio-buttons-list
}
