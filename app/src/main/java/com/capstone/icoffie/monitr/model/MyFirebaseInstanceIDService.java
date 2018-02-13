package com.capstone.icoffie.monitr.model;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by iCoffie on 2/13/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("Firbase id login", "Refreshed token: " + refreshedToken);
            saveToken(refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveToken(String token){
        //saving the token on shared preferences
        SharedPrefManager.getClassinstance(getApplicationContext()).saveFirebaseDeviceToken(token);
    }

}
