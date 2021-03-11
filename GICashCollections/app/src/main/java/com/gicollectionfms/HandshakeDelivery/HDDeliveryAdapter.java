package com.gicollectionfms.HandshakeDelivery;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.gicollectionfms.activity.DeliveryAttemptActivity;
import com.gicollectionfms.activity.DeliveryCamModule;
import com.gicollectionfms.activity.DeliveryFailedReason;
import com.gicollectionfms.activity.GpsTracker;
import com.gicollectionfms.activity.InvoiceActivity;
import com.gicollectionfms.activity.MapsActivitynew;
import com.gicollectionfms.activity.RescheduleActivity;
import com.gicollectionfms.activity.Signature;
import com.gicollectionfms.interfaces.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HDDeliveryAdapter extends BaseAdapter implements Filterable {
    private static LayoutInflater inflater = null;
    private Activity activity;
    ProgressDialog progressDialog;

    List<Map<String, String>> originaldata;
    List<Map<String, String>> filterData;
    AlertDialog.Builder alertDialogBuilder;
    CommonAlertDialog alert;
    private GpsTracker gpsTracker;

    public HDDeliveryAdapter(Activity context, List<Map<String, String>> listCollectionone) {
        activity = context;
        originaldata = listCollectionone;
        filterData = listCollectionone;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflater = LayoutInflater.from(activity);
        alertDialogBuilder = new AlertDialog.Builder(activity);
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        gpsTracker = new GpsTracker(activity);
        alert = new CommonAlertDialog(activity);
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

            vi = inflater.inflate(R.layout.hd_delivery_adapter, null);
            holder = new ViewHolder();

            holder.TxtshipmentID = vi
                    .findViewById(R.id.shipmentid);
            holder.TxtDeliverTo = vi
                    .findViewById(R.id.deliverto);
            holder.LyMap = vi
                    .findViewById(R.id.txtmap);


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
            holder.TxtCODAmount = vi
                    .findViewById(R.id.cod_amount);
            holder.TxtDeliveryCount = vi
                    .findViewById(R.id.delivery_count);

            holder.TxtDeliverName = vi
                    .findViewById(R.id.d_name);
            holder.TxtDeliverDate = vi
                    .findViewById(R.id.d_date);
            holder.TxtDeliverMobileno = vi
                    .findViewById(R.id.d_phoneno);
            holder.LyHandoverMobileno = vi
                    .findViewById(R.id.ly_ho_mobileno);
            holder.TxtAddress = vi
                    .findViewById(R.id.d_address);


            holder.arrow = vi
                    .findViewById(R.id.arrow);
            holder.lytop = vi
                    .findViewById(R.id.layouttop);
            holder.lybottom = vi
                    .findViewById(R.id.layoutbottom);
            holder.lyOutforDelivery = vi
                    .findViewById(R.id.layout_outfordelivery);
            holder.lyThreeButton = vi
                    .findViewById(R.id.ly_threebutton);


            holder.rootlayout = vi
                    .findViewById(R.id.root);
            holder.line = vi
                    .findViewById(R.id.removeline);


            holder.BtnOutforDelivery = vi
                    .findViewById(R.id.Btn_outfordelivery);
            holder.BtnReschedule = vi
                    .findViewById(R.id.Btn_reschedule);

            holder.BtnSignIn = vi
                    .findViewById(R.id.sign);
            holder.BtnDeliveryDone = vi
                    .findViewById(R.id.delivery_done);
            holder.BtnNotDone = vi
                    .findViewById(R.id.not_done);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.TxtshipmentID.setText(filterData.get(position).get(
                "ShipmentId"));
        holder.TxtDeliverTo.setText("Delivery");

        if (Double.parseDouble(filterData.get(position).get(
                "COD")) > 0) {
            holder.TxtCODAmount.setText("COD Amount :" + filterData.get(position).get(
                    "COD"));
        } else {
            holder.TxtCODAmount.setVisibility(View.GONE);
        }

        holder.TxtDeliverName.setText(filterData.get(position).get(
                "ConsigneeName"));
        holder.TxtAddress.setText(filterData.get(position).get(
                "Address"));

        holder.TxtDeliverDate.setText(filterData.get(position).get(
                "CreatedDT"));
        holder.TxtDeliverMobileno.setText(filterData.get(position).get(
                "ConsigneeMobileNo"));

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

        if (Integer.valueOf(filterData.get(position).get(
                "DeliveryAttemptCount")) > 0) {
            holder.TxtDeliveryCount.setText("Delivery Attempt Count : " + filterData.get(position).get(
                    "DeliveryAttemptCount"));
        } else {
            holder.TxtDeliveryCount.setVisibility(View.GONE);
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Integer.valueOf(filterData.get(position).get(
                        "IsOutForDelivery")) == 0) {
                    holder.lyOutforDelivery.setVisibility(View.VISIBLE);
                    holder.lyThreeButton.setVisibility(View.GONE);
                } else if (Integer.valueOf(filterData.get(position).get("IsOutForDelivery")) == 1 && Double.parseDouble(filterData.get(position).get("COD")) == 0.00) {

                    holder.lyThreeButton.setVisibility(View.VISIBLE);
                    holder.lyOutforDelivery.setVisibility(View.GONE);
                    //Log.e("if", filterData.get(position).toString());

                } else if (Double.parseDouble(filterData.get(position).get(
                        "COD")) > 0 && Integer.valueOf(filterData.get(position).get(
                        "IsOutForDelivery")) == 1) {
                    holder.lyThreeButton.setVisibility(View.VISIBLE);
                    holder.lyOutforDelivery.setVisibility(View.GONE);
                    holder.BtnDeliveryDone.setText("COD Collect");
                }
            }
        });

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
                            // holder.lytop.setBackgroundResource(R.color.white);
                            holder.line.setVisibility(View.VISIBLE);
                            int viewColor = ((ColorDrawable) holder.lytop.getBackground()).getColor();
                            if (viewColor == Color.parseColor("#6af5ee")) {
                                holder.lytop.setBackgroundResource(R.color.colorAccent);
                            } else {
                                holder.lytop.setBackgroundResource(R.color.white);
                            }
                        } else {
                            holder.lybottom.setVisibility(View.VISIBLE);
                            holder.arrow.setImageResource(R.drawable.up);
                            holder.rootlayout.setBackgroundResource(R.drawable.editext);
                            //holder.lytop.setBackgroundResource(R.color.lightgray);
                            holder.line.setVisibility(View.GONE);
                            int viewColor = ((ColorDrawable) holder.lytop.getBackground()).getColor();
                            if (viewColor == Color.parseColor("#6af5ee")) {
                                holder.lytop.setBackgroundResource(R.color.colorAccent);
                            } else {
                                holder.lytop.setBackgroundResource(R.color.lightgray);
                            }
                        }

                        // notifyDataSetChanged();
                    }
                });
            }
        });

        holder.TxtInvoive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadinvoicelist(filterData.get(position).get(
                        "OrderNumber"));
            }
        });
        holder.BtnOutforDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutforDelivery(filterData.get(position).get(
                        "WaybillNumber"));
            }
        });
        holder.BtnReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(activity, RescheduleActivity.class);
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                activity.startActivity(i);

            }
        });
        holder.TxtDeliveryCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliveryattempt(filterData.get(position).get(
                        "WaybillNumber"));

            }
        });


        holder.BtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(activity, Signature.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                i.putExtra("COD", filterData.get(position).get(
                        "COD"));

                activity.startActivity(i);

            }
        });

        holder.BtnNotDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, DeliveryFailedReason.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                activity.startActivity(i);
            }
        });
        holder.BtnDeliveryDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(activity, DeliveryCamModule.class);
                i.putExtra("AppointmentId", filterData.get(position).get(
                        "AppointmentId"));
                i.putExtra("WaybillNumber", filterData.get(position).get(
                        "WaybillNumber"));
                i.putExtra("COD", filterData.get(position).get(
                        "COD"));

                activity.startActivity(i);

            }
        });
        holder.LyMap.setOnClickListener(new View.OnClickListener() {
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

        holder.LyHandoverMobileno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tel", filterData.get(position).get(
                        "ConsigneeMobileNo"));

                if (!filterData.get(position).get(
                        "ConsigneeMobileNo").isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + filterData.get(position).get(
                            "ConsigneeMobileNo")));
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    activity.startActivity(callIntent);
                } else {
                    alert.build(activity.getString(R.string.mobileno_notfound));
                }
            }
        });

        return vi;
    }

    private void deliveryattempt(String waybillno) {

        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", Util.getData("UserId", activity.getApplicationContext()));
            obj.put("WaybillNo", waybillno);

            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(activity, params.toString(), Util.DELIVERY_ATTEMPT, new VolleyResponseListener() {
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
                        //  Log.e("OUTPUT:::", response);
                        JSONObject resobject = response;

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("Table");

                            if (jsonArray.length() > 0) {
                                Util.Logcat.e("length"+ jsonArray.length());
                                Log.e("Table", resobject.optString("DeliveryAttemptDate"));
                                Log.e("Table", resobject.optString("Reason"));
                                Intent mIntent = new Intent(activity, DeliveryAttemptActivity.class);
                                mIntent.putExtra("deliveryattemptdata", resobject.toString());
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

    private void OutforDelivery(String waybillno) {

        {
            String data = "";
            JSONObject obj = new JSONObject();

            try {
                obj.put("UserId", Util.getData("UserId", activity.getApplicationContext()));
                obj.put("WaybillNo", waybillno);
                Util.Logcat.e("INPUT:::"+ obj.toString());
                // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
                data = Util.EncryptURL(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);
                CallApi.postResponse(activity, params.toString(), Util.OUT_FOR_DELIVERY, new VolleyResponseListener() {
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
                    } else if (message.contains("ParseError")) {
                        alert.build(activity.getString(R.string.product_not_found));
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

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("_GetProductList");


                            if (jsonArray.length() > 0) {
                                Util.Logcat.e("length"+ jsonArray.length());
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
        private TextView TxtshipmentID, TxtDeliverTo, TxtBagid, TxtPicupZone, TxtDeliveryZone, TxtQty, TxtInvoive;
        private TextView TxtDeliverName, TxtDeliverDate, TxtDeliverMobileno, TxtAddress, TxtCODAmount, TxtDeliveryCount;
        private LinearLayout lytop, lybottom, rootlayout, BtnOutforDelivery, BtnReschedule, lyOutforDelivery, lyThreeButton, LyHandoverMobileno, LyMap;
        Button BtnSignIn, BtnDeliveryDone, BtnNotDone;
        ImageView arrow;
        View line;

    }
}

