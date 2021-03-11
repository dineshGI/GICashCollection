package com.gicollectionfms.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.module.CashHandover;
import com.gicollectionfms.module.CashHistory;
import com.gicollectionfms.module.ChangePassword;
import com.gicollectionfms.module.HandshakeDelivery;
import com.gicollectionfms.module.HomeFragment;
import com.gicollectionfms.module.PickupFragment;
import com.gicollectionfms.module.RTOFragment;
import com.gicollectionfms.module.ShipmentHistory;
import com.gicollectionfms.module.UploadFragment;
import com.gicollectionfms.module.ViewOTP;
import com.gicollectionfms.service.ImgUpload;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;
import com.gicollectionfms.R;
import com.gicollectionfms.service.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    NavigationView navigationView;
    private DrawerLayout drawer;
    public static int navItemIndex = 0;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_CHANGEPASSWORD = "changepassword";
    public static String TAG_PICKUP = "pickup";
    private static final String TAG_HS_DELIVERY = "handshakedelivery";
    private static final String TAG_RTO = "rto";
    private static final String TAG_CASHHANDOVER = "cashhandover";
    private static final String TAG_SHIPMENTHISTORY = "shipmenthistory";
    private static final String TAG_CASHHISTORY = "cashthistory";
    private static final String TAG_VIEWOTP = "viewotp";
    private static final String TAG_UPLOAD = "upload";
    public static String CURRENT_TAG = TAG_HOME;

    private String[] activityTitles;
    private View navHeader;
    private TextView txtName, txtloginid, txtbranch, txttime, txtappname;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private Menu menu;
    TextView name;

    // TextView tvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (Util.getData("RoleId", getApplicationContext()).equalsIgnoreCase("2")) {
            setContentView(R.layout.supervisor);
        } else {
            setContentView(R.layout.attender);
        }*/
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* tvSave = (TextView) findViewById(R.id.toolbar_save);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.app_name) , Toast.LENGTH_SHORT).show();
            }
        });*/
        createpath();

        //change menu
        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);

        String workstatus = Util.getData("WorkStatus", getApplicationContext());
        if (workstatus.equalsIgnoreCase("1")) {
            startService(new Intent(this, Location.class));

            startService(new Intent(this, ImgUpload.class));
        }


       /* ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        navigationView = findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        // name=navigationView.findViewById(R.id.username_header);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        loadNavHeader();

        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
        // navigationView.setNavigationItemSelectedListener(this);
    }

    private void createpath() {

        String root = Environment.getExternalStorageDirectory().toString();

        Util.saveData("directory", Util.directoryname,getApplicationContext());
        // the directory where the signature will be saved
        File myDir = new File(root + "/" + Util.getData("directory", getApplicationContext()));
        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_camera:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        // return homeFragment;
                        break;

                    case R.id.menupickup:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 1;
                            CURRENT_TAG = TAG_PICKUP;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }

                        break;
                    case R.id.menuhsdelivery:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 2;
                            CURRENT_TAG = TAG_HS_DELIVERY;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));

                        }

                        break;
                    case R.id.rto:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 3;
                            CURRENT_TAG = TAG_RTO;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }

                        break;
                    case R.id.menu_cashhandover:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 4;
                            CURRENT_TAG = TAG_CASHHANDOVER;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }

                        break;

                    case R.id.menu_shipmenthistory:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 5;
                            CURRENT_TAG = TAG_SHIPMENTHISTORY;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }

                        break;
                    case R.id.menu_cashhistory:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 6;
                            CURRENT_TAG = TAG_CASHHISTORY;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }

                        break;
                    case R.id.menu_viewotp:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 7;
                            CURRENT_TAG = TAG_VIEWOTP;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }

                        break;
                    case R.id.menu_upload:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 8;
                            CURRENT_TAG = TAG_UPLOAD;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        break;
                    case R.id.change_password:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 9;
                            CURRENT_TAG = TAG_CHANGEPASSWORD;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        break;

                    case R.id.Logout:
                        AlertDialog.Builder alertDialogBuilder;
                        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.alertDialog);
                        alertDialogBuilder.setMessage(R.string.want_to_logout);
                        alertDialogBuilder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(logout);
                                        finish();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();

                        drawer.closeDrawers();
                        return true;

                    default:
                        navItemIndex = 0;

                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                menuItem.setChecked(true);
                loadHomeFragment();
                return true;

            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                txttime.setText(Util.getdatetime());
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        /* if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            //   toggleFab();
            return;
        }*/

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        //toggleFab();
        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {

            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;

            case 1:
                // photos
                PickupFragment pickup = new PickupFragment();
                return pickup;

            case 2:
                // photos
                HandshakeDelivery hsdelivery = new HandshakeDelivery();
                return hsdelivery;

            case 3:
                // photos
                RTOFragment rto = new RTOFragment();
                return rto;

            case 4:
                // photos
                CashHandover cashhandover = new CashHandover();
                return cashhandover;

            case 5:
                // photos
                ShipmentHistory shipmenthistory = new ShipmentHistory();
                return shipmenthistory;

            case 6:
                // photos
                CashHistory cashhistory = new CashHistory();
                return cashhistory;

            case 7:
                // photos
                ViewOTP viewotp = new ViewOTP();
                return viewotp;

            case 8:
                // photos
                UploadFragment upload = new UploadFragment();
                return upload;

            case 9:
                // photos
                ChangePassword changepassword = new ChangePassword();
                return changepassword;

            default:
                return new HomeFragment();

        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void loadNavHeader() {
        // name, website
        txtName = navHeader.findViewById(R.id.username_header);
        txtloginid = navHeader.findViewById(R.id.loginid);
        txtbranch = navHeader.findViewById(R.id.branch);
        txttime = navHeader.findViewById(R.id.showtime);
        txtappname = navHeader.findViewById(R.id.appname);
        //Util.Logcat.e("navusername", Util.getData("LoginId", getApplicationContext()));
        if (Util.getData("UserName", getApplicationContext()) != "") {
            txtName.setText(Util.getData("UserName", getApplicationContext()));
            txtloginid.setText(Util.getData("EmpCode", getApplicationContext()));
            txtbranch.setText(Util.getData("SABranchName", getApplicationContext()));

            if (Util.getData("app", getApplicationContext()).equalsIgnoreCase("onemg")) {
                txtappname.setText("1mg");
            } else {
                txtappname.setText("GoFynd");
            }

        }

     /*   // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);*/

        // showing dot next to notifications label
        // navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;

        MenuItem MenuItem = menu.findItem(R.id.start_stop);
        menu.findItem(R.id.bulk_scan).setVisible(false);
        String workstatus = Util.getData("WorkStatus", getApplicationContext());
        if (workstatus.equals("2")) {
            MenuItem.setTitle("START");
        } else if (workstatus.equals("1")) {
            MenuItem.setTitle("STOP");
        } else {
            MenuItem.setTitle("");
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // action with ID action_refresh was selected

            case R.id.start_stop:

                if (Util.isOnline(this)) {
                    if (item.getTitle().equals("STOP")) {
                        callapi("2", item.getTitle().toString());
                    } else if (item.getTitle().equals("START")) {
                        callapi("1", item.getTitle().toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.bulk_scan:
                Intent i = new Intent(this, BarcodeScan.class);
                i.putExtra("frombulkscan", "true");
                startActivity(i);
                break;
            case R.id.delivery:

                Intent collectlist = new Intent(this, CollectListActivity.class);
                startActivity(collectlist);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callapi(String status, final String title) {

        if (Util.isOnline(getApplicationContext())) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("LoggedStatus", status);

                obj.put("SAUserId", Util.getData("UserId", getApplicationContext()));
                Util.Logcat.e("INPUT:::"+ obj.toString());
                String data = Util.EncryptURL(obj.toString());
                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);
                CallApi.postResponse(MainActivity.this, params.toString(), Util.WORK_STATUS, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {

                        if (message.contains("TimeoutError")) {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.timeout_error));
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                            alert.build(getString(R.string.server_error));
                        }
                         Util.Logcat.e("onError"+ message);
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                         Util.Logcat.e("onResponse"+ response);
                        try {
                            Util.Logcat.e("OUTPUT:::"+ Util.Decrypt(response.getString("Postresponse")));
                            JSONObject resobject = new JSONObject(Util.Decrypt(response.getString("Postresponse")));
                            Util.saveData("WorkStatus", resobject.getString("WorkStatus"), getApplicationContext());
                            if (resobject.getString("Status").equalsIgnoreCase("0")) {
                                MenuItem myItem = menu.findItem(R.id.start_stop);
                                if (title.equalsIgnoreCase("START")) {
                                    myItem.setTitle("STOP");
                                    startService(new Intent(getApplicationContext(), Location.class));
                                    startService(new Intent(getApplicationContext(), ImgUpload.class));
                                } else if (title.equalsIgnoreCase("STOP")) {
                                    myItem.setTitle("START");
                                    stopService(new Intent(getApplicationContext(), Location.class));
                                    stopService(new Intent(getApplicationContext(), ImgUpload.class));
                                    Location.mTimer.cancel();
                                    ImgUpload.mTimers.cancel();
                                }
                                CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
                                alert.build(resobject.getString("StatusDesc"));

                            } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                                CommonAlertDialog alert = new CommonAlertDialog(MainActivity.this);
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

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.app_name) + "\n" + getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
        }
    }

}
