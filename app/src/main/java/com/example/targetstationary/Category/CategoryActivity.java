package com.example.targetstationary.Category;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.targetstationary.Cart.CartActivity;
import com.example.targetstationary.Home.MainActivity;
import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.Model.CategoryModel;
import com.example.targetstationary.ProductDetails;
import com.example.targetstationary.ProductListActivity;
import com.example.targetstationary.ProductListActivity;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.CategoryViewHolder;
import com.example.targetstationary.database.Database;
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
    TextView textCartItemCount;
    Database localDB;
    FirebaseDatabase database;
    DatabaseReference category;
    Query categoryQuery;

    RecyclerView recycler_categories;
    GridLayoutManager layoutManager;
    FirebaseRecyclerAdapter<CategoryModel, CategoryViewHolder> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Log.d(TAG, "onCreate: Started");
        setupBottomNavigationView();

        /*Toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Store");
        localDB = new Database(this);

        /*Init Firebase*/
        database = FirebaseDatabase.getInstance();
        category = database.getReference().child("Category");
        categoryQuery = category.orderByKey();

        /*Load category*/
        recycler_categories = (RecyclerView) findViewById(R.id.recycler_categories);
        layoutManager = new GridLayoutManager(this,2);
        recycler_categories.setHasFixedSize(true);
        recycler_categories.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

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
                        Intent productIntent = new Intent(CategoryActivity.this, ProductListActivity.class);
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

    /*Toolbar*/
    /*Toolbar*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toptoolmenu, menu);

        final MenuItem menuItem = menu.findItem(R.id.ic_cartTop);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + "OrderDetails";
        SQLiteDatabase db = localDB.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private void setupBadge() {
        int m = 10;
        if (textCartItemCount != null) {
            if (m == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(getItemsCount(), 99)));
                localDB.close();
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.ic_cartTop){
            Intent i = new Intent(CategoryActivity.this, CartActivity.class);
            startActivity(i);
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
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
