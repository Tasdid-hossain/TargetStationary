package com.example.targetstationary.Model;

import java.util.ArrayList;
import java.util.List;

public class Request {
    public String phone;
    public String total;
    public String displayName;
    public String status;
    public ArrayList<OrderModel> orders;

    public Request() {
    }

    public Request(String phone, String total, String displayName, String status, ArrayList<OrderModel> orders) {
        this.phone = phone;
        this.total = total;
        this.displayName = displayName;
        this.status = status;
        this.orders = orders;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<OrderModel> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<OrderModel> orders) {
        this.orders = orders;
    }
}
