package com.capstone.autofix;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import com.capstone.autofix.helper.SQLiteHandler;
import com.capstone.autofix.helper.SessionManager;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private TextView currentuser,currentuseremail;
    private Button btnLogout;
    private CircleImageView imageView;

    private SQLiteHandler db;
    private SessionManager session;
    String URL="http://192.168.43.93/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()){
            logoutUser();
        }

        HashMap<String,String> user = db.getUserDetails();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);

        currentuser = (TextView) view.findViewById(R.id.current_user);
        currentuseremail = (TextView) view.findViewById(R.id.current_user_email);
        imageView = (CircleImageView) view.findViewById(R.id.current_image);

        String p = user.get("photo");
        String pic;
        if (p.equals("null")){
            pic = URL+"AutoFix/uploads/blank.png";
        }else {
            pic = URL+"AutoFix/uploads/" + p;
        }
        String name = user.get("fullname");
        String email = user.get("email");

        currentuser.setText(name);
        currentuseremail.setText(email);
        Picasso.get().load(pic).resize(200,200).centerCrop().into(imageView);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                break;
            case R.id.nav_cars:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CarsFragment()).commit();
                break;
            case R.id.nav_booking:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new BookingFragment()).commit();
                break;
            case R.id.nav_order:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrderFragment()).commit();
                break;
            case R.id.nav_cart:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CartFragment()).commit();
                break;
            case R.id.nav_settings:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingsFragment()).commit();
                Intent intent = new Intent(HomeActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_about:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AboutFragment()).commit();
                break;
            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,R.style.MyAlertDialog);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //logoutUser();
                        dialog.dismiss();
                        final ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this,R.style.MyAlertDialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Signing Out\nPlease Wait...");
                        progressDialog.show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                logoutUser();
                                progressDialog.dismiss();
                            }
                        }, 3000);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return true;
    }

    public void logoutUser(){
        session.setLogin(false);

        db.deleteUsers();
//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },3000);
        Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}
