package com.gicollectionfms.KotakPickup;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.gicollectionfms.HDBDirect.KCCBankReason;
import com.gicollectionfms.R;
import com.gicollectionfms.activity.NotCollectedActivity;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KPickupNewAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    private Activity activity;
    ProgressDialog progressDialog;
    List<Map<String, String>> originaldata;
    List<Map<String, String>> filterData;
    CommonAlertDialog alert;

    public KPickupNewAdapter(Activity context, List<Map<String, String>> listCollectionone) {

        activity = context;
        originaldata = listCollectionone;
        filterData = listCollectionone;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
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

            vi = inflater.inflate(R.layout.cfb_new_adapter, null);
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
            holder.BtnNotCollectC = vi
                    .findViewById(R.id.c_notcollect);

            //Credit Layout
            //AccNoC,CardNoC,BalaceAmountC,TotalAmountC,DueDateC
            holder.MobileCallCredit = vi
                    .findViewById(R.id.ly_c_mobileno);
            holder.AccNoC = vi
                    .findViewById(R.id.c_accno);
            holder.CardNoC = vi
                    .findViewById(R.id.c_cardno);

            holder.BalaceAmountC = vi
                    .findViewById(R.id.balance_amount);
            holder.TotalAmountC = vi
                    .findViewById(R.id.c_totalbillamt);
            holder.AmountTobeCollectedC = vi
                    .findViewById(R.id.amt_tobecollect);
            holder.DueDateC = vi
                    .findViewById(R.id.c_paymentduedate);
            holder.DateC = vi
                    .findViewById(R.id.c_date);
            holder.TimeC = vi
                    .findViewById(R.id.c_time);
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

        holder.TxtshipmentID.setText(filterData.get(position).get(
                "ShipmentId") + " - " + "Cycle : " + filterData.get(position).get(
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
            holder.TimeC.setVisibility(View.VISIBLE);
            holder.DateC.setVisibility(View.VISIBLE);
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
        }

        if (!filterData.get(position).get(
                "ConfirmedDT").isEmpty()) {
            holder.TxtCreatedDate.setText("CONFIRMED :" + filterData.get(position).get(
                    "ConfirmedDT"));
            holder.TxtCreatedDate.setTextColor(activity.getResources().getColor(R.color.dark_green));

        } else {
            holder.TxtCreatedDate.setText("Pickup Date:" + " " + filterData.get(position).get(
                    "RequestPickupDT") + " " + filterData.get(position).get(
                    "RequestPickupTime"));
        }

        holder.AccNoC.setText("Account No : " + filterData.get(position).get(
                "InvoiceRefNo"));
        holder.CardNoC.setText("Card No : " + filterData.get(position).get(
                "ClientOrderNumber"));

        holder.AmountTobeCollectedC.setText("Amount to be Collect :" + filterData.get(position).get(
                "CodAmount"));
        holder.BalaceAmountC.setText(filterData.get(position).get(
                "BalanceAmount"));
        holder.TotalAmountC.setText(filterData.get(position).get(
                "TotalBillAmount"));
        holder.DueDateC.setText("Payment Due Date:" + filterData.get(position).get(
                "PaymentDueDate"));
        holder.DateC.setText("Lead Date:" + filterData.get(position).get(
                "CreatedDT"));
       /* holder.TimeC.setText("Pickup Time :" + filterData.get(position).get(
                "RequestPickupTime"));*/
        holder.TimeC.setVisibility(View.GONE);
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
                    i.putExtra("from", "collect");
                    activity.startActivity(i);
                } else {
                    //CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                    alert.build(activity.getString(R.string.start_msg));
                }

            }
        });

        //credit
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
                    i.putExtra("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"));
                    Util.saveData("ShipmentId", filterData.get(position).get(
                            "ShipmentId"), activity.getApplicationContext());
                    i.putExtra("from", "collect");
                    activity.startActivity(i);
                } else {
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
                    Intent i = new Intent(activity, KCCBankReason.class);
                    i.putExtra("AppointmentId", filterData.get(position).get(
                            "AppointmentId"));
                    i.putExtra("WaybillNumber", filterData.get(position).get(
                            "WaybillNumber"));
                    i.putExtra("amount", filterData.get(position).get(
                            "CodAmount"));
                    i.putExtra("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"));
                    i.putExtra("from", "notcollect");
                    i.putExtra("Direct", "false");
                    activity.startActivity(i);
                } else {
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
                    Intent i = new Intent(activity, KCCBankReason.class);
                    i.putExtra("AppointmentId", filterData.get(position).get(
                            "AppointmentId"));
                    i.putExtra("WaybillNumber", filterData.get(position).get(
                            "WaybillNumber"));
                    i.putExtra("amount", filterData.get(position).get(
                            "CodAmount"));
                    i.putExtra("SellerContactNo", filterData.get(position).get(
                            "SellerContactNo"));
                    i.putExtra("from", "notcollect");
                    i.putExtra("Direct", "false");
                    activity.startActivity(i);
                } else {
                    alert.build(activity.getString(R.string.start_msg));
                }

            }
        });

        holder.MobileCallCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("call SET", String.valueOf(Util.call));
                Log.e("tel", holder.MobileNoC.getText().toString());

                if (!holder.MobileNoC.getText().toString().isEmpty()) {

                    Log.e("call", String.valueOf(Util.call));
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

      /*  holder.TxtCreatedDate.setText("Pickup Date:" + " " + filterData.get(position).get(
                "RequestPickupDT") + " " + filterData.get(position).get(
                "RequestPickupTime"));*/
        holder.arrow.setTag(position);

        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View vas) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                        if (data.get("ShipmentId").contains(constraint) || data.get("PickupType").contains(constraint) || data.get("SellerName").contains(constraint) || data.get("SellerContactNo").contains(constraint) || data.get("ClientOrderNumber").contains(constraint) || data.get("Cycle").contains(constraint)|| data.get("SellerPin").contains(constraint)) {
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
        private LinearLayout lytop, lybottom, rootlayout, notcollectclick;
        private LinearLayout lycredit, MobileCallCredit, lyButton, LyReshedule;
        ImageView arrow, logo;
        View line;
        //bank
        //credit
        private TextView BtnCollectC, BtnNotCollectC, BtnNotCollectCC, NameC, AddressC, MobileNoC, TxtConfiredTime;
        private TextView AccNoC, CardNoC, BalaceAmountC, TotalAmountC, AmountTobeCollectedC, DueDateC, DateC, TimeC, ResheduleC;
    }
}

