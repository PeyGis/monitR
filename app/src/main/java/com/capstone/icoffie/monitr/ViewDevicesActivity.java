package com.capstone.icoffie.monitr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.icoffie.monitr.adapters.EmptyRecyclerViewAdapter;
import com.capstone.icoffie.monitr.adapters.GenericRecyclerViewAdaper;
import com.capstone.icoffie.monitr.adapters.UserAccountAdapter;
import com.capstone.icoffie.monitr.model.API_ENDPOINT;
import com.capstone.icoffie.monitr.model.SharedPrefManager;
import com.capstone.icoffie.monitr.model.SingletonApi;
import com.capstone.icoffie.monitr.model.UserAccountModel;
import com.capstone.icoffie.monitr.model.UserDeviceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewDevicesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Toolbar toolbar;
    final Context context = this;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<UserDeviceModel> userDeviceModelArrayList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_devices);

        // set toolbar
        toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set recyclerview ready for use
        recyclerView = (RecyclerView) findViewById(R.id.devices_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //init arraylist
        userDeviceModelArrayList = new ArrayList<>();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.device_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    //fetch user devices data from server
                getUserDevices();
            }
        });

    }
    // fetch user accounts from API
    public void getUserDevices() {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setMessage("Fetching Your Devices(s).....");
//        progressDialog.show();
        mSwipeRefreshLayout.setRefreshing(true);

        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.USERSECURITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // stop dialog
                        mSwipeRefreshLayout.setRefreshing(false);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {

                                JSONArray userDevices = jsonObject.getJSONArray("mydevices");
                                for(int i=0; i < userDevices.length(); i++) {
                                    JSONObject oneDevice = userDevices.getJSONObject(i);

                                    UserDeviceModel device = new UserDeviceModel(
                                            oneDevice.getString("Device_Name"),
                                            oneDevice.getString("Device_IMEI"),
                                            oneDevice.getString("Status"),
                                            oneDevice.getString("Online_Account_Id"));

                                    if(!isInList(oneDevice.getString("Device_IMEI"))){
                                        userDeviceModelArrayList.add(device);
                                    }

                                }
                                //set recyclerview adapter
                                adapter = new GenericRecyclerViewAdaper(null, userDeviceModelArrayList, getApplicationContext(), 2);
                                recyclerView.setAdapter(adapter);
                                //Toast.makeText(getActivity().getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                            } else {
                                adapter = new EmptyRecyclerViewAdapter("You have no device added");
                                recyclerView.setAdapter(adapter);
                                //Toast.makeText(getActivity().getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        adapter = new EmptyRecyclerViewAdapter("Check internet connection and refresh");
                        recyclerView.setAdapter(adapter);
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Oops! Check internet connection and refresh", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("type", "select");
                params.put("policy", "device");
                params.put("userId", String.valueOf(SharedPrefManager.getClassinstance(getApplicationContext()).getUserId()));

                return params;
            }
        };
        //requestQueue.add(stringRequest);
        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);

    }

    @Override
    public void onRefresh() {
        getUserDevices();
    }

    private boolean isInList(String key){

        for(UserDeviceModel userDeviceModel : userDeviceModelArrayList){
            if(userDeviceModel.getDeviceIMEI().equals(key)){
                return true;
            }
        }
        return false;
    }
}
