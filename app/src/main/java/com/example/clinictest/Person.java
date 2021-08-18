package com.example.clinictest;

import java.io.Serializable;

public abstract class Person implements Serializable {

    protected String name;

    protected String username;

    protected String password;

    protected String status;

    protected String role;

    protected String id;

    public String get_status() {
        return status;
    }

    public String get_role() {
        return role;
    }

    public String get_name(){
        return this.name;
    }

    public String get_username(){
        return this.username;
    }

    public String get_password(){
        return this.password;
    }

    public void setId(String id){this.id = id;}

    public String getId(){return id;}

    public void print(){
        System.out.println("Name: " + name + ", Username: " + username + ", Password: " + password);
    }

    public abstract boolean login();

}