package com.example.targetstationary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "ProductActivity";

    FirebaseDatabase database;
    DatabaseReference product;
    Query productQuery;

    String CategoryID;


    RecyclerView recycler_product;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<ProductModel, ProductViewHolder> adapterProduct;

    //search functionality
    FirebaseRecyclerAdapter<ProductModel, ProductViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

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

        /*Get the intent data which is sent from CategoryActivit*/
        if(getIntent() != null)
            CategoryID = getIntent().getStringExtra("CategoryID");
        if(!CategoryID.isEmpty())
        {
            loadProduct(CategoryID);
        }

        recycler_product.setAdapter(adapterProduct);

        /*Search functions*/
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your product name");
        loadSuggested();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*Suggest list will change according to the text input from users*/
                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search bar is close restore to original
                if(!enabled)
                    recycler_product.setAdapter(adapterProduct);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish
                //show result
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        /*This is basically a query that will be included when creating the search adapter*/
        FirebaseRecyclerOptions searchOptions = new FirebaseRecyclerOptions.Builder<ProductModel>().
                setQuery(productQuery.orderByChild("Name").startAt(text.toString()).
                                endAt(text.toString()+"\uf8ff"),
                ProductModel.class).build();

        searchAdapter = new FirebaseRecyclerAdapter<ProductModel, ProductViewHolder>(searchOptions) {
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

                        /*Goes into Product Details*/
                        Intent prodDetails = new Intent(ProductActivity.this, ProductDetails.class);
                        prodDetails.putExtra("ProductID", adapterProduct.getRef(position).getKey());
                        startActivity(prodDetails);
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
        searchAdapter.startListening();
        recycler_product.setAdapter(searchAdapter);
    }

    private void loadSuggested(){
        product.orderByChild("CategoryID").equalTo(CategoryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    ProductModel item = postSnapShot.getValue(ProductModel.class);
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadProduct(String categoryID) {
        /*This is basically a query that will be included when creating the adapter. Matches the categoryID with the */
        FirebaseRecyclerOptions productOptions = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(productQuery.orderByChild("CategoryID").equalTo(categoryID),
                ProductModel.class).build();

        /*The firebase recycler adapter. which takes the products depending on the category from the firebase.
        * It creates a recycler view and shows*/
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

                        /*Goes into Product Details*/
                        Intent prodDetails = new Intent(ProductActivity.this, ProductDetails.class);
                        prodDetails.putExtra("ProductID", adapterProduct.getRef(position).getKey());
                        startActivity(prodDetails);
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

    /*Must include onstart and onstop for firebaserecycleradapter*/
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
