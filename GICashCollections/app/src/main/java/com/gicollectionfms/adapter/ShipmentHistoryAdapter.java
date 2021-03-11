package com.gicollectionfms.adapter;

import android.app.Activity;
import android.app.AlertDialog;
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

public class ShipmentHistoryAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Activity activity;
    ProgressDialog progressDialog;

    List<Map<String, String>> originaldata;
    List<Map<String, String>> filterData;
    AlertDialog.Builder alertDialogBuilder;

    public ShipmentHistoryAdapter(Activity context, List<Map<String, String>> listCollectionone) {
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

            vi = inflater.inflate(R.layout.shipmenthistory_adapter, null);
            holder = new ViewHolder();
            holder.TxtRunnerName = vi.findViewById(R.id.runner_name);
            holder.TxtArea = vi.findViewById(R.id.area);
            holder.TxtWaybillNo = vi.findViewById(R.id.waybill_no);
            holder.TxtPickupDate = vi.findViewById(R.id.pickup_date);
            holder.TxtLegStatus = vi.findViewById(R.id.legstatus);
            holder.TxtTask = vi.findViewById(R.id.task);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.TxtRunnerName.setText(filterData.get(position).get(
                "RunnerName"));
        holder.TxtArea.setText(filterData.get(position).get(
                "Area"));
        holder.TxtWaybillNo.setText(filterData.get(position).get(
                "WaybillNo"));
        holder.TxtPickupDate.setText(filterData.get(position).get(
                "PickUpDoneDate"));
        holder.TxtLegStatus.setText(filterData.get(position).get(
                "LegStatus"));
        holder.TxtTask.setText(filterData.get(position).get(
                "TASK"));

        return vi;
    }


    public static class ViewHolder {
        TextView TxtRunnerName, TxtArea, TxtWaybillNo, TxtPickupDate, TxtLegStatus, TxtTask;

    }
}

