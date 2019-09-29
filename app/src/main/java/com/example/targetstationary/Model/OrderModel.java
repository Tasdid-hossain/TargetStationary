package com.example.targetstationary.Model;

public class OrderModel {
    String ProductID;
    String ProductName;
    String Price;
    String Discount;
    String Quantity;

    public OrderModel(String productID, String productName, String price, String discount) {
        ProductID = productID;
        ProductName = productName;
        Price = price;
        Discount = discount;

    }

    public OrderModel() {
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}
