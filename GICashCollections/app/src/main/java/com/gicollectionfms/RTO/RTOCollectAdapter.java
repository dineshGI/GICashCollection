package com.gicollectionfms.RTO;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;
import com.gicollectionfms.activity.AlertActivity;
import com.gicollectionfms.activity.GpsTracker;
import com.gicollectionfms.activity.InvoiceActivity;
import com.gicollectionfms.interfaces.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RTOCollectAdapter extends BaseAdapter implements Filterable {
    private static LayoutInflater inflater = null;
    private Activity activity;
    ProgressDialog progressDialog;

    List<Map<String, String>> originaldata;
    List<Map<String, String>> filterData;
    CommonAlertDialog alert;
    private GpsTracker gpsTracker;

    public RTOCollectAdapter(Activity context, List<Map<String, String>> listCollectionone) {
        activity = context;
        originaldata = listCollectionone;
        filterData = listCollectionone;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflater = LayoutInflater.from(activity);
        alert = new CommonAlertDialog(activity);
        gpsTracker = new GpsTracker(activity);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        // add values for your ArrayList any where...

    }

    @Override
    public int getCount() {
        return this.filterData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.filterData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public void pos(int position) {

        filterData.remove(filterData.get(position));

    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (vi == null) {

            vi = inflater.inflate(R.layout.rto_collect_adapter, null);
            holder = new ViewHolder();

            holder.TxtshipmentID = vi
                    .findViewById(R.id.shipmentid);
            holder.TxtHandoverTo = vi
                    .findViewById(R.id.handoverto);
            holder.TxtHSshipmentDate = vi
                    .findViewById(R.id.hs_shipmentdate);
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

            holder.TxtHandoverName = vi
                    .findViewById(R.id.ho_name);
            holder.TxtHandoverDate = vi
                    .findViewById(R.id.ho_date);
            holder.TxtHandoverMobileno = vi
                    .findViewById(R.id.ho_mobileno);
            holder.LyHandoverMobileno = vi
                    .findViewById(R.id.ly_ho_mobileno);

            holder.arrow = vi
                    .findViewById(R.id.arrow);
            holder.lytop = vi
                    .findViewById(R.id.layouttop);
            holder.lybottom = vi
                    .findViewById(R.id.layoutbottom);
            holder.rootlayout = vi
                    .findViewById(R.id.root);

            holder.line = vi
                    .findViewById(R.id.removeline);
            holder.BtnMap = vi
                    .findViewById(R.id.Btn_map);
            holder.BtnCollect = vi
                    .findViewById(R.id.Btn_collect);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.TxtshipmentID.setText(filterData.get(position).get(
                "ShipmentId"));
        holder.TxtHandoverTo.setText(filterData.get(position).get(
                "HandOverTo"));
        holder.TxtHSshipmentDate.setText("Shipment Date :" + filterData.get(position).get(
                "CreatedDT"));
        holder.TxtHandoverName.setText(filterData.get(position).get(
                "handovername"));
        holder.TxtHandoverDate.setText(filterData.get(position).get(
                "AssignedDate"));
        holder.TxtHandoverMobileno.setText(filterData.get(position).get(
                "HandOverMobileNumber"));
        holder.TxtBagid.setText("Bag ID : " + filterData.get(position).get(
                "ShipmentId"));
        holder.TxtPicupZone.setText("Pickup Zone: " + filterData.get(position).get(
                "PickupZone"));
        holder.TxtDeliveryZone.setText("Delivery Zone : " + filterData.get(position).get(
                "DeliveryZone"));
        holder.TxtQty.setText("Total Pcs : " + filterData.get(position).get(
                "TotalQty"));
        holder.TxtInvoive.setText("Invoice : " + filterData.get(position).get(
                "InvoiceRefNo"));
        holder.arrow.setTag(position);
        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

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
                            holder.lytop.setBackgroundResource(R.color.lightgray);
                            holder.line.setVisibility(View.GONE);
                        }
                        // notifyDataSetChanged();
                    }
                });
            }
        });

        holder.BtnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.BtnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskHandSack(filterData.get(position).get(
                        "LegTrackId"));
            }
        });

        holder.TxtInvoive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadinvoicelist(filterData.get(position).get(
                        "OrderNumber"));
            }
        });
        holder.LyHandoverMobileno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + holder.TxtHandoverMobileno.getText().toString()));
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                activity.startActivity(callIntent);
            }
        });
        return vi;
    }

    private void TaskHandSack(String legTrackId) {

        {
            String data = "";
            JSONObject obj = new JSONObject();
            String latitude = "";
            String longitude = "";
            if (gpsTracker.canGetLocation()) {
                latitude = String.valueOf(gpsTracker.getLatitude());
                longitude = String.valueOf(gpsTracker.getLongitude());
                // Log.e("lat:::", String.valueOf(latitude));
                //  Log.e("long:::", String.valueOf(longitude));
            }
            try {
                obj.put("UserId", Util.getData("UserId", activity.getApplicationContext()));
                obj.put("LegTrackId", legTrackId);
                obj.put("Latitude", latitude);
                obj.put("Longitude", longitude);
                Util.Logcat.e("INPUT:::"+ obj.toString());
                // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
                data = Util.EncryptURL(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);
                CallApi.postResponse(activity, params.toString(),Util.HD_COLLECT_SINGLE, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {

                        if (message.contains("TimeoutError")) {
                            alert.build(activity.getString(R.string.timeout_error));
                        } else {
                            alert.build(activity.getString(R.string.server_error));
                        }
                         Util.Logcat.e("onError"+ message);
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                         Util.Logcat.e("onResponse"+ response);
                        try {
                            Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                            JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                            Intent alert = new Intent(activity, AlertActivity.class);
                            alert.putExtra("status", resobject.getString("Status"));
                            alert.putExtra("StatusDesc", resobject.getString("StatusDesc"));
                            activity.startActivity(alert);

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

    private void loadinvoicelist(final String orderNumber) {

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

            CallApi.postResponse(activity, params.toString(), Util.INVOICE, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(activity.getString(R.string.timeout_error));
                    }else if(message.contains("ParseError")){
                        alert.build(activity.getString(R.string.product_not_found));
                    }  else {
                        alert.build(activity.getString(R.string.server_error));
                    }
                     Util.Logcat.e("onError"+ message);
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
                                alert.build(activity.getString(R.string.server_empty));
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
        private TextView TxtshipmentID, TxtHandoverTo, TxtBagid, TxtPicupZone, TxtDeliveryZone, TxtQty, TxtInvoive;
        private TextView TxtHSshipmentDate, TxtHandoverName, TxtHandoverDate, TxtHandoverMobileno;
        private LinearLayout lytop, lybottom, rootlayout, BtnMap, BtnCollect, LyHandoverMobileno;
        ImageView arrow;
        View line;

    }
}

