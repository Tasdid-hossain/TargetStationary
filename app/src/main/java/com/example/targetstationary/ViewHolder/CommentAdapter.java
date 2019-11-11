package com.example.targetstationary.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.targetstationary.CommentActivity;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.Model.Rating;
import com.example.targetstationary.OrderDetailsActivity;
import com.example.targetstationary.ProductDetails;
import com.example.targetstationary.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder>{
    private ArrayList<String> listData = new ArrayList<>();
    private CommentActivity context;

    public CommentAdapter(ArrayList<String> listData, CommentActivity c) {
        this.listData = listData;
        this.context = c;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.comments_item, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.textComment.setText(listData.get(position));
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }
}
