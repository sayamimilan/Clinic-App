package com.example.clinictest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class Admin extends AppCompatActivity {

//    private Button userButton, serviceButton;
    private ImageView userButton, serviceButton;
    private Administrator activeUser;
    private TextView welcome;
    private ArrayList<DataBaseService> services;
    private ArrayList<DataBaseUser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin);

        userButton = (ImageView) findViewById(R.id.usersBtn);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openActivity(ListUsers.class);
                openUsers();
            }
        });

        serviceButton = (ImageView) findViewById(R.id.serviceBtn);
        serviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(ViewServices.class);
            }
        });

        Intent i = getIntent();
        activeUser = (Administrator) i.getSerializableExtra("user");
        services = (ArrayList<DataBaseService>) i.getSerializableExtra("services");
        users = (ArrayList<DataBaseUser>) i.getSerializableExtra("users");

    }

    @Override
    public void onBackPressed() {

    }
    public void openActivity(Class activity){
        Intent intent = new Intent(this, activity);
        intent.putExtra("user", activeUser);
        intent.putExtra("services", services);
        intent.putExtra("users", users);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(Admin.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void openUsers(){
        Intent intent = new Intent(Admin.this, ListUsers.class);
//        Intent intent = new Intent(Admin.this, ViewUsers.class);
        startActivity(intent);
    }
}