package com.example.targetstationary.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.ProductDetails;
import com.example.targetstationary.ProductListActivity;
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
    ArrayList <String> IdList;
    ArrayList <String> descList;

    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView prodImage;
        TextView prodPrice, prodName;
        private ItemClickListener itemClickListener;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = (ImageView) itemView.findViewById(R.id.product_imageSearch);
            prodName = (TextView) itemView.findViewById(R.id.product_nameSearch);
            prodPrice = (TextView) itemView.findViewById(R.id.product_priceSearch);

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

    public SearchAdapter(Context context, ArrayList<String> imageList, ArrayList<String> nameList, ArrayList<String> priceList,
                         ArrayList<String> idList, ArrayList<String> DescList) {
        this.context = context;
        ImageList = imageList;
        NameList = nameList;
        PriceList = priceList;
        IdList = idList;
        descList = DescList;
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
        holder.prodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProductDetails.class);
                i.putExtra("ProductID", IdList.get(position));
                context.startActivity(i);
            }
        });

    }


    @Override
    public int getItemCount() {
        return NameList.size();
    }
}
