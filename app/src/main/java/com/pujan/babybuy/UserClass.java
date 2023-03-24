package com.pujan.babybuy;

public class UserClass {
    private String userid;
    private String name;
    private String phone;
    private String email;


    public UserClass(String userid, String name, String phone, String email) {
        this.userid = userid;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
    public UserClass(){
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
