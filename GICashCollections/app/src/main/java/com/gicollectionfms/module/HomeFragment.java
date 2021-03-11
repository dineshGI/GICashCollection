package com.gicollectionfms.module;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gicollectionfms.R;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;


public class HomeFragment extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //https://api.mlab.com/api/1/databases/blindapp/collections/beacons?apiKey=EVDAtEeJwaIMAwOpjOOxdN2IiMmfLDJI


    private String mParam1;
    private String mParam2;

    LinearLayout pickup, handshakedelivery, RTO, cashhandover;

    ProgressDialog progressDialog;
    AlertDialog.Builder alertDialogBuilder;
    CommonAlertDialog alert;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
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

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Log.e("KEYCODE_BACK", "KEYCODE_BACK");
                        return true;
                    }
                }
                return false;
            }
        });
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        pickup = rootView.findViewById(R.id.pickup);
        pickup.setOnClickListener(this);

        handshakedelivery = rootView.findViewById(R.id.handshake_delivery);
        handshakedelivery.setOnClickListener(this);

        cashhandover = rootView.findViewById(R.id.cashhandover);
        cashhandover.setOnClickListener(this);

        RTO = rootView.findViewById(R.id.rto);
        RTO.setOnClickListener(this);

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alert = new CommonAlertDialog(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        String workstatus = Util.getData("WorkStatus", getActivity().getApplicationContext());
        Fragment fragment = null;
        switch (view.getId()) {

            case R.id.pickup:

                if (workstatus.equalsIgnoreCase("1")) {
                    fragment = new PickupFragment();
                    replaceFragment(fragment);
                } else {
                    alert.build(getString(R.string.start_msg));
                }

                break;
            case R.id.handshake_delivery:
                Log.e("handshake_delivery", "handshake_delivery");
                if (workstatus.equalsIgnoreCase("1")) {
                    fragment = new HandshakeDelivery();
                    replaceFragment(fragment);
                } else {
                    alert.build(getString(R.string.start_msg));
                }

                break;
            case R.id.rto:
                if (workstatus.equalsIgnoreCase("1")) {
                    fragment = new RTOFragment();
                    replaceFragment(fragment);
                } else {
                    alert.build(getString(R.string.start_msg));
                }
                break;
            case R.id.cashhandover:
                if (workstatus.equalsIgnoreCase("1")) {
                    fragment = new CashHandover();
                    replaceFragment(fragment);
                } else {
                    alert.build(getString(R.string.start_msg));
                }
                break;

            default:
                break;
        }
    }

    public void replaceFragment(Fragment someFragment) {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, someFragment);
        transaction.addToBackStack(null);
        // transaction.disallowAddToBackStack();
        transaction.commit();
       /* FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.asd, someFragment);
        transaction.addToBackStack(null);
       // transaction.disallowAddToBackStack();
        transaction.commit();*/
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
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