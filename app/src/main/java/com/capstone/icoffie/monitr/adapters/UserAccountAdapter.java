package com.capstone.icoffie.monitr.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capstone.icoffie.monitr.MapsActivity;
import com.capstone.icoffie.monitr.R;
import com.capstone.icoffie.monitr.TrackAccount;
import com.capstone.icoffie.monitr.model.UserAccountModel;

import java.util.ArrayList;

/**
 * Created by iCoffie on 12/26/2017.
 */

public class UserAccountAdapter extends RecyclerView.Adapter<UserAccountAdapter.ViewHolder> {

    private ArrayList<UserAccountModel> userAccountModelArrayList;
    private Context context;
    private int facebook = R.mipmap.ic_facebook;
    private int jumia = R.mipmap.ic_jumia;
    private int ecobank = R.mipmap.ic_ecobank;
    private int moodle = R.mipmap.ic_moodle;

    //constructor to be used in main activity to get data and context
    public UserAccountAdapter(ArrayList<UserAccountModel> userAccountModelArrayList, Context context) {
        this.userAccountModelArrayList = userAccountModelArrayList;
        this.context = context;
    }

    // create a view and inflate it.... it uses the list view created in the xml file
    @Override
    public UserAccountAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_account_list_item, parent, false);
        return new ViewHolder(view);
    }

    // get item selected and bind data to view
    @Override
    public void onBindViewHolder(UserAccountAdapter.ViewHolder holder, int position) {
        final UserAccountModel userAccount = userAccountModelArrayList.get(position);
        holder.accountName.setText(userAccount.getAccountName());
        holder.accountTagline.setText(userAccount.getAccountTagline());

        // setting appropriate imgae for an online account
        int accountId = Integer.parseInt(userAccount.getAccountId());
        switch (accountId){
            case 1:
                holder.accountImage.setImageResource(ecobank);
                break;

            case 2:
                holder.accountImage.setImageResource(jumia);
                break;

            case 3:
                holder.accountImage.setImageResource(moodle);
                break;

            case 4:
                holder.accountImage.setImageResource(facebook);
                break;

            default:
                holder.accountImage.setImageResource(moodle);
                break;
        }

        // when the layout is clicked takes user to TackAccount Activity... but I pass the account model details to that activity
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
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

    }

    // return size of list
    @Override
    public int getItemCount() {
        return userAccountModelArrayList.size();
    }

// an inner class that represents the user_account_list_item xml layout
    public class ViewHolder extends RecyclerView.ViewHolder{

        //define view components
        public TextView accountName;
        public TextView accountTagline;
        public LinearLayout linearLayout;
        public ImageView accountImage;

        public ViewHolder(View itemView) {
            super(itemView);
            accountName = (TextView)itemView.findViewById(R.id.textAccountName);
            accountTagline = (TextView)itemView.findViewById(R.id.textAccountTagline);
            accountImage = (ImageView)itemView.findViewById(R.id.imageHolder);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linearLayout);

        }
    }
}
