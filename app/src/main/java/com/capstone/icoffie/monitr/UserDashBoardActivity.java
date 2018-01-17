package com.capstone.icoffie.monitr;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.capstone.icoffie.monitr.fragments.UserAccountsFragment;
import com.capstone.icoffie.monitr.model.SharedPrefManager;

public class UserDashBoardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private View headerView;
    private TextView welcomeUserTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board );

        //if user isn't logged in
        if(!SharedPrefManager.getClassinstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(UserDashBoardActivity.this, LoginActivity.class));
            return;
        }

        // action bar settings
        toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open, R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //displays default fragment
        Fragment defaultFragment = new UserAccountsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentarea, defaultFragment);
        fragmentTransaction.commit();

        //navigation item
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);

        welcomeUserTv = (TextView) headerView.findViewById(R.id.welcomename);
        welcomeUserTv.setText("Welcome " + SharedPrefManager.getClassinstance(this).getUserName());
        setUpDrawerContent(navigationView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  void setUpDrawerContent(final NavigationView navigationView){

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // first set all menu items to unchecked
                uncheckAllMenuItems(navigationView);
                displayFragment(item);
                return true;
            }
        });
    }

    public void displayFragment(MenuItem menuItem){

        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_account:
                fragment = new UserAccountsFragment();
                break;
            case R.id.nav_settings:
                startActivity(new Intent(UserDashBoardActivity.this, Settings.class));
                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                fragment = new UserAccountsFragment();
                break;

        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentarea, fragment);
            fragmentTransaction.commit();
        }
        // now set clicked menu item to checked
        menuItem.setChecked(true);

        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();

    }

    public void logout(){

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(UserDashBoardActivity.this);
        alertBuilder.setMessage("Do you want to close the app?");
        alertBuilder.setCancelable(false);
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPrefManager.getClassinstance(getApplicationContext()).logout();
                finish();
            }
        });

        AlertDialog dialog = alertBuilder.create();
        dialog.setTitle("Exit App !!!");
        dialog.setIcon(R.drawable.icon);
        dialog.show();
    }

    private void uncheckAllMenuItems(NavigationView navigationView) {
        final Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                SubMenu subMenu = item.getSubMenu();
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    subMenuItem.setChecked(false);
                }
            } else {
                item.setChecked(false);
            }
        }
    }

}