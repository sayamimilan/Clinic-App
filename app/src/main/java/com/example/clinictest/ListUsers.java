package com.example.clinictest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class ListUsers extends AppCompatActivity {

    RecyclerView recyclerView;
    myadapter adapter;
    ImageView edit;
    DataBaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list_users);

        getSupportActionBar().hide();

        recyclerView = (RecyclerView) findViewById(R.id.listUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        edit = (ImageView) findViewById(R.id.editBtn);

        FirebaseRecyclerOptions<DataBaseUser> options = new FirebaseRecyclerOptions.Builder<DataBaseUser>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("users"), DataBaseUser.class)
                .build();

        adapter = new myadapter(options);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}