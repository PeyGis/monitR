package com.capstone.icoffie.monitr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ConfirmPINActivity extends AppCompatActivity {
    EditText six_digit_pin;
    Button btnConfirm;
    String digit_from_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pin);

        //get data from Bundle or Extra
        Bundle digit_extra = getIntent().getExtras();
        digit_from_intent = digit_extra.getString("SIX_DIGIT_PIN");


        //init view component
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        six_digit_pin = (EditText)findViewById(R.id.confirm_six_digit_pin);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(six_digit_pin.getText().toString().length() == 6){
                    if(Integer.parseInt(six_digit_pin.getText().toString()) == Integer.parseInt(digit_from_intent)){
                        callRegisterPINAPI(digit_from_intent);
                    } else{
                        showToast("PIN didn't match");
                    }

                } else{
                    showToast("PIN must be of 6 digit length");
                }

            }
        });
    }
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void callRegisterPINAPI(final String pin){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Auth PIN....");
        progressDialog.setCancelable(false);
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
                                showToast(jsonResponse.getString("message"));
                                if(SharedPrefManager.getClassinstance(getApplicationContext()).saveUserPIN(pin)){
                                    startActivity(new Intent(ConfirmPINActivity.this, UserDashBoardActivity.class));
                                    finish();
                                }

                            } else{
                                showToast(jsonResponse.getString("message"));
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
                showToast("Error occured! Check internet connection");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pin",pin);
                params.put("email", SharedPrefManager.getClassinstance(getApplicationContext()).getUserEmail());
                params.put("type", "add_pin");
                return params;
            }
        };

        SingletonApi.getClassinstance(getApplicationContext()).addToRequest(stringRequest);
    }
}
