package com.gicollectionfms.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gicollectionfms.R;

import java.util.List;
import java.util.Map;

public class NotCollectAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Map<String, String>> listdetails;
    ProgressDialog progressDialog;


    public NotCollectAdapter(Activity context, List<Map<String, String>> listCollectionone) {
        activity = context;
        listdetails = listCollectionone;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        // add values for your ArrayList any where...

    }

    @Override
    public int getCount() {
        return listdetails.size();
    }

    @Override
    public Object getItem(int position) {
        return listdetails.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void pos(int position) {

        listdetails.remove(listdetails.get(position));

    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (vi == null) {

            vi = inflater.inflate(R.layout.notcollect_adapter, null);
            holder = new ViewHolder();

            holder.FEName = vi
                    .findViewById(R.id.FEName);
            holder.PickupDTTime = vi
                    .findViewById(R.id.PickupDTTime);
            holder.NotCollectedReason = vi
                    .findViewById(R.id.NotCollectedReason);
            holder.Remarks = vi
                    .findViewById(R.id.Remarks);
            holder.NotCollectedSubReason = vi
                    .findViewById(R.id.NotCollectedSubReason);
            holder.PTPDate = vi
                    .findViewById(R.id.PTPDate);


            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.FEName.setText("FE Name : " + listdetails.get(position).get(
                "FEName"));
        holder.PickupDTTime.setText("Pickup Date : " + listdetails.get(position).get(
                "PickupDTTime"));
        holder.NotCollectedReason.setText("Not Collected Reason : " + listdetails.get(position).get(
                "NotCollectedReason"));
        holder.NotCollectedSubReason.setText("Sub Reason : " + listdetails.get(position).get(
                "NotCollectedSubReason"));
        holder.Remarks.setText("Remarks : " + listdetails.get(position).get(
                "Remarks"));

        if (listdetails.get(position).get(
                "NotCollectedSubReason").equalsIgnoreCase("CONFIRMED")) {
            holder.PTPDate.setText("Confirmed Date : " + listdetails.get(position).get(
                    "PTPDate"));
        } else if (!listdetails.get(position).get(
                "PTPDate").equalsIgnoreCase("null") && !listdetails.get(position).get(
                "PTPDate").isEmpty()) {
            holder.PTPDate.setText("PTP Date : " + listdetails.get(position).get(
                    "PTPDate"));
        } else {
            holder.PTPDate.setVisibility(View.GONE);
            //   holder.PTPDate.setText("PTP Date : ");
        }

        return vi;
    }


    public static class ViewHolder {
        private TextView FEName, PickupDTTime, NotCollectedReason, Remarks, NotCollectedSubReason, PTPDate;

    }
}

