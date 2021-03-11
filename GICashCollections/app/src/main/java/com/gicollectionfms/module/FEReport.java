package com.gicollectionfms.module;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.R;
import com.gicollectionfms.adapter.BackLogAdapter;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gicollectionfms.utils.Util.CHANGEPASSWORD;
import static com.gicollectionfms.utils.Util.DASHBOARD;
import static com.gicollectionfms.utils.Util.FEREPORT;

public class FEReport extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ProgressDialog progressDialog;
    CommonAlertDialog alert;
    TextView FromDate, ToDate;
    LinearLayout FromCalender, ToCalender, LyGrid;
    Button BtnSubmit;
    TextView TxtAllocated, TxtCollected, TxtPending, TxtNotCollected, TxtPTP;
    private OnFragmentInteractionListener mListener;

    public FEReport() {
        // Required empty public constructor
    }

    public static FEReport newInstance(String param1, String param2) {
        FEReport fragment = new FEReport();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fe_report, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
                return false;
            }
        });

        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        alert = new CommonAlertDialog(getActivity());
        TxtAllocated = rootView.findViewById(R.id.allocated);
        TxtCollected = rootView.findViewById(R.id.collected);
        TxtPending = rootView.findViewById(R.id.pending);
        TxtNotCollected = rootView.findViewById(R.id.notcollected);
        TxtPTP = rootView.findViewById(R.id.ptp);
        FromDate = rootView.findViewById(R.id.from_date);
        FromDate.setText(Util.getdateonly());
        ToDate = rootView.findViewById(R.id.to_date);
        ToDate.setText(Util.getdateonly());
        FromCalender = rootView.findViewById(R.id.from_calendar);
        ToCalender = rootView.findViewById(R.id.to_calendar);
        LyGrid = rootView.findViewById(R.id.grid);
        BtnSubmit = rootView.findViewById(R.id.submit);
        BtnSubmit.setOnClickListener(this);
        FromCalender.setOnClickListener(this);
        ToCalender.setOnClickListener(this);

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:

                if (FromDate.getText().toString().isEmpty()) {
                    alert.build("Select From Date");
                } else if (ToDate.getText().toString().isEmpty()) {
                    alert.build("Select To Date");
                } else if (isDateAfter(FromDate.getText().toString(), ToDate.getText().toString()) == false) {
                    alert.build("From Date Should not be greater than To Date");
                } else {
                    GetFEReport();
                }

                break;
            case R.id.from_calendar:
                fromdate();
                break;
            case R.id.to_calendar:
                todate();
                break;
            default:
                break;
        }
    }

    private void fromdate() {

        final Calendar c = Calendar.getInstance();

        Integer mYear = c.get(Calendar.YEAR);
        Integer mMonth = c.get(Calendar.MONTH);
        Integer mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //  String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                // FromDate.setText(_data);

                if (monthOfYear <= 8 && dayOfMonth > 9) {
                    String _data = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                    FromDate.setText(_data);
                } else if (monthOfYear <= 8 && dayOfMonth <= 9) {
                    String _data = "0" + dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                    FromDate.setText(_data);
                } else {
                    if (dayOfMonth <= 9) {
                        String _data = "0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        FromDate.setText(_data);
                    } else {
                        String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        FromDate.setText(_data);
                    }

                }

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void todate() {
        final Calendar c = Calendar.getInstance();

        Integer mYear = c.get(Calendar.YEAR);
        Integer mMonth = c.get(Calendar.MONTH);
        Integer mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                // ToDate.setText(_data);
                if (monthOfYear <= 8 && dayOfMonth > 9) {
                    String _data = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                    ToDate.setText(_data);
                } else if (monthOfYear <= 8 && dayOfMonth <= 9) {
                    String _data = "0" + dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                    ToDate.setText(_data);
                } else {
                    if (dayOfMonth <= 9) {
                        String _data = "0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        ToDate.setText(_data);
                    } else {
                        String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        ToDate.setText(_data);
                    }
                }
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void GetFEReport() {

        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("FromDate", FromDate.getText().toString());
            obj.put("ToDate", ToDate.getText().toString());

            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(getActivity(), params.toString(), FEREPORT, new VolleyResponseListener() {
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
                    Util.Logcat.e("onResponse" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            JSONArray jsonArray = resobject.optJSONArray("_lstGetAppFESummaryOutputModel");
                            JSONObject imageObject = jsonArray.getJSONObject(0);
                            Log.e("LENGHT", String.valueOf(jsonArray.length()));
                            if (imageObject!=null) {
                                LyGrid.setVisibility(View.VISIBLE);
                                TxtAllocated.setText(imageObject.optString("Allocated"));
                                TxtCollected.setText(imageObject.optString("Collected"));
                                TxtNotCollected.setText(imageObject.optString("NotCollected"));
                                TxtPending.setText(imageObject.optString("CollectionPending"));
                                TxtPTP.setText(imageObject.optString("PTP"));
                            } else {
                                LyGrid.setVisibility(View.GONE);
                            }

                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                            alert.build(resobject.getString("StatusDesc"));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LyGrid.setVisibility(View.GONE);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("CHANGE PASSWORD", "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.start_stop);
        item.setVisible(false);
        MenuItem bulk_scan = menu.findItem(R.id.bulk_scan);
        bulk_scan.setVisible(false);
        MenuItem delivery = menu.findItem(R.id.delivery);
        delivery.setVisible(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isDateAfter(String startDate, String endDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);
            if (date1.after(startingDate) || date1.equals(startingDate)) {
                Log.e("VAL1", "true");
                return true;
            } else {
                Log.e("VAL2", "false");
                return false;
            }
        } catch (Exception e) {
            Log.e("VAL3", "false");
            Log.e("VAL3", String.valueOf(e));
            return false;
        }
    }
}