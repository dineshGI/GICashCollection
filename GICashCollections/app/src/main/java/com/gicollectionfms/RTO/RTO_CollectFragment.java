package com.gicollectionfms.RTO;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;
import com.gicollectionfms.interfaces.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RTO_CollectFragment extends Fragment {

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
    public RTOCollectAdapter adapter;
    AlertDialog.Builder alertDialogBuilder;
    CommonAlertDialog alert;
    TextView count;
    Button Btnupdate;
    private EditText filterText;
    //  public ArrayList<Integer> selectedIds = new ArrayList<Integer>();
    public ArrayList<String> waybillno = new ArrayList<String>();

    ProgressDialog progressDialog;


    public ArrayList<String> name = new ArrayList<String>();
    String assignedto;
    private OnFragmentInteractionListener mListener;

    public RTO_CollectFragment() {
        // Required empty public constructor
    }


    public static RTO_CollectFragment newInstance(String param1, String param2) {
        RTO_CollectFragment fragment = new RTO_CollectFragment();
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

        View rootView = inflater.inflate(R.layout.hd_handshake_fragment, container, false);
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

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        ListCollection = new ArrayList<Map<String, String>>();
        holditems = new ArrayList<Map<String, String>>();
        listView = rootView.findViewById(R.id.listview);
        filterText = rootView.findViewById(R.id.search);
        count = rootView.findViewById(R.id.count);
        alert = new CommonAlertDialog(getActivity());
        Btnupdate = rootView.findViewById(R.id.update);
        alertDialogBuilder = new AlertDialog.Builder(getActivity());

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

        return rootView;
    }


    private void loadlistrtocollect() {
        filterText.setText("");
        ListCollection.clear();
        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAId", Util.getData("SAId", getActivity().getApplicationContext()));
            obj.put("SABranchId", Util.getData("SABranchId", getActivity().getApplicationContext()));
            obj.put("TaskType", "3");
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("ZoneId", "0");
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), Util.RTO_HANDSHAKELIST, new VolleyResponseListener() {
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
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                alert.build(getString(R.string.nodata_available));
                                count.setText("Total - " + "0");
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    JSONArray imageArray = jsonObject.getJSONArray("orders");
                                    JSONObject order = imageArray.getJSONObject(0);

                                    if (jsonObject.optString("DeliveryType").equalsIgnoreCase("1")) {
                                        DataHashMap = new HashMap<String, String>();
                                        DataHashMap.put("HandOverTo", "Collect From " + jsonObject.optString("RunnerName"));
                                        DataHashMap.put("ShipmentId", jsonObject.optString("ShipmentId"));
                                        DataHashMap.put("LegTrackId", jsonObject.optString("LegTrackId"));
                                        DataHashMap.put("CreatedDT", jsonObject.optString("CreatedDT"));
                                        DataHashMap.put("WaybillNumber", order.getString("WaybillNumber"));
                                        DataHashMap.put("HandOverId", order.getString("HandOverId"));
                                        DataHashMap.put("PickupZone", order.getString("PickupZone"));
                                        DataHashMap.put("DeliveryZone", order.getString("DeliveryZone"));
                                        DataHashMap.put("TotalQty", order.getString("TotalQty"));
                                        DataHashMap.put("OrderNumber", order.getString("OrderNumber"));
                                        DataHashMap.put("AssignedDate", order.getString("AssignedDate"));

                                        if (order.getString("RunnerName") != null) {
                                            DataHashMap.put("handovername", order.getString("RunnerName"));
                                        } else {
                                            DataHashMap.put("handovername", "");
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
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (ListCollection.size() >= 1) {
                                            count.setText("Total - " + ListCollection.size());
                                            searchResults = ListCollection;
                                            adapter = new RTOCollectAdapter(getActivity(), ListCollection);
                                            listView.setAdapter(adapter);
                                        } else {
                                            count.setText("No Data");
                                            alert.build(getString(R.string.nodata_available));
                                        }

                                    }
                                });
                            }

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
        loadlistrtocollect();
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}