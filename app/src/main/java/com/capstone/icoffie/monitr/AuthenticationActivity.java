package com.capstone.icoffie.monitr;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone.icoffie.monitr.model.SharedPrefManager;

public class AuthenticationActivity extends AppCompatActivity {
    EditText six_digit_pin;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        btnNext = (Button)findViewById(R.id.btnNext);
        six_digit_pin = (EditText)findViewById(R.id.six_digit_pin);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(six_digit_pin.getText().length() == 6){
                    String pin = six_digit_pin.getText().toString();

                    if(SharedPrefManager.getClassinstance(getApplicationContext()).getUserPin() != null){
                        String sharedpref_pin = SharedPrefManager.getClassinstance(getApplicationContext()).getUserPin();
                        if(Integer.parseInt(six_digit_pin.getText().toString()) == Integer.parseInt(sharedpref_pin)){
                            Intent intent = new Intent(getApplicationContext(), UserDashBoardActivity.class);
                            startActivity(intent);
                            finish();
                        } else{
                            showToast("PIN didn't match");
                        }
                    } else{
                        Intent intent = new Intent(getApplicationContext(), ConfirmPINActivity.class);
                        intent.putExtra("SIX_DIGIT_PIN", pin);
                        startActivity(intent);
                        finish();
                    }

                } else{
                    showToast("PIN must be of 6 digit length");
                }

            }
        });
    }
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
