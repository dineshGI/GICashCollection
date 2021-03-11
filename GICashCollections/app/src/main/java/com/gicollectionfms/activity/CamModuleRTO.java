package com.gicollectionfms.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.PhotoProvider;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CamModuleRTO extends AppCompatActivity {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    Uri fileUri;
    //private ImageView ItemPic;
    String appid, latitude, longitude;
    static String waybillno;
    private GpsTracker gpsTracker;
    AlertDialog.Builder alertDialogBuilder;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    String imageStoragePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cammodule);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        alertDialogBuilder = new AlertDialog.Builder(this, R.style.alertDialog);
        //  ItemPic = findViewById(R.id.itempic);
        gpsTracker = new GpsTracker(this);
        if (gpsTracker.canGetLocation()) {
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());
            // Log.e("lat:::", String.valueOf(latitude));
            //  Log.e("long:::", String.valueOf(longitude));
        }
        Intent intent = getIntent();
        appid = intent.getStringExtra("AppointmentId");
        waybillno = intent.getStringExtra("WaybillNumber");
        Log.e("appid", appid + "\n" + waybillno);

      /*  Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = PhotoProvider.getOutputMediaFile(1);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = PhotoProvider.getOutputMediaFileUri(this, file);
        cam.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cam, REQUEST_CAMERA);*/
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    //  PROFILE_PIC_COUNT = 1;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File file = PhotoProvider.getOutputMediaFile(1);
                    if (file != null) {
                        imageStoragePath = file.getAbsolutePath();
                    }

                    Uri fileUri = PhotoProvider.getOutputMediaFileUri(CamModuleRTO.this, file);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[item].equals("Choose from Library")) {
                    //  PROFILE_PIC_COUNT = 1;
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    //  PROFILE_PIC_COUNT = 0;
                    dialog.dismiss();
                    finish();
                }
            }
        });
        builder.show();
    }


    private void previewCapturedImage() {
        try {
            // ItemPic.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            //  ItemPic.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

     /*   if (requestCode == REQUEST_CAMERA) {
            Bitmap bitmap = PhotoProvider.optimizeBitmap(8, imageStoragePath);
            if (bitmap != null) {
                try {
                    // PhotoProvider.refreshGallery(getActivity(), imageStoragePath);
                    bitmap = PhotoProvider.optimizeBitmap(8, imageStoragePath);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    callapi(encoded);

                } catch (Exception e) {

                }
            } else {
                finish();
            }

        }*/
        // if the result is capturing Image

        switch (requestCode) {
            case REQUEST_CAMERA:

                Bitmap bitmap = PhotoProvider.optimizeBitmap(8, imageStoragePath);
                if (bitmap != null) {
                    try {
                        // PhotoProvider.refreshGallery(getActivity(), imageStoragePath);
                        bitmap = PhotoProvider.optimizeBitmap(8, imageStoragePath);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        callapi(encoded);

                    } catch (Exception e) {

                    }
                } else {
                    finish();
                }
                break;
            case SELECT_FILE:

                if (resultCode == RESULT_OK && data != null) {
                Toast.makeText(this,
                        "Image Selected ", Toast.LENGTH_SHORT)
                        .show();
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getRealPathFromURI(selectedImageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                options.inSampleSize = 8;
                Bitmap bmap = BitmapFactory.decodeFile(selectedImagePath,
                        options);
                bmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] byteArray = stream.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                callapi(encoded);}
                else {
                    finish();
                }

                break;
            default:
                break;
        }
    }

    private void callapi(final String bitmapstring) {
        String data = "";
        JSONObject obj = new JSONObject();

        try {
            obj.put("AppointmentId", appid);
            obj.put("WaybillNo", waybillno);
            obj.put("Latitude", latitude);
            obj.put("Longitude", longitude);
            obj.put("UserId", Util.getData("UserId", getApplicationContext()));
            obj.put("UploadImg", bitmapstring);
            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(CamModuleRTO.this, params.toString(), Util.DELIVERY_SUCCESS, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                     Util.Logcat.e("onError"+ message);
                }

                @Override
                public void onResponse(JSONObject response) {
                     Util.Logcat.e("onResponse"+ response);
                    try {
                        Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                        JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));

                        if (resobject.getString("Status").equalsIgnoreCase("0")) {

                            try {
                                alertDialogBuilder.setMessage(resobject.getString("StatusDesc"));
                                alertDialogBuilder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                                finish();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                            Toast.makeText(getApplicationContext(),
                                    resobject.getString("StatusDesc"), Toast.LENGTH_SHORT)
                                    .show();
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
    private String getRealPathFromURI(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }
}



