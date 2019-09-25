package com.example.targetstationary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.targetstationary.Search.SearchActivity;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
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
    private static final int ACTIVITY_NUM =1;

    FirebaseDatabase database;
    DatabaseReference category;
    Query categoryQuery;

    RecyclerView recycler_categories;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<CategoryModel, ProductViewHolder> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Log.d(TAG, "onCreate: Started");
        setupBottomNavigationView();

        /*Init Firebase*/
        database = FirebaseDatabase.getInstance();
        category = database.getReference().child("Category");
        categoryQuery = category.orderByKey();

        /*Load category*/
        recycler_categories = (RecyclerView) findViewById(R.id.recycler_categories);
        layoutManager = new LinearLayoutManager(this);
        recycler_categories.setHasFixedSize(true);
        recycler_categories.setLayoutManager(new GridLayoutManager(this,2));

        loadCategory();

        recycler_categories.setAdapter(adapter);
    }

    /*Create firebaserecycleroptions and firebase recycler adapter. Then load that into recyclerview*/
    private void loadCategory(){
        FirebaseRecyclerOptions categoryOptions = new FirebaseRecyclerOptions.Builder<CategoryModel>().setQuery(categoryQuery, CategoryModel.class).build();

        adapter = new FirebaseRecyclerAdapter<CategoryModel, ProductViewHolder>(categoryOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull CategoryModel model) {
                holder.textProductName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageViewProduct);

                final CategoryModel clickItem = model;
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
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /*BottomNavigationView Setup*/
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(ProductActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
