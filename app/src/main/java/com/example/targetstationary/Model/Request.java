package com.example.targetstationary.Model;

import java.util.List;

public class Request {
    public String phone;
    public String total;
    public String displayName;
    public List<OrderModel> orders;

    public Request() {
    }

    public Request(String phone, String total, String displayName, List<OrderModel> orders) {
        this.phone = phone;
        this.total = total;
        this.displayName = displayName;
        this.orders = orders;
    }
}
