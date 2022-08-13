package com.example.clinictest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    TextInputEditText userName, password;
    ArrayList<DataBaseUser> users;
    ArrayList<DataBaseService> services;
    private static DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("users");
    private static DatabaseReference databaseServices = FirebaseDatabase.getInstance().getReference("services");

    private Person activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().hide();

        ImageButton button1 = findViewById(R.id.signInImgBtn);

        button1.setOnClickListener(this);

        userName = (TextInputEditText) findViewById(R.id.usernameField2);
        password = (TextInputEditText) findViewById(R.id.passwordField2);

        users = new ArrayList<>();
        services = new ArrayList<>();
        updateUsers();
        updateServices();

        ImageView toSignUp = (ImageView) findViewById(R.id.SignUpSlideText);

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSIgnUp();
            }
        });
    }

    int counter = 0;

    @Override
    public void onBackPressed() {

        counter++;
        if(counter == 1){
            Toast.makeText(this, "Press Back again to exit the app", Toast.LENGTH_SHORT).show();
        }else{
            super.onBackPressed();
            this.finishAffinity();
        }

    }

    public void updateUsers(){
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();  // might need to remove
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    DataBaseUser usr = postSnapshot.getValue(DataBaseUser.class);
                    users.add(usr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateServices(){
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                services.clear();  // might need to remove
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    DataBaseService service = postSnapshot.getValue(DataBaseService.class);
                    services.add(service);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInImgBtn:
                if (validateForm()){
                    if(activeUser instanceof Employee){
                        openEmployee();
                    }else if (activeUser instanceof Patient){
                        openPatient();
                    }else{
                        openAdmin();
                    }
                }
                break;
        }
    }


    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public boolean validateForm(){
        String uname = userName.getText().toString();
        String Password = password.getText().toString();

        if(uname.equals("")){
            Toast.makeText(getApplicationContext(), "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }else if(Password.equals("")){
            Toast.makeText(getApplicationContext(), "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            for(DataBaseUser usr : users){
                String otherUsername = usr.getUsername();
                String pwd = usr.getPassword();
                String name = usr.getName();
                String role = usr.getRole();
                String id = usr.getId();
                String status = usr.getStatus();
                String clinicId = usr.getClinicId();
                if (otherUsername.equals(uname)){
                    if (pwd.equals(Password)){
                        if (status.equals("disabled")) {
                            Toast.makeText(this, "User is disabled. Please contact admin.", Toast.LENGTH_SHORT).show();
                            return false;
                        }else{
                            // successful login
                            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
                            if (role.equals("Employee")){
                                activeUser = new Employee(name, otherUsername, pwd, clinicId);
                            }else if (role.equals("Patient")){
                                activeUser = new Patient(name, otherUsername, pwd);
                            }else{
                                activeUser = new Administrator(otherUsername, pwd);
                            }
                            activeUser.setId(id);
                            return true;
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Incorrect password!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        Toast.makeText(getApplicationContext(), "Incorrect username!", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void openPatient() {
        Intent intent = new Intent(this, PatientUser.class);
        intent.putExtra("user", activeUser);
        intent.putExtra("users", users);
        intent.putExtra("services", services);
        startActivity(intent);
    }

    public void openEmployee() {
        Intent intent = new Intent(this, EmployeeUser.class);
        intent.putExtra("user", activeUser);
        intent.putExtra("users", users);
        intent.putExtra("services", services);
        startActivity(intent);
    }

    public void openAdmin(){
        Intent intent = new Intent(this, Admin.class);
        intent.putExtra("user", activeUser);
        intent.putExtra("users", users);
        intent.putExtra("services", services);
        startActivity(intent);
    }

    public void openSIgnUp(){
        Intent intent = new Intent(SignIn.this, SignUp.class);
        startActivity(intent);
    }
}