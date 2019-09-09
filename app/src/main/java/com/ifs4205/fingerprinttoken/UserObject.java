package com.ifs4205.fingerprinttoken;


public class UserObject {

    private String username;
    private String password;

    public UserObject(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}