package com.capstone.icoffie.monitr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button auth, delete, manage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //set app toolbar
        toolbar =(Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init view component
        Button auth = (Button) findViewById(R.id.two_step_auth);
        Button delete = (Button) findViewById(R.id.delete_monitr);
        Button manage = (Button) findViewById(R.id.manage_synced_account);

        auth.setOnClickListener(this);
        delete.setOnClickListener(this);
        manage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.two_step_auth){
            startActivity(new Intent(getApplicationContext(), TwoStepAuthActivity.class));
        }
        if (id == R.id.delete_monitr){
            showToast("Delete Feature not implemented");
        }
        if (id == R.id.manage_synced_account){
            showToast("Manage Sync Account Feature not implemented");
        }
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
