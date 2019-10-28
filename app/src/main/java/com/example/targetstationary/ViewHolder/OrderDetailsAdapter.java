package com.example.targetstationary.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.targetstationary.Cart.CartActivity;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.OrderDetailsActivity;
import com.example.targetstationary.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsViewHolder>{
    private ArrayList<OrderModel> listData = new ArrayList<>();
    private OrderDetailsActivity context;

    public OrderDetailsAdapter(ArrayList<OrderModel> listData, OrderDetailsActivity c) {
        this.listData = listData;
        this.context = c;
    }

    @NonNull
    @Override
    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.order_details_item, parent, false);
        return new OrderDetailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsViewHolder holder, int position) {
        holder.deails_name.setText(listData.get(position).getProductName());
        holder.details_id.setText(listData.get(position).getProductID());
        holder.details_price.setText(listData.get(position).getPrice());
        holder.details_quantity.setText(listData.get(position).getQuantity());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
