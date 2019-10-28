package com.example.targetstationary.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderModel implements Parcelable {
    String ProductID;
    String ProductName;
    String Quantity;
    String Price;
    String Discount;

    public OrderModel() {
    }

    public OrderModel(String productID, String productName, String quantity, String price, String discount) {
        ProductID = productID;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
    }

    protected OrderModel(Parcel in) {
        ProductID = in.readString();
        ProductName = in.readString();
        Quantity = in.readString();
        Price = in.readString();
        Discount = in.readString();
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

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

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ProductID);
        dest.writeString(ProductName);
        dest.writeString(Quantity);
        dest.writeString(Price);
        dest.writeString(Discount);
    }
}
