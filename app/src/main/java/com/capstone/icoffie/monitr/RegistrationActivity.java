package com.capstone.icoffie.monitr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.icoffie.monitr.model.API_ENDPOINT;
import com.capstone.icoffie.monitr.model.SharedPrefManager;
import com.capstone.icoffie.monitr.model.SingletonApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputLayout userNamewrapper;
    private TextInputLayout emailwrapper;
    private TextInputLayout passwordwrapper;
    private Button signupBtn;
    private TextView loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        makeToast(SharedPrefManager.getClassinstance(this).getDeviceToken());
        //get views by id
        loginBtn = (TextView) findViewById(R.id.loginBtn);
        signupBtn = (Button) findViewById(R.id.signupBtn);

        //get textinput layout views also by id
        userNamewrapper = (TextInputLayout) findViewById(R.id.usernamewrapper);
        emailwrapper = (TextInputLayout) findViewById(R.id.emailwrapper);
        passwordwrapper = (TextInputLayout) findViewById(R.id.passwordwrapper);

        // prepare textinput layout to set floating label
        userNamewrapper.setHint("User Name");
        emailwrapper.setHint("Email Address");
        passwordwrapper.setHint("Password");

        //set onclick listener to login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

        //set onclick listener to signup button
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupUser();

            }
        });
    }

    public void signupUser(){

        //declare variables to hold user input data
        String userName = "";
        String email = "";
        String password = "";

        // clear error messages
        userNamewrapper.setError(null);
        emailwrapper.setError(null);
        passwordwrapper.setError(null);

        // trying to catch null pointer exception in case no edit text field is available in textinput layout
        if(userNamewrapper.getEditText() != null){
            userName = userNamewrapper.getEditText().getText().toString();
        }
        if(emailwrapper.getEditText() != null){
            email = emailwrapper.getEditText().getText().toString();
        }
        if(passwordwrapper.getEditText() != null){
            password = passwordwrapper.getEditText().getText().toString();
        }

        // throw error when user does not provide right data
        if (userName.isEmpty()){
            userNamewrapper.setError("Not a valid name");
        }
        else if (email.isEmpty() || (!email.contains("@"))){
            emailwrapper.setError("Not a valid email");
        }
        else if (password.isEmpty() || password.length() < 6){
            passwordwrapper.setError("Password must be at least 6 chars");
        }
        else{
            userNamewrapper.setErrorEnabled(false);
            emailwrapper.setErrorEnabled(false);
            passwordwrapper.setErrorEnabled(false);
            callRegisterAPI(userName, email, password);

        }
    }

    public void callRegisterAPI(final String uname, final String email, final String password){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User....");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        //get response in JSON
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if(!jsonResponse.getBoolean("error")){
                                makeToast(jsonResponse.getString("message"));
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                finish();
                            } else{
                                makeToast(jsonResponse.getString("message"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
                makeToast("Error occured! Check internet connection");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username",uname);
                params.put("email", email);
                params.put("password", password);
                params.put("fcm_token", SharedPrefManager.getClassinstance(getApplicationContext()).getDeviceToken());
                params.put("type", "register");
                return params;
            }
        };

        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);
    }


    public void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}

