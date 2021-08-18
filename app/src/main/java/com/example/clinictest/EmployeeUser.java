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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmployeeUser extends AppCompatActivity {

//    private TextView welcome;
    private Employee activeUser;
    private ArrayList<DataBaseService> services;
    private ArrayList<DataBaseUser> users;
//    private Button button1;
    private ImageView button2, button3, button4;
    private static DatabaseReference databaseWalkIn = FirebaseDatabase.getInstance().getReference("clinics");
    private boolean profileComplete = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_employee);

//        welcome = (TextView) findViewById(R.id.welcomeText);

        Intent i = getIntent();
        activeUser = (Employee) i.getSerializableExtra("user");
        services = (ArrayList<DataBaseService>) i.getSerializableExtra("services");
        users = (ArrayList<DataBaseUser>) i.getSerializableExtra("users");

//        button1 = (Button) findViewById(R.id.aptBtn);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openActivity(ClinicAppointments.class);
//            }
//        });

        button2 = (ImageView) findViewById(R.id.profileButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(Profile.class);
            }
        });

        button3 = (ImageView) findViewById(R.id.hoursButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileComplete)
                    openActivity(Hours.class);
                else{
                    Toast.makeText(getApplicationContext(), "Please complete your profile first", Toast.LENGTH_SHORT).show();
                }

            }
        });

        button4 = (ImageView) findViewById(R.id.servicesButton);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileComplete)
                    openActivity(Services.class);
                else{
                    Toast.makeText(getApplicationContext(), "Please complete your profile first", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        button5 = (Button) findViewById(R.id.employeeHoursBtn);
//        button5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(profileComplete) {
//                    openActivity(EmployeeHours.class);
//                }else{
//                    Toast.makeText(getApplicationContext(), "Please complete your profile first", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        databaseWalkIn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    WalkInClinic clinic = postSnapshot.getValue(WalkInClinic.class);
                    if (clinic.getId().equals(activeUser.getClinicId())){// DON'T REMOVE THIS
                        activeUser.setWalkInClinic(clinic);
                        if(!activeUser.getWalkInClinic().getName().equals("")){
                            profileComplete = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        welcome.setText("Welcome " + activeUser.get_username() + " you are logged in as employee.");


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
                Intent intent = new Intent(EmployeeUser.this, MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;

    }

}