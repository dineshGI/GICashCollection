package com.gicollectionfms.module;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gicollectionfms.R;
import com.gicollectionfms.cashhandover.CH_BankDepositFragment;
import com.gicollectionfms.cashhandover.CH_PersonFragment;


public class CashHandover extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //https://api.mlab.com/api/1/databases/blindapp/collections/beacons?apiKey=EVDAtEeJwaIMAwOpjOOxdN2IiMmfLDJI


    private String mParam1;
    private String mParam2;
    private FragmentTabHost mTabHost;


    ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public CashHandover() {
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

    public static CashHandover newInstance(String param1, String param2) {
        CashHandover fragment = new CashHandover();
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

        View rootView = inflater.inflate(R.layout.cashhandover, container, false);
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
        mTabHost = rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("PERSON").setIndicator("Person"),
                CH_PersonFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("BANK DEPOSIT").setIndicator("Bank Deposit"),
                CH_BankDepositFragment.class, null);

        TextView one = mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        one.setTextSize(12);
        one.setAllCaps(false);
        one.setTextColor(getResources().getColor(R.color.white));
        TextView two = mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        two.setTextSize(12);
        two.setAllCaps(false);
        two.setTextColor(getResources().getColor(R.color.white));
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
      //  ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cash Handover");
        Log.e("CASH HANDOVER", "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
         ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cash Handover");
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