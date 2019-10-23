package com.example.targetstationary.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

    public TextView textProductName;
    public ImageView imageViewProduct, favorite_image;
    public TextView textProductPrice;

    private ItemClickListener itemClickListener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        textProductName = (TextView) itemView.findViewById(R.id.product_name);
        imageViewProduct = (ImageView) itemView.findViewById(R.id.product_image);
        textProductPrice = (TextView) itemView.findViewById(R.id.product_price);
        favorite_image = (ImageView) itemView.findViewById(R.id.fav);

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
