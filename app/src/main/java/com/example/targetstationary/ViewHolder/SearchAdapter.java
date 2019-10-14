package com.example.targetstationary.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.targetstationary.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchAdapter extends RecyclerView.Adapter <SearchAdapter.SearchViewHolder> {
    Context context;

    ArrayList <String> ImageList;
    ArrayList <String> NameList;
    ArrayList <String> PriceList;

    class SearchViewHolder extends RecyclerView.ViewHolder{
        ImageView prodImage;
        TextView prodPrice, prodName;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = (ImageView) itemView.findViewById(R.id.product_imageSearch);
            prodName = (TextView) itemView.findViewById(R.id.product_nameSearch);
            prodPrice = (TextView) itemView.findViewById(R.id.product_priceSearch);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> imageList, ArrayList<String> nameList, ArrayList<String> priceList) {
        this.context = context;
        ImageList = imageList;
        NameList = nameList;
        PriceList = priceList;
    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.product_search,parent,false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.prodPrice.setText(PriceList.get(position));
        holder.prodName.setText(NameList.get(position));
        Picasso.get().load(ImageList.get(position)).into(holder.prodImage);

    }


    @Override
    public int getItemCount() {
        return NameList.size();
    }
}
