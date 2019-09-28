package com.example.targetstationary.Category;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.targetstationary.ProductActivity;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";
    private static final int ACTIVITY_NUM =1;

    FirebaseDatabase database;
    DatabaseReference category;
    Query categoryQuery;

    RecyclerView recycler_categories;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
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

        adapter = new FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder>(categoryOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull CategoryModel model) {
                holder.textCategoryName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageViewCategory);

                final CategoryModel clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        /*Get CategoryID and send to ProductActivity*/
                        Intent productIntent = new Intent(CategoryActivity.this, ProductActivity.class);
                        productIntent.putExtra("CategoryID", adapter.getRef(position).getKey().toString());
                        Toast.makeText(CategoryActivity.this, ""+adapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();

                        startActivity(productIntent);
                     }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
                return new CategoryViewHolder(view);
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
        BottomNavigationViewHelper.enableNavigation(CategoryActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
