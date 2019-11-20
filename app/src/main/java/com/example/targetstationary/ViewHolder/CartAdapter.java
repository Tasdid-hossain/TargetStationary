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
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.targetstationary.Cart.CartActivity;
import com.example.targetstationary.Category.CategoryActivity;
import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.ProductListActivity;
import com.example.targetstationary.R;
import com.example.targetstationary.database.Database;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    TextView txt_cart_name, txt_price;
    ElegantNumberButton cart_quantity;

    private ItemClickListener itemClickListener;

    public TextView setTxt_cart_name() {
        return txt_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        cart_quantity = (ElegantNumberButton) itemView.findViewById(R.id.cart_quantity);

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
    private CartActivity context;

    public CartAdapter(List<OrderModel> listData, CartActivity c) {
        this.listData = listData;
        this.context = c;
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

        Locale locale = new Locale("en", "us");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        /*Needs to be multiplied by quantity later on*/
        //int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        int price = Integer.parseInt(listData.get(position).getPrice());
        //holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());
        holder.cart_quantity.setNumber(listData.get(position).getQuantity());
        holder.txt_price.setText(Integer.toString(price*Integer.parseInt(holder.cart_quantity.getNumber())));
        holder.cart_quantity.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.txt_price.setText(Integer.toString(price*Integer.parseInt(holder.cart_quantity.getNumber())));
                OrderModel orderModel = listData.get(position);
                orderModel.setQuantity(holder.cart_quantity.getNumber());
                new Database(context).updatecart(orderModel);

                int total= 0;
                List<OrderModel> orderModels = new Database(context).getCarts();
                for (OrderModel order:orderModels){

                    total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
                }
                /*Locale locale = new Locale("en", "us");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);*/
                context.totalPrice.setText(String.valueOf(total));
            }
        });

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
