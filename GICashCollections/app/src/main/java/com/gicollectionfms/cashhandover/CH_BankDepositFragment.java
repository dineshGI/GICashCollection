package com.gicollectionfms.cashhandover;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.KotakPickup.KotakPickup;
import com.gicollectionfms.R;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.module.HomeFragment;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.PhotoProvider;
import com.gicollectionfms.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CH_BankDepositFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //https://api.mlab.com/api/1/databases/blindapp/collections/beacons?apiKey=EVDAtEeJwaIMAwOpjOOxdN2IiMmfLDJI


    private String mParam1;
    private String mParam2;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    CommonAlertDialog alert;
    ProgressDialog progressDialog;
    List<String> spinnerlist;
    Spinner bankspinner;
    JSONArray spinnerarray;
    JSONArray array;
    TextView Amount, TxtBankName, TxtName, TxtAccNo, TxtIFSC, TxtBankAddress, TxtDateTime;
    EditText TxtUpdatedAmount, RefNo;
    private OnFragmentInteractionListener mListener;
    LinearLayout listView;
    int updatedamount = 0;
    LinearLayout BtnCalender;
    Button BtnSubmit;
    public ArrayList<String> waybillno = new ArrayList<String>();
    String BankID;
    AlertDialog.Builder alertDialogBuilder;
    String imageStoragePath;

    public CH_BankDepositFragment() {
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

    public static CH_BankDepositFragment newInstance(String param1, String param2) {
        CH_BankDepositFragment fragment = new CH_BankDepositFragment();
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

        View rootView = inflater.inflate(R.layout.ch_bankdeposit_fragment, container, false);
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
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        alert = new CommonAlertDialog(getActivity());
        alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.alertDialog);
        Amount = rootView.findViewById(R.id.total_amt);
        bankspinner = rootView.findViewById(R.id.bank_spinner);
        TxtBankName = rootView.findViewById(R.id.bank_name);
        listView = rootView.findViewById(R.id.listview);
        TxtUpdatedAmount = rootView.findViewById(R.id.updated_amount);
        updatedamount = 0;
        RefNo = rootView.findViewById(R.id.deposit_refno);
        BtnCalender = rootView.findViewById(R.id.calendar);
        TxtDateTime = rootView.findViewById(R.id.date_time);
        BtnSubmit = rootView.findViewById(R.id.submit);

        BtnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                Integer mYear = c.get(Calendar.YEAR);
                Integer mMonth = c.get(Calendar.MONTH);
                Integer mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (monthOfYear <= 8&&dayOfMonth>9) {
                            String _data = dayOfMonth + "/" + "0" + (monthOfYear+1) + "/" + year;
                            TxtDateTime.setText(_data);
                        }else if (monthOfYear <= 8 && dayOfMonth<=9 ) {
                            String _data = "0"+dayOfMonth + "/" + "0" + (monthOfYear+1) + "/" + year;
                            TxtDateTime.setText(_data);
                        } else {
                            if(dayOfMonth<=9){
                                String _data = "0"+dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                                TxtDateTime.setText(_data);
                            }else {
                                String _data = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                                TxtDateTime.setText(_data);
                            }
                        }
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String _dataHora = TxtDateTime.getText().toString() + " " + String.format("%02d:%02d", hourOfDay, minute) + ":" + "00";
                                // String _dataHora = TxtDateTime.getText().toString() + " " + "15" + ":" + "00" + ":" + "00";
                                TxtDateTime.setText(_dataHora);
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
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });

        TxtName = rootView.findViewById(R.id.account_name);
        TxtAccNo = rootView.findViewById(R.id.account_no);
        TxtIFSC = rootView.findViewById(R.id.ifsccode);
        TxtBankAddress = rootView.findViewById(R.id.bank_address);

        spinnerarray = new JSONArray();
        spinnerlist = new ArrayList<>();
        array = new JSONArray();

        bankspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //@Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                //Toast.makeText(spinner.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                String bankname = bankspinner.getSelectedItem().toString();
                Log.e("name", bankname);
                try {
                    for (int i = 0; i < spinnerarray.length(); i++) {
                        JSONObject jsonobject = spinnerarray.getJSONObject(i);
                        if (bankname.equalsIgnoreCase(jsonobject.getString("BankName"))) {
                            BankID = jsonobject.getString("BankDetailId");
                            TxtBankName.setText(jsonobject.getString("BankName"));
                            TxtName.setText(jsonobject.getString("AccountName"));
                            TxtAccNo.setText(jsonobject.getString("AccountNumber"));
                            TxtIFSC.setText(jsonobject.getString("IFSCCode"));
                            TxtBankAddress.setText(jsonobject.getString("BranchName") + " - " + jsonobject.getString("City"));
                            break;
                        }
                    }

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

                if ((Util.getData("WorkStatus",getActivity().getApplicationContext()).equalsIgnoreCase("1"))) {

                    if (waybillno.isEmpty()) {
                        alert.build("Select Amount");
                    } else if (RefNo.getEditableText().toString().isEmpty()) {
                        alert.build("Enter Reference No");
                    } else if (TxtDateTime.getText().toString().isEmpty()) {
                        alert.build("Select Date and Time");
                    } else {

                        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                        builder.setTitle("Add Photo!");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {

                                if (items[item].equals("Take Photo")) {
                                    //  PROFILE_PIC_COUNT = 1;
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                    File file = PhotoProvider.getOutputMediaFile(1);
                                    if (file != null) {
                                        imageStoragePath = file.getAbsolutePath();
                                    }

                                    Uri fileUri = PhotoProvider.getOutputMediaFileUri(getActivity(), file);

                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                    startActivityForResult(intent, REQUEST_CAMERA);

                                } else if (items[item].equals("Choose from Library")) {
                                    //  PROFILE_PIC_COUNT = 1;
                                    Intent intent = new Intent(
                                            Intent.ACTION_PICK,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, SELECT_FILE);
                                } else if (items[item].equals("Cancel")) {
                                    //  PROFILE_PIC_COUNT = 0;
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();

                    }
                } else {
                    //CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                    alert.build(getString(R.string.start_msg));
                }


            }
        });
        return rootView;
    }


    private void cashdeposit(String image) {

        StringBuffer strBuffer = new StringBuffer();
        for (String i : waybillno) { // iterate -list by list
            Log.e("i", i);
            //strBuffer.append(i);
            strBuffer.append(i).append("|");
        }

        Log.e("waybillno", strBuffer.toString());
        final String waybillno = strBuffer.toString();

        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAUserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("ImageName", "");
            obj.put("DepositedBankId", BankID);
            obj.put("CashInHand", Amount.getText().toString().substring(Amount.getText().toString().lastIndexOf("₹") + 1).trim());
            obj.put("DepositedAmount", TxtUpdatedAmount.getText().toString());
            obj.put("DepositedDT", TxtDateTime.getText().toString());
            obj.put("DepositedRefNo", RefNo.getEditableText().toString());
            obj.put("WaybillInfo", waybillno);
            obj.put("ScanImagePath", image);

            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.CH_CASHDEPOSIT, new VolleyResponseListener() {
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
                                        getbankdetails();
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
                                        getbankdetails();
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
                            alert.build(resobject.getString("StatusDesc"));
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, new KotakPickup())
                                    .addToBackStack(null)
                                    .commit();

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
                                //checkBox.setButtonTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorPrimary)));
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
                                        Log.e("updatedamount||", String.valueOf(updatedamount));
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

    private void getbankdetails() {

        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAUserId", Util.getData("UserId", getActivity().getApplicationContext()));
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.CH_GETBANKDETAILS, new VolleyResponseListener() {
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
                            JSONArray jsonArray = resobject.optJSONArray("_DepositBankDetailsModel");
                            Util.Logcat.e("length"+ jsonArray.length());
                            // count.setText("Total - " + String.valueOf(jsonArray.length()));

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                spinnerlist.add(jsonObject.optString("BankName"));
                                JSONObject savedata = new JSONObject();
                                savedata.put("ClientId", jsonObject.optString("ClientId"));
                                savedata.put("BankDetailId", jsonObject.optString("BankDetailId"));
                                savedata.put("BankName", jsonObject.optString("BankName"));
                                savedata.put("BranchName", jsonObject.optString("BranchName"));
                                savedata.put("AccountNumber", jsonObject.optString("AccountNumber"));
                                savedata.put("IFSCCode", jsonObject.optString("IFSCCode"));
                                savedata.put("City", jsonObject.optString("City"));
                                savedata.put("AccountName", jsonObject.optString("AccountName"));
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
                                    bankspinner.getBackground().setColorFilter(getActivity().getResources().getColor(
                                            R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                                    bankspinner.setAdapter(spinnerArrayAdapter);
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


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.start_stop);
        item.setVisible(false);
        MenuItem bulk_scan = menu.findItem(R.id.bulk_scan);
        bulk_scan.setVisible(false);
        MenuItem delivery = menu.findItem(R.id.delivery);
        delivery.setVisible(false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("requestCode camera", String.valueOf(requestCode));
//REQUEST_CAMERA = 1;
//    private static final int SELECT_FILE = 2;
        // if the result is capturing Image
        // if (resultCode == RESULT_OK) {

        switch (requestCode) {
            case REQUEST_CAMERA:

                try {
                    // PhotoProvider.refreshGallery(getActivity(), imageStoragePath);
                    Bitmap bitmap = PhotoProvider.optimizeBitmap(8, imageStoragePath);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    cashdeposit(encoded);

                   /* Toast.makeText(getActivity(),
                            "success", Toast.LENGTH_SHORT)
                            .show();*/

                } catch (Exception e) {

                    Log.e("Camera", e.toString());
                }
                break;
            case SELECT_FILE:
                if (data != null) {
                    Toast.makeText(getActivity(),
                            "Image Selected ", Toast.LENGTH_SHORT)
                            .show();
                   /* Uri selectedImageUri = data.getData();
                    String selectedImagePath = getRealPathFromURI(selectedImageUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    options.inSampleSize = 8;
                    Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath,
                            options);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] imageBytes = stream.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    Log.e("bitmap gallery", encodedImage);*/
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bmap = BitmapFactory.decodeFile(picturePath);
                    bmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] imageBytes = stream.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    cashdeposit(encodedImage);
                }

                break;
            default:
                break;

        }

    }

    private String getRealPathFromURI(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    @Override
    public void onStart() {
        super.onStart();
        updatedamount = 0;
        TxtDateTime.setText("");
        listView.removeAllViews();
        waybillno.clear();
        TxtUpdatedAmount.setText("0");
        RefNo.setText("");
        gettotalamount();
        getbankdetails();
        getwaybilllist();
    }


}