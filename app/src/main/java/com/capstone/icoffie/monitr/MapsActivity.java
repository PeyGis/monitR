package com.capstone.icoffie.monitr;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.icoffie.monitr.adapters.UserAccountAdapter;
import com.capstone.icoffie.monitr.model.API_ENDPOINT;
import com.capstone.icoffie.monitr.model.LoginHistory;
import com.capstone.icoffie.monitr.model.SharedPrefManager;
import com.capstone.icoffie.monitr.model.SingletonApi;
import com.capstone.icoffie.monitr.model.UserAccountModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Toolbar toolbar;
    private Button takeAction;
    String userTokenExtra = "";
    private Map<String, LoginHistory> loginHistoryMap;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // action bar settings
        toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get bundle or extras from TRACK ACCOUNT activity
       Bundle bundle = getIntent().getExtras();
        String accountNameExtra = bundle.getString("ACCOUNT_NAME");
       userTokenExtra = bundle.getString("USER_TOKEN");

        //init arrayList
        loginHistoryMap = new HashMap<>();
        //initialize map object
        initMap();

    }

    public void initMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            if (googleMap != null){
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMinZoomPreference(8);
                mMap.setOnMarkerClickListener(this);

                //fetch login details from data model
                getUserLoginHistory(userTokenExtra);

               LatLng kwabenya = new LatLng(5.89800, -0.219751);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(kwabenya));


            }

        } catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR", "GOOGLE MAPS NOT LOADED");

        }

    }

    public void openDialog(){
        AlertDialog.Builder markerDialog = new AlertDialog.Builder(this);
        markerDialog.setTitle("Take Action");
        final String[] options = {"Logout", "Block Device", "Report"};
        markerDialog.setIcon(R.drawable.appicon);
        markerDialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Action Successful", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = markerDialog.create();
        alertDialog.show();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        //inflate layout and get the dialog view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.login_details_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
       // builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_monitr);


        //get view components by referencing id
        TextView deviceNameTv = (TextView) view.findViewById(R.id.deviceNameTv);
        TextView deviceIMETv = (TextView) view.findViewById(R.id.deviceIMETv);
        TextView deviceLocationTv = (TextView) view.findViewById(R.id.locationTv);
        TextView deviceDateTv = (TextView) view.findViewById(R.id.loginDateTv);
        TextView deviceStatusTv = (TextView) view.findViewById(R.id.statusTv);
        Button noBtn = (Button)view.findViewById(R.id.noBtn);
        Button yesBtn = (Button)view.findViewById(R.id.yesBtn);

        //get Login history Object from marker
        String markerId = marker.getId();
        LoginHistory loginHistory = loginHistoryMap.get(markerId);

        //set data from marker object to view widgets or dialog
        deviceNameTv.append(loginHistory.getDeviceName());
        deviceIMETv.append(loginHistory.getDeviceIme());
        deviceLocationTv.append(String.valueOf(loginHistory.getLatitude()) + ", " + String.valueOf(loginHistory.getLongitude()));
        deviceDateTv.append(loginHistory.getDate());
        deviceStatusTv.append(loginHistory.getStatus());

        // when user clicks no button, open a dialog so user can take action
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openDialog();
            }
        });

        //when user clicks yes button
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder yesOption = new AlertDialog.Builder(context);
                yesOption.setTitle("Adding Device");
                yesOption.setCancelable(false);
                yesOption.setIcon(R.drawable.ic_monitr);
                yesOption.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showToast("Added Succesfully");

                    }
                }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // create alert dialog
                AlertDialog yesalertDialog = yesOption.create();
                // show alert
                yesalertDialog.show();

            }
        });

        // create alert dialog
        AlertDialog loginDetailsalertDialog = builder.create();
        //alertDialog.setIcon(R.drawable.icon);
        // show alert
        loginDetailsalertDialog.show();
//        AlertDialog.Builder markerDialog = new AlertDialog.Builder(this);
//        markerDialog.setTitle("Take Action");
//        final String[] options = {"Device", "Block Device", "Report"};
//        markerDialog.setIcon(R.drawable.appicon);
//        markerDialog.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if(which == 0){
//                    //marker.remove();
//                    String markerTag = marker.getId();
//                    showToast(loginHistoryMap.get(markerTag).getDeviceName());
//                    //showToast(loginHistoryList.get(i).getDeviceName());
//                }
//                else if(which == 1) {
//                    marker.remove();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Report Sent Successfully", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        AlertDialog alertDialog = markerDialog.create();
//        alertDialog.show();
        return true;
    }
    private void drawCircle(LatLng point){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(1000);
        circleOptions.strokeColor(Color.RED);
        circleOptions.strokeWidth(10);

        mMap.addCircle(circleOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.geofence_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.set_geofence:
                Toast.makeText(getApplicationContext(), "Setting Geofence", Toast.LENGTH_SHORT).show();
                return  true;

            default:
                return super.onOptionsItemSelected(item);


        }

    }
    // show toast
    public void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    // fetch user login History from API
    public void getUserLoginHistory(final String user_token) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Connecting to Service Provider.....");
        progressDialog.show();

        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.USER_LOGIN_HISTORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {

                                JSONArray login_audit_array = jsonObject.getJSONArray("login_audit");
                                if(login_audit_array != null){
                                    int i;
                                    for(i=0; i < login_audit_array.length(); i++) {
                                        JSONObject login_record = login_audit_array.getJSONObject(i);
                                        LoginHistory loginHistory = new LoginHistory(
                                                login_record.getDouble("Latitude"),
                                                login_record.getDouble("Longitude"),
                                                login_record.getString("Device_Name"),
                                                login_record.getString("Device_IME"),
                                                login_record.getString("Token"),
                                                login_record.getString("Status"),
                                                login_record.getString("Date_Created")
                                                );

                                        LatLng position = new LatLng(login_record.getDouble("Latitude"), login_record.getDouble("Longitude"));
                                        String title = login_record.getString("Device_Name");
                                       Marker marker =  mMap.addMarker(new MarkerOptions().position(position).title(title));
                                        loginHistoryMap.put(marker.getId(), loginHistory);

                                    }
                                    showToast(i + " " + jsonObject.getString("message"));


                                }

                            } else if (jsonObject.getBoolean("error")) {
                                showToast(jsonObject.getString("message"));
                            } else{
                                // do nothing
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Oops! Check internet connection", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("type", "audit");
                params.put("user_token", user_token);

                return params;
            }
        };
        //requestQueue.add(stringRequest);
        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);

    }
}
