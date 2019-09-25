package com.example.targetstationary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.Model.CategoryModel;
import com.example.targetstationary.Model.ProductModel;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.CategoryViewHolder;
import com.example.targetstationary.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "ProductActivity";

    FirebaseDatabase database;
    DatabaseReference product;
    Query productQuery;

    String CategoryID;

    RecyclerView recycler_product;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<ProductModel, ProductViewHolder> adapterProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Log.d(TAG, "onCreate: Started");
        setupBottomNavigationView();

        /*Init Firebase*/
        database = FirebaseDatabase.getInstance();
        product = database.getReference().child("Product");
        productQuery = product;

        /*Load category*/
        recycler_product = (RecyclerView) findViewById(R.id.recycler_product);
        layoutManager = new LinearLayoutManager(this);
        recycler_product.setHasFixedSize(true);
        recycler_product.setLayoutManager(new GridLayoutManager(this,2));

        /*Get the intent*/
        if(getIntent() != null)
            CategoryID = getIntent().getStringExtra("CategoryID");
        if(!CategoryID.isEmpty())
        {
            loadProduct(CategoryID);
        }

        recycler_product.setAdapter(adapterProduct);
    }

    private void loadProduct(String categoryID) {

        FirebaseRecyclerOptions productOptions = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(productQuery.orderByChild("CategoryID").equalTo(categoryID),
                ProductModel.class).build();

        adapterProduct = new FirebaseRecyclerAdapter<ProductModel, ProductViewHolder>(productOptions) {


            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductModel model) {
                holder.textProductName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageViewProduct);
                holder.textProductPrice.setText(model.getPrice());

                final ProductModel clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(ProductActivity.this, ""+clickItem.getName(), Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
                return new ProductViewHolder(view);
            }
        };
    }

    /*Must include on start and on stop for firebaserecycleradapter*/
    @Override
    protected void onStart() {
        super.onStart();
        adapterProduct.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterProduct.stopListening();
    }

    /*BottomNavigationView Setup*/
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(ProductActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();

    }
}
