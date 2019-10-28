package com.example.targetstationary.ViewHolder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.targetstationary.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDetailsViewHolder extends RecyclerView.ViewHolder {
    TextView deails_name, details_price, details_quantity, details_id;

    public OrderDetailsViewHolder(@NonNull View itemView) {
        super(itemView);

        deails_name = (TextView) itemView.findViewById(R.id.details_name);
        details_price = (TextView) itemView.findViewById(R.id.details_price);
        details_quantity = (TextView) itemView.findViewById(R.id.details_quantity);
        details_id = (TextView) itemView.findViewById(R.id.details_id);
    }


}

