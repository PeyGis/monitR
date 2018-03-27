package com.capstone.icoffie.monitr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceProvidersActivity extends AppCompatActivity implements View.OnClickListener {
    private Button google, jumia, fidelity;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_providers);

        // set toolbar
        toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get view components
        google = (Button)findViewById(R.id.actGoogle);
        jumia = (Button)findViewById(R.id.actJumia);
        fidelity = (Button)findViewById(R.id.actFidelity);


        //bind onclick event to buttons
        google.setOnClickListener(this);
        jumia.setOnClickListener(this);
        fidelity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.actGoogle:
//                Intent googleintent = new Intent(this, SyncAccountActivity.class);
//                googleintent.putExtra("PROVIDER_NAME", "easy-Pay");
//                googleintent.putExtra("ACCOUNT_ID", "3");
//                startActivity(googleintent);
                Toast.makeText(getApplicationContext(), "Provider not activated", Toast.LENGTH_LONG).show();
                break;

            case R.id.actJumia:
                Intent jumiaintent = new Intent(this, SyncAccountActivity.class);
                jumiaintent.putExtra("PROVIDER_NAME", "Shoppn");
                jumiaintent.putExtra("ACCOUNT_ID", "2");
                startActivity(jumiaintent);
                break;

            case R.id.actFidelity:
                Intent fidelityintent = new Intent(this, SyncAccountActivity.class);
                fidelityintent.putExtra("PROVIDER_NAME", "Bankex");
                fidelityintent.putExtra("ACCOUNT_ID", "1");
                startActivity(fidelityintent);
                break;

            default:
                break;
        }
    }
}
