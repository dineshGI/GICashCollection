package com.gicollectionfms.LandTDirect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.KotakPickup.KPickupNewAdapter;
import com.gicollectionfms.R;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gicollectionfms.utils.Util.PICKUP;


public class LNTDirectFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    List<Map<String, String>> searchResults;
    ListView listView;
    public LNTDirectAdapter adapter;

    TextView count;
    private EditText filterText;
    CommonAlertDialog alert;
    ProgressDialog progressDialog;
    String filter;

    private OnFragmentInteractionListener mListener;

    public LNTDirectFragment() {
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

    public static LNTDirectFragment newInstance(String param1, String param2) {
        LNTDirectFragment fragment = new LNTDirectFragment();
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

        View rootView = inflater.inflate(R.layout.lnt_direct_fragment, container, false);
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
        Log.e("lifecyle", "onCreate");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        alert = new CommonAlertDialog(getActivity());
        ListCollection = new ArrayList<>();
        listView = rootView.findViewById(R.id.listview);
        filterText = rootView.findViewById(R.id.search);

        count = rootView.findViewById(R.id.count);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();

        //here is your list array
        if (bundle.getString("Title").equalsIgnoreCase("New")) {
            filter = "0";
        } else if (bundle.getString("Title").equalsIgnoreCase("Completed")) {
            filter = "1";
        } else if (bundle.getString("Title").equalsIgnoreCase("NotCompleted")) {
            filter = "2";
        }

        loadpickuplist();

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

    private void loadpickuplist() {

        ListCollection.clear();
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAId", Util.getData("SAId", getActivity().getApplicationContext()));
            obj.put("SABranchId", Util.getData("SABranchId", getActivity().getApplicationContext()));
            obj.put("TaskType", "2");
            obj.put("FilterType", filter);
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("ClientId", "1017");
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), PICKUP, new VolleyResponseListener() {
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
                    filterText.setText("");
                    ListCollection.clear();
                    Util.Logcat.e("onResponse" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("appointments");

                            if (jsonArray == null) {
                                //txtnodata.setVisibility(View.VISIBLE);
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                                count.setText("Total - " + "0");
                                alert.build(getString(R.string.nopickup_available));
                            } else {
                                Util.Logcat.e("length" + jsonArray.length());
                                count.setText("Total - " + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    JSONArray imageArray = jsonObject.getJSONArray("orders");
                                    JSONObject imageObject = imageArray.getJSONObject(0);

                                    DataHashMap = new HashMap<>();
                                    DataHashMap.put("ShipmentId", jsonObject.optString("ShipmentId"));
                                    DataHashMap.put("filter", filter);
                                    DataHashMap.put("CreatedDT", jsonObject.optString("CreatedDT"));
                                    DataHashMap.put("SellerName", jsonObject.optString("SellerName"));
                                    DataHashMap.put("SellerAddress", jsonObject.optString("SellerAddress"));
                                    DataHashMap.put("statepin", jsonObject.optString("SellerState") + " - " + jsonObject.optString("SellerPin"));
                                    DataHashMap.put("SellerContactNo", jsonObject.optString("SellerContactNo"));
                                    DataHashMap.put("Lat", jsonObject.optString("Lat"));
                                    DataHashMap.put("Long", jsonObject.optString("Long"));
                                    DataHashMap.put("ClientName", jsonObject.optString("ClientName"));
                                    DataHashMap.put("SellerPin", jsonObject.optString("SellerPin"));
                                    DataHashMap.put("ShipmentType", jsonObject.optString("ShipmentType"));
                                    //orderd details

                                    //bank
                                    DataHashMap.put("Lead_Id", imageObject.optString("Lead_Id"));
                                    DataHashMap.put("IsCurrentDayShipment", imageObject.optString("IsCurrentDayShipment"));
                                    DataHashMap.put("RequestPickupTime", imageObject.optString("RequestPickupTime"));
                                    DataHashMap.put("RequestPickupDT", imageObject.optString("RequestPickupDT"));
                                    DataHashMap.put("PickupType", imageObject.optString("PickupType"));

                                    DataHashMap.put("SAName", imageObject.optString("SAName"));
                                    DataHashMap.put("SABranchName", imageObject.optString("SABranchName"));
                                    DataHashMap.put("NACH", imageObject.getString("NACH"));
                                    DataHashMap.put("PenaltyCharges", imageObject.getString("PenaltyCharges"));
                                    DataHashMap.put("PaymentMode", imageObject.getString("PaymentMode"));
                                    DataHashMap.put("CodAmount", imageObject.getString("CodAmount"));

                                    DataHashMap.put("OSBounce", imageObject.getString("OSBounce"));
                                    DataHashMap.put("OSCharges", imageObject.getString("OSCharges"));
                                    DataHashMap.put("OSPenalty", imageObject.getString("OSPenalty"));
                                    DataHashMap.put("ForeclosureValue", imageObject.getString("ForeclosureValue"));

                                    //credit
                                    DataHashMap.put("ClientOrderNumber", imageObject.getString("ClientOrderNumber"));
                                    DataHashMap.put("InvoiceRefNo", imageObject.getString("InvoiceRefNo"));
                                    DataHashMap.put("BalanceAmount", imageObject.getString("BalanceAmount"));
                                    DataHashMap.put("TotalBillAmount", imageObject.getString("TotalBillAmount"));
                                    DataHashMap.put("PaymentDueDate", imageObject.getString("PaymentDueDate"));


                                    DataHashMap.put("WaybillNumber", imageObject.getString("WaybillNumber"));
                                    DataHashMap.put("PickupZone", imageObject.getString("PickupZone"));
                                    DataHashMap.put("DeliveryZone", imageObject.getString("DeliveryZone"));
                                    DataHashMap.put("TotalQty", imageObject.getString("TotalQty"));
                                    DataHashMap.put("InvoiceRefNo", imageObject.getString("InvoiceRefNo"));
                                    DataHashMap.put("AppointmentId", imageObject.getString("AppointmentId"));
                                    DataHashMap.put("OrderNumber", imageObject.getString("OrderNumber"));
                                    DataHashMap.put("ButtonStatus", imageObject.getString("ButtonStatus"));
                                    DataHashMap.put("ConfirmedDT", imageObject.getString("ConfirmedDT"));
                                    DataHashMap.put("Cycle", imageObject.getString("Cycle"));
                                    ListCollection.add(DataHashMap);
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        searchResults = ListCollection;
                                        adapter = new LNTDirectAdapter(getActivity(), ListCollection);
                                        listView.setAdapter(adapter);

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
    public void onResume() {
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("L & T Direct");
        Log.e("lifecyle", String.valueOf(Util.call));
        if (Util.call==true) {
            loadpickuplist();
        }
        Log.e("lifecyle", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("lifecyle", "onDestroy");
    }

    @Override
    public void onStop() {
        super.onStop();
        Util.call=false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.start_stop);
        item.setVisible(true);
        MenuItem asd = menu.findItem(R.id.bulk_scan);
        asd.setVisible(false);
        MenuItem delivery = menu.findItem(R.id.delivery);
        delivery.setVisible(false);
    }

}
