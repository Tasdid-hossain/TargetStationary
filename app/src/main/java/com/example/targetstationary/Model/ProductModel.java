package com.example.targetstationary.Model;

public class ProductModel {
    private String Name;
    private String Image;
    private String Description;
    private String Price;
    private String CategoryID;

    public ProductModel(String name, String image, String description, String price, String categoryID) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        CategoryID=categoryID;
    }

    public ProductModel() {
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
