package com.gicollectionfms.module;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.R;
import com.gicollectionfms.activity.GpsTracker;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import static com.gicollectionfms.utils.Util.SOS;


public class Dashbaord extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FragmentTabHost mTabHost;
    ImageView ImgSOS;
    private GpsTracker gpsTracker;
    ProgressDialog progressDialog;
    CommonAlertDialog alert;
    private OnFragmentInteractionListener mListener;

    public Dashbaord() {
        //Required empty public constructor
    }

    public static Dashbaord newInstance(String param1, String param2) {
        Dashbaord fragment = new Dashbaord();
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

        View rootView = inflater.inflate(R.layout.dashboard, container, false);
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
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
        gpsTracker = new GpsTracker(getActivity());
        alert = new CommonAlertDialog(getActivity());
        mTabHost = rootView.findViewById(android.R.id.tabhost);
        ImgSOS = rootView.findViewById(R.id.btn_sos);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        Bundle arg1 = new Bundle();
        arg1.putString("Title", "DTD");
        mTabHost.addTab(mTabHost.newTabSpec("DTD").setIndicator("DTD"),
                DtdFragment.class, arg1);
        Bundle arg2 = new Bundle();
        arg2.putString("Title", "MTD");
        mTabHost.addTab(mTabHost.newTabSpec("MTD").setIndicator("MTD"),
                MtdFragment.class, arg2);

        TextView one = mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        one.setTextSize(15);
        one.setAllCaps(false);
        one.setTextColor(getResources().getColor(R.color.white));

        TextView two = mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        two.setTextSize(15);
        two.setAllCaps(false);
        two.setTextColor(getResources().getColor(R.color.white));

        ImgSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSOS();
            }
        });

        return rootView;

    }

    private void callSOS() {

        try {
            JSONObject obj = new JSONObject();
            obj.put("SAUserId", Util.getData("UserId", getActivity().getApplicationContext()));
            obj.put("SenderMobile", Util.getData("LoginId", getActivity().getApplicationContext()));
            obj.put("SenderName", Util.getData("UserName", getActivity().getApplicationContext()));
            if (gpsTracker.canGetLocation()) {
                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
            }
            Util.Logcat.e("CallSOS:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(getActivity(), params.toString(), SOS, new VolleyResponseListener() {
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
                        Util.Logcat.e("SOS OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dashboard");
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