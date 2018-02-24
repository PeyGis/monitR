package com.capstone.icoffie.monitr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                        showToast("PIN Match... Sending to server");
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
}
