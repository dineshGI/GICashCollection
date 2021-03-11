package com.gicollectionfms.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.gicollectionfms.BajajDirect.BajajDirect;
import com.gicollectionfms.EquitasDirect.EquitasDirect;
import com.gicollectionfms.EquitasDirect.EquitasDirectFragment;
import com.gicollectionfms.EquitasPickup.EquitasPickup;
import com.gicollectionfms.HDFCDirect.HDFCDirect;
import com.gicollectionfms.IDFCDirect.IDFCDirect;
import com.gicollectionfms.IDFCPickup.IDFCPickup;
import com.gicollectionfms.KVBDirect.KVBDirect;
import com.gicollectionfms.KVBPickup.KVBPickup;
import com.gicollectionfms.KotakPickup.KotakPickup;
import com.gicollectionfms.KotakDirect.KotakDirect;
import com.gicollectionfms.Http.CallApi;
import com.gicollectionfms.HDBDirect.HDBDirect;
import com.gicollectionfms.HDBPickup.HDBPickup;
import com.gicollectionfms.LandTDirect.LNTDirect;
import com.gicollectionfms.LandTDirect.LNTDirectFragment;
import com.gicollectionfms.NACH.NACH;
import com.gicollectionfms.R;
import com.gicollectionfms.RBLDirect.RBLDirect;
import com.gicollectionfms.StartekIDFCDirect.StarteckIDFCDirect;
import com.gicollectionfms.StartekIDFCPickup.StarteckIDFCPickup;
import com.gicollectionfms.TATADirect.TATADirect;
import com.gicollectionfms.TATAPickup.TATAPickup;
import com.gicollectionfms.interfaces.VolleyResponseListener;
import com.gicollectionfms.module.CashHandover;
import com.gicollectionfms.module.ChangePassword;
import com.gicollectionfms.module.Dashbaord;
import com.gicollectionfms.module.FEReport;
import com.gicollectionfms.module.HomeFragment;
import com.gicollectionfms.module.UploadFragment;
import com.gicollectionfms.service.ImgUpload;
import com.gicollectionfms.service.Location;
import com.gicollectionfms.utils.CommonAlertDialog;
import com.gicollectionfms.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class MainActivityKodak extends AppCompatActivity {
    Toolbar toolbar;
    NavigationView navigationView;
    private DrawerLayout drawer;
    public static int navItemIndex = 0;
    // tags used to attach the fragments
    // private static final String TAG_HOME = "home";
    private static final String TAG_CHANGEPASSWORD = "changepassword";
    private static final String TAG_FEREPOR = "fereport";
    private static final String TAG_KODAK_CREDIT = "kodakcredit";
    private static final String TAG_KODAK_CFB = "kodakcfb";
    private static final String TAG_KODAK_LOAN = "kodakloan";
    private static final String TAG_HDBI = "hdbi";
    private static final String TAG_NACH = "nach";
    private static final String TAG_IDFC_PICKUP = "idfcpickup";
    private static final String TAG_IDFC_DIRECT = "idfcdirect";
    private static final String TAG_RBL_DIRECT = "rbldirect";

    private static final String TAG_TATA_PICKUP = "tatapickup";
    private static final String TAG_TATA_DIRECT = "tatadirect";
    private static final String TAG_L_and_T = "landt";
    private static final String TAG_HDFC_DIRECT = "hdfcdirect";
    private static final String TAG_STARTEK_IDFC_PICKUP = "starttekhdfcpickup";
    private static final String TAG_STARTEK_IDFC_DIRECT = "starttekhdfcdirect";
    private static final String TAG_BAJAN_DIRECT = "bajandirect";
    private static final String TAG_KVB_PICKUP = "kvbpickup";
    private static final String TAG_KVB_DIRECT = "kvbdirect";
    private static final String TAG_EQUITAS_PICKUP = "equitaspickup";
    private static final String TAG_EQUITAS_DIRECT = "equitasdirect";
    private static final String TAG_CASHHANDOVER = "cashhandover";
    private static final String TAG_UPLOAD = "upload";
    private static final String TAG_DASHBOARD = "dashboard";

    public static String CURRENT_TAG = TAG_DASHBOARD;

    private String[] activityTitles;
    private View navHeader;
    private TextView txtName, txtloginid, txtbranch, txttime, txtappname;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private Menu menu;
    private GpsTracker gpsTracker;
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
        gpsTracker = new GpsTracker(this);
        //change menu
        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);

        String workstatus = Util.getData("WorkStatus", getApplicationContext());
        if (workstatus.equalsIgnoreCase("1")) {
            startService(new Intent(this, ImgUpload.class));
            startService(new Intent(this, Location.class));

        }

       /* ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem home = menu.findItem(R.id.home);
        home.setVisible(true);

        MenuItem M1008 = menu.findItem(R.id.menu_kotak_pickup);
        MenuItem M1009 = menu.findItem(R.id.menu_kotak_direct);
        MenuItem M1010 = menu.findItem(R.id.menu_nach);
        MenuItem M1004 = menu.findItem(R.id.menu_hdb_pickup);
        MenuItem M1005 = menu.findItem(R.id.menu_hdb_direct);
        MenuItem M1011 = menu.findItem(R.id.menu_idfc_pickup);
        MenuItem M1012 = menu.findItem(R.id.menu_idfc_direct);
        MenuItem M1014 = menu.findItem(R.id.menu_rbl_direct);
        MenuItem M1015 = menu.findItem(R.id.menu_tata_pickup);
        MenuItem M1016 = menu.findItem(R.id.menu_tata_direct);
        MenuItem M1017 = menu.findItem(R.id.menu_lt);
        MenuItem M1018 = menu.findItem(R.id.menu_hdfc_direct);
        MenuItem M1019 = menu.findItem(R.id.menu_startek_IDFC_Pickup);
        MenuItem M1020 = menu.findItem(R.id.menu_startek_IDFC_Direct);
        MenuItem M1021 = menu.findItem(R.id.menu_bajaj_Direct);
        MenuItem M1022 = menu.findItem(R.id.menu_kvb_pickup);
        MenuItem M1023 = menu.findItem(R.id.menu_kvb_direct);
        MenuItem M1024 = menu.findItem(R.id.menu_equitas_pickup);
        MenuItem M1025 = menu.findItem(R.id.menu_equitas_direct);
        String mapping = getIntent().getStringExtra("ClientMapping");
        if (mapping.contains("1008")) {
            M1008.setVisible(true);
        }
        if (mapping.contains("1009")) {
            M1009.setVisible(true);
        }
        if (mapping.contains("1010")) {
            M1010.setVisible(true);
        }
        if (mapping.contains("1004")) {
            M1004.setVisible(true);
        }
        if (mapping.contains("1005")) {
            M1005.setVisible(true);
        }
        if (mapping.contains("1011")) {
            M1011.setVisible(true);
        }
        if (mapping.contains("1012")) {
            M1012.setVisible(true);
        }
        if (mapping.contains("1014")) {
            M1014.setVisible(true);
        }
        if (mapping.contains("1015")) {
            M1015.setVisible(true);
        }
        if (mapping.contains("1016")) {
            M1016.setVisible(true);
        }
        if (mapping.contains("1017")) {
            M1017.setVisible(true);
        }
        if (mapping.contains("1018")) {
            M1018.setVisible(true);
        }
        if (mapping.contains("1019")) {
            M1019.setVisible(true);
        }
        if (mapping.contains("1020")) {
            M1020.setVisible(true);
        }
        if (mapping.contains("1021")) {
            M1021.setVisible(true);
        }
        if (mapping.contains("1022")) {
            M1022.setVisible(true);
        }
        if (mapping.contains("1023")) {
            M1023.setVisible(true);
        }
        if (mapping.contains("1024")) {
            M1024.setVisible(true);
        }
        if (mapping.contains("1025")) {
            M1025.setVisible(true);
        }
        navHeader = navigationView.getHeaderView(0);
        // name=navigationView.findViewById(R.id.username_header);

        activityTitles = getResources().getStringArray(R.array.nav_item_kodak);

        loadNavHeader();

        setUpNavigationView();
       /* MenuItem kodak_cfb = menu.findItem(R.id.menu_kodak_cfb);
        kodak_cfb.setVisible(false);*/
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_DASHBOARD;
            loadHomeFragment();
        }
        // navigationView.setNavigationItemSelectedListener(this);
    }

    //gicollection
    private void createpath() {

        String root = Environment.getExternalStorageDirectory().toString();
        Util.saveData("directory", Util.directoryname, getApplicationContext());
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
                    case R.id.home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_DASHBOARD;
                        Util.call = false;
                        break;
                    case R.id.menu_kotak_pickup:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 1;
                            CURRENT_TAG = TAG_KODAK_CFB;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_kotak_direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 2;
                            CURRENT_TAG = TAG_HDBI;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_nach:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 3;
                            CURRENT_TAG = TAG_NACH;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_hdb_pickup:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 4;
                            CURRENT_TAG = TAG_KODAK_LOAN;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));

                        }
                        Util.call = false;
                        break;

                    case R.id.menu_hdb_direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 5;
                            CURRENT_TAG = TAG_KODAK_CREDIT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_idfc_pickup:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 6;
                            CURRENT_TAG = TAG_IDFC_PICKUP;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));

                        }
                        Util.call = false;
                        break;

                    case R.id.menu_idfc_direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 7;
                            CURRENT_TAG = TAG_IDFC_DIRECT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_rbl_direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 8;
                            CURRENT_TAG = TAG_RBL_DIRECT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;


                    case R.id.menu_tata_pickup:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 9;
                            CURRENT_TAG = TAG_TATA_PICKUP;


                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_tata_direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 10;
                            CURRENT_TAG = TAG_TATA_DIRECT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_lt:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 11;
                            CURRENT_TAG = TAG_L_and_T;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_hdfc_direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 12;
                            CURRENT_TAG = TAG_HDFC_DIRECT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;

                    case R.id.menu_startek_IDFC_Pickup:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 13;
                            CURRENT_TAG = TAG_STARTEK_IDFC_PICKUP;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;

                    case R.id.menu_startek_IDFC_Direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 14;
                            CURRENT_TAG = TAG_STARTEK_IDFC_DIRECT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;

                    case R.id.menu_bajaj_Direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 15;
                            CURRENT_TAG = TAG_BAJAN_DIRECT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_kvb_pickup:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 16;
                            CURRENT_TAG = TAG_KVB_PICKUP;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_kvb_direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 17;
                            CURRENT_TAG = TAG_KVB_DIRECT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_equitas_pickup:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 18;
                            CURRENT_TAG = TAG_EQUITAS_PICKUP;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_equitas_direct:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 19;
                            CURRENT_TAG = TAG_EQUITAS_DIRECT;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.menu_cashhandover:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 20;
                            CURRENT_TAG = TAG_CASHHANDOVER;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;

                    case R.id.menu_upload:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 21;
                            CURRENT_TAG = TAG_UPLOAD;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.fe_report:
                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 22;
                            CURRENT_TAG = TAG_FEREPOR;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;
                    case R.id.change_password:

                        if ((Util.getData("WorkStatus", getApplicationContext()).equalsIgnoreCase("1"))) {
                            navItemIndex = 23;
                            CURRENT_TAG = TAG_CHANGEPASSWORD;
                        } else {
                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.start_msg));
                        }
                        Util.call = false;
                        break;

                    case R.id.Logout:
                        AlertDialog.Builder alertDialogBuilder;
                        alertDialogBuilder = new AlertDialog.Builder(MainActivityKodak.this, R.style.alertDialog);
                        alertDialogBuilder.setMessage(R.string.want_to_logout);
                        alertDialogBuilder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent logout = new Intent(MainActivityKodak.this, LoginActivity.class);
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
                CURRENT_TAG = TAG_DASHBOARD;
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
                Dashbaord dashboard = new Dashbaord();
                return dashboard;
            case 1:
                KotakPickup cfb = new KotakPickup();
                return cfb;
            case 2:
                KotakDirect hdb = new KotakDirect();
                return hdb;
            case 3:
                NACH nach = new NACH();
                return nach;
            case 4:
                HDBPickup kodakloan = new HDBPickup();
                return kodakloan;
            case 5:
                HDBDirect kodakcredit = new HDBDirect();
                return kodakcredit;
            case 6:
                IDFCPickup idfcpickup = new IDFCPickup();
                return idfcpickup;
            case 7:
                IDFCDirect idfcdirect = new IDFCDirect();
                return idfcdirect;
            case 8:
                RBLDirect rbldirect = new RBLDirect();
                return rbldirect;
            case 9:
                TATAPickup tatapickup = new TATAPickup();
                return tatapickup;
            case 10:
                TATADirect tatadirect = new TATADirect();
                return tatadirect;

            case 11:
                LNTDirect LandT = new LNTDirect();
                return LandT;
            case 12:
                HDFCDirect hdfcdirect = new HDFCDirect();
                return hdfcdirect;
            case 13:
                StarteckIDFCPickup StartekIDFCPickup = new StarteckIDFCPickup();
                return StartekIDFCPickup;
            case 14:
                StarteckIDFCDirect StartekIDFCDirect = new StarteckIDFCDirect();
                return StartekIDFCDirect;
            case 15:
                BajajDirect BajajDirect = new BajajDirect();
                return BajajDirect;

            case 16:
                KVBPickup kvbpickup = new KVBPickup();
                return kvbpickup;
            case 17:
                KVBDirect kvbdirect = new KVBDirect();
                return kvbdirect;
            case 18:
                EquitasPickup equitaspickup = new EquitasPickup();
                return equitaspickup;
            case 19:
                EquitasDirect equitasdirect = new EquitasDirect();
                return equitasdirect;

            case 20:
                CashHandover cashhandover = new CashHandover();
                return cashhandover;
            case 21:
                UploadFragment upload = new UploadFragment();
                return upload;
            case 22:
                FEReport fereport = new FEReport();
                return fereport;
            case 23:
                ChangePassword changepassword = new ChangePassword();
                return changepassword;
            default:
                return new Dashbaord();

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
        // Util.Logcat.e("navusername", Util.getData("LoginId", getApplicationContext()));
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
        Util.Logcat.e("workstatus????" + workstatus);
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
            String data = "";
            JSONObject obj = new JSONObject();
            try {
                obj.put("LoggedStatus", status);
                obj.put("SAUserId", Util.getData("UserId", getApplicationContext()));
                if (gpsTracker.canGetLocation()) {
                    obj.put("Latitude", String.valueOf(gpsTracker.getLatitude()));
                    obj.put("Longitude", String.valueOf(gpsTracker.getLongitude()));
                }
                Util.Logcat.e("INPUT:::" + obj.toString());
                // Util.Logcat.e("INPUT ENCRYPT:::", Util.EncryptURL(obj.toString()));
                data = Util.EncryptURL(obj.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject params = new JSONObject();
                params.put("Getrequestresponse", data);

                CallApi.postResponse(MainActivityKodak.this, params.toString(), Util.WORK_STATUS, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {

                        if (message.contains("TimeoutError")) {

                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.timeout_error));

                        } else {

                            CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                            alert.build(getString(R.string.server_error));

                        }
                        Util.Logcat.e("onError" + message);
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        Util.Logcat.e("onResponse" + response);
                        try {
                            Util.Logcat.e("OUTPUT:::" + Util.Decrypt(response.getString("Postresponse")));
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
                                    Location.mTimer.cancel();
                                    stopService(new Intent(getApplicationContext(), ImgUpload.class));
                                    ImgUpload.mTimers.cancel();
                                }
                                CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
                                alert.build(resobject.getString("StatusDesc"));

                            } else if (resobject.getString("Status").equalsIgnoreCase("1")) {
                                CommonAlertDialog alert = new CommonAlertDialog(MainActivityKodak.this);
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
