package com.capstone.icoffie.monitr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.icoffie.monitr.model.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button editProfileBtn;
    private EditText userName, userEmail, userPaswd;
    private TextView profileBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //setting support for action bar
        toolbar =(Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize view components
        editProfileBtn = (Button) findViewById(R.id.edit_profile);
        userName = (EditText) findViewById(R.id.usernametv);
        userEmail = (EditText) findViewById(R.id.useremailtv);
        userPaswd = (EditText) findViewById(R.id.userpasswordtv);
        profileBanner = (TextView) findViewById(R.id.textProfileName);

        //get sharedpref data and bind to view
        profileBanner.setText(SharedPrefManager.getClassinstance(this).getUserName());
        userName.setText(SharedPrefManager.getClassinstance(this).getUserName());
        userEmail.setText(SharedPrefManager.getClassinstance(this).getUserEmail());

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //declare variables to hold user input data
                String editedname = "";
                String editedemail = "";
                String editepassword = "";
                editedname = userName.getText().toString();
                editedemail = userEmail.getText().toString();
                editepassword = userPaswd.getText().toString();

                if (editedname.isEmpty()){
                    showToast("Not a valid name");
                }
                else if (editedemail.isEmpty() || (!editedemail.contains("@"))){
                    showToast("Not a valid email");
                }
                else if (editepassword.isEmpty() || editepassword.length() < 6){
                    showToast("Password must be at least 6 chars");
                }
                else{
                    showToast("Editing Profile....");
                }
            }
        });
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
