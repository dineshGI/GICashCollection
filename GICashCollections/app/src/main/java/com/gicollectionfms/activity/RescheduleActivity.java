package com.gicollectionfms.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class RescheduleActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialogBuilder;
    String waybillno;
    TextView TxtDate;
    LinearLayout ImgCalender;
    EditText Reason;
    Button submit;
//Collect Reason
    //https://stackoverflow.com/questions/41295784/how-to-call-a-function-of-fragment-from-custom-adapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reschedule);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        TxtDate = findViewById(R.id.reschedule_datetime);
        TxtDate.setText(Util.getdatetime());
        ImgCalender = findViewById(R.id.reschedule_calendar);
        ImgCalender.setOnClickListener(this);
        Reason = findViewById(R.id.reschedule_reason);
        submit = findViewById(R.id.reschedule_submit);
        submit.setOnClickListener(this);

        waybillno = getIntent().getStringExtra("WaybillNumber");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reschedule_submit:

                if (Reason.getEditableText().toString().isEmpty()) {
                    alertDialogBuilder.setMessage(getString(R.string.enter_reason));
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
                    updatereason();
                }

                break;
            case R.id.reschedule_calendar:

                getdate();
                break;
            default:
                break;
        }
    }

    private void updatereason() {

        {
            String data = "";
            JSONObject obj = new JSONObject();

            try {
                obj.put("UserId", Util.getData("UserId", getApplicationContext()));
                obj.put("WaybillNo", waybillno);
                obj.put("RescheduleDate", TxtDate.getText().toString());
                obj.put("Reason", Reason.getEditableText().toString());
                Util.Logcat.e("INPUT:::"+ obj.toString());
                // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
                data = Util.EncryptURL(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);
                CallApi.postResponse(RescheduleActivity.this, params.toString(), Util.getData("appurl",getApplicationContext()) + Util.RESCHEDULE, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {

                        if (message.contains("TimeoutError")) {

                            alertDialogBuilder.setMessage(getString(R.string.timeout_error));
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
                        } else {
                            alertDialogBuilder.setMessage(getString(R.string.server_error));
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

    private void getdate() {
        final Calendar c = Calendar.getInstance();
        Integer mYear = c.get(Calendar.YEAR);
        Integer mMonth = c.get(Calendar.MONTH);
        Integer mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(RescheduleActivity.this, R.style.DatePickerDialogTheme,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                TxtDate.setText(_data);
                TimePickerDialog timePickerDialog = new TimePickerDialog(RescheduleActivity.this, R.style.DatePickerDialogTheme,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String AM_PM = " AM";
                        String mm_precede = "";
                        if (hourOfDay >= 12) {
                            AM_PM = " PM";
                            if (hourOfDay >= 13 && hourOfDay < 24) {
                                hourOfDay -= 12;
                            } else {
                                hourOfDay = 12;
                            }
                        } else if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        if (minute < 10) {
                            mm_precede = "0";
                        }

                        String _dataHora = TxtDate.getText().toString() + " " + hourOfDay + ":" + mm_precede + minute + AM_PM;

                        TxtDate.setText(_dataHora);
                    }
                }, 0, 0, false);
                timePickerDialog.setCancelable(false);
                timePickerDialog.show();
                timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
    }
}
