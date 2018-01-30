package com.capstone.icoffie.monitr;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.icoffie.monitr.adapters.UserAccountAdapter;
import com.capstone.icoffie.monitr.model.API_ENDPOINT;
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

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Toolbar toolbar;
    private Button takeAction;
    String userTokenExtra = "";

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

        //initialize map object
        initMap();

        // get user login history
       // getUserLoginHistory(userTokenExtra);
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
                // Add a marker in Sydney and move the camera
                LatLng ashesi = new LatLng(5.759800, -0.219751);
                LatLng kwabenya = new LatLng(7.019800, 0.419751);
                LatLng kumasi = new LatLng(6.8771, -1.622);

                mMap.addMarker(new MarkerOptions().position(ashesi).title("Your Device was logged in at Ashesi").icon(BitmapDescriptorFactory.fromResource(R.drawable.phone)));
                mMap.addMarker(new MarkerOptions().position(kwabenya).title("Your Device was logged in at Tamale").icon(BitmapDescriptorFactory.fromResource(R.drawable.desktop)));
                mMap.addMarker(new MarkerOptions().position(kumasi).title("Your Device was logged in at Kumasi").icon(BitmapDescriptorFactory.fromResource(R.drawable.phone)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(kwabenya));

                getUserLoginHistory(userTokenExtra);
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
        AlertDialog.Builder markerDialog = new AlertDialog.Builder(this);
        markerDialog.setTitle("Take Action");
        final String[] options = {"Logout", "Block Device", "Report"};
        markerDialog.setIcon(R.drawable.appicon);
        markerDialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    marker.remove();
                }
                else if(which == 1) {
                    marker.remove();
                } else {
                    Toast.makeText(getApplicationContext(), "Report Sent Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog alertDialog = markerDialog.create();
        alertDialog.show();
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

                                //JSONArray userAccounts = jsonObject.getJSONArray("login_audit");
//                                for(int i=0; i < userAccounts.length(); i++) {
//                                    JSONObject oneAccount = userAccounts.getJSONObject(i);
//
//
//                                }
                                Toast.makeText(getApplicationContext(),
                                        jsonObject.toString(), Toast.LENGTH_LONG).show();


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
