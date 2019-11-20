package com.example.targetstationary.Category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.targetstationary.Home.MainActivity;
import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.Model.ProductModel;
import com.example.targetstationary.OrderActivity;
import com.example.targetstationary.OrderDetailsActivity;
import com.example.targetstationary.ProductDetails;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.CartAdapter;
import com.example.targetstationary.ViewHolder.ProductViewHolder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PreSchool extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference preschool;
    Query preschoolQuery;
    ArrayList<ProductModel> products = new ArrayList<ProductModel>();

    RecyclerView recyclerViewPreSchool;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ProductModel> object;
    ArrayList<String> prodIds;
    ProdAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_school);

        database = FirebaseDatabase.getInstance();
        preschool = database.getReference().child("UsefulInfo").child("PreSchool");
        preschoolQuery = preschool.orderByKey();

        recyclerViewPreSchool = (RecyclerView) findViewById(R.id.preschoolRecycler);
        recyclerViewPreSchool.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewPreSchool.setLayoutManager(layoutManager);


        Intent intent = getIntent();
        if(object==null){
            object = intent.getExtras().getParcelableArrayList("List");
            prodIds = intent.getStringArrayListExtra("IDList");
        }


        createView(object);
    }

    private void createView(ArrayList<ProductModel> p) {
        adapter = new ProdAdapter(p, this);
        adapter.notifyDataSetChanged();

        recyclerViewPreSchool.setAdapter(adapter);
    }

/*    private void loadProducts() {
        preschool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Iterable<DataSnapshot> productList = dataSnapshot.getChildren();

                for (DataSnapshot productDataSnap : productList) {
                    String productID = productDataSnap.getKey();
                    DatabaseReference prodRef = FirebaseDatabase.getInstance().getReference();

                    prodRef.child("Product").child(productID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            products.add(dataSnapshot.getValue(ProductModel.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    public class ProdAdapter extends RecyclerView.Adapter<ProductViewHolder> {

        private List<ProductModel> listData = new ArrayList<>();
        private Context context;

        public ProdAdapter(List<ProductModel> listData, Context context) {
            this.listData = listData;
            this.context = context;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.product_item, parent, false);
            return new ProductViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            holder.textProductName.setText(listData.get(position).getName());
            Picasso.get().load(listData.get(position).getImage()).into(holder.imageViewProduct);
            holder.textProductPrice.setText(listData.get(position).getPrice());

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                   Intent i = new Intent(PreSchool.this, ProductDetails.class);
                   i.putExtra("ProductID", prodIds.get(position));
                   startActivity(i);
                }
            });
        }


        @Override
        public int getItemCount() {
            return listData.size();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        object.clear();
    }
}
