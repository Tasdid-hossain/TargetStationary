package com.example.targetstationary.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.targetstationary.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    TextView textComment;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        textComment = (TextView) itemView.findViewById(R.id.textcomments);
    }


}

