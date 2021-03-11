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

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.KotakPickup.KPickupNewAdapter;
import com.gicollectionfms.adapter.InvoiceAdapter;
import com.gicollectionfms.adapter.NotCollectAdapter;
import com.gicollectionfms.interfaces.VolleyResponseListener;
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

import static com.gicollectionfms.utils.Util.DASHBOARD_NOTCOLLECTED;
import static com.gicollectionfms.utils.Util.PICKUP;

public class NotCollectedActivity extends AppCompatActivity {

    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    ListView listView;
    public NotCollectAdapter adapter;
    ProgressDialog progressDialog;
    CommonAlertDialog alert;

    //https://stackoverflow.com/questions/41295784/how-to-call-a-function-of-fragment-from-custom-adapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notcollected_activity);
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
        listView = findViewById(R.id.notcollect_listview);
        Util.refresh = false;
        GetDetails(getIntent().getStringExtra("WaybillNumber"));


    }

    private void GetDetails(String waybillno) {

        ListCollection.clear();
        try {
            JSONObject obj = new JSONObject();

            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("WaybillNo", waybillno);
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(this, params.toString(), DASHBOARD_NOTCOLLECTED, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(getString(R.string.timeout_error));
                    } else {
                        alert.build(getString(R.string.server_error));
                    }
                    Util.Logcat.e("onError" + message);
                }

                @Override
                public void onResponse(JSONObject response) {
                    ListCollection.clear();
                    Util.Logcat.e("onResponse" + response);
                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        JSONArray jsonArray = resobject.optJSONArray("_lstGetNotCollectedShipmentsOutputModel");

                        if (jsonArray.length() > 0) {
                            Util.Logcat.e("length" + jsonArray.length());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                DataHashMap = new HashMap<>();
                                DataHashMap.put("WaybillNumber", jsonObject.optString("WaybillNumber"));
                                DataHashMap.put("NotCollectedReason", jsonObject.optString("NotCollectedReason"));
                                DataHashMap.put("NotCollectedSubReason", jsonObject.optString("NotCollectedSubReason"));
                                DataHashMap.put("FEName", jsonObject.optString("FEName"));
                                DataHashMap.put("PickupDTTime", jsonObject.optString("PickupDTTime"));
                                DataHashMap.put("Remarks", jsonObject.optString("Remarks"));
                                DataHashMap.put("PTPDate", jsonObject.optString("PTPDate"));

                                ListCollection.add(DataHashMap);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    adapter = new NotCollectAdapter(NotCollectedActivity.this, ListCollection);
                                    listView.setAdapter(adapter);

                                }
                            });
                        } else {
                            alert.build("No Disposition Available");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
