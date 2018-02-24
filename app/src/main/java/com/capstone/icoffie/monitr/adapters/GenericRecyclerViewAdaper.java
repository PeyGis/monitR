package com.capstone.icoffie.monitr.adapters;

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

import com.capstone.icoffie.monitr.MapsActivity;
import com.capstone.icoffie.monitr.R;
import com.capstone.icoffie.monitr.model.UserAccountModel;
import com.capstone.icoffie.monitr.model.UserDeviceModel;

import java.util.ArrayList;

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
    private int jumia = R.mipmap.ic_jumia;
    private int ecobank = R.mipmap.ic_ecobank;
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
                    accountViewHolder.accountImage.setImageResource(ecobank);
                    break;

                case 2:
                    accountViewHolder.accountImage.setImageResource(jumia);
                    break;

                case 3:
                    accountViewHolder.accountImage.setImageResource(moodle);
                    break;

                case 4:
                    accountViewHolder.accountImage.setImageResource(facebook);
                    break;

                default:
                    accountViewHolder.accountImage.setImageResource(moodle);
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
            String enableDisable = userDeviceModel.getDeviceType();
            if(enableDisable.equals("Active")) {
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
                        showToast("is checked");
                        deviceViewHolder.btnDeviceType.setBackgroundColor(Color.rgb(0,128,128));
                        deviceViewHolder.btnDeviceType.setText("Enable");
                    } else{
                        showToast("is not checked");
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
}
