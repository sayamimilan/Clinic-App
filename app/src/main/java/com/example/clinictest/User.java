package com.example.clinictest;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User extends Person {

    private String userRole;
    private String status;

    private static DatabaseReference databaseUsers;

    public User(String name, String username, String password){
        this.id = "";
        this.name = name;
        this.password = password;
        this.userRole = "";
        this.status = "enabled";
        this.username = username;
        init();
    }

    public User(String username, String password){
        this.id = "";
        this.name = null;
        this.password = password;
        this.username = username;
        init();
    }

//    public User(String name, String username, String userRole, String status) {
//        this.name = name;
//        this.username = username;
//        this.userRole = userRole;
//        this.status = status;
//    }

    private void init(){
        try{
            databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        }catch(Exception e){}
    }


    public void addRole(String role){
        this.userRole = role;
    }

    public String getRole(){
        return this.userRole;
    }

    public String getStatus() {return this.status;}


    public boolean save(){
      if (this.name != null && this.username != null && this.password!=null && this.userRole != null && this.status != null){
            String id = databaseUsers.push().getKey();  // get unique database key
            DataBaseUser DB = new DataBaseUser(id, name, username, password, userRole, status);
            this.id = id;
            databaseUsers.child(id).setValue(DB);  // save in database
            return true;
        }
        return false;
    }

    public String getId(){return this.id;}

    @Override
    public boolean login() {
        return false;
    }

}