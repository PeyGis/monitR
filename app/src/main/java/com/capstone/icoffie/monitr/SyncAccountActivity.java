package com.capstone.icoffie.monitr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class SyncAccountActivity extends AppCompatActivity {
    private TextView providerName;
    private Toolbar toolbar;
    private TextInputLayout emailwrapper;
    private TextInputLayout passwordwrapper;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_account);

        // set toolbar
        toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //get bundle or extras from fragment activity
        Bundle bundle = getIntent().getExtras();
        final String providerNameExtra = bundle.getString("PROVIDER_NAME");
        final String accountIdExtra = bundle.getString("ACCOUNT_ID");

        // get view components by ids
        providerName = (TextView) findViewById(R.id.providerName);
        emailwrapper = (TextInputLayout) findViewById(R.id.emailwrapper);
        passwordwrapper = (TextInputLayout) findViewById(R.id.passwordwrapper);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        //set provider name from data passed from prev actiivyt
        providerName.append(providerNameExtra);

        // do some validation and sync account
        emailwrapper.setHint("your email address");
        passwordwrapper.setHint("your password");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailwrapper.setError(null);
                passwordwrapper.setError(null);

                String useremail = ""; String userpassword = "";
                if(emailwrapper.getEditText() != null) {
                    useremail = emailwrapper.getEditText().getText().toString();
                }

                if(passwordwrapper.getEditText() != null) {
                    userpassword = passwordwrapper.getEditText().getText().toString();
                }

                if(useremail.isEmpty() || !useremail.contains("@")){
                    emailwrapper.setError("Not a valid Email");
                } else if (userpassword.isEmpty() || (userpassword.length() < 6)){
                    passwordwrapper.setError("Password must be at least 6 chars");
                } else {
                    emailwrapper.setErrorEnabled(false);
                    passwordwrapper.setErrorEnabled(false);
                    callSyncAccountAPI(useremail, userpassword, accountIdExtra, providerNameExtra);
                }
            }
        });
    }

    public void callSyncAccountAPI(final String email, final String password, final String accountId, final String providerName) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Synching Your Account.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Just a few moment");
        progressDialog.show();

        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.USERACCOUNT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

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
                params.put("type", "create");
                params.put("userId", String.valueOf(SharedPrefManager.getClassinstance(getApplicationContext()).getUserId()));
                params.put("accountId", accountId);
                params.put("providerName", providerName);
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        //requestQueue.add(stringRequest);
        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);
    }
}
