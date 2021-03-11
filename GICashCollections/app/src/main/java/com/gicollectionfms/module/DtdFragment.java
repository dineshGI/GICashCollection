package com.gicollectionfms.module;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.R;
import com.gicollectionfms.adapter.BackLogAdapter;
import com.gicollectionfms.adapter.DtdBackLogAdapter;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.ExpandableHeightGridView;
import com.gicollectionfms.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gicollectionfms.utils.Util.DASHBOARD;

public class DtdFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ProgressDialog progressDialog;

    CommonAlertDialog alert;

    TextView TxtTotalLeads, TxtCompleted, TxtPending, TxtBacklog,TxtNotCompleted;
    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    private OnFragmentInteractionListener mListener;
    ExpandableHeightGridView gridview;
    public DtdBackLogAdapter adapter;
    LinearLayout Grid;

    public DtdFragment() {
        // Required empty public constructor
    }

    public static DtdFragment newInstance(String param1, String param2) {
        DtdFragment fragment = new DtdFragment();
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

        View rootView = inflater.inflate(R.layout.dtdnew, container, false);
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

       /* Bundle bundle = getArguments();
        //here is your list array
        if (bundle.getString("Title").equalsIgnoreCase("DTD")) {
            filter = "2";
        } else {
            filter = "1";
        }*/

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        alert = new CommonAlertDialog(getActivity());

        Grid = rootView.findViewById(R.id.gridlayout);
        TxtTotalLeads = rootView.findViewById(R.id.total_leads);
        TxtCompleted = rootView.findViewById(R.id.lead_completed);
        TxtPending = rootView.findViewById(R.id.pending);
        TxtBacklog = rootView.findViewById(R.id.backlogs);
        TxtNotCompleted = rootView.findViewById(R.id.not_collected);

        ListCollection = new ArrayList<>();
        gridview = rootView.findViewById(R.id.grid);
        gridview.setExpanded(true);

        GetSalesReport();
        return rootView;
    }

    private void GetSalesReport() {
        ListCollection.clear();
        try {
            JSONObject obj = new JSONObject();
            //obj.put("UserId", "998");
            obj.put("UserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("ClientId", "0");
            obj.put("FilterType", "2");
            obj.put("FromDate", "");
            obj.put("ToDate", "");

            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(getActivity(), params.toString(), DASHBOARD, new VolleyResponseListener() {
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
                            JSONArray jsonArray = resobject.optJSONArray("_lstGetAppDashboardOutputModel1");
                            JSONObject imageObject = jsonArray.getJSONObject(0);

                            TxtTotalLeads.setText(imageObject.optString("TotalLeads"));
                            TxtCompleted.setText(imageObject.optString("LeadCompleted"));
                            TxtPending.setText(imageObject.optString("Pending"));
                            TxtBacklog.setText(imageObject.optString("BackLogs"));
                            TxtNotCompleted.setText(imageObject.optString("LeadNotCompleted"));
                            JSONArray backlogarray = resobject.optJSONArray("_lstGetAppDashboardOutputModel3");
                            if (backlogarray.length() > 0) {
                                Grid.setVisibility(View.VISIBLE);
                            }
                            for (int i = 0; i < backlogarray.length(); i++) {
                                JSONObject das = backlogarray.getJSONObject(i);
                                DataHashMap = new HashMap<>();
                                DataHashMap.put("ClientName", das.optString("ClientName"));
                                DataHashMap.put("LeadCount", das.optString("LeadCount"));
                                ListCollection.add(DataHashMap);
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new DtdBackLogAdapter(getActivity(), ListCollection);
                                    gridview.setAdapter(adapter);

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
        item.setVisible(true);
        MenuItem bulk_scan = menu.findItem(R.id.bulk_scan);
        bulk_scan.setVisible(false);
        MenuItem delivery = menu.findItem(R.id.delivery);
        delivery.setVisible(false);
    }
}