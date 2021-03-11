package com.gicollectionfms.HDBDirect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.R;
import com.gicollectionfms.activity.AlertActivity;
import com.gicollectionfms.activity.GpsTracker;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.gicollectionfms.utils.Util.BANK_PICKUP;

public class KCCBankReason extends AppCompatActivity implements View.OnClickListener {
    RadioGroup radiogroup;
    EditText EditAmount, EditRemarks;
    TextView cancel, submit, title, TxAmount;
    private GpsTracker gpsTracker;
    AlertDialog.Builder alertDialogBuilder;
    String appid;
    static String waybillno;
    LinearLayout hidelayout;
    CommonAlertDialog alert;
    LinearLayout LayoutAmount;
    String amount, CAMERA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kcc_bank_reason);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        radiogroup = findViewById(R.id.radiobtn);
        LayoutAmount = findViewById(R.id.lyAmount);
        EditAmount = findViewById(R.id.edit_collectamount);
        EditRemarks = findViewById(R.id.edit_remarks);
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
        CAMERA = intent.getStringExtra("Direct");
        amount = intent.getStringExtra("amount");
        EditAmount.setText(amount);
        TxAmount.setText("Amount to be Collected " + "(" + amount + ")");
        Util.Logcat.e("from:::" + from);
        Util.saveData("radioselection", "nodata", getApplicationContext());

        if (from.equalsIgnoreCase("collect")) {
            Callradio();
        } else {
            LayoutAmount.setVisibility(View.GONE);
            NotCollect();
        }

    }

    private void NotCollect() {
        title.setText("Choose Not Collect Reason");
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            Util.Logcat.e("INPUT:::" + obj.toString());

            String data = Util.EncryptURL(obj.toString());
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
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse:" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));
                        Util.saveData("radioresponse", resobject.toString(), getApplicationContext());
                        JSONArray jsonArray = resobject.optJSONArray("_RRFailedModel");
                        //JSONArray jsonArray = resobject.optJSONArray("_BankFailedModel");
                        // JSONArray jsonArray = resobject.optJSONArray("_BankFailedModel");
                        if (jsonArray != null && jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                RadioButton radioButton = new RadioButton(KCCBankReason.this);
                                radioButton.setTextSize(15);
                                radioButton.setText(jsonObject.optString("PickupFailedReasonDesc"));
                                radioButton.setTextColor(getResources().getColor(R.color.text_secondary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
                                radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
                                radiogroup.addView(radioButton);
                                Util.Logcat.e("DeliveryFailed" + jsonObject.optString("DeliveryFailedReasonDesc"));
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
        RadioButton CashradioButton = new RadioButton(KCCBankReason.this);
        CashradioButton.setTextSize(15);
        CashradioButton.setText("Cash");
        CashradioButton.setTextColor(getResources().getColor(R.color.text_secondary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
        CashradioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
        radiogroup.addView(CashradioButton);
        RadioButton ChequeradioButton = new RadioButton(KCCBankReason.this);
        ChequeradioButton.setTextSize(15);
        ChequeradioButton.setText("Cheque");
        ChequeradioButton.setTextColor(getResources().getColor(R.color.text_secondary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
        ChequeradioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
        radiogroup.addView(ChequeradioButton);

        /*RadioButton OnlineradioButton = new RadioButton(KCCBankReason.this);
        OnlineradioButton.setTextSize(15);
        OnlineradioButton.setText("Online");
        OnlineradioButton.setTextColor(getResources().getColor(R.color.text_secondary));// radioButton.setId(1234);//set radiobutton id and store it somewhere
        OnlineradioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
        radiogroup.addView(OnlineradioButton);*/

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedRadioButtonId = group.getCheckedRadioButtonId();
                View radioButton = radiogroup.findViewById(checkedRadioButtonId);
                int radioId = radiogroup.indexOfChild(radioButton);
                RadioButton btn = (RadioButton) radiogroup.getChildAt(radioId);
                String selection = (String) btn.getText();
                Util.saveData("radioselection", selection, getApplicationContext());
                if (selection.equalsIgnoreCase("Online")) {
                    EditRemarks.setVisibility(View.VISIBLE);
                    EditAmount.setVisibility(View.GONE);
                } else {
                    EditRemarks.setVisibility(View.GONE);
                    EditAmount.setVisibility(View.VISIBLE);
                }
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
                } else if (EditRemarks.getVisibility() == View.VISIBLE && EditRemarks.getEditableText().toString().isEmpty()) {
                    alert.build("Enter Remarks");
                } else {
                    hidelayout.setVisibility(View.INVISIBLE);
                    //  callapiradio();
                    Util.Logcat.e("data::::" + Util.getData("radioselection", getApplicationContext()));
                    if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("Cash")) {
                        Util.Logcat.e("Cash" + Util.getData("radioselection", getApplicationContext()));
                        callapiradio("Cash", "1");
                    } else if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("Online")) {
                        Util.Logcat.e("Online" + Util.getData("radioselection", getApplicationContext()));
                        callapiradio("Online", "4");
                    } else if (Util.getData("radioselection", getApplicationContext()).equalsIgnoreCase("Cheque")) {
                        Util.Logcat.e("Cheque" + Util.getData("radioselection", getApplicationContext()));
                        //camera module
                        Intent i = new Intent(KCCBankReason.this, KCCCamModule.class);
                        i.putExtra("AppointmentId", appid);
                        i.putExtra("WaybillNumber", waybillno);
                        i.putExtra("amount", EditAmount.getEditableText().toString());
                        startActivity(i);
                        finish();
                    } else {
                        Util.Logcat.e("APPOINTMENT" + Util.getData("radioselection", getApplicationContext()));
                        Intent i = new Intent(KCCBankReason.this, KCCReasonActivity.class);
                        i.putExtra("AppointmentId", appid);
                        i.putExtra("WaybillNumber", waybillno);
                        i.putExtra("amount", EditAmount.getEditableText().toString());
                        i.putExtra("radioselection", Util.getData("radioselection", getApplicationContext()));
                        i.putExtra("camera", CAMERA);
                        startActivity(i);
                        finish();
                    } /*else {
                       Util.Logcat.e("Others",Util.getData("radioselection", getApplicationContext()));
                        CashCollectionFailed();
                    }*/
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    private void callapiradio(String cash, String cashid) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("WaybillNo", waybillno);
            obj.put("PODFilePath", "");

            if (gpsTracker.canGetLocation()) {
                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
            }

            obj.put("CollectedAmount", EditAmount.getEditableText().toString());
            //  obj.put("SAUserid", Util.getData("UserId", getApplicationContext()));

            obj.put("CheqNo", EditRemarks.getEditableText().toString());
            obj.put("BankName", "");
            obj.put("CheqDate", "");
            obj.put("SMASelection", "");

            obj.put(getString(R.string.SAUserID), Util.getData("UserId", getApplicationContext()));
            //obj.put("SAUserid", Util.getData("UserId", getApplicationContext()));
            obj.put("FEName", Util.getData("UserName", getApplicationContext()));
            obj.put("ShipmentRefNo", Util.getData("ShipmentId", getApplicationContext()));
            obj.put("Lead_Id", "");
            obj.put("Pickup_Date", Util.getdatetime());
            obj.put("SAName", Util.getData("SAName", getApplicationContext()));
            obj.put("SABranchName", Util.getData("SABranchName", getApplicationContext()));

            obj.put("PaymentMode", cash);
            obj.put("PaymentModeId", cashid);
            obj.put("MobileNo", Util.getData("SellerContactNo", getApplicationContext()));
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(KCCBankReason.this, params.toString(), BANK_PICKUP, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(getString(R.string.timeout_error));
                    } else {
                        alert.build(getString(R.string.server_error));
                    }
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse:" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            Intent alert = new Intent(KCCBankReason.this, AlertActivity.class);
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

}