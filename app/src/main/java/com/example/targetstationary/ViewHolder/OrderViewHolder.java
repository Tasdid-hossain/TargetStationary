package com.example.targetstationary.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView order_items, orderPrice, orderTime, orderStatus;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        order_items = (TextView) itemView.findViewById(R.id.order_items);
        orderPrice = (TextView) itemView.findViewById(R.id.order_totalPrice);
        orderTime = (TextView) itemView.findViewById(R.id.order_time);
        orderStatus = (TextView) itemView.findViewById(R.id.order_status);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(itemView, getAdapterPosition(), false);
    }
}

