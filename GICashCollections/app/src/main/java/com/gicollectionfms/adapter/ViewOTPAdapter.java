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

public class ViewOTPAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Activity activity;
    ProgressDialog progressDialog;

    List<Map<String, String>> filterData;

    public ViewOTPAdapter(Activity context, List<Map<String, String>> listCollectionone) {
        activity = context;
        filterData = listCollectionone;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflater = LayoutInflater.from(activity);
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

            vi = inflater.inflate(R.layout.viewotp_adapter, null);
            holder = new ViewHolder();
            holder.TxtOTP = vi.findViewById(R.id.otp);
            holder.TxtDate = vi.findViewById(R.id.date);
            holder.TxtAmount = vi.findViewById(R.id.amount);
            holder.TxtCollectFrom = vi.findViewById(R.id.collect_from);

            vi.setTag(holder);

        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.TxtOTP.setText(filterData.get(position).get(
                "OTP"));
        holder.TxtDate.setText(filterData.get(position).get(
                "Date"));
        holder.TxtAmount.setText(filterData.get(position).get(
                "TotalAmount"));
        holder.TxtCollectFrom.setText(filterData.get(position).get(
                "CollectFrom"));

        return vi;
    }

    public static class ViewHolder {
        TextView TxtOTP, TxtDate, TxtAmount, TxtCollectFrom;

    }
}

