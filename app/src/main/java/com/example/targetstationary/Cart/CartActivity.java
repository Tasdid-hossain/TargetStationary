package com.example.targetstationary.Cart;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.targetstationary.Account.SignIn;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.Model.Request;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.CartAdapter;
import com.example.targetstationary.database.Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private static final int ACTIVITY_NUM =3;
    RecyclerView recyclerViewCart;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView totalPrice;
    ArrayList <OrderModel> cart = new ArrayList<>();
    CartAdapter adapter;
    FloatingActionButton place_order;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Log.d(TAG, "onCreate: Started");
        setupBottomNavigationView();

        /*Toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Cart");

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Orders");

        recyclerViewCart = (RecyclerView) findViewById(R.id.cartRecycler);
        recyclerViewCart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCart.setLayoutManager(layoutManager);
        totalPrice = (TextView) findViewById(R.id.total);

        mAuth = FirebaseAuth.getInstance();
        place_order= (FloatingActionButton) findViewById(R.id.place_order);
        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser==null)
                    Toast.makeText(CartActivity.this, "Please login to place the order", Toast.LENGTH_SHORT).show();
                else
                {
                    Request request = new Request(
                            currentUser.getPhoneNumber(),
                            totalPrice.getText().toString(),
                            currentUser.getDisplayName(),
                            "Order Received",
                            cart
                    );
                    Toast.makeText(CartActivity.this, "Your order has been placed!", Toast.LENGTH_SHORT).show();

                    requests.child(currentUser.getUid()).child(String.valueOf(Calendar.getInstance().getTime())).setValue(request);
                    new Database(getBaseContext()).cleanCart();
                    finish();
                }

            }
        });

        loadListProduct();
    }

    /*BottomNavigationView Setup*/
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(CartActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(false);
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

    /*Check auth status*/
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
