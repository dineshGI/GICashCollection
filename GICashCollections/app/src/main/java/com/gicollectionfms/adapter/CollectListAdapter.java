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

public class CollectListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Map<String, String>> listdetails;
    ProgressDialog progressDialog;


    public CollectListAdapter(Activity context, List<Map<String, String>> listCollectionone) {
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

            vi = inflater.inflate(R.layout.collectlist_adapter, null);
            holder = new ViewHolder();

            holder.TxtName = vi
                    .findViewById(R.id.name);
            holder.TxtCount = vi
                    .findViewById(R.id.count);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.TxtName.setText(listdetails.get(position).get(
                "name"));
        holder.TxtCount.setText("Collect - " + listdetails.get(position).get(
                "count"));
        return vi;
    }


    public static class ViewHolder {
        private TextView TxtName, TxtCount;

    }
}

