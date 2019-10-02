package com.example.targetstationary.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.targetstationary.Category.CategoryActivity;
import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.ProductActivity;
import com.example.targetstationary.R;
import com.example.targetstationary.database.Database;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView txt_cart_name, txt_price;
    public ImageView img_cart_count;

    private ItemClickListener itemClickListener;

    public TextView setTxt_cart_name() {
        return txt_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        img_cart_count = (ImageView) itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");
        menu.add(0,0,getAdapterPosition(),"Delete");
    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<OrderModel> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<OrderModel> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        TextDrawable textDrawable = TextDrawable.builder().buildRound(""+listData.get(position).getQuantity(), Color.RED);
        holder.img_cart_count.setImageDrawable(textDrawable);

        Locale locale = new Locale("en", "us");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        /*Needs to be multiplied by quantity later on*/
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

//        holder.setItemClickListener(new ItemClickListener() {
//            @Override
//            public void onClick(View view, int position, boolean isLongClick) {
//                /*YEEEEEEEEEEEEEEEEEEEEEEESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS*/
//                new Database(context).cleanCart(listData.get(position));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
