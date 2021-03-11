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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.R;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.utils.PhotoProvider;
import com.gicollectionfms.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BankCamModule extends AppCompatActivity {

    Uri fileUri;
    String appid, latitude, longitude;
    static String waybillno, amount;
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
        }
        Intent intent = getIntent();
        appid = intent.getStringExtra("AppointmentId");
        waybillno = intent.getStringExtra("WaybillNumber");
        amount = intent.getStringExtra("amount");
        Log.e("appid", appid + "\n" + waybillno);

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

                    Uri fileUri = PhotoProvider.getOutputMediaFileUri(BankCamModule.this, file);

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
        Log.e("RESULT:::", String.valueOf(requestCode));
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
                        callapi(bitmap);

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
                    callapi(bmap);
                } else {
                    finish();
                }
                break;
            default:
                break;
        }


    }

    private void callapi(final Bitmap u) {
        String data = "";
        JSONObject obj = new JSONObject();

        try {

            obj.put("WaybillNo", waybillno);
            // obj.put("Barcode", "");
            obj.put("PODFilePath", "");
            obj.put("Latitude", latitude);
            obj.put("Longitude", longitude);
            obj.put("CollectedAmount", amount);
            obj.put("SAUserid", Util.getData("UserId", getApplicationContext()));

            Util.Logcat.e("INPUT:::"+ obj.toString());
            // Log.e("HAI:::", Util.EncryptURL(obj.toString()));
            data = Util.EncryptURL(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject params = new JSONObject();
            params.put("Getrequestresponse", data);
            CallApi.postResponseNopgrss(BankCamModule.this, params.toString(), Util.BANK_PICKUP, new VolleyResponseListener() {
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
                                saveImage(u, resobject.getString("StatusDesc"));
                                Util.refresh = true;
                                //  ItemPic.setImageBitmap(bitmap);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else if (resobject.getString("Status").equalsIgnoreCase("1")) {

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
                        }/* else {
                            File mediaStorageDir = new File(
                                    Environment
                                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    IMAGE_DIRECTORY_NAME);
                            File target = new File(mediaStorageDir.getPath() + File.separator
                                    + "P_" + waybillno + ".jpg");
                            if (target.exists() && target.isFile() && target.canWrite()) {
                                target.delete();
                                Log.d("d_file", "" + target.getName());
                            }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    final void saveImage(Bitmap signature, String msg) {

        String root = Environment.getExternalStorageDirectory().toString();

        // the directory where the signature will be saved

        File myDir = new File(root + "/" + Util.getData("directory", getApplicationContext()));

        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        // set the file name of your choice
        String fname = "P_" + waybillno + ".jpg";

        // in our case, we delete the previous file, you can remove this
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }

        try {
            // save the signature
            FileOutputStream out = new FileOutputStream(file);
            signature.compress(Bitmap.CompressFormat.JPEG, 100, out);
            //signature.compress(Bitmap.CompressFormat.JPEG, 100, out);
            signature.sameAs(signature);
            out.flush();
            out.close();

            alertDialogBuilder.setMessage(msg);
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

        } catch (Exception e) {
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



