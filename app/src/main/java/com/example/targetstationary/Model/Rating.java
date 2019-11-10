package com.example.targetstationary.Model;

public class Rating {
    private String uid;
    private String productID;
    private String rateValue;
    private String comment;

    public Rating() {
    }

    public Rating(String uid, String productID, String rateValue, String comment) {
        this.uid = uid;
        this.productID = productID;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
