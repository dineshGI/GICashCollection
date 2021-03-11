package com.gicollectionfms.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.gicollectionfms.adapter.InvoiceAdapter;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceActivity extends AppCompatActivity {

    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    ListView listView;
    public InvoiceAdapter adapter;
    ProgressDialog progressDialog;
    CommonAlertDialog alert;

    //https://stackoverflow.com/questions/41295784/how-to-call-a-function-of-fragment-from-custom-adapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        alert = new CommonAlertDialog(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        ListCollection = new ArrayList<Map<String, String>>();
        listView = findViewById(R.id.invoice_listview);
        Util.refresh = false;
        try {
            JSONObject hai = new JSONObject(getIntent().getStringExtra("ITEM_EXTRA"));
            if (hai.getString("StatusDesc").equalsIgnoreCase("Successfully")) {


                JSONArray jsonArray = hai.optJSONArray("_GetProductList");

                if (jsonArray != null && jsonArray.length() > 0) {
                    Util.Logcat.e("length"+ jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        DataHashMap = new HashMap<String, String>();
                        DataHashMap.put("ProductReferenceCode", jsonObject.optString("ProductReferenceCode"));
                        DataHashMap.put("ProductDesc", jsonObject.optString("ProductDesc"));
                        DataHashMap.put("HSNCode", jsonObject.optString("HSNCode"));
                        DataHashMap.put("Qty", jsonObject.optString("Qty"));
                        ListCollection.add(DataHashMap);

                    }
                } else {
                    alert.build(getString(R.string.server_empty));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter = new InvoiceAdapter(InvoiceActivity.this, ListCollection);
                        listView.setAdapter(adapter);

                    }
                });


            }

            //  Log.e(TAG, "Example Item: " + json_object.getString("KEY"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

       /* if (!intent.getStringExtra("OrderNumber").equalsIgnoreCase(null)) {
            loadlist(intent.getStringExtra("OrderNumber"));
        }*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
