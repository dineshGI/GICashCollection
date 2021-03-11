package com.gicollectionfms.adapter;

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

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.R;
import com.gicollectionfms.activity.BankReason;
import com.gicollectionfms.activity.BarcodeScan;
import com.gicollectionfms.activity.CamModule;
import com.gicollectionfms.activity.GpsTracker;
import com.gicollectionfms.activity.InvoiceActivity;
import com.gicollectionfms.activity.MapsActivitynew;
import com.gicollectionfms.activity.RadioButtonActivity;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gicollectionfms.utils.Util.INVOICE;

public class PickupListviewAdapter extends BaseAdapter implements Filterable {

    private static LayoutInflater inflater = null;
    private Activity activity;
    ProgressDialog progressDialog;
    private GpsTracker gpsTracker;
    List<Map<String, String>> originaldata;
    List<Map<String, String>> filterData;
    CommonAlertDialog alert;

    public PickupListviewAdapter(Activity context, List<Map<String, String>> listCollectionone) {

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

            vi = inflater.inflate(R.layout.pickup_adapter, null);
            holder = new ViewHolder();

            holder.TxtshipmentID = vi
                    .findViewById(R.id.shipmentid);
            holder.TxtCreatedDate = vi
                    .findViewById(R.id.crtddate);
            holder.TxtSellerName = vi
                    .findViewById(R.id.sellername);
            holder.TxtSellerAddress = vi
                    .findViewById(R.id.selleraddress);
            holder.TxtStatePin = vi
                    .findViewById(R.id.state_pincode);
            holder.TxtMobileNo = vi
                    .findViewById(R.id.mobileno);
            holder.arrow = vi
                    .findViewById(R.id.arrow);
            holder.logo = vi
                    .findViewById(R.id.company_logo);
            holder.lytop = vi
                    .findViewById(R.id.layouttop);
            holder.lybottom = vi
                    .findViewById(R.id.layoutbottom);
            holder.lyOnemg = vi
                    .findViewById(R.id.ly_onemg);
            holder.lybank = vi
                    .findViewById(R.id.layoutbank);
            holder.lycredit = vi
                    .findViewById(R.id.layoutcredit);

            holder.rootlayout = vi
                    .findViewById(R.id.root);
            holder.lybuttonstatus = vi
                    .findViewById(R.id.lybuttonstatus);
            holder.line = vi
                    .findViewById(R.id.removeline);
            holder.TxtBagid = vi
                    .findViewById(R.id.bagid);
            holder.TxtPicupZone = vi
                    .findViewById(R.id.pickupzone);
            holder.TxtDeliveryZone = vi
                    .findViewById(R.id.deliveryzone);
            holder.TxtQty = vi
                    .findViewById(R.id.totalqty);
            holder.TxtInvoive = vi
                    .findViewById(R.id.invoice);
            holder.BtnScan = vi
                    .findViewById(R.id.scan);
            holder.BtnDone = vi
                    .findViewById(R.id.done);
            holder.BtnNotDone = vi
                    .findViewById(R.id.notdone);
            holder.BtnMap = vi
                    .findViewById(R.id.txtmap);
            //Bank layout

            holder.NameB = vi
                    .findViewById(R.id.b_name);
            holder.NameC = vi
                    .findViewById(R.id.c_name);
            holder.MobileNoB = vi
                    .findViewById(R.id.b_mobileno);
            holder.MobileNoC = vi
                    .findViewById(R.id.c_mobileno);
            holder.MobileCallLy = vi
                    .findViewById(R.id.ly_b_mobileno);
            holder.PaymentMode = vi
                    .findViewById(R.id.b_paymentmode);
            holder.Nach = vi
                    .findViewById(R.id.b_nach);
            holder.EMI = vi
                    .findViewById(R.id.b_emi);
            holder.Penalty = vi
                    .findViewById(R.id.b_penalty);
            holder.AddressB = vi
                    .findViewById(R.id.b_address);
            holder.AddressC = vi
                    .findViewById(R.id.c_address);
            //OSBounce,OSCharges,OSPenalty,ForeclosureValue;
            holder.OSBounce = vi
                    .findViewById(R.id.os_bounce);
            holder.OSCharges = vi
                    .findViewById(R.id.os_charge);
            holder.OSPenalty = vi
                    .findViewById(R.id.os_penality);
            holder.ForeclosureValue = vi
                    .findViewById(R.id.fclosevalue);


            holder.BtnCollectB = vi
                    .findViewById(R.id.b_collect);
            holder.BtnCollectC = vi
                    .findViewById(R.id.c_collect);

            holder.BtnNotCollectB = vi
                    .findViewById(R.id.b_notcollect);
            holder.BtnNotCollectC = vi
                    .findViewById(R.id.c_notcollect);

            //Credit Layout
            //AccNoC,CardNoC,AmountPromisedC,BalaceAmountC,TotalAmountC,DueDateC
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

            vi.setTag(holder);

        } else {
            holder = (ViewHolder) vi.getTag();
        }
        //
        if (filterData.get(position).get(
                "ClientName").equalsIgnoreCase("1MG")) {
            holder.logo.setImageDrawable(activity.getDrawable(R.drawable.pic_onemg));
            holder.TxtshipmentID.setText(filterData.get(position).get(
                    "ShipmentId") + " - " + filterData.get(position).get(
                    "ClientName"));
            holder.lyOnemg.setVisibility(View.VISIBLE);
            holder.lybank.setVisibility(View.GONE);

        } else if (filterData.get(position).get(
                "ClientName").equalsIgnoreCase("GoFynd")) {
            holder.logo.setImageDrawable(activity.getDrawable(R.drawable.pic_gofynd));
            holder.TxtshipmentID.setText(filterData.get(position).get(
                    "ShipmentId") + " - " + filterData.get(position).get(
                    "ClientName"));
            holder.lyOnemg.setVisibility(View.VISIBLE);
            holder.lybank.setVisibility(View.GONE);
        } else if (filterData.get(position).get(
                "ClientName").equalsIgnoreCase("Kotak")) {

            if (filterData.get(position).get(
                    "ShipmentType").equalsIgnoreCase("2")) {
                holder.logo.setImageDrawable(activity.getDrawable(R.drawable.pic_kodak));
                holder.TxtshipmentID.setText(filterData.get(position).get(
                        "ShipmentId") + " - " + filterData.get(position).get(
                        "ClientName") + " Loan");
                holder.lyOnemg.setVisibility(View.GONE);
                holder.lybank.setVisibility(View.VISIBLE);
                holder.lycredit.setVisibility(View.GONE);
            } else if (filterData.get(position).get(
                    "ShipmentType").equalsIgnoreCase("3")) {
                holder.logo.setImageDrawable(activity.getDrawable(R.drawable.pic_kodak));
                holder.TxtshipmentID.setText(filterData.get(position).get(
                        "ShipmentId") + " - " + filterData.get(position).get(
                        "ClientName") + " Credit");
                holder.lyOnemg.setVisibility(View.GONE);
                holder.lybank.setVisibility(View.GONE);
                holder.lycredit.setVisibility(View.VISIBLE);
            }

        }
        /*if (filterData.get(position).get(
                "ClientName").equalsIgnoreCase("1MG")) {
            holder.logo.setImageDrawable(activity.getDrawable(R.drawable.pic_onemg));
            holder.TxtshipmentID.setText(filterData.get(position).get(
                    "ShipmentId") + " - " + filterData.get(position).get(
                    "ClientName"));
            holder.lyOnemg.setVisibility(View.VISIBLE);
            holder.lybank.setVisibility(View.GONE);

        } else if (filterData.get(position).get(
                "ClientName").equalsIgnoreCase("GoFynd")) {
            holder.logo.setImageDrawable(activity.getDrawable(R.drawable.pic_gofynd));
            holder.TxtshipmentID.setText(filterData.get(position).get(
                    "ShipmentId") + " - " + filterData.get(position).get(
                    "ClientName"));
            holder.lyOnemg.setVisibility(View.VISIBLE);
            holder.lybank.setVisibility(View.GONE);
        } else if (filterData.get(position).get(
                "ClientName").equalsIgnoreCase("Kotak")) {
            holder.logo.setImageDrawable(activity.getDrawable(R.drawable.pic_kodak));
            holder.TxtshipmentID.setText(filterData.get(position).get(
                    "ShipmentId") + " - " + filterData.get(position).get(
                    "ClientName"));
            holder.lyOnemg.setVisibility(View.GONE);
            holder.lybank.setVisibility(View.VISIBLE);
        }*/

