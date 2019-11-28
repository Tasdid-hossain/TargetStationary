package com.example.targetstationary;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.targetstationary.Cart.CartActivity;
import com.example.targetstationary.Category.CategoryActivity;
import com.example.targetstationary.Home.MainActivity;
import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.Model.CategoryModel;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.Model.Request;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.CategoryViewHolder;
import com.example.targetstationary.ViewHolder.OrderViewHolder;
import com.example.targetstationary.database.Database;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.CALL_PHONE;
import static android.view.View.VISIBLE;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = "OrderActivity";
    private static final int ACTIVITY_NUM =3;
    FirebaseDatabase database;
    DatabaseReference Orders;
    Query orderyQuery;

    RecyclerView recycler_orders;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    TextView textCartItemCount;
    int mCartItemCount = 10;
    Database localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Log.d(TAG, "onCreate: Started");
        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        localDB = new Database(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        setupBottomNavigationView();

        if(currentUser==null){

            Toast.makeText(this,"Log in to view orders", Toast.LENGTH_SHORT).show();
        }
        else{
            /*Init Firebase*/
            database = FirebaseDatabase.getInstance();
            Orders = database.getReference().child("Orders").child(currentUser.getUid());
            orderyQuery = Orders.orderByKey();

            /*Load category*/
            recycler_orders = (RecyclerView) findViewById(R.id.recycler_orders);
            layoutManager = new LinearLayoutManager(this);
            recycler_orders.setHasFixedSize(true);
            recycler_orders.setLayoutManager(new LinearLayoutManager(this));
            loadOrders();
            recycler_orders.setAdapter(adapter);
        }

    }


    private void loadOrders() {

        FirebaseRecyclerOptions orderOptions = new FirebaseRecyclerOptions.Builder<Request>().setQuery(orderyQuery, Request.class).build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(orderOptions) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Request model) {
                ArrayList<OrderModel> orderModels = model.getOrders();

                SimpleDateFormat forma = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
                try {
                    Date newDate = forma.parse(getRef(position).getKey());
                    forma = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                    holder.orderTime.setText(forma.format(newDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.order_items.setText(String.valueOf(orderModels.size()));

                holder.orderPrice.setText(model.getTotal());
                holder.orderStatus.setText(model.getStatus());
                if(model.getStatus().equals("Delivered")){
                    holder.btn_refund.show();
                    holder.btn_refund.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:082242358"));
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                startActivity(callIntent);
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(new String[]{CALL_PHONE}, 1);
                                }
                            }
                        }
                    });
                }
                else
                    holder.btn_refund.hide();



                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent i = new Intent(OrderActivity.this, OrderDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("mylist",orderModels);
                        bundle.putString("status",model.getStatus());
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                return new OrderViewHolder(view);
            }
        };
    }

    /*Must include on start and on stop for firebaserecycleradapter*/
    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentUser!=null)
            adapter.stopListening();
    }

    /*TOP TOOLBAR*/
    /*Toolbar*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toptoolmenu, menu);

        final MenuItem menuItem = menu.findItem(R.id.ic_cartTop);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + "OrderDetails";
        SQLiteDatabase db = localDB.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(getItemsCount(), 99)));
                localDB.close();
                if (textCartItemCount.getVisibility() != VISIBLE) {
                    textCartItemCount.setVisibility(VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.ic_cartTop){
            Intent i = new Intent(OrderActivity.this, CartActivity.class);
            startActivity(i);
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }


    /*BottomNavigationView Setup*/
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(OrderActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
    }

}
