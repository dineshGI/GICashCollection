package com.gicollectionfms.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.R;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;

public class LoginActivity extends Activity {

    EditText EdUsername, EdPassword;
    Button BtnLogin;
    private CheckBox chkbox;
    String device;
    private GpsTracker gpsTracker;
    String latitude, longitude;
    String osName;
    CommonAlertDialog alert;
    ProgressDialog loading;
    TextView TxtVersion;
    ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /*if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.REQUEST_INSTALL_PACKAGES},
                        0);
            }
        }*/

        if (shouldAskPermissions()) {
            askPermissions();
        }

        pd = new ProgressDialog(this);
        TxtVersion = findViewById(R.id.app_vers);
        TxtVersion.setText(Util.app_version_name);
        EdUsername = findViewById(R.id.username);
        EdPassword = findViewById(R.id.password);
        EdPassword.setTypeface(Typeface.DEFAULT);
        EdPassword.setTransformationMethod(new PasswordTransformationMethod());
        loading = new ProgressDialog(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AutofillManager autofillManager = getSystemService(AutofillManager.class);
            autofillManager.disableAutofillServices();
            EdUsername.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
            EdPassword.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }

        BtnLogin = findViewById(R.id.login);
        chkbox = findViewById(R.id.rememberme);
        alert = new CommonAlertDialog(this);

        gpsTracker = new GpsTracker(LoginActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());
        }

        devicedetails();
        chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!Util.getData("loginuser", getApplicationContext()).isEmpty()) {
                        EdUsername.setText(Util.getData("loginuser", getApplicationContext()));
                        EdPassword.setText(Util.getData("loginpass", getApplicationContext()));
                    }
                } else {
                    EdUsername.setText("");
                    EdPassword.setText("");
                }
            }
        });

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (Util.isOnline(getApplicationContext())) {
                if (EdUsername.getEditableText().toString().isEmpty()) {
                    alert.build(getResources().getString(R.string.enter_username));
                } else if (EdPassword.getEditableText().toString().isEmpty()) {
                    alert.build(getResources().getString(R.string.enter_password));
                } else {
                    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    } else {
                        login();
                    }
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.BLUETOOTH",
                "android.permission.CAMERA",
                "android.permission.READ_PHONE_STATE",
                "android.permission.INSTALL_PACKAGES",
                "android.permission.REQUEST_INSTALL_PACKAGES",
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void login() {

        try {
            JSONObject obj = new JSONObject();
            obj.put("LoginId", EdUsername.getEditableText().toString());
            Util.saveData("LoginId", EdUsername.getEditableText().toString(), getApplicationContext());
            obj.put("Password", Util.EncryptURL(EdPassword.getEditableText().toString()));
            obj.put("DeviceType", "2");
            obj.put("DeviceInfo", device);
            Util.saveData("DeviceInfo", device, getApplicationContext());
            obj.put("Version", Util.app_version);
            obj.put("Lat", latitude);
            obj.put("Long", longitude);
            obj.put("FCMToken", Util.getData("FCMToken", getApplicationContext()));
            Util.Logcat.e("INPUT:::" + obj.toString());
            String data = Util.EncryptURL(obj.toString());
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);

            CallApi.postResponse(LoginActivity.this, params.toString(), Util.LOGIN, new VolleyResponseListener() {
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

                    Util.Logcat.e("onResponse" + response);

                    try {
                        Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
                        final JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));
                        Util.Logcat.e("set UserId:::" + resobject.getString("UserId"));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {
                            Util.saveData("UserId", resobject.getString("UserId"), getApplicationContext());
                            Util.saveData("WorkStatus", resobject.getString("WorkStatus"), getApplicationContext());
                            Util.saveData("UserName", resobject.getString("UserName"), getApplicationContext());
                            Util.saveData("LoginId", resobject.getString("LoginId"), getApplicationContext());
                            Util.saveData("SAId", resobject.getString("SAId"), getApplicationContext());
                            Util.saveData("SABranchId", resobject.getString("SABranchId"), getApplicationContext());
                            Util.saveData("RoleName", resobject.getString("RoleName"), getApplicationContext());
                            Util.saveData("SABranchName", resobject.getString("SABranchName"), getApplicationContext());
                            Util.saveData("EmpCode", resobject.getString("EmpCode"), getApplicationContext());
                            Util.saveData("TrackTiming", resobject.getString("TrackTiming"), getApplicationContext());
                            Util.saveData("ImageUpload", resobject.getString("ImageUpload"), getApplicationContext());
                            //Normal Login
                            /*Intent home = new Intent(LoginActivity.this, MainActivityKodak.class);
                            home.putExtra("ClientMapping", resobject.getString("ClientMapping"));
                            startActivity(home);
                            finish();*/

                            //Version Check Login
                            if (!resobject.getString("VersionChk").equalsIgnoreCase("1")) {
                                Intent home = new Intent(LoginActivity.this, MainActivityKodak.class);
                                home.putExtra("ClientMapping", resobject.getString("ClientMapping"));
                                startActivity(home);
                                finish();

                            } else {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(LoginActivity.this, R.style.alertDialog);
                                // dlg.setTitle("App Update");
                                dlg.setMessage("Kindly Update New version");
                                dlg.setCancelable(false);
                                dlg.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            update(resobject.getString("Downloadlink"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                dlg.show();
                            }

                            if (chkbox.isChecked() == true) {
                                Util.saveData("loginuser", EdUsername.getEditableText().toString(), getApplicationContext());
                                Util.saveData("loginpass", EdPassword.getEditableText().toString(), getApplicationContext());
                            }

                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {

                            alert.build(resobject.getString("StatusDesc"));
                        } else {
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

    private void update(String url) {
        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        String dirPath = "/mnt/sdcard/Download";
        String fileName = "XeniaCollection.apk";
        AndroidNetworking.download(url, dirPath, fileName)
                .setTag("App Update")
                .setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                        pd.show();
                        pd.setCancelable(false);
                        pd.setMessage("Updating New Version App. Please Wait...");
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        pd.dismiss();

                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "XeniaCollection.apk");

                        if (file.exists()) {

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Uri uri = FileProvider.getUriForFile(LoginActivity.this, "com.gicollectionfms", file);
                                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } else {
                                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "ّFile not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Util.Logcat.e(String.valueOf(error));
                        pd.dismiss();
                        alert.build("App Update Failed ! Contact Admin");
                    }
                });
    }

    private void devicedetails() {
        String device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            Field[] fields = Build.VERSION_CODES.class.getFields();
            osName = "Android " + fields[Build.VERSION.SDK_INT + 1].getName();
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        /*TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();*/
        device = device_id + "," + "null" + "," + osName + "," + Build.VERSION.RELEASE + "," + Build.SERIAL + "," + Build.MANUFACTURER + "," + Build.MODEL + "," + "null" + "," + "null" + "," + "null" + "," + latitude + "," + longitude + "," + "null" + ",";
        Util.Logcat.e("device>" + device);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            loading.show();
            loading.setMessage("Please wait...");
            loading.setCancelable(false);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loading.dismiss();
                }
            }, 3000);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.alertDialog);
        builder.setMessage(R.string.enable_gps)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
