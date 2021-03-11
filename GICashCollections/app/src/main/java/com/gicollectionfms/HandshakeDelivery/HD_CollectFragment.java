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
import android.widget.Toast;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;
import com.gicollectionfms.activity.AlertActivity;
import com.gicollectionfms.interfaces.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HD_CollectFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //https://api.mlab.com/api/1/databases/blindapp/collections/beacons?apiKey=EVDAtEeJwaIMAwOpjOOxdN2IiMmfLDJI


    private String mParam1;
    private String mParam2;

    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    private List<Map<String, String>> holditems;
    List<Map<String, String>> searchResults;
    ListView listView;
    public HDCollectAdapter adapter;
    AlertDialog.Builder alertDialogBuilder;

    TextView count;
    Button Btnupdate;

    private EditText filterText;
    //  public ArrayList<Integer> selectedIds = new ArrayList<Integer>();
    public ArrayList<String> waybillno = new ArrayList<String>();
    CommonAlertDialog alert;
    ProgressDialog progressDialog;


    public ArrayList<String> name = new ArrayList<String>();
    String assignedto;
    private OnFragmentInteractionListener mListener;

    public HD_CollectFragment() {
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

    public static HD_HandshakeFragment newInstance(String param1, String param2) {
        HD_HandshakeFragment fragment = new HD_HandshakeFragment();
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

        View rootView = inflater.inflate(R.layout.hd_collect_fragment, container, false);
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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        ListCollection = new ArrayList<Map<String, String>>();
        holditems = new ArrayList<Map<String, String>>();
        listView = rootView.findViewById(R.id.listview);
        filterText = rootView.findViewById(R.id.search);
        count = rootView.findViewById(R.id.count);
        Btnupdate = rootView.findViewById(R.id.update);
        alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.alertDialog);
        alert = new CommonAlertDialog(getActivity());

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

        Btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuffer strBuffer = new StringBuffer();
                for (String i : waybillno) { // iterate -list by list
                    strBuffer.append(i).append("|");
                }
                Log.e("handoveritem", strBuffer.toString());
                CallBulkCollect(strBuffer.toString());
            }
        });
        return rootView;
    }

    private void CallBulkCollect(String legtrackinfo) {

        {
            String data = "";
            JSONObject obj = new JSONObject();
            try {
                obj.put("LegTrackInfo", legtrackinfo);
                obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
                Util.Logcat.e("INPUT:::"+ obj.toString());
                data = Util.EncryptURL(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);
                CallApi.postResponse(getActivity(), params.toString(), Util.HD_COLLECT_BULK, new VolleyResponseListener() {
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
                            //  JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                            try {
                                Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                                JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                                Intent alert = new Intent(getActivity(), AlertActivity.class);
                                alert.putExtra("status", resobject.getString("Status"));
                                alert.putExtra("StatusDesc", resobject.getString("StatusDesc"));
                                getActivity().startActivity(alert);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                           /* if (resobject.getString("Status").equalsIgnoreCase("0")) {
                                alertDialogBuilder.setMessage(resobject.getString("StatusDesc").toString());
                                alertDialogBuilder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                                loadlist();
                                                //PickupListviewAdapter dad=new PickupListviewAdapter() ;
                                            }
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                            } else if (resobject.getString("Status").equalsIgnoreCase("1")) {

                                alertDialogBuilder.setMessage(resobject.getString("StatusDesc").toString());

                                alertDialogBuilder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                                loadlist();

                                            }
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                            } else {

                                alertDialogBuilder.setMessage(resobject.getString("StatusDesc").toString());
                                alertDialogBuilder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                loadlist();
                                            }
                                        });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                            }*/
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

    private void loadlist() {
        filterText.setText("");
        waybillno.clear();
        name.clear();
        Btnupdate.setVisibility(View.GONE);
        assignedto = "";

        String data = "";

        try {
            JSONObject obj = new JSONObject();
            obj.put("SAId", Util.getData("SAId", getActivity().getApplicationContext()));
            obj.put("SABranchId", Util.getData("SABranchId", getActivity().getApplicationContext()));
            obj.put("TaskType", "2");
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("Filter", "1");
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.HD_COLLECTLIST, new VolleyResponseListener() {
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
                                alert.build(getString(R.string.nocollect_available));
                                count.setText("Total - " + "0");
                            } else {

                                count.setText("Total - " + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    JSONArray imageArray = jsonObject.getJSONArray("orders");
                                    JSONObject order = imageArray.getJSONObject(0);

                                    DataHashMap = new HashMap<String, String>();
                                    DataHashMap.put("ShipmentId", jsonObject.optString("ShipmentId"));
                                    DataHashMap.put("LegTrackId", jsonObject.optString("LegTrackId"));
                                    DataHashMap.put("CreatedDT", jsonObject.optString("CreatedDT"));
                                    DataHashMap.put("WaybillNumber", jsonObject.optString("WaybillNumber"));
                                    DataHashMap.put("HandOverId", jsonObject.optString("HandOverId"));

                                    if (jsonObject.optString("DeliveryType").equalsIgnoreCase("1")) {
                                        DataHashMap.put("HandOverTo", "Collect From " + jsonObject.optString("RunnerName"));
                                    } else {
                                        DataHashMap.put("HandOverTo", "Collect From " + "");
                                    }
                                    //orderd details
                                    DataHashMap.put("PickupZone", order.getString("PickupZone"));
                                    DataHashMap.put("DeliveryZone", order.getString("DeliveryZone"));
                                    DataHashMap.put("TotalQty", order.getString("TotalQty"));
                                    DataHashMap.put("OrderNumber", order.getString("OrderNumber"));

                                    if (order.getString("AssignedDate").equalsIgnoreCase("null")) {
                                        DataHashMap.put("AssignedDate", "");
                                    } else {
                                        DataHashMap.put("AssignedDate", order.getString("AssignedDate"));
                                    }
                                    if (order.getString("RunnerName").equals("null")) {
                                        DataHashMap.put("handovername", "");
                                    } else {
                                        DataHashMap.put("handovername", jsonObject.optString("RunnerName"));
                                    }
                                    if (order.getString("MobileNumber").equals("null")) {
                                        DataHashMap.put("HandOverMobileNumber", "");
                                    } else {
                                        // Log.e("HandOverMobileNumber", order.getString("HandOverMobileNumber"));
                                        DataHashMap.put("HandOverMobileNumber", order.getString("MobileNumber"));
                                    }
                                    DataHashMap.put("InvoiceRefNo", order.getString("InvoiceRefNo"));
                                    ListCollection.add(DataHashMap);
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                         //http://codesfor.in/contextual-actionbar-in-android/
                                        searchResults = ListCollection;
                                        adapter = new HDCollectAdapter(getActivity(), ListCollection);
                                        listView.setAdapter(adapter);
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> arg0, View arg1,
                                                                    int position, long arg3) {
                                                // color selection select item
                                                Log.e("ShipmentId", ((TextView) arg1.findViewById(R.id.shipmentid)).getText().toString());
                                                for (Map<String, String> data : ListCollection) {

                                                    if (data.get("ShipmentId").contains(((TextView) arg1.findViewById(R.id.shipmentid)).getText().toString())) {
                                                        // color selection select item
                                                        String billno = data.get("LegTrackId");
                                                        //  String legid = data.get("LegTrackId").toString();
                                                        if (name.isEmpty()) {
                                                            name.add(data.get("handovername"));
                                                            assignedto = data.get("HandOverId");
                                                            if (waybillno.contains(billno)) {

                                                                arg1.findViewById(R.id.arrow).setClickable(true);
                                                                arg1.findViewById(R.id.layouttop).setBackgroundColor(getResources().getColor(R.color.white));
                                                                Log.e("position", String.valueOf(position));
                                                                //String playerName = ListCollection.get(position).get("WaybillNumber").toString();
                                                                Log.e("remove WaybillNumber", billno);
                                                                waybillno.remove(billno);
                                                            } else {
                                                                waybillno.add(billno);
                                                                // selectedIds.add(ListCollection.get(position).get("handovername").toString());
                                                                arg1.findViewById(R.id.arrow).setClickable(false);
                                                                arg1.findViewById(R.id.layouttop).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                                                Log.e("position", String.valueOf(position));
                                                                // String playerName = ListCollection.get(position).get("WaybillNumber").toString();
                                                                // waybill.add();
                                                                Log.e("add WaybillNumber", billno);
                                                            }
                                                            Btnupdate.setVisibility(View.VISIBLE);
                                                        } else if (name.contains(data.get("handovername"))) {

                                                            if (waybillno.contains(billno)) {

                                                                arg1.findViewById(R.id.arrow).setClickable(true);
                                                                arg1.findViewById(R.id.layouttop).setBackgroundColor(getResources().getColor(R.color.white));
                                                                Log.e("position", String.valueOf(position));
                                                                //String playerName = ListCollection.get(position).get("WaybillNumber").toString();
                                                                Log.e("remove WaybillNumber", billno);
                                                                waybillno.remove(billno);
                                                            } else {
                                                                waybillno.add(billno);
                                                                // selectedIds.add(ListCollection.get(position).get("handovername").toString());
                                                                arg1.findViewById(R.id.arrow).setClickable(false);
                                                                arg1.findViewById(R.id.layouttop).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                                                Log.e("position", String.valueOf(position));
                                                                // String playerName = ListCollection.get(position).get("WaybillNumber").toString();
                                                                // waybill.add();
                                                                Log.e("add WaybillNumber", billno);
                                                            }
                                                        } else {
                                                            Toast.makeText(getActivity(), "select only same data", Toast.LENGTH_SHORT).show();
                                                        }
                                                        if (waybillno.isEmpty()) {
                                                            name.clear();
                                                            //Toast.makeText(getActivity(), "called empty" + name, Toast.LENGTH_SHORT).show();
                                                            Btnupdate.setVisibility(View.GONE);
                                                            assignedto = "";
                                                        }
                                                    }
                                                }

                                                // String billno = ListCollection.get(position).get("LegTrackId").toString();
                                                // String handovername = ListCollection.get(position).get("handovername").toString();


                                            }
                                        });

                                    }
                                });

                            }

                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {

                            count.setText("");
                            if(adapter!=null){
                            adapter.notifyDataSetChanged();
                            }
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
        Log.e("HD_HandshakeFragment", "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadlist();
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}