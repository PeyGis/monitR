package com.capstone.icoffie.monitr.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by iCoffie on 10/3/2017.
 */

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "PrefSaveUserInfo";
    private static final String KEY_USER_NAME = "User_Name";
    private static final String KEY_USER_EMAIL = "Email";
    private static final String KEY_USER_ID = "User_Id";
    private static final String KEY_USER_STATUS = "Status";
    private static final String FCM_TOKEN = "fcmtoken";

    private  static SharedPrefManager classinstance;
    private static Context context;

    private SharedPrefManager(Context cntxt)
    {
        context = cntxt;
    }

    public boolean saveUserDetails(int id, String uname, String email, String status)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USER_NAME, uname);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_STATUS, status);

        editor.apply();

        return  true;
    }

    public boolean saveFirebaseDeviceToken( String token)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(FCM_TOKEN, token);
        editor.apply();
        return  true;
    }

    public boolean isLoggedIn()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_USER_NAME, null) != null){
            return  true;
        }
        return false;
    }

    public boolean logout()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_STATUS);
       // editor.clear();
        editor.apply();

        return true;
    }

    public static synchronized SharedPrefManager getClassinstance(Context mycontext)
    {
        if (classinstance == null)
        {
            classinstance = new SharedPrefManager(mycontext);
        }
        return classinstance;
    }

    public String getUserName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_NAME, "user");
    }

    public String getUserEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, "admin@email.com");
    }

    public int getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, 1);
    }

    public String getUserStatus() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_STATUS, "Active");
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(FCM_TOKEN, null);
    }
}
