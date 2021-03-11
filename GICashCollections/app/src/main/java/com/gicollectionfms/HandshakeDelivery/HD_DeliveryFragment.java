package com.gicollectionfms.HandshakeDelivery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;
import com.gicollectionfms.activity.Getsalesperson_Transfer;
import com.gicollectionfms.interfaces.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HD_DeliveryFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //https://api.mlab.com/api/1/databases/blindapp/collections/beacons?apiKey=EVDAtEeJwaIMAwOpjOOxdN2IiMmfLDJI


    private String mParam1;
    private String mParam2;

    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    List<Map<String, String>> searchResults;
    ListView listView;
    public HDDeliveryAdapter adapter;
    AlertDialog.Builder alertDialogBuilder;
    TextView count;
    private EditText filterText;
    private List<Map<String, String>> holditems;
    // AlertDialog.Builder alertDialogBuilder;

    ProgressDialog progressDialog;

    CommonAlertDialog alert;

    private OnFragmentInteractionListener mListener;

    public ArrayList<String> legtrackid = new ArrayList<String>();
    public ArrayList<String> waybillno = new ArrayList<String>();
    String assignedto;
    Button BtnTransfer;

    public HD_DeliveryFragment() {
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

    public static HD_DeliveryFragment newInstance(String param1, String param2) {
        HD_DeliveryFragment fragment = new HD_DeliveryFragment();
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

        View rootView = inflater.inflate(R.layout.hd_delivery_fragment, container, false);
        // Inflate the layout for this fragment
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

        alert = new CommonAlertDialog(getActivity());
        alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.alertDialog);
        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage("Loading...");
        ListCollection = new ArrayList<Map<String, String>>();
        holditems = new ArrayList<Map<String, String>>();
        listView = rootView.findViewById(R.id.listview);
        BtnTransfer = rootView.findViewById(R.id.transfer);
        filterText = rootView.findViewById(R.id.search);
        count = rootView.findViewById(R.id.count);
        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        BtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuffer strBuffer = new StringBuffer();
                for (String i : legtrackid) { // iterate -list by list
                    Log.e("i", i);
                    //strBuffer.append(i);
                    strBuffer.append(i).append("|");
                }

                Log.e("handoveritem", strBuffer.toString());
                // CallBulkHandover(strBuffer.toString());

                Intent calltrasfer = new Intent(getActivity(), Getsalesperson_Transfer.class);
                calltrasfer.putExtra("appoinmentinfo", strBuffer.toString());
                calltrasfer.putExtra("waybillno", String.valueOf(waybillno.get(0)));

                getActivity().startActivity(calltrasfer);
            }
        });
        loadlistdelivery();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Util.DeliveryRefresh == true) {
            //Toast.makeText(getActivity(), "Calling ONRESUME", Toast.LENGTH_SHORT).show();
            loadlistdelivery();
            Util.DeliveryRefresh = false;
        } else {
            //Toast.makeText(getActivity(), "ONRESUME Failed", Toast.LENGTH_SHORT).show();
        }
        // loadlist();
    }

    private void loadlistdelivery() {
        filterText.setText("");
        waybillno.clear();
        legtrackid.clear();
        //name.clear();
        BtnTransfer.setVisibility(View.GONE);
        assignedto = "";
        ListCollection.clear();

        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAId", Util.getData("SAId", getActivity().getApplicationContext()));
            obj.put("SABranchId", Util.getData("SABranchId", getActivity().getApplicationContext()));
            obj.put("TaskType", "2");
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("Filter", "0");
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.HD_DELIVERY_LIST, new VolleyResponseListener() {
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
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("Delivery");

                            if (jsonArray == null) {
                                //txtnodata.setVisibility(View.VISIBLE);
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                alert.build(getString(R.string.nodelivery_available));
                                count.setText("Total - " + "0");
                            } else {
                                Util.Logcat.e("length"+ jsonArray.length());
                                count.setText("Total - " + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    JSONArray imageArray = jsonObject.getJSONArray("orders");
                                    JSONObject orders = imageArray.getJSONObject(0);

                                    DataHashMap = new HashMap<String, String>();
                                    if (jsonObject.optString("DeliveryType").equalsIgnoreCase("0") || jsonObject.optString("DeliveryType").equalsIgnoreCase("3")) {
                                        DataHashMap.put("ShipmentId", jsonObject.optString("ShipmentId"));
                                        DataHashMap.put("CreatedDT", jsonObject.optString("CreatedDT"));
                                        DataHashMap.put("LegTrackId", jsonObject.optString("LegTrackId"));
                                        DataHashMap.put("ConsigneeName", jsonObject.optString("ConsigneeName"));
                                        DataHashMap.put("Address", jsonObject.optString("ConsigneeAdd1") + "," + jsonObject.optString("ConsigneeState") + "-" + jsonObject.optString("ConsigneePin"));
                                        DataHashMap.put("ConsigneeMobileNo", jsonObject.optString("ConsigneeMobileNo"));
                                        DataHashMap.put("DeliveryAttemptCount", jsonObject.optString("DeliveryAttemptCount"));
                                        DataHashMap.put("WaybillNumber", jsonObject.optString("WaybillNumber"));

                                        DataHashMap.put("Lat", jsonObject.optString("Lat"));
                                        DataHashMap.put("Long", jsonObject.optString("Long"));

                                        //orderd details
                                        DataHashMap.put("PickupZone", orders.getString("PickupZone"));
                                        DataHashMap.put("DeliveryZone", orders.getString("DeliveryZone"));
                                        DataHashMap.put("TotalQty", orders.getString("TotalQty"));
                                        DataHashMap.put("InvoiceRefNo", orders.getString("InvoiceRefNo"));
                                        DataHashMap.put("COD", orders.getString("COD"));
                                        DataHashMap.put("IsOutForDelivery", orders.getString("IsOutForDelivery"));
                                        DataHashMap.put("OrderNumber", orders.getString("OrderNumber"));
                                        DataHashMap.put("WaybillNumber", orders.getString("WaybillNumber"));
                                        DataHashMap.put("AppointmentId", orders.getString("AppointmentId"));
                                        ListCollection.add(DataHashMap);
                                    }
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        searchResults = ListCollection;
                                        adapter = new HDDeliveryAdapter(getActivity(), ListCollection);
                                        listView.setAdapter(adapter);

                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> arg0, View arg1,
                                                                    int position, long arg3) {
                                                // color selection select item
                                                //  String billno = "", legtrack = "";

                                                Log.e("ShipmentId", ((TextView) arg1.findViewById(R.id.shipmentid)).getText().toString());

                                                for (Map<String, String> data : ListCollection) {

                                                    if (data.get("ShipmentId").contains(((TextView) arg1.findViewById(R.id.shipmentid)).getText().toString())) {

                                                        String billno = data.get("WaybillNumber");
                                                        String legtrack = data.get("LegTrackId");
                                                        if (waybillno.contains(billno)) {
                                                            // arg1.findViewById(R.id.layoutbottom).setClickable(true);
                                                            // arg1.findViewById(R.id.layoutbottom).setClickable(true);
                                                            arg1.findViewById(R.id.layouttop).setBackgroundColor(getResources().getColor(R.color.white));
                                                            // arg1.findViewById(R.id.arrow).setClickable(true);

                                                            Log.e("remove WaybillNumber", billno);
                                                            waybillno.remove(billno);
                                                            legtrackid.remove(legtrack);

                                                        } else {
                                                            waybillno.add(billno);
                                                            legtrackid.add(legtrack);
                                                            // selectedIds.add(ListCollection.get(position).get("handovername").toString());
                                                            // arg1.findViewById(R.id.arrow).setClickable(false);
                                                            //  arg1.findViewById(R.id.layoutbottom).setClickable(false);
                                                            arg1.findViewById(R.id.layouttop).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                                            Log.e("add WaybillNumber", billno);
                                                            BtnTransfer.setVisibility(View.VISIBLE);
                                                        }

                                                        if (waybillno.isEmpty()) {
                                                            // Toast.makeText(getActivity(), "called empty" + name, Toast.LENGTH_SHORT).show();
                                                            BtnTransfer.setVisibility(View.GONE);
                                                            assignedto = "";
                                                        }
                                                    }

                                                }

                                              /*  if (ListCollection.get(position).get("ShipmentId").toString().equalsIgnoreCase(((TextView) arg1.findViewById(R.id.shipmentid)).getText().toString())) {

                                                }*/
                                                // String handovername = ListCollection.get(position).get("handovername").toString();
                                                // assignedto = ListCollection.get(position).get("HandOverId").toString();


                                            }
                                        });

                                    }
                                });
                            }
                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                            count.setText("");
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
        Log.e("HD_DeliveryFragment", "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

}