        //Bank data
       /* holder.ID.setText(filterData.get(position).get(
                "ShipmentId"));*/
        holder.NameB.setText(filterData.get(position).get(
                "SellerName"));
        holder.NameC.setText(filterData.get(position).get(
                "SellerName"));
        holder.MobileNoB.setText(filterData.get(position).get(
                "SellerContactNo"));
        holder.MobileNoC.setText(filterData.get(position).get(
                "SellerContactNo"));
        holder.PaymentMode.setText(filterData.get(position).get(
                "PaymentMode"));
        holder.Nach.setText(filterData.get(position).get(
                "NACH"));
        holder.EMI.setText(filterData.get(position).get(
                "CodAmount"));
        holder.Penalty.setText(filterData.get(position).get(
                "PenaltyCharges"));
        holder.AddressB.setText(filterData.get(position).get(
                "SellerAddress")+" - "+filterData.get(position).get(
                "SellerPin"));
        holder.AddressC.setText(filterData.get(position).get(
                "SellerAddress")+" - "+filterData.get(position).get(
                "SellerPin"));

        holder.OSBounce.setText(filterData.get(position).get(
                "OSBounce"));
        holder.OSCharges.setText(filterData.get(position).get(
                "OSCharges"));
        holder.OSPenalty.setText(filterData.get(position).get(
                "OSPenalty"));
        holder.ForeclosureValue.setText(filterData.get(position).get(
                "ForeclosureValue"));
        //credit


