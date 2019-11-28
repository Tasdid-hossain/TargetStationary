package com.example.targetstationary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

import com.example.targetstationary.Home.MainActivity;
import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.Model.ProductModel;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.ProductViewHolder;
import com.example.targetstationary.database.Database;
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

public class ProductListActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference productRef;
    FirebaseRecyclerAdapter<ProductModel, ProductViewHolder> adapter;
    Query productQuery;
    RecyclerView prod_recycler;
    String CategoryID="";
    StaggeredGridLayoutManager mLayoutmanager;
    Database localDB;

    //search functionality
    FirebaseRecyclerAdapter<ProductModel, ProductViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        localDB = new Database(this);
        setupBottomNavigationView();
        /*Get the intent data which is sent from CategoryActivit*/
        if(getIntent() != null)
            CategoryID = getIntent().getStringExtra("CategoryID");
        if(!CategoryID.isEmpty())
        {
            //Toast.makeText(this, "got it", Toast.LENGTH_SHORT).show();
            productQuery = productRef;
        }

        database = FirebaseDatabase.getInstance();
        productRef = database.getReference().child("Product");
        productQuery = productRef;
        mLayoutmanager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        prod_recycler = (RecyclerView) findViewById(R.id.prod_recycler);
        loadProduct(CategoryID);
        prod_recycler.setLayoutManager(new GridLayoutManager(this,2));
        prod_recycler.setAdapter(adapter);

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
                    prod_recycler.setAdapter(adapter);
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
                        Toast.makeText(ProductListActivity.this, ""+clickItem.getName(), Toast.LENGTH_SHORT).show();

                        /*Goes into Product Details*/
                        Intent prodDetails = new Intent(ProductListActivity.this, ProductDetails.class);
                        prodDetails.putExtra("ProductID", adapter.getRef(position).getKey());
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
        prod_recycler.setAdapter(searchAdapter);
    }

    private void loadSuggested(){
        productRef.orderByChild("CategoryID").equalTo(CategoryID).addValueEventListener(new ValueEventListener() {
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

    private void loadProduct(String x) {
        /*This is basically a query that will be included when creating the adapter. Matches the categoryID with the */
        FirebaseRecyclerOptions productOptions = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(productQuery.orderByChild("CategoryID").equalTo(x),
                ProductModel.class).build();

        /*The firebase recycler adapter. which takes the products depending on the category from the firebase.
         * It creates a recycler view and shows*/
        adapter = new FirebaseRecyclerAdapter<ProductModel, ProductViewHolder>(productOptions) {


            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductModel model) {
                holder.textProductName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageViewProduct);
                holder.textProductPrice.setText(model.getPrice());
                if(localDB.isfavorites(adapter.getRef(position).getKey()))
                    holder.favorite_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                holder.favorite_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isfavorites(adapter.getRef(position).getKey()))
                        {
                            localDB.addtofavorites(adapter.getRef(position).getKey());
                            holder.favorite_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                        }
                        else{
                            localDB.deletefromfavorites(adapter.getRef(position).getKey());
                            holder.favorite_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        }

                    }
                });

                final ProductModel clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(ProductListActivity.this, ""+clickItem.getName(), Toast.LENGTH_SHORT).show();

                        /*Goes into Product Details*/
                        Intent prodDetails = new Intent(ProductListActivity.this, ProductDetails.class);
                        prodDetails.putExtra("ProductID", adapter.getRef(position).getKey());
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

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /*BottomNavigationView Setup*/
    private void setupBottomNavigationView(){
        Log.d("Prod List", "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(ProductListActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }
}
