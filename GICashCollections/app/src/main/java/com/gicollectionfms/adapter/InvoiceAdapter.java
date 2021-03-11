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

public class InvoiceAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Activity activity;
    private List<Map<String, String>> listdetails;
    ProgressDialog progressDialog;


    public InvoiceAdapter(Activity context, List<Map<String, String>> listCollectionone) {
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

            vi = inflater.inflate(R.layout.invoice_adapter, null);
            holder = new ViewHolder();

            holder.TxtProdCode = vi
                    .findViewById(R.id.product_code);
            holder.TxtProdTitle = vi
                    .findViewById(R.id.product_title);
            holder.TxtHSNCode = vi
                    .findViewById(R.id.hsncode);
            holder.TxtQty = vi
                    .findViewById(R.id.product_qty);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.TxtProdCode.setText("Product Code : "+listdetails.get(position).get(
                "ProductReferenceCode"));
        holder.TxtProdTitle.setText(listdetails.get(position).get(
                "ProductDesc"));
        holder.TxtHSNCode.setText("HSNCode : "+listdetails.get(position).get(
                "HSNCode"));
        holder.TxtQty.setText("Quantity : "+listdetails.get(position).get(
                "Qty"));
        return vi;
    }


    public static class ViewHolder {
        private TextView TxtProdCode, TxtProdTitle, TxtHSNCode, TxtQty;

    }
}

