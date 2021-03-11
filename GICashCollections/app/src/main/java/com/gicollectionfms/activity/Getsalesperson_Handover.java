package com.gicollectionfms.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class Getsalesperson_Handover extends AppCompatActivity implements View.OnClickListener {
    RadioGroup radiogroup;
    TextView cancel, submit, title;
    private GpsTracker gpsTracker;
    AlertDialog.Builder alertDialogBuilder;
    String assignedto;
    static String waybillno, appoinmentinfo;
    LinearLayout hidelayout;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radio);
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        radiogroup = findViewById(R.id.radiobtn);
        cancel = findViewById(R.id.btn_cancel);
        hidelayout = findViewById(R.id.dialog_layout);
        cancel.setOnClickListener(this);
        submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(this);
        title = findViewById(R.id.radiotitle);
        title.setText("Select Handover User");
        gpsTracker = new GpsTracker(this);
        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        Intent intent = getIntent();
        appoinmentinfo = intent.getStringExtra("appoinmentinfo");
        assignedto = intent.getStringExtra("assignedto");
        waybillno = intent.getStringExtra("waybillno");
        Util.saveData("nameselection", "nodata", getApplicationContext());
        getsalesperson();
    }

    private void getsalesperson() {
        String data = "";
        JSONObject obj = new JSONObject();
        try {

            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("SAId", Util.getData("SAId", getApplicationContext()));
            obj.put("BranchId", Util.getData("SABranchId", getApplicationContext()));
            obj.put("WaybillNo", waybillno);
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(this, params.toString(),Util.GET_SALESPERSON, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                     Util.Logcat.e("onError"+ message);
                }

                @SuppressLint("WrongConstant")
                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        jsonArray = resobject.optJSONArray("_RoadrunnerUser");

                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                jsonObject.optString("SalesPersonName");
                                RadioButton radioButton = new RadioButton(Getsalesperson_Handover.this);
                                radioButton.setTextSize(15);
                                radioButton.setText(jsonObject.optString("SalesPersonName"));

                                radioButton.setTextColor(getResources().getColor(R.color.text_secondary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
                                radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
                                radiogroup.addView(radioButton);
                                Log.e("SalesPersonName", jsonObject.optString("SalesPersonName"));
                            }

                            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {

                                    int checkedRadioButtonId = group.getCheckedRadioButtonId();
                                    View radioButton = radiogroup.findViewById(checkedRadioButtonId);
                                    int radioId = radiogroup.indexOfChild(radioButton);
                                    RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                                    String selection = (String) btn.getText();
                                    Util.saveData("nameselection", selection, getApplicationContext());

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:

                if (Util.getData("nameselection", getApplicationContext()).equalsIgnoreCase("nodata")) {
                    Toast.makeText(this, "Select Option", Toast.LENGTH_SHORT).show();

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
                    // TaskHandSack();
                    if (jsonArray != null && jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if ((Util.getData("nameselection", getApplicationContext()).equalsIgnoreCase(jsonObject.optString("SalesPersonName")))) {
                                    assignedto = jsonObject.optString("SAUserId");
                                    Log.e("assignedto", assignedto);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    CallBulkHandover(appoinmentinfo);
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


    private void CallBulkHandover(String appoinmentinfo) {
        {
            String data = "";
            JSONObject obj = new JSONObject();
            try {
                obj.put("AssignedTo", assignedto);
                obj.put("AppointmentInfo", appoinmentinfo);
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
                CallApi.postResponse(this, params.toString(),Util.HD_HANDOVER_BULK, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {

                        if (message.contains("TimeoutError")) {
                            alertDialogBuilder.setMessage(getString(R.string.timeout_error));
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
                        } else {
                            alertDialogBuilder.setMessage(getString(R.string.server_error));
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

    }
}
