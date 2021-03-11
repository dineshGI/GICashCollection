package com.gicollectionfms.cashhandover;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.module.HomeFragment;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CH_PersonFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //https://api.mlab.com/api/1/databases/blindapp/collections/beacons?apiKey=EVDAtEeJwaIMAwOpjOOxdN2IiMmfLDJI


    private String mParam1;
    private String mParam2;
    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    List<String> spinnerlist;
    Spinner namespinner;
    LinearLayout listView;
    //  public CHPersonAdapter adapter;
    CommonAlertDialog alert;
    AlertDialog.Builder alertDialogBuilder;
    TextView Amount, RESENDOTP;
    JSONArray array;
    JSONArray spinnerarray;

    Button BtnSubmit;
    EditText TxtUpdatedAmount, OTP;

    ProgressDialog progressDialog;
    int updatedamount = 0;
    public ArrayList<String> waybillno = new ArrayList<String>();
    String USERID, LEGID;

    private OnFragmentInteractionListener mListener;

    public CH_PersonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */

    public static CH_PersonFragment newInstance(String param1, String param2) {
        CH_PersonFragment fragment = new CH_PersonFragment();
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

        View rootView = inflater.inflate(R.layout.ch_person_fragment, container, false);
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
        ListCollection = new ArrayList<Map<String, String>>();

        spinnerlist = new ArrayList<>();
        listView = rootView.findViewById(R.id.listview);
        namespinner = rootView.findViewById(R.id.name_spinner);
        Amount = rootView.findViewById(R.id.total_amt);
        RESENDOTP = rootView.findViewById(R.id.resendotp);
        TxtUpdatedAmount = rootView.findViewById(R.id.updated_amount);
        alert = new CommonAlertDialog(getActivity());
        alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.alertDialog);
        BtnSubmit = rootView.findViewById(R.id.submit);
        OTP = rootView.findViewById(R.id.otp);
        array = new JSONArray();
        spinnerarray = new JSONArray();

        updatedamount = 0;


        namespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //@Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub

                //Toast.makeText(spinner.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                String name = namespinner.getSelectedItem().toString();
                Log.e("name", name);
                try {
                    for (int i = 0; i < spinnerarray.length(); i++) {
                        JSONObject jsonobject = spinnerarray.getJSONObject(i);
                        String userid = jsonobject.getString("SAUserId");
                        if (name.equalsIgnoreCase(jsonobject.getString("EmployeeName"))) {
                            USERID = userid;
                            break;
                        }
                    }
                    Log.e("userid", USERID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Util.getData("WorkStatus", getActivity().getApplicationContext()).equalsIgnoreCase("1"))) {
                    if (BtnSubmit.getText().toString().equalsIgnoreCase("Submit")) {
                        //waybillno.clear();
                        //  if (TxtUpdatedAmount.getText().toString().equalsIgnoreCase("0") || TxtUpdatedAmount.getText().toString().isEmpty()) {
                        if (waybillno.isEmpty()) {
                            alert.build("Please select amount");
                        } else {
                            StringBuffer strBuffer = new StringBuffer();
                            for (String i : waybillno) { // iterate -list by list
                                Log.e("i", i);
                                //strBuffer.append(i);
                                strBuffer.append(i).append("|");
                            }

                            Log.e("WaybillInfo", strBuffer.toString());
                            codhandover(strBuffer.toString());
                        }
                    } else {
                        if (OTP.getEditableText().toString().isEmpty()) {
                            alert.build("Enter OTP");
                        } else {
                            otpverification();
                        }

                    }
                } else {
                    //CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                    alert.build(getString(R.string.start_msg));
                }


                //if()
                // array.remove()
            }
        });

        RESENDOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendotp();
            }
        });

        return rootView;
    }

    private void resendotp() {

        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("LedgerId", LEGID);
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.CH_RESENDOTP, new VolleyResponseListener() {
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

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            // alert.build(resobject.getString("StatusDesc"));
                            alert.build(resobject.getString("StatusDesc"));


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

    private void otpverification() {


        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAUserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("HandOverTo", USERID);
            obj.put("LedgerId", LEGID);
            obj.put("HandOverAmount", TxtUpdatedAmount.getText().toString());
            obj.put("OTP", OTP.getEditableText().toString());
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(),Util.CH_OTPVERIFY, new VolleyResponseListener() {
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

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            // alert.build(resobject.getString("StatusDesc"));
                            alert.build(resobject.getString("StatusDesc"));
                            waybillno.clear();

                           // getActivity().onBackPressed();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame,new HomeFragment())
                                    .addToBackStack(null)
                                    .commit();
                            //reload fragment
                          /*  Fragment fragment = new HomeFragment();
                            //replacing the fragment
                            if (fragment != null) {
                               // FragmentTransaction ft = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.frame, fragment);
                                ft.addToBackStack(null);
                                //ft.addToBackStack("home");
                                ft.commit();
                            }*/
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



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      /*  if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
        Log.e("Handshake", "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }



    private void getwaybilllist() {

        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAUserId", Util.getData("UserId", getActivity().getApplicationContext()));
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.CH_WAYBILLLIST, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(getString(R.string.timeout_error));
                    } else {
                        alert.build(getString(R.string.server_error));
                    }
                     Util.Logcat.e("onError"+ message);
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("_WaybillnoCOD");

                            // count.setText("Total - " + String.valueOf(jsonArray.length()));
                            ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                CheckBox checkBox = new CheckBox(getActivity());
                                checkBox.setLayoutParams(lparams);
                                checkBox.setText(jsonObject.optString("ShipmentRefId") + " - " + "Rs." + jsonObject.optString("CODCollectedAmount"));
                                checkBox.setTextSize(15);
                                //       checkBox.setButtonTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorPrimary)));
                                JSONObject savedata = new JSONObject();
                                savedata.put("ShipmentRefId", jsonObject.optString("ShipmentRefId"));
                                savedata.put("CODCollectedAmount", jsonObject.optString("CODCollectedAmount"));
                                savedata.put("WaybillNumber", jsonObject.optString("WaybillNumber"));

                                array.put(savedata);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    checkBox.setButtonTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorPrimary)));
                                }
                                //
                                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        String string = buttonView.getText().toString();
                                        String amount = string.substring(string.lastIndexOf(".") + 1).trim();
                                        String shipmentid = string.substring(0, string.indexOf("-") + 1).replace("-", "").trim();
                                        Log.e("shipmentid", shipmentid);

                                        if (isChecked) {
                                            updatedamount = updatedamount + Integer.parseInt(amount);
                                            try {
                                                for (int i = 0; i < array.length(); i++) {
                                                    JSONObject jsonobject = array.getJSONObject(i);
                                                    String shipmentrefid = jsonobject.getString("ShipmentRefId");
                                                    String wb = jsonobject.getString("WaybillNumber");

                                                    if (shipmentrefid.equalsIgnoreCase(shipmentid)) {
                                                        Log.e("if", shipmentid + "-" + shipmentrefid);
                                                        waybillno.add(wb);
                                                        Log.e("add", wb);
                                                        break;
                                                    }

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            updatedamount = updatedamount - Integer.parseInt(amount);
                                            try {

                                                for (int i = 0; i < array.length(); i++) {
                                                    JSONObject jsonobject = array.getJSONObject(i);
                                                    String shipmentrefid = jsonobject.getString("ShipmentRefId");
                                                    String wb = jsonobject.getString("WaybillNumber");
                                                    if (shipmentrefid.equalsIgnoreCase(shipmentid)) {
                                                        waybillno.remove(wb);
                                                        break;
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Log.e("WaybillNumber", waybillno.toString());

                                        TxtUpdatedAmount.setText(String.valueOf(updatedamount));

                                    }
                                });
                                listView.addView(checkBox);
                                //   DataHashMap.put("ShipmentRefId", jsonObject.optString("ShipmentRefId").toString());
                                //   DataHashMap.put("CODCollectedAmount", jsonObject.optString("CODCollectedAmount").toString());
                            }
                            Log.e("waybilllistarray", array.toString());
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

    private void codhandover(String WaybillInfo) {

        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAUserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("HandOverTo", USERID);
            obj.put("CashInHand", Amount.getText().toString().substring(Amount.getText().toString().lastIndexOf("₹") + 1).trim());
            obj.put("HandOverAmount", TxtUpdatedAmount.getText().toString());
            obj.put("WaybillInfo", WaybillInfo);
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.CH_CODHANDOVER, new VolleyResponseListener() {
                @Override
                public void onError(String message) {


                    if (message.contains("TimeoutError")) {

                        alertDialogBuilder.setMessage(getString(R.string.timeout_error));
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        updatedamount = 0;
                                        TxtUpdatedAmount.setText("");
                                        gettotalamount();
                                        getnamelist();
                                        getwaybilllist();
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
                                        updatedamount = 0;
                                        TxtUpdatedAmount.setText("");
                                        gettotalamount();
                                        getnamelist();
                                        getwaybilllist();

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

                            // alert.build(resobject.getString("StatusDesc"));
                            LEGID = resobject.getString("LedgerId");
                            alert.build(resobject.getString("StatusDesc"));
                            OTP.setVisibility(View.VISIBLE);
                            RESENDOTP.setVisibility(View.VISIBLE);
                            BtnSubmit.setText("VERIFY OTP");

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

    private void getnamelist() {

        String data = "";
        try {
            JSONObject obj = new JSONObject();
          //  obj.put("SAId", Util.getData("SAId", getActivity().getApplicationContext()));
            obj.put("SAUserId", Util.getData("UserId", getActivity().getApplicationContext()));
            //obj.put("BranchId", Util.getData("SABranchId", getActivity().getApplicationContext()));
           // obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.CH_NAMELIST, new VolleyResponseListener() {
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
                        Log.e("NAME:::", Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("_SABCHUserModel");
                            Util.Logcat.e("length"+ jsonArray.length());
                            // count.setText("Total - " + String.valueOf(jsonArray.length()));

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                spinnerlist.add(jsonObject.optString("EmployeeName"));
                                JSONObject savedata = new JSONObject();
                                savedata.put("EmployeeName", jsonObject.optString("EmployeeName"));
                                savedata.put("SAUserId", jsonObject.optString("SAUserId"));
                                spinnerarray.put(savedata);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                            (getActivity(), android.R.layout.simple_spinner_item,
                                                    spinnerlist); //selected item will look like a spinner set from XML
                                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                            .simple_spinner_dropdown_item);
                                    namespinner.getBackground().setColorFilter(getActivity().getResources().getColor(
                                            R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    namespinner.setAdapter(spinnerArrayAdapter);
                                }
                            });

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

    private void gettotalamount() {

        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAUserId", Util.getData("UserId", getActivity().getApplicationContext()));
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.CH_TOTALAMOUNT, new VolleyResponseListener() {
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

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            Amount.setText("Cash In Hand : ₹ " + resobject.getString("CashInHand"));

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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.start_stop);
        item.setVisible(false);
        MenuItem bulk_scan = menu.findItem(R.id.bulk_scan);
        bulk_scan.setVisible(false);
        MenuItem delivery = menu.findItem(R.id.delivery);
        delivery.setVisible(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        waybillno.clear();
        listView.removeAllViews();
        TxtUpdatedAmount.setText("0");
        updatedamount = 0;
        gettotalamount();
        getnamelist();
        getwaybilllist();
    }
}