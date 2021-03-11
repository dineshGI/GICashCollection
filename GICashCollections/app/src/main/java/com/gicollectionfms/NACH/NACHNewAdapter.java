package com.gicollectionfms.NACH;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.HDBDirect.KCCBankReason;
import com.gicollectionfms.R;
import com.gicollectionfms.activity.AlertActivity;
import com.gicollectionfms.activity.GpsTracker;
import com.gicollectionfms.activity.NotCollectedActivity;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gicollectionfms.utils.Util.BANK_PICKUP;
import static com.gicollectionfms.utils.Util.FORM_DELIVERED;

public class NACHNewAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    private Activity activity;
    ProgressDialog progressDialog;
    private GpsTracker gpsTracker;
    List<Map<String, String>> originaldata;
    List<Map<String, String>> filterData;
    CommonAlertDialog alert;

    public NACHNewAdapter(Activity context, List<Map<String, String>> listCollectionone) {

        activity = context;
        // listdetails = listCollectionone;
        originaldata = listCollectionone;
        filterData = listCollectionone;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        gpsTracker = new GpsTracker(activity);
        alert = new CommonAlertDialog(activity);

    }

    @Override
    public int getCount() {
        return filterData.size();
    }

    @Override
    public Object getItem(int position) {
        return filterData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void pos(int position) {
        filterData.remove(filterData.get(position));
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (vi == null) {

            vi = inflater.inflate(R.layout.nach_new_adapter, null);
            holder = new ViewHolder();

            holder.TxtshipmentID = vi
                    .findViewById(R.id.shipmentid);
            holder.TxtCreatedDate = vi
                    .findViewById(R.id.crtddate);

            holder.arrow = vi
                    .findViewById(R.id.arrow);
            holder.logo = vi
                    .findViewById(R.id.company_logo);
            holder.lytop = vi
                    .findViewById(R.id.layouttop);
            holder.lybottom = vi
                    .findViewById(R.id.layoutbottom);
            holder.lycredit = vi
                    .findViewById(R.id.layoutcredit);
            holder.lyButton = vi
                    .findViewById(R.id.hide_layout);

            holder.rootlayout = vi
                    .findViewById(R.id.root);

            holder.line = vi
                    .findViewById(R.id.removeline);
            //Bank layout
            holder.NameC = vi
                    .findViewById(R.id.c_name);

            holder.MobileNoC = vi
                    .findViewById(R.id.c_mobileno);

            holder.AddressC = vi
                    .findViewById(R.id.c_address);

            holder.BtnCollectC = vi
                    .findViewById(R.id.c_collect);
            holder.BtnNotCollectCC = vi
                    .findViewById(R.id.cc_not_collect);
            holder.BtnFormDelivered = vi
                    .findViewById(R.id.form_delivered);

            holder.BtnNotCollectC = vi
                    .findViewById(R.id.c_notcollect);
            holder.DateC = vi
                    .findViewById(R.id.c_date);
            holder.TimeC = vi
                    .findViewById(R.id.c_time);
            //Credit Layout
            //AccNoC,CardNoC,BalaceAmountC,TotalAmountC,DueDateC
            holder.MobileCallCredit = vi
                    .findViewById(R.id.ly_c_mobileno);
            holder.AccNoC = vi
                    .findViewById(R.id.c_accno);
            holder.LyReshedule = vi
                    .findViewById(R.id.ly_reshedule);
            holder.ResheduleC = vi
                    .findViewById(R.id.c_reshedule);
            holder.TxtConfiredTime = vi
                    .findViewById(R.id.confirmed);
            holder.notcollectclick = vi
                    .findViewById(R.id.notcollectclick);

            vi.setTag(holder);

        } else {
            holder = (ViewHolder) vi.getTag();
        }
        //
        holder.TxtshipmentID.setText(filterData.get(position).get(
                "ShipmentId") + " - "  +"Cycle : " +filterData.get(position).get(
                "Cycle"));
        holder.lycredit.setVisibility(View.VISIBLE);

        holder.NameC.setText(filterData.get(position).get(
                "SellerName"));

        holder.MobileNoC.setText(filterData.get(position).get(
                "SellerContactNo"));

        holder.AddressC.setText(filterData.get(position).get(
                "SellerAddress") + " - " + filterData.get(position).get(
                "SellerPin"));

        //credit
        if (filterData.get(position).get(
                "filter").equalsIgnoreCase("0")) {
            holder.lyButton.setVisibility(View.VISIBLE);
            holder.DateC.setVisibility(View.VISIBLE);
            holder.TimeC.setVisibility(View.VISIBLE);
            if (!filterData.get(position).get(
                    "ConfirmedDT").isEmpty()) {
                holder.TxtConfiredTime.setVisibility(View.VISIBLE);
                holder.TxtConfiredTime.setText("CONFIRMED :" + filterData.get(position).get(
                        "ConfirmedDT"));
            }
        } else {
            holder.lyButton.setVisibility(View.GONE);
            holder.DateC.setVisibility(View.VISIBLE);
        }

        if (filterData.get(position).get(
                "filter").equalsIgnoreCase("2") && filterData.get(position).get(
                "IsCurrentDayShipment").equalsIgnoreCase("1")) {
            holder.LyReshedule.setVisibility(View.VISIBLE);
            Log.e("IsCurrentDayShipment", filterData.get(position).get(
                    "IsCurrentDayShipment"));
        }
        if (filterData.get(position).get(
                "filter").equalsIgnoreCase("1")) {
            holder.BtnFormDelivered.setVisibility(View.GONE);
        } else {
            //holder.BtnFormDelivered.setVisibility(View.VISIBLE);
            holder.BtnFormDelivered.setVisibility(View.GONE);
        }

        /*holder.DateC.setText("Lead Date:" + filterData.get(position).get(
                "CreatedDT"));*/
        holder.DateC.setVisibility(View.GONE);
        holder.TimeC.setText("Pickup Time :" + filterData.get(position).get(
                "RequestPickupTime"));
        holder.TimeC.setVisibility(View.GONE);
        holder.AccNoC.setText("Product : " + filterData.get(position).get(
                "product"));

        //credit
        holder.ResheduleC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Util.getData("WorkStatus", activity.getApplicationContext()).equalsIgnoreCase("1"))) {
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Util.saveData("Lead_Id", filterData.get(position).get(
                            "Lead_Id"), activity.getApplicationContext());
                    Util.saveData("SAName", filterData.get(position).get(
                            "SAName"), activity.getApplicationContext());
                    Util.saveData("SABranchName", filterData.get(position).get(
                            "SABranchName"), activity.getApplicationContext());

                    showAlert(filterData.get(position).get(
                            "WaybillNumber"), filterData.get(position).get(
                            "CodAmount"), filterData.get(position).get(
                            "SellerContactNo"));
                } else {
                    //CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                    alert.build(activity.getString(R.string.start_msg));
                }

            }
        });
        holder.BtnCollectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Util.getData("WorkStatus", activity.getApplicationContext()).equalsIgnoreCase("1"))) {
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Util.saveData("Lead_Id", filterData.get(position).get(
                            "Lead_Id"), activity.getApplicationContext());
                    Util.saveData("SAName", filterData.get(position).get(
                            "SAName"), activity.getApplicationContext());
                    Util.saveData("SABranchName", filterData.get(position).get(
                            "SABranchName"), activity.getApplicationContext());

                    showAlert(filterData.get(position).get(
                            "WaybillNumber"), filterData.get(position).get(
                            "CodAmount"), filterData.get(position).get(
                            "SellerContactNo"));
                } else {
                    //CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                    alert.build(activity.getString(R.string.start_msg));
                }

            }
        });

        holder.BtnFormDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Util.getData("WorkStatus", activity.getApplicationContext()).equalsIgnoreCase("1"))) {
                    FormDelivered(filterData.get(position).get(
                            "WaybillNumber"));
                } else {
                    //CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                    alert.build(activity.getString(R.string.start_msg));
                }


            }
        });
        holder.BtnNotCollectCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Util.getData("WorkStatus", activity.getApplicationContext()).equalsIgnoreCase("1"))) {
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Util.saveData("Lead_Id", filterData.get(position).get(
                            "Lead_Id"), activity.getApplicationContext());
                    Util.saveData("SAName", filterData.get(position).get(
                            "SAName"), activity.getApplicationContext());
                    Util.saveData("SABranchName", filterData.get(position).get(
                            "SABranchName"), activity.getApplicationContext());
                    Util.saveData("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"), activity.getApplicationContext());
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Intent i = new Intent(activity, KCCBankReason.class);
                    i.putExtra("AppointmentId", filterData.get(position).get(
                            "AppointmentId"));
                    i.putExtra("WaybillNumber", filterData.get(position).get(
                            "WaybillNumber"));
                    i.putExtra("amount", filterData.get(position).get(
                            "CodAmount"));
                    i.putExtra("from", "notcollect");
                    i.putExtra("Direct", "false");
                    activity.startActivity(i);
                } else {
                    //CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                    alert.build(activity.getString(R.string.start_msg));
                }


            }
        });

        holder.BtnNotCollectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Util.getData("WorkStatus", activity.getApplicationContext()).equalsIgnoreCase("1"))) {
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Util.saveData("Lead_Id", filterData.get(position).get(
                            "Lead_Id"), activity.getApplicationContext());
                    Util.saveData("SAName", filterData.get(position).get(
                            "SAName"), activity.getApplicationContext());
                    Util.saveData("SABranchName", filterData.get(position).get(
                            "SABranchName"), activity.getApplicationContext());
                    Util.saveData("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"), activity.getApplicationContext());
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    Intent i = new Intent(activity, KCCBankReason.class);
                    i.putExtra("AppointmentId", filterData.get(position).get(
                            "AppointmentId"));
                    i.putExtra("WaybillNumber", filterData.get(position).get(
                            "WaybillNumber"));
                    i.putExtra("amount", filterData.get(position).get(
                            "CodAmount"));
                    i.putExtra("from", "notcollect");
                    i.putExtra("Direct", "false");
                    activity.startActivity(i);
                } else {
                    //CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                    alert.build(activity.getString(R.string.start_msg));
                }


            }
        });

        holder.MobileCallCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!holder.MobileNoC.getText().toString().isEmpty()) {
                    Util.call=false;
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + holder.MobileNoC.getText().toString()));
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    activity.startActivity(callIntent);
                } else {
                    alert.build(activity.getString(R.string.mobileno_notfound));
                }

            }
        });


        if (!filterData.get(position).get(
                "ConfirmedDT").isEmpty()) {
            holder.TxtCreatedDate.setText("CONFIRMED :" + filterData.get(position).get(
                    "ConfirmedDT"));
        } else {
            holder.TxtCreatedDate.setText("Lead Date:" + " " + filterData.get(position).get(
                    "CreatedDT"));

        }

        holder.arrow.setTag(position);
        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View vas) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Log.e("position", String.valueOf(position));
                        if (holder.lybottom.getVisibility() == View.VISIBLE) {
                            holder.lybottom.setVisibility(View.GONE);
                            holder.arrow.setImageResource(R.drawable.down);
                            holder.rootlayout.setBackgroundResource(0);
                            holder.lytop.setBackgroundResource(0);
                            holder.line.setVisibility(View.VISIBLE);
                        } else {
                            holder.lybottom.setVisibility(View.VISIBLE);
                            holder.arrow.setImageResource(R.drawable.up);
                            holder.rootlayout.setBackgroundResource(R.drawable.editext);
                            holder.lytop.setBackgroundResource(R.color.collapse_header);
                            holder.line.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        holder.notcollectclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterData.get(position).get(
                        "filter").equalsIgnoreCase("2")) {
                    Intent notcollect = new Intent(activity, NotCollectedActivity.class);
                    notcollect.putExtra("WaybillNumber", filterData.get(position).get(
                            "WaybillNumber"));
                    activity.startActivity(notcollect);
                }
            }
        });

        return vi;
    }

    private void FormDelivered(String waybillNumber) {


        try {
            JSONObject obj = new JSONObject();
            obj.put("SAUserid", Util.getData("UserId",activity.getApplicationContext()));
            obj.put("WaybillNo", waybillNumber);
            if (gpsTracker.canGetLocation()) {
                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
            }
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponse(activity, params.toString(), FORM_DELIVERED, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(activity.getString(R.string.timeout_error));
                    } else {
                        alert.build(activity.getString(R.string.server_error));
                    }
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse:" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            Intent alert = new Intent(activity, AlertActivity.class);
                            alert.putExtra("status", resobject.getString("Status"));
                            alert.putExtra("StatusDesc", resobject.getString("StatusDesc"));
                            activity.startActivity(alert);

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

    private void showAlert(final String waybill, final String amount, final String mobileno) {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(activity, R.style.alertDialog);
        alertDialogBuilder.setMessage(R.string.want_to_continue);
        alertDialogBuilder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        collect(waybill, amount, mobileno);
                    }
                });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private void collect(String waybill, String amount, String mobileno) {


        try {
            JSONObject obj = new JSONObject();
            obj.put("WaybillNo", waybill);
            obj.put("PODFilePath", "");
            if (gpsTracker.canGetLocation()) {
                obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
            }
            obj.put("CollectedAmount", amount);
            //  obj.put("SAUserid", Util.getData("UserId", getApplicationContext()));

            obj.put("CheqNo", "");
            obj.put("BankName", "");
            obj.put("CheqDate", "");
            obj.put("SMASelection", "");

            obj.put(activity.getString(R.string.SAUserID), Util.getData("UserId", activity.getApplicationContext()));
            //obj.put("SAUserid", Util.getData("UserId", getApplicationContext()));
            obj.put("FEName", Util.getData("UserName", activity.getApplicationContext()));
            obj.put("ShipmentRefNo", Util.getData("ShipmentId", activity.getApplicationContext()));
            obj.put("Lead_Id", Util.getData("Lead_Id", activity.getApplicationContext()));
            obj.put("Pickup_Date", Util.getdatetime());
            obj.put("SAName", Util.getData("SAName", activity.getApplicationContext()));
            obj.put("SABranchName", Util.getData("SABranchName", activity.getApplicationContext()));

            obj.put("PaymentMode", "");
            obj.put("PaymentModeId", "");
            obj.put("MobileNo", mobileno);

            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(activity, params.toString(), BANK_PICKUP, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(activity.getString(R.string.timeout_error));
                    } else {
                        alert.build(activity.getString(R.string.server_error));
                    }
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Util.Logcat.e("onResponse:" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            Intent alert = new Intent(activity, AlertActivity.class);
                            alert.putExtra("status", resobject.getString("Status"));
                            alert.putExtra("StatusDesc", resobject.getString("StatusDesc"));
                            activity.startActivity(alert);

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


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = originaldata;
                    results.count = originaldata.size();
                } else {

                    List<Map<String, String>> filterResultsData = new ArrayList<>();
                    for (Map<String, String> data : originaldata) {
                        if (data.get("ShipmentId").contains(constraint) || data.get("PickupType").contains(constraint) || data.get("SellerName").contains(constraint) || data.get("SellerContactNo").contains(constraint) || data.get("ClientOrderNumber").contains(constraint)|| data.get("Cycle").contains(constraint)|| data.get("SellerPin").contains(constraint)) {
                            filterResultsData.add(data);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterData = (List<Map<String, String>>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder {
        private TextView TxtshipmentID, TxtCreatedDate;
        private LinearLayout lytop, lybottom, rootlayout,notcollectclick;
        private LinearLayout lycredit, MobileCallCredit, lyButton, LyReshedule;
        ImageView arrow, logo;
        View line;
        //bank
        //credit
        private TextView BtnCollectC, BtnNotCollectC, BtnNotCollectCC, BtnFormDelivered, NameC, AddressC, MobileNoC, DateC, TimeC, ResheduleC;
        private TextView AccNoC, TxtConfiredTime;
    }
}

