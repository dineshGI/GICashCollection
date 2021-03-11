package com.gicollectionfms.module;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gicollectionfms.adapter.ShipmentHistoryAdapter;
import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.R;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gicollectionfms.utils.Util.SHIPMENTHISTORY;


public class ShipmentHistory extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //https://api.mlab.com/api/1/databases/blindapp/collections/beacons?apiKey=EVDAtEeJwaIMAwOpjOOxdN2IiMmfLDJI


    private String mParam1;
    private String mParam2;

    CommonAlertDialog alert;
    ProgressDialog progressDialog;
    ShipmentHistoryAdapter adapter;
    TextView FromDate, ToDate;
    LinearLayout FromCalender, ToCalender;
    Button BtnSubmit;
    ListView listView;
    Spinner SpinType;
    String Strtype;
    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;

    private OnFragmentInteractionListener mListener;

    public ShipmentHistory() {
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

    public static ShipmentHistory newInstance(String param1, String param2) {
        ShipmentHistory fragment = new ShipmentHistory();
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

        View rootView = inflater.inflate(R.layout.shipment_history, container, false);
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
        alert = new CommonAlertDialog(getActivity());
        FromDate = rootView.findViewById(R.id.from_date);
        FromDate.setText(Util.getdateonly());
        ToDate = rootView.findViewById(R.id.to_date);
        ToDate.setText(Util.getdateonly());
        FromCalender = rootView.findViewById(R.id.from_calendar);
        ToCalender = rootView.findViewById(R.id.to_calendar);
        BtnSubmit = rootView.findViewById(R.id.submit);
        BtnSubmit.setOnClickListener(this);
        FromCalender.setOnClickListener(this);
        ToCalender.setOnClickListener(this);
        listView = rootView.findViewById(R.id.listview);
        SpinType = rootView.findViewById(R.id.type);
        String[] type = {"Pickup", "Handshake/Delivery"};
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, type);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinType.getBackground().setColorFilter(getActivity().getResources().getColor(
                R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        SpinType.setAdapter(aa);

        SpinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //@Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                //Toast.makeText(spinner.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                String type = SpinType.getSelectedItem().toString();
                if (type.equalsIgnoreCase("Pickup")) {
                    Strtype = "1";
                } else {
                    Strtype = "2";
                }
                Log.e("name", Strtype);

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        return rootView;

    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.e("Shipment History", "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:

                if (FromDate.getText().toString().isEmpty()) {
                    alert.build("Select From Date");
                } else if (ToDate.getText().toString().isEmpty()) {
                    alert.build("Select To Date");
                } else {
                    GetHistory();
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

    private void GetHistory() {



        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAUserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("FromDate", FromDate.getText().toString());
            obj.put("ToDate", ToDate.getText().toString());
            obj.put("Type", Strtype);
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.getData("appurl", getActivity().getApplicationContext()) +SHIPMENTHISTORY, new VolleyResponseListener() {
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
                    ListCollection.clear();
                    try {
                        //Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        // JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));
                        //JSONObject resobject = new JSONObject(response);
                        if (response.getString("Status").equalsIgnoreCase("0")) {

                            JSONArray jsonArray = response.getJSONObject("ds").optJSONArray("Table");
                            Util.Logcat.e("length"+ jsonArray.length());

                            if (jsonArray == null) {
                                //txtnodata.setVisibility(View.VISIBLE);
                                alert.build(getString(R.string.nodata_available));

                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    DataHashMap = new HashMap<String, String>();

                                    DataHashMap.put("RunnerName", jsonObject.optString("RunnerName"));
                                    DataHashMap.put("Area", jsonObject.optString("Area"));
                                    DataHashMap.put("WaybillNo", jsonObject.optString("WaybillNo"));
                                    DataHashMap.put("PickUpDoneDate", jsonObject.optString("PickUpDoneDate"));
                                    DataHashMap.put("LegStatus", jsonObject.optString("LegStatus"));
                                    DataHashMap.put("TASK", jsonObject.optString("TASK"));

                                    ListCollection.add(DataHashMap);
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapter = new ShipmentHistoryAdapter(getActivity(), ListCollection);
                                        listView.setAdapter(adapter);
                                        setListViewHeightBasedOnItems(listView);

                                    }
                                });
                            }
                        } else if (response.getString("Status").equalsIgnoreCase("1")) {
                            alert.build(getString(R.string.nodata_available));
                            if(adapter!=null){
                                listView.invalidateViews();
                            }
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

    private boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            //setDynamicHeight(listView);
            return true;

        } else {
            listView.invalidateViews();
            return false;
        }
    }

    private void todate() {
        final Calendar c = Calendar.getInstance();

        Integer mYear = c.get(Calendar.YEAR);
        Integer mMonth = c.get(Calendar.MONTH);
        Integer mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                ToDate.setText(_data);

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void fromdate() {

        final Calendar c = Calendar.getInstance();

        Integer mYear = c.get(Calendar.YEAR);
        Integer mMonth = c.get(Calendar.MONTH);
        Integer mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String _data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                FromDate.setText(_data);

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Shipment History");
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

}