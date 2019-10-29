package com.example.targetstationary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.targetstationary.Category.CategoryActivity;
import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.Model.CategoryModel;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.Model.Request;
import com.example.targetstationary.ViewHolder.CategoryViewHolder;
import com.example.targetstationary.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = "OrderActivity";

    FirebaseDatabase database;
    DatabaseReference Orders;
    Query orderyQuery;

    RecyclerView recycler_orders;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Log.d(TAG, "onCreate: Started");


        if(currentUser==null){

            Toast.makeText(this,"Log in to view orders", Toast.LENGTH_SHORT).show();
        }
        else{
            /*Init Firebase*/
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
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

                SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                holder.order_items.setText(String.valueOf(orderModels.size()));

                holder.orderPrice.setText(model.getTotal());
                holder.orderStatus.setText(model.getStatus());



                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent i = new Intent(OrderActivity.this, OrderDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("mylist",orderModels);
                        i.putExtras(bundle);
                        startActivity(i);
                        Toast.makeText(OrderActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
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



}
