package com.example.targetstationary.Cart;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.CartAdapter;
import com.example.targetstationary.database.Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private static final int ACTIVITY_NUM =3;
    RecyclerView recyclerViewCart;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference request;

    public TextView totalPrice;
    List <OrderModel> cart = new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Log.d(TAG, "onCreate: Started");
        setupBottomNavigationView();

        database = FirebaseDatabase.getInstance();
        request = database.getReference("Requests");

        recyclerViewCart = (RecyclerView) findViewById(R.id.cartRecycler);
        recyclerViewCart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCart.setLayoutManager(layoutManager);

        totalPrice = (TextView) findViewById(R.id.total);

        loadListProduct();
    }

    /*BottomNavigationView Setup*/
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(CartActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void loadListProduct(){
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();

        recyclerViewCart.setAdapter(adapter);

        int total= 0;
        for (OrderModel order:cart){

            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("en", "us");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        totalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Delete"))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position){
        cart.remove(position);
        new Database(this).cleanCart();
        for(OrderModel item:cart){
            new Database(this).addToCart(item);
        }
        loadListProduct();
    }
}
