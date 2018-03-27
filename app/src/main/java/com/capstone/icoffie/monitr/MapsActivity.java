package com.capstone.icoffie.monitr;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.icoffie.monitr.model.API_ENDPOINT;
import com.capstone.icoffie.monitr.model.LoginHistory;
import com.capstone.icoffie.monitr.model.SharedPrefManager;
import com.capstone.icoffie.monitr.model.SingletonApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private Toolbar toolbar;
    private Circle mCircle;
    private FloatingActionButton fab;
    LinearLayout linearLayout;
    String userTokenExtra = "";
    String accountNameExtra = "";
    String accountIdExtra = "";
    private Map<String, LoginHistory> loginHistoryMap;
    final Context context = this;
    public static final int LOCATION_REQUEST = 10;
    LocationManager locationManager;
    double latitude = 5.24;
    double longitude = -0.57;
    AlertDialog loginDetailsalertDialog;
    AlertDialog geofenceAlertDialog;

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
        accountNameExtra = bundle.getString("ACCOUNT_NAME");
        accountIdExtra = bundle.getString("ACCOUNT_ID");
        userTokenExtra = bundle.getString("USER_TOKEN");
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);

        //set floating action button
        fab = (FloatingActionButton)findViewById(R.id.locationfab);
        fab.setOnClickListener(this);

            //init arrayList
            loginHistoryMap = new HashMap<>();

            //initializa location manager
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER )) {
                buildAlertMessageNoGps();
            } else{
                getLocation(); // get user location
            }

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
                mMap.setMinZoomPreference(7);
                mMap.setOnMarkerClickListener(this);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setOnMapLongClickListener(this);

                //fetch login details from data model
                //detect network
                if(!isConnectedToNetwork()){
                    Snackbar mSnackbar = Snackbar.make(linearLayout, "No Internet Connection", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            });
                    // Changing message text color
                    mSnackbar.setActionTextColor(Color.RED);
                    mSnackbar.show();

                } else{
                    getUserLoginHistory(userTokenExtra, accountNameExtra);
                }



               LatLng userLocation = new LatLng(latitude, longitude);
                drawCircle(userLocation, 1000);
                mMap.addMarker(new MarkerOptions().title("My Current Location").position(userLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag("0");
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));



            }

        } catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR", "GOOGLE MAPS NOT LOADED");

        }

    }

    public void openDialog(final String providerName, final String token, final Marker marker, final String device_name, final String device_imei){
        AlertDialog.Builder markerDialog = new AlertDialog.Builder(this);
        markerDialog.setTitle("Take Action");
        final String[] options = {"Logout", "Block Device"};
        markerDialog.setIcon(R.drawable.ic_monitr);
        markerDialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){
                    //call logout API
                    terminateSession(providerName, token, marker);
                } else if (which ==1){
                    addNewDevice(device_name, device_imei, "Blocked", accountIdExtra);
                } else{
                    showToast("Reporting to service provider");
                }
            }
        });

        AlertDialog alertDialog = markerDialog.create();
        alertDialog.show();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        //get Login history Object from marker
        if(marker.getTag() != null && marker.getTag().equals("0")){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13));
        } else {

            //inflate layout and get the dialog view
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.login_details_dialog, null);
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            builder.setCancelable(true);
            builder.setIcon(R.drawable.ic_monitr);


            //get view components by referencing id
            TextView deviceNameTv = (TextView) view.findViewById(R.id.deviceNameTv);
            TextView deviceIMETv = (TextView) view.findViewById(R.id.deviceIMETv);
            TextView deviceLocationTv = (TextView) view.findViewById(R.id.locationTv);
            TextView deviceDateTv = (TextView) view.findViewById(R.id.loginDateTv);
            TextView deviceStatusTv = (TextView) view.findViewById(R.id.statusTv);
            Button noBtn = (Button) view.findViewById(R.id.noBtn);
            Button yesBtn = (Button) view.findViewById(R.id.yesBtn);

            String markerId = marker.getId();
            final LoginHistory loginHistory = loginHistoryMap.get(markerId);

            //set data from marker object to view widgets or dialog
            deviceNameTv.append(loginHistory.getDeviceName());
            deviceIMETv.append(loginHistory.getDeviceIme());
            LatLng latLng = new LatLng(loginHistory.getLatitude(), loginHistory.getLongitude());
            //deviceLocationTv.append(String.valueOf(loginHistory.getLatitude()) + ", " + String.valueOf(loginHistory.getLongitude()));
            deviceLocationTv.append(getCity(latLng));
            deviceDateTv.append(loginHistory.getDate());
            deviceStatusTv.append(loginHistory.getStatus());
            final String login_device_token = loginHistory.getToken();

            // when user clicks no button, open a dialog so user can take action
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog(accountNameExtra, login_device_token, marker, loginHistory.getDeviceName(), loginHistory.getDeviceIme());
                }
            });

            //when user clicks yes button
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder yesOption = new AlertDialog.Builder(context);
                    yesOption.setTitle("Add Device");
                    yesOption.setMessage("Want to add this to your devices?");
                    yesOption.setCancelable(false);
                    yesOption.setIcon(R.drawable.ic_monitr);
                    yesOption.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //adding new device
                            addNewDevice(loginHistory.getDeviceName(), loginHistory.getDeviceIme(), "Trusted", accountIdExtra);
                        }
                    }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    // create alert dialog
                    AlertDialog addDeviceDialog = yesOption.create();
                    // show alert
                    addDeviceDialog.show();
                }
            });

            // create alert dialog for the main view (login history dialog)
            loginDetailsalertDialog = builder.create();
            // show alert
            loginDetailsalertDialog.show();

        }
        return true;
    }
    private void drawCircle(LatLng point, int radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.RED);
        circleOptions.strokeWidth(10);

        mMap.addCircle(circleOptions);
    }

    private void drawGeofenceCircle(LatLng point, int radius){
        if(mCircle != null){ mCircle.remove();}
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.rgb(0,128,128));
        circleOptions.strokeWidth(10);

        mCircle = mMap.addCircle(circleOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.maps_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.set_geofence:
                Toast.makeText(getApplicationContext(), "Long press on the Map to set Geofence", Toast.LENGTH_LONG).show();
                return  true;

            case R.id.maps_logout:
                //SharedPrefManager.getClassinstance(getApplicationContext()).logout();
                finish();
                return  true;

            case R.id.my_devices:
                Intent devicesIntent = new Intent(MapsActivity.this, ViewDevicesActivity.class);
                //mapIntent.putExtra("ACCOUNT_NAME", accountNameExtra);
                //mapIntent.putExtra("USER_TOKEN", userTokenExtra);
                startActivity(devicesIntent);
                return  true;

            case R.id.help:
                Toast.makeText(getApplicationContext(), " Help help", Toast.LENGTH_SHORT).show();
                return  true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show toast
     * @param msg message
     */
    public void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    /**
     * fetch user login History from API
     * @param user_token user token
     */
    public void getUserLoginHistory(final String user_token, final String provider_name) {
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
                                       Marker marker =  mMap.addMarker(new MarkerOptions().position(position).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.smatfone)));
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
                params.put("type", "get_login_history");
                params.put("user_token", user_token);
                params.put("provider_name", provider_name);

                return params;
            }
        };
        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);

    }

    public String readableDate(String sqlDate){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        String date = formatter.format(Date.parse(sqlDate));
        return date;

    }

    /**
     * Get User Location
     */
    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if(location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                showToast("Location found");
            } else if(location1 != null){
                latitude = location1.getLatitude();
                longitude = location1.getLongitude();
                showToast("Location found");
            } else if(location2 != null){
                latitude = location2.getLatitude();
                longitude = location2.getLongitude();
                showToast("Location found");
            } else{
                showToast("Couldnt get location");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST:
                getLocation();
                break;
            default:
                Log.d("LOCATION", "Location service");
                break;
        }
    }

    /**
     * A function to terminate session or simply to remotely logout any suspicious account usage
     * @param provider_name
     * @param login_device_token
     */
    public void terminateSession(final String provider_name, final String login_device_token, final Marker marker) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging Out This Device.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Just a few moment");
        progressDialog.show();

        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.LOGOUT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                loginDetailsalertDialog.dismiss();
                                marker.remove();

                            } else if (jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                                "Oops! Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("userToken", login_device_token);
                params.put("providerName", provider_name);

                return params;
            }
        };
        //requestQueue.add(stringRequest);
        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //showToast("You touched: Lat " + latLng.latitude + " Lng: " + latLng.longitude);
        displayGeofenceDialog(latLng);
    }

    // show alert daiglog to enable location
    public void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Location")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void displayGeofenceDialog(final LatLng latLng){

        //inflate layout and get the dialog view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.geofence_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_monitr);


        //get view components by referencing id
       // TextView deviceLocationTv = (TextView) view.findViewById(R.id.locationTv);
       // final TextView distanceTv = (TextView) view.findViewById(R.id.distanceTv);
        final SeekBar seekBar = (SeekBar)view.findViewById(R.id.seekbar);
        Button saveGeofence = (Button) view.findViewById(R.id.saveGeofenceBtn);

        //seekbar
        seekBar.setMax(2000);
        seekBar.setProgress(200);
        //distanceTv.append(String.valueOf(seekBar.getProgress()) + " meters");


        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                showToast("Radius : " + String.valueOf(progressChangedValue) + " meters");

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                drawGeofenceCircle(latLng, progressChangedValue);
            }
        });

         //when user clicks no button, open a dialog so user can take action
        saveGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGeofenceAPI(String.valueOf(seekBar.getProgress()), String.valueOf(latLng.latitude), String.valueOf(latLng.longitude), accountIdExtra);
            }
        });

        // create alert dialog for the main view (login history dialog)
        geofenceAlertDialog = builder.create();
       geofenceAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // show alert
        geofenceAlertDialog.show();

    }

    /**
     * A function to add a new white device to a user's online account
     * @param device_name
     * @param device_imei
     * @param account_id
     * @param  device_type
     */
    public void addNewDevice(final String device_name, final String device_imei, final String device_type, final String account_id) {
        if(device_type.equals("Trusted")){
            showToast("Adding " + device_name + " device...");
        } else{
            showToast("Blocking " + device_name + " device...");
        }


        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.USERSECURITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                if(device_type.equals("Trusted")){
                                    loginDetailsalertDialog.dismiss();
                                }
                            } else if (jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Oops! Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(SharedPrefManager.getClassinstance(getApplicationContext()).getUserId()));
                params.put("accountId", account_id);
                params.put("deviceName", device_name);
                params.put("deviceImei", device_imei);
                params.put("deviceType", device_type);
                params.put("type", "create");
                params.put("policy", "device");

                return params;
            }
        };
        //requestQueue.add(stringRequest);
        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);
    }

    public void addGeofenceAPI(final String distance, final String lat, final String lng, final String account_id) {
        showToast("Adding Your Geofence...");

        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.USERSECURITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                geofenceAlertDialog.dismiss();

                            } else if (jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Oops! Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(SharedPrefManager.getClassinstance(getApplicationContext()).getUserId()));
                params.put("accountId", account_id);
                params.put("distance", distance);
                params.put("lat", lat);
                params.put("lng", lng);
                params.put("policyName", "Geofence");
                params.put("type", "create");
                params.put("policy", "geofence");

                return params;
            }
        };
        //requestQueue.add(stringRequest);
        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);
    }

    public boolean isConnectedToNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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

    public String getCity(LatLng latLng){
        String toReturn = "";
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(null!=listAddresses&&listAddresses.size()>0){
                String locality = listAddresses.get(0).getAddressLine(0);
                if(locality.isEmpty()){
                    toReturn = (String.valueOf(latLng.latitude) + ", " + String.valueOf(latLng.longitude));
                } else{
                    toReturn =locality;
                }

            } else{
                toReturn = (String.valueOf(latLng.latitude) + ", " + String.valueOf(latLng.longitude));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id ==R.id.locationfab){
            if(mMap != null){
                LatLng userLocation = new LatLng(latitude, longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
            }
        }
    }
}
