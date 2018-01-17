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

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailwrapper;
    private TextInputLayout passwordwrapper;
    private Button loginBtn;
    private TextView signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(SharedPrefManager.getClassinstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(LoginActivity.this, UserDashBoardActivity.class));
            return;
        }

        // get view by id
        emailwrapper = (TextInputLayout) findViewById(R.id.emailwrapper);
        passwordwrapper = (TextInputLayout) findViewById(R.id.passwordwrapper);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        signupBtn = (TextView) findViewById(R.id.signupBtn);

        // open intent to sign up page
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

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
                    callLoginAPI(useremail, userpassword);
                }
            }
        });

    }

    public void callLoginAPI(final String email, final String password) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loging in.....");
        progressDialog.show();

        // RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_ENDPOINT.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                JSONObject userObject = jsonObject.getJSONObject("user");

                                if(SharedPrefManager.getClassinstance(getApplicationContext()).saveUserDetails(
                                        userObject.getInt("User_Id"), userObject.getString("Username"),
                                        userObject.getString("Email"), userObject.getString("Status")
                                )){
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(LoginActivity.this, UserDashBoardActivity.class));
                                    finish();
                                } else{
                                    Toast.makeText(getApplicationContext(), "Couldn't Save User Details", Toast.LENGTH_LONG).show();
                                }



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
                                "Error occured! Check internet connection", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        //requestQueue.add(stringRequest);
        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);
    }

}
