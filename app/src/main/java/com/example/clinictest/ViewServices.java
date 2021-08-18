package com.example.clinictest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;

import android.widget.EditText;
import android.widget.ListView;
import android.app.AlertDialog;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewServices extends AppCompatActivity {
    private Administrator activeUser;
    private ArrayList<DataBaseService> services;
    private static DatabaseReference databaseServices = FirebaseDatabase.getInstance().getReference("services");
    ListView listViewServices;
    Button buttonAddService;

    EditText editService;
    TextInputEditText createService;

    RadioButton rbDoctor, rbNurse, rbStaff ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_services);

        getSupportActionBar().hide();

        services = new ArrayList<>();

        listViewServices = (ListView)findViewById(R.id.serviceList);
        buttonAddService = (Button) findViewById(R.id.addBTN);
        createService =  (TextInputEditText) findViewById(R.id.serviceNameTXT);
        editService =  findViewById(R.id.updateService);
        rbDoctor = (RadioButton) findViewById(R.id.DoctorRB);
        rbNurse =  (RadioButton) findViewById(R.id.NurseRB);
        rbStaff =  (RadioButton) findViewById(R.id.StaffRB);

        // setting up the call for long press on item
        listViewServices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataBaseService service = services.get(i);
                showUpdateDeleteDialog(service);
                return true;
            }
        });

        Intent i = getIntent();
        activeUser = (Administrator) i.getSerializableExtra("user");
        services = (ArrayList<DataBaseService>) i.getSerializableExtra("services");
        activeUser.setServices(services);

        buttonAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClinicService();
            }
        });

        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                services.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DataBaseService service = postSnapshot.getValue(DataBaseService.class);
                    services.add(service);
                }
                activeUser.setServices(services);
                ServiceList serviceAdapter = new ServiceList(ViewServices.this, services);
                listViewServices.setAdapter(serviceAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Admin.class);
        startActivity(intent);
    }

    private void showUpdateDeleteDialog(final DataBaseService service) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog b = dialogBuilder.create();
        b.show();

        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateService);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteService);
        final EditText updateTextName = (EditText) dialogView.findViewById(R.id.updateService);
        final RadioButton dialogrbDoctor = (RadioButton) dialogView.findViewById(R.id.DoctorRB);
        final RadioButton dialogrbNurse =  (RadioButton) dialogView.findViewById(R.id.NurseRB);
        final RadioButton dialogrbStaff =  (RadioButton) dialogView.findViewById(R.id.StaffRB);
        updateTextName.setText(service.getName());
        if (service.getRole() == ServiceRole.Doctor){
            dialogrbDoctor.setChecked(true);
        }else{
            dialogrbDoctor.setChecked(false);
        }
        if (service.getRole() == ServiceRole.Nurse){
            dialogrbNurse.setChecked(true);
        }else{
            dialogrbNurse.setChecked(false);
        }
        if (service.getRole() == ServiceRole.Staff){
            dialogrbStaff.setChecked(true);
        }else{
            dialogrbStaff.setChecked(false);
        }


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updateName = updateTextName.getText().toString().trim();
                if (!updateName.equals("")) {
                    ServiceRole role;
                    if (dialogrbDoctor.isChecked()) {
                        role = ServiceRole.Doctor;
                    } else if (dialogrbNurse.isChecked()) {
                        role = ServiceRole.Nurse;
                    } else {
                        role = ServiceRole.Staff;
                    }

                    boolean updated = updateClinicService(service, updateName, role);
                    if(updated) {
                        hideKeyboard();
                        b.dismiss();
                    }
                }else{
                    Toast.makeText(view.getContext(), "Please enter a service name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteClinicService(service);
                b.dismiss();
                }
            }
        );
    }

    private void addClinicService() throws IllegalArgumentException{

        String service = createService.getText().toString().trim();

        if (!service.equals("")) {
            try {
                ServiceRole role;
                if (rbDoctor.isChecked())
                    role = ServiceRole.Doctor;
                else if (rbNurse.isChecked())
                    role = ServiceRole.Nurse;
                else
                    role = ServiceRole.Staff;

                if(service.length() <= 2){
                    Toast.makeText(this, "Service Name Too Short", Toast.LENGTH_SHORT).show();
                }else {
                    activeUser.addService(service, role);
                    createService.getText().clear();
                    hideKeyboard();
                }

            } catch (IllegalArgumentException ex) {
                Toast.makeText(this, "Service Already Exists", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please Enter a Service Name", Toast.LENGTH_SHORT).show();
        }

    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void deleteClinicService(DataBaseService service) {
        try {
            activeUser.deleteService(service);
            Toast.makeText(this, "Service deleted", Toast.LENGTH_SHORT).show();
        }catch (IllegalArgumentException ex){
            Toast.makeText(this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean updateClinicService(DataBaseService service, String updateName, ServiceRole role) {
        boolean didUpdate = false;

        try {
            didUpdate = activeUser.updateService(service, updateName, role);
            if (!didUpdate){
                Toast.makeText(this, "Unexpected Error Occured", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Service Updated", Toast.LENGTH_SHORT).show();
            }
        }catch (IllegalArgumentException ex){
            Toast.makeText(this, "Service already exists", Toast.LENGTH_SHORT).show();
        }


        return didUpdate;
    }


}
