package com.capstone.icoffie.monitr.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.icoffie.monitr.MapsActivity;
import com.capstone.icoffie.monitr.R;
import com.capstone.icoffie.monitr.ViewDevicesActivity;
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

/**
 * Created by iCoffie on 2/24/2018.
 */

public class GenericRecyclerViewAdaper extends RecyclerView.Adapter {

//instance variables
    private ArrayList<UserAccountModel> userAccountModelArrayList;
    private ArrayList<UserDeviceModel> userDevicesArrayList;
    //private ArrayList<UserAccountModel> userGeofenceArrayList;
    private Context context;
    private int facebook = R.mipmap.ic_facebook;
    private int shoppn = R.mipmap.ic_shoppn;
    private int bankex = R.mipmap.ic_bankex;
    private int moodle = R.mipmap.ic_moodle;
    private int type;

    public GenericRecyclerViewAdaper(ArrayList<UserAccountModel> userAccountModelArrayList, ArrayList<UserDeviceModel> userDevicesArrayList, Context context, int type) {
        this.userAccountModelArrayList = userAccountModelArrayList;
        this.userDevicesArrayList = userDevicesArrayList;
        this.context = context;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType ==1){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_account_list_item, parent, false);
            return new UserAccountViewHolder(view);
        } else if(viewType ==2){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_devices_list_item, parent, false);
            return new UserDeviceViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof UserAccountViewHolder){
            UserAccountViewHolder accountViewHolder = (UserAccountViewHolder) holder;
            final UserAccountModel userAccount = userAccountModelArrayList.get(position);
            accountViewHolder.accountName.setText(userAccount.getAccountName());
            accountViewHolder.accountTagline.setText(userAccount.getAccountTagline());

            // setting appropriate imgae for an online account
            int accountId = Integer.parseInt(userAccount.getAccountId());
            switch (accountId) {
                case 1:
                    accountViewHolder.accountImage.setImageResource(bankex);
                    break;

                case 2:
                    accountViewHolder.accountImage.setImageResource(shoppn);
                    break;

                case 3:
                    accountViewHolder.accountImage.setImageResource(moodle);
                    break;

                case 4:
                    accountViewHolder.accountImage.setImageResource(facebook);
                    break;

                default:
                    accountViewHolder.accountImage.setImageResource(bankex);
                    break;
            }

            // when the layout is clicked takes user to TackAccount Activity... but I pass the account model details to that activity
            accountViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(v.getContext(), MapsActivity.class);
                    myIntent.putExtra("ACCOUNT_NAME", userAccount.getAccountName());
                    myIntent.putExtra("ACCOUNT_TAGLINE", userAccount.getAccountTagline());
                    myIntent.putExtra("ACCOUNT_ID", userAccount.getAccountId());
                    myIntent.putExtra("USER_ACCOUNT_ID", userAccount.getUserAccountId());
                    myIntent.putExtra("USER_TOKEN", userAccount.getUserToken());
                    v.getContext().startActivity(myIntent);
                }
            });

        } else if (holder instanceof UserDeviceViewHolder){
            final UserDeviceViewHolder deviceViewHolder = (UserDeviceViewHolder)holder;
            UserDeviceModel userDeviceModel = userDevicesArrayList.get(position);
            deviceViewHolder.deviceName.setText(userDeviceModel.getDeviceName());
            deviceViewHolder.deviceImei.append(userDeviceModel.getDeviceType());
            final String deviceType = userDeviceModel.getDeviceType();
            final String device_id = userDeviceModel.getDeviceId();
            if(deviceType.equals("Trusted")) {
                deviceViewHolder.btnDeviceType.setText("Disable");
                deviceViewHolder.btnDeviceType.setBackgroundColor(Color.RED);
                deviceViewHolder.btnDeviceType.setTextColor(Color.BLACK);
            }
            else {
                deviceViewHolder.btnDeviceType.setText("Enable");
                deviceViewHolder.btnDeviceType.setBackgroundColor(Color.rgb(0,128,128));
            }

            //setonchangelistener for toggle button
            deviceViewHolder.btnDeviceType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){
                        String detect = deviceViewHolder.btnDeviceType.getText().toString();
                        if(detect.equals("Disable")){
                            updateDeviceType("Blocked", device_id);
                        }
                        deviceViewHolder.btnDeviceType.setBackgroundColor(Color.rgb(0,128,128));
                        deviceViewHolder.btnDeviceType.setText("Enable");

                    } else{
                        String detect = deviceViewHolder.btnDeviceType.getText().toString();
                        if(detect.equals("Enable")){
                            updateDeviceType("Trusted", device_id);
                        }
                        deviceViewHolder.btnDeviceType.setBackgroundColor(Color.RED);
                        deviceViewHolder.btnDeviceType.setText("Disable");
                    }
                }
            });


        }else{
            //do nothing for now
        }

    }

    @Override
    public int getItemCount() {
        int listSize = 0;
        if(this.userAccountModelArrayList !=null) {
            listSize = userAccountModelArrayList.size();
        }
        else if(this.userDevicesArrayList !=null) {
            listSize = userDevicesArrayList.size();
        }
        return listSize;
    }

    @Override
    public int getItemViewType(int position) {
        return this.type;
    }

    //User Devices View Layout inner class
    public class UserDeviceViewHolder extends RecyclerView.ViewHolder{

        //define view components
        public TextView deviceName;
        public TextView deviceImei;
        public ToggleButton btnDeviceType;

        public UserDeviceViewHolder(View itemView) {
            super(itemView);
            deviceName = (TextView) itemView.findViewById(R.id.textDeviceName);
            deviceImei = (TextView) itemView.findViewById(R.id.textDeviceImei);
            btnDeviceType = (ToggleButton) itemView.findViewById(R.id.btnDeviceType);

        }
    }

    // an inner class that represents the user_account_list_item xml layout
    public class UserAccountViewHolder extends RecyclerView.ViewHolder{

        //define view components
        public TextView accountName;
        public TextView accountTagline;
        public LinearLayout linearLayout;
        public ImageView accountImage;

        public UserAccountViewHolder(View itemView) {
            super(itemView);
            accountName = (TextView)itemView.findViewById(R.id.textAccountName);
            accountTagline = (TextView)itemView.findViewById(R.id.textAccountTagline);
            accountImage = (ImageView)itemView.findViewById(R.id.imageHolder);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);

        }
    }

    public void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // update user type from API
    public void updateDeviceType(final String deviceType, final String deviceId) {
//        final ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setMessage("Fetching Your Devices(s).....");
//        progressDialog.show();

        Toast.makeText(context.getApplicationContext(), "Updating Device Status...", Toast.LENGTH_LONG).show();
        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.USERSECURITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // stop dialog
                       // progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {

                                Toast.makeText(context.getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context.getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // progressDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(context.getApplicationContext(), "Oops! Check internet connection and refresh", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("type", "update");
                params.put("policy", "device");
                params.put("deviceType", deviceType);
                params.put("userDeviceId", deviceId);

                return params;
            }
        };
        //requestQueue.add(stringRequest);
        SingletonApi.getClassinstance(context).addToRequest(stringRequest);

    }

    private String detectDeviceType(String type){
        String toReturn = "";

        if(type.equals("Trusted")){
            toReturn = "Blocked";
        }
        if(type.equals("Blocked")){
            toReturn = "Trusted";
        }

        return toReturn;

    }

}
