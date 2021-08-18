package com.example.clinictest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        Button button1 = findViewById(R.id.SignInButton);
        Button button2 = findViewById(R.id.SignUpButton);


        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SignInButton:
                openSignIn();
                break;
            case R.id.SignUpButton:
                openSignUp();
                break;
        }
    }

    public void openSignIn() {
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
    }

    public void openSignUp() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

}