        holder.AccNoC.setText("Account No : "+filterData.get(position).get(
                "InvoiceRefNo"));
        holder.CardNoC.setText("Card No : "+filterData.get(position).get(
                "ClientOrderNumber"));

        holder.AmountTobeCollectedC.setText("Amount to be Collect :"+filterData.get(position).get(
                "CodAmount"));
        holder.BalaceAmountC.setText(filterData.get(position).get(
                "BalanceAmount"));
        holder.TotalAmountC.setText(filterData.get(position).get(
                "TotalBillAmount"));
        holder.DueDateC.setText("Amount to be Collect :"+filterData.get(position).get(
                "PaymentDueDate"));
        holder.DateC.setText("Lead Date:" + filterData.get(position).get(
                "CreatedDT"));
        holder.TimeC.setText("Pickup Time :" + filterData.get(position).get(
                "RequestPickupTime"));
        holder.TimeC.setVisibility(View.GONE);

        holder.BtnCollectB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(activity, BankReason.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                i.putExtra("amount", filterData.get(position).get(
                        "CodAmount"));
                i.putExtra("from", "collect");
                activity.startActivity(i);
            }
        });

        holder.BtnNotCollectB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, BankReason.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                i.putExtra("amount", filterData.get(position).get(
                        "CodAmount"));
                i.putExtra("from", "notcollect");
                activity.startActivity(i);

            }
        });
        //credit
        holder.BtnCollectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(activity, BankReason.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                i.putExtra("amount", filterData.get(position).get(
                        "CodAmount"));
                i.putExtra("from", "collect");
                activity.startActivity(i);

            }
        });
        holder.BtnNotCollectC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(activity, BankReason.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                i.putExtra("amount", filterData.get(position).get(
                        "CodAmount"));
                i.putExtra("from", "notcollect");
                activity.startActivity(i);

            }
        });


        holder.MobileCallCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!holder.MobileNoC.getText().toString().isEmpty()) {
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

        holder.MobileCallLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!holder.MobileNoB.getText().toString().isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + holder.MobileNoB.getText().toString()));
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    activity.startActivity(callIntent);
                } else {
                    alert.build(activity.getString(R.string.mobileno_notfound));
                }

            }
        });

        holder.TxtCreatedDate.setText("Pickup Date:" + " " + filterData.get(position).get(
                "RequestPickupDT")+" "+filterData.get(position).get(
                "RequestPickupTime"));
        holder.TxtSellerName.setText(filterData.get(position).get(
                "SellerName"));
        holder.TxtSellerAddress.setText(filterData.get(position).get(
                "SellerAddress"));
        holder.TxtStatePin.setText(filterData.get(position).get(
                "statepin"));
        holder.TxtMobileNo.setText(filterData.get(position).get(
                "SellerContactNo"));
        holder.TxtBagid.setText(activity.getText(R.string.bag_id) + filterData.get(position).get(
                "WaybillNumber"));
        holder.TxtPicupZone.setText(activity.getText(R.string.pickup_zone) + filterData.get(position).get(
                "PickupZone"));
        holder.TxtDeliveryZone.setText(activity.getText(R.string.delivery_zone) + filterData.get(position).get(
                "DeliveryZone"));
        holder.TxtQty.setText(activity.getText(R.string.total_pcs) + filterData.get(position).get(
                "TotalQty"));
        holder.TxtInvoive.setText(activity.getText(R.string.invoice) + filterData.get(position).get(
                "InvoiceRefNo"));
        if (filterData.get(position).get(
                "ButtonStatus").equalsIgnoreCase("0")) {
            holder.lybuttonstatus.setVisibility(View.VISIBLE);
        } else {
            holder.lybuttonstatus.setVisibility(View.GONE);
        }

        holder.TxtInvoive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadinvoicelist(filterData.get(position).get(
                        "OrderNumber"));

            }
        });

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

        holder.BtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("clicked", "BtnScan");
                Intent i = new Intent(activity, BarcodeScan.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                i.putExtra("OrderNumber", filterData.get(position).get(
                        "OrderNumber"));
                i.putExtra("frombulkscan", "false");

                activity.startActivity(i);
            }
        });

        holder.BtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cameraview
                Intent i = new Intent(activity, CamModule.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                // filterData.get(position);
                // activity.startActivity(i);
                activity.startActivity(i);
            }
        });

        holder.BtnNotDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* showRadioButtonDialog(filterData.get(position).get(
                        "AppointmentId"), filterData.get(position).get(
                        "WaybillNumber"));*/
                Intent i = new Intent(activity, RadioButtonActivity.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                activity.startActivity(i);
            }
        });

        holder.BtnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = filterData.get(position).get(
                        "Lat");
                String lng = filterData.get(position).get(
                        "Long");
                if (lat.length() > 4 && lng.length() > 4) {
                    Intent i = new Intent(activity, MapsActivitynew.class);
                    i.putExtra("lat", lat);
                    i.putExtra("lng", lng);
                    activity.startActivity(i);
                } else {
                    alert.build("Location Not Found");
                }
            }
        });

        return vi;
    }

    private void loadinvoicelist(String orderNumber) {

        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", activity.getApplicationContext()));
            obj.put("ClientOrderNumber", orderNumber);

            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(activity, params.toString(), INVOICE, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Log.e("message", message);
                    if (message.contains("TimeoutError")) {
                        alert.build(activity.getString(R.string.timeout_error));
                    } else if (message.contains("ParseError")) {
                        alert.build(activity.getString(R.string.product_not_found));
                    } else {
                        alert.build(activity.getString(R.string.server_error));
                    }
                }

                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);

                    try {
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("_GetProductList");
                            Util.Logcat.e("length"+ jsonArray.length());

                            if (jsonArray.length() > 0) {
                                Intent mIntent = new Intent(activity, InvoiceActivity.class);
                                mIntent.putExtra("ITEM_EXTRA", resobject.toString());
                                activity.startActivity(mIntent);
                            } else {
                                alert.build(activity.getString(R.string.product_not_found));
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

                    List<Map<String, String>> filterResultsData = new ArrayList<Map<String, String>>();
                    for (Map<String, String> data : originaldata) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.get("ShipmentId").contains(constraint)) {
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
        private TextView TxtshipmentID, TxtCreatedDate, TxtBagid, TxtPicupZone, TxtDeliveryZone, TxtQty, TxtInvoive;
        private TextView TxtSellerName, TxtSellerAddress, TxtStatePin, TxtMobileNo;
        private TextView BtnScan, BtnDone, BtnNotDone;
        private LinearLayout lytop, lybottom, rootlayout, BtnMap, lybuttonstatus;
        private LinearLayout lyOnemg, lybank, lycredit, MobileCallLy,MobileCallCredit;
        ImageView arrow, logo;
        View line;
        //bank
        private TextView BtnCollectB, BtnNotCollectB, AddressB, MobileNoB, PaymentMode, Nach, EMI, Penalty, NameB, OSBounce, OSCharges, OSPenalty, ForeclosureValue;
        //credit
        private TextView BtnCollectC, BtnNotCollectC, NameC, AddressC, MobileNoC;
        private TextView AccNoC, CardNoC, BalaceAmountC, TotalAmountC,AmountTobeCollectedC, DueDateC, DateC, TimeC;
    }
}

