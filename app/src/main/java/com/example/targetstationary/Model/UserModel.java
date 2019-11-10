package com.example.targetstationary.Model;

public class UserModel {
    public String displayName;
    public String phone;
    public String email;
    public String address;
    public String payment;
    public String userType;

    public UserModel() {
    }

    public UserModel(String displayName, String phone, String email, String address, String payment, String userType) {
        this.displayName = displayName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.payment = payment;
        this.userType = userType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
