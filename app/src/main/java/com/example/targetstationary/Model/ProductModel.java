package com.example.targetstationary.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductModel implements Parcelable {
    private String Name;
    private String Image;
    private String Description;
    private String Price;
    private String CategoryID;
    private ImageListModel imageList;

    public ProductModel(String name, String image, String description, String price, String categoryID, ImageListModel i) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        CategoryID=categoryID;
        imageList = i;
    }

    public ProductModel(Parcel in) {
        Name = in.readString();
        Image = in.readString();
        Description = in.readString();
        Price = in.readString();
        CategoryID = in.readString();
    }

    public ProductModel() {
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

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

    public ImageListModel getImageList() {
        return imageList;
    }

    public void setImageList(ImageListModel imageList) {
        this.imageList = imageList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Image);
        dest.writeString(Description);
        dest.writeString(Price);
        dest.writeString(CategoryID);

    }
}
