package com.gicollectionfms.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;


public class AlertActivity extends AppCompatActivity {
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogBuilder = new AlertDialog.Builder(this,R.style.alertDialog);
        alertDialogBuilder.setCancelable(false);
        String status = getIntent().getStringExtra("status");
        String StatusDesc = getIntent().getStringExtra("StatusDesc");

        if (status.equalsIgnoreCase("0")) {
            alertDialogBuilder.setMessage(StatusDesc);
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Util.DeliveryRefresh = true;
                            Util.call = true;
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();

        } else {
            alertDialogBuilder.setMessage(StatusDesc);
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Util.call = true;
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

    }

}
