package com.example.targetstationary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.ViewHolder.OrderDetailsAdapter;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {

    OrderDetailsAdapter orderDetailsAdapter;
    RecyclerView recyclerView_orderDetails;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<OrderModel> orders;
    String status;
    TextView order_status_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Order Details");


        Bundle bundle = getIntent().getExtras();
        orders = bundle.getParcelableArrayList("mylist");
        status = bundle.getString("status");

        recyclerView_orderDetails = (RecyclerView) findViewById(R.id.recycler_details);
        recyclerView_orderDetails.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_orderDetails.setLayoutManager(layoutManager);
        order_status_details = (TextView) findViewById(R.id.order_details_status);
        order_status_details.setText(status);

        loadOrderDetails();
    }

    private void loadOrderDetails() {
        orderDetailsAdapter = new OrderDetailsAdapter(orders,this);
        orderDetailsAdapter.notifyDataSetChanged();

        recyclerView_orderDetails.setAdapter(orderDetailsAdapter);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
