package com.example.targetstationary.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.targetstationary.Model.OrderModel;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static String DB_NAME = "OrderDetails.db";
    private static int DB_VER = 1;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public ArrayList <OrderModel> getCarts(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"ProductID", "ProductName","Quantity","Price", "Discount"};
        String sqlTable = "OrderDetails";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null,null,null,null,null);

        final ArrayList<OrderModel> result = new ArrayList<>();
        if(c.moveToFirst()){
            do {
                result.add(new OrderModel(c.getString(c.getColumnIndex("ProductID")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                        )
                );
            }while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(OrderModel orderModel)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Insert into OrderDetails (ProductID,ProductName,Quantity,Price,Discount) VALUES ('%s','%s','%s','%s','%s');",
                orderModel.getProductID(),
                orderModel.getProductName(),
                orderModel.getQuantity(),
                orderModel.getPrice(),
                orderModel.getDiscount());                ;

        db.execSQL(query);
    }

    public void cleanCart()
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetails");

        db.execSQL(query);
    }

    public void updatecart(OrderModel orderModel) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetails SET Quantity = %s where ProductID = %s", orderModel.getQuantity(), orderModel.getProductID());
        db.execSQL(query);
    }

    public void addtofavorites(String favorite)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Insert into Favorites (ProductID) VALUES ('%s');", favorite);
        db.execSQL(query);
    }

    public void deletefromfavorites(String favorite)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Delete from Favorites WHERE ProductID='%s';", favorite);
        db.execSQL(query);
    }

    public boolean isfavorites(String favorite)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE ProductID='%s';", favorite);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount()<=0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
