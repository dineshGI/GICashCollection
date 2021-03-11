package com.gicollectionfms.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;
import com.gicollectionfms.adapter.CollectListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectListActivity extends AppCompatActivity {

    private HashMap<String, String> DataHashMap;
    private List<Map<String, String>> ListCollection;
    ListView listView;
    public CollectListAdapter adapter;
    ProgressDialog progressDialog;
    // AlertDialog.Builder alertDialogBuilder;
    CommonAlertDialog alert;

    List<String> name;

    //https://stackoverflow.com/questions/41295784/how-to-call-a-function-of-fragment-from-custom-adapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colleclist);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        alert = new CommonAlertDialog(this);
        ListCollection = new ArrayList<Map<String, String>>();
        listView = findViewById(R.id.collect_listview);
        name = new ArrayList<>();

        loadlist();
    }

    private void loadlist() {
        ListCollection.clear();
        String data = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("SAId", Util.getData("SAId", getApplicationContext()));
            obj.put("SABranchId", Util.getData("SABranchId", getApplicationContext()));
            obj.put("TaskType", "2");
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("Filter", "1");
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("REQUEST:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());

            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(this, params.toString(), Util.HD_COLLECTLIST, new VolleyResponseListener() {
                @Override
                public void onError(String message) {

                    if (message.contains("TimeoutError")) {
                        alert.build(getString(R.string.timeout_error));
                    } else {
                        alert.build(getString(R.string.server_error));
                    }
                     Util.Logcat.e("onError"+ message);
                }

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            JSONArray jsonArray = resobject.optJSONArray("Delivery");

                            if (jsonArray != null && jsonArray.length() > 0) {
                                Util.Logcat.e("length"+ jsonArray.length());

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    name.add(jsonObject.optString("RunnerName"));
                                }

                                Set<String> uniqueSet = new HashSet<String>(name);
                                for (String temp : uniqueSet) {
                                    if (!temp.equalsIgnoreCase("")) {
                                        System.out.println(temp + ": " + Collections.frequency(name, temp));
                                        DataHashMap = new HashMap<String, String>();
                                        DataHashMap.put("name", temp);
                                        DataHashMap.put("count", String.valueOf(Collections.frequency(name, temp)));
                                        ListCollection.add(DataHashMap);
                                    }
                                }
                            }
                        } else if(resobject.getString("Status").equalsIgnoreCase("1")){
                            alert.build(resobject.getString("StatusDesc"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//http://codesfor.in/contextual-actionbar-in-android/
                            adapter = new CollectListAdapter(CollectListActivity.this, ListCollection);
                            listView.setAdapter(adapter);

                        }
                    });
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
