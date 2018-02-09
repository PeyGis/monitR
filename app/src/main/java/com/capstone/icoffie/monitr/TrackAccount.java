package com.capstone.icoffie.monitr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.*;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.icoffie.monitr.model.API_ENDPOINT;
import com.capstone.icoffie.monitr.model.SingletonApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TrackAccount extends AppCompatActivity {

    private LinearLayout viewAllDevices, viewLoginActivity, addDevice, addGeofence;
    private TextView accountTaglineTv, accountNameTv;
    private Toolbar toolbar;
  final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_account);

        // set toolbar
        toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get bundle or extras from fragment activity
        Bundle bundle = getIntent().getExtras();
        final String accountNameExtra = bundle.getString("ACCOUNT_NAME");
        String accountTaglineExtra = bundle.getString("ACCOUNT_TAGLINE");
        String accountIdExtra = bundle.getString("ACCOUNT_ID");
        String userAccountIdExtra = bundle.getString("USER_ACCOUNT_ID");
        final String userTokenExtra = bundle.getString("USER_TOKEN");

        // get view components by ids
        accountTaglineTv = (TextView) findViewById(R.id.accountTagline);
        accountNameTv = (TextView) findViewById(R.id.accountName);
        viewAllDevices = (LinearLayout) findViewById(R.id.viewalldevices);
        viewLoginActivity = (LinearLayout) findViewById(R.id.viewloginactivity);
        addDevice = (LinearLayout) findViewById(R.id.adddevice);
        addGeofence = (LinearLayout) findViewById(R.id.addgeofence);

        //set Account Header Info
        accountNameTv.setText(accountNameExtra.toUpperCase());
        accountTaglineTv.setText(accountTaglineExtra);



        viewAllDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent devicesIntent = new Intent(TrackAccount.this, ViewDevicesActivity.class);
                //mapIntent.putExtra("ACCOUNT_NAME", accountNameExtra);
                //mapIntent.putExtra("USER_TOKEN", userTokenExtra);
                startActivity(devicesIntent);
            }
        });

        viewLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(TrackAccount.this, MapsActivity.class);
                mapIntent.putExtra("ACCOUNT_NAME", accountNameExtra);
                mapIntent.putExtra("USER_TOKEN", userTokenExtra);
                startActivity(mapIntent);

            }
        });

        addGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("Adding Geofence...");
            }
        });

        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //inflate layout and get the dialog view
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.add_device_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view);

                //get view components by referencing id
                final EditText device_title = (EditText)view.findViewById(R.id.deviceTitle);
                final EditText device_ime = (EditText)view.findViewById(R.id.deviceId);
                Button thisDevice = (Button)view.findViewById(R.id.thisDevice);

                thisDevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String serial = getDeviceIMEI();
                        String model = Build.MODEL;
                        device_ime.setText(serial);
                        device_title.setText(model);
                        device_ime.setFocusable(false);
                        device_ime.setClickable(false);
                        device_title.setFocusable(false);
                        device_title.setClickable(false);
                    }
                });
//                Button canDevice = (Button)view.findViewById(R.id.cancelDevice);
                //makeToast("Adding New Device...");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.icon);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = device_title.getText().toString();
                        String ime = device_ime.getText().toString();
                        makeToast("{Title: " + title + ", Ime: " + ime + "}");

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = builder.create();
                //alertDialog.setIcon(R.drawable.icon);
                // show alert
                alertDialog.show();
            }
        });
    }

    public void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = android.provider.Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }
}

