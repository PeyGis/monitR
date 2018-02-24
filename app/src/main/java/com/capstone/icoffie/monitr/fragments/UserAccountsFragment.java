package com.capstone.icoffie.monitr.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.icoffie.monitr.R;
import com.capstone.icoffie.monitr.ServiceProvidersActivity;
import com.capstone.icoffie.monitr.adapters.EmptyRecyclerViewAdapter;
import com.capstone.icoffie.monitr.adapters.GenericRecyclerViewAdaper;
import com.capstone.icoffie.monitr.adapters.UserAccountAdapter;
import com.capstone.icoffie.monitr.model.API_ENDPOINT;
import com.capstone.icoffie.monitr.model.SharedPrefManager;
import com.capstone.icoffie.monitr.model.SingletonApi;
import com.capstone.icoffie.monitr.model.UserAccountModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAccountsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private View view;
    private TextView txtUserName, emptyView;
    private FloatingActionButton fab;
    private ArrayList<UserAccountModel> userAccountModelArrayList;
    final Context context = getContext();
    ConnectivityManager connectivityManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_account_list, container, false);


        // set recyclerview ready for use
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

//set welcome user name textview
        txtUserName = (TextView)view.findViewById(R.id.userName);
        txtUserName.append(SharedPrefManager.getClassinstance(getContext()).getUserName());

        //set floating action button
        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ServiceProvidersActivity.class));
            }
        });

        // initialize Arraylist
        userAccountModelArrayList = new ArrayList<>();

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
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

                //detect network
                if(!isConnectedToNetwork()){
                    Snackbar mSnackbar = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG)
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
                    mSwipeRefreshLayout.setRefreshing(true);
                    //fetch user accounts data from server
                    getUserAccounts();
                }

            }
        });
        return view;
    }

    // fetch user accounts from API
    public void getUserAccounts() {
//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setMessage("Fetching Online Account(s).....");
//        progressDialog.show();
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);

        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.USERACCOUNT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // stop refreshing
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {

                                JSONArray userAccounts = jsonObject.getJSONArray("myaccounts");
                                    for(int i=0; i < userAccounts.length(); i++) {
                                        JSONObject oneAccount = userAccounts.getJSONObject(i);

                                        UserAccountModel onlineAccount = new UserAccountModel(
                                                oneAccount.getString("Account_Name"),
                                                oneAccount.getString("Account_Tagline"),
                                                oneAccount.getString("Online_Account_Id"),
                                                oneAccount.getString("User_Online_Account_Id"),
                                                oneAccount.getString("User_Token"));
                                        if(!isInList(oneAccount.getString("Online_Account_Id"))){
                                            userAccountModelArrayList.add(onlineAccount);
                                        }

                                    }
                                    //set recyclerview adapter
                                   // adapter = new UserAccountAdapter(userAccountModelArrayList, getActivity().getApplicationContext());
                                adapter = new GenericRecyclerViewAdaper(userAccountModelArrayList, null, getActivity().getApplicationContext(), 1);
                                    recyclerView.setAdapter(adapter);
                                //Toast.makeText(getActivity().getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                            } else {
                                adapter = new EmptyRecyclerViewAdapter("You haven't synced any account yet. click on the circular orange icon to syn now");
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
                        // Stopping swipe refresh
                        mSwipeRefreshLayout.setRefreshing(false);
                        adapter = new EmptyRecyclerViewAdapter("Error Occurred. Check interent and refresh");
                        recyclerView.setAdapter(adapter);

                        error.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Oops! Check internet connection and refresh", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("type", "select");
                params.put("userId", String.valueOf(SharedPrefManager.getClassinstance(getActivity().getApplicationContext()).getUserId()));

                return params;
            }
        };
        //requestQueue.add(stringRequest);
        SingletonApi.getClassinstance(getActivity().getApplicationContext()).addToRequest(stringRequest);

    }


    public void showToast(String msg){
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRefresh() {
        //fetch user accounts data from server
        getUserAccounts();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean isConnectedToNetwork(){
        if(getActivity() != null){
            this.connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnectedOrConnecting());
        }
        return false;

    }

    private boolean isInList(String key){

        for(UserAccountModel userAccountModel : userAccountModelArrayList){
            if(userAccountModel.getAccountId().equals(key)){
                return true;
            }
        }
        return false;
    }
}

