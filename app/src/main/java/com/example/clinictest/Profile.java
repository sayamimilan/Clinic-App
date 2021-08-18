package com.example.clinictest;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;

public class Profile extends AppCompatActivity {

    private Button saveButton, pickButton;
//    private EditText nameField, phoneField, addressField;
    private TextInputEditText nameField, phoneField, addressField;
    private CheckBox cash, debit, credit;
    private Employee activeUser;
    private WalkInClinic clinic;
    private ArrayList<DataBaseService> services;
    private ArrayList<DataBaseUser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();

        nameField = (TextInputEditText) findViewById(R.id.nameField);
        phoneField = (TextInputEditText) findViewById(R.id.phoneNumField);
        addressField = (TextInputEditText) findViewById(R.id.addressField2);

        cash = (CheckBox) findViewById(R.id.cashCB);
        debit = (CheckBox) findViewById(R.id.debitCB);
        credit = (CheckBox) findViewById(R.id.creditCB);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClinic();
            }
        });

        pickButton = (Button) findViewById(R.id.pickLocation);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps();
            }
        });

        Intent i = getIntent();
        activeUser = (Employee) i.getSerializableExtra("user");
        services = (ArrayList<DataBaseService>) i.getSerializableExtra("services");
        users = (ArrayList<DataBaseUser>) i.getSerializableExtra("users");

        if(activeUser.getWalkInClinic()!=null){
            clinic = activeUser.getWalkInClinic();
            addressField.setText(clinic.getAddress());
            phoneField.setText(clinic.getPhoneNumber());
            nameField.setText(clinic.getName());

            ArrayList<PaymentMethod> paymentMethods = clinic.getPaymentMethods();
            if(paymentMethods.contains(PaymentMethod.CASH)){
                cash.setChecked(true);
            }
            if(paymentMethods.contains(PaymentMethod.DEBIT)){
                debit.setChecked(true);
            }
            if(paymentMethods.contains(PaymentMethod.CREDIT)){
                credit.setChecked(true);
            }

        }else{
            clinic = new WalkInClinic();
        }
    }

    public ArrayList<DataBaseUser> getUsers(){
        return users;
    }

    public void onClick(View v){}

    public void updateClinic(){
        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
//        ArrayList<InsuranceType> insuranceTypes = new ArrayList<>();

        if(!addressField.getText().toString().equals("") && !nameField.getText().toString().equals("") && !phoneField.getText().toString().equals("")){
            if(cash.isChecked()){
                paymentMethods.add(PaymentMethod.CASH);
            }
            if(debit.isChecked()){
                paymentMethods.add(PaymentMethod.DEBIT);
            }
            if(credit.isChecked()){
                paymentMethods.add(PaymentMethod.CREDIT);
            }
            if(paymentMethods.size() == 0){
                Toast.makeText(getApplicationContext(), "Choose at Least One Payment Method", Toast.LENGTH_SHORT).show();
                return;
            }


//            if(!validPhone(phoneField.getText().toString())){
//                Toast.makeText(getApplicationContext(),"Invalid Phone Number", Toast.LENGTH_LONG).show();
//                return;
//            }

//            if (!validateAddress(addressField.getText().toString())){
//                Toast.makeText(getApplicationContext(),"Invalid Address", Toast.LENGTH_LONG).show();
//                return;
//            }

            clinic.setAddress(addressField.getText().toString());
            clinic.setName(nameField.getText().toString());
            clinic.setPhoneNumber(phoneField.getText().toString());
            clinic.setPaymentMethods(paymentMethods);
//            clinic.setInsuranceTypes(insuranceTypes);
            try {
                activeUser.updateWalkinClinic();  // if clinic is already in database
            }catch(IllegalArgumentException ex){ // if clinic isn't yet in database
                activeUser.createWalkInClinic(clinic);
            }
            Toast.makeText(getApplicationContext(),"Profile Updated", Toast.LENGTH_SHORT).show();
            openUser();

        }else{
            Toast.makeText(getApplicationContext(), "Please Complete Each Field", Toast.LENGTH_SHORT).show();
        }
    }

    public void openUser() {
        Intent intent = new Intent(this, EmployeeUser.class);
        intent.putExtra("user", activeUser);
        intent.putExtra("users", users);
        intent.putExtra("services", services);
        startActivity(intent);
    }


    public void openMaps() {
        Uri gmmIntentUri = Uri.parse("http://maps.google.co.in/maps?q="+nameField.getText());
        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");
        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent);
    }


//    public boolean validPhone(String phone){
//        if(phone.length() < 10){
//            return false;
//        }
//
//        String digits = "";
//        ArrayList<Character> valid = new ArrayList<>(Arrays.asList('1','2','3','4','5','6','7','8','9','0'));
//
//        Character last = '_';
//        int count = 0;
//        for(Character c:phone.toCharArray()){
//            if(!valid.contains(c)){
//                boolean one = c.equals('(') && (count==0 || count ==2);
//                boolean two = c.equals(')') && (count==4 || count == 6);
//                boolean three = c.equals('+') && count==0;
//                boolean four = c.equals('-') && digits.length()%3 == 0;
//
//                if(!(one || two || three || four)){
//                    return false;
//                }
//
//            }else{
//                if(Character.isDigit(c)){
//                    if(!last.equals('+')){
//                        digits = digits + c.toString();
//                    }
//                }
//            }
//            last = c;
//            count++;
//        }
//        if(digits.length() == 10){
//            return true;
//        }
//
//        return false;
//    }


    public String makeValidPhone(String phone){
        return phone;
    }

    public void test(){
        ArrayList<String> closing = new ArrayList<String>();
        ArrayList<String> opening = new ArrayList<String>();

        closing.add("12:00");
        closing.add("14:00");
        closing.add("14:00");
        closing.add("14:00");
        closing.add("16:00");
        closing.add("16:00");
        closing.add("16:00");

        opening.add("7:00");
        opening.add("7:00");
        opening.add("7:00");
        opening.add("7:00");
        opening.add("7:00");
        opening.add("7:00");
        opening.add("7:00");

        activeUser.setClosingTimes(closing);
        activeUser.setOpeningTimes(opening);
        activeUser.updateWalkinClinic();
    }
}
