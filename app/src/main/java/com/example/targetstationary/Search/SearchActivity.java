package com.example.targetstationary.Search;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.targetstationary.Cart.CartActivity;
import com.example.targetstationary.Home.MainActivity;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.SearchAdapter;
import com.example.targetstationary.database.Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM =2;
    TextView textCartItemCount;
    Database localDB;

    EditText search_edit_text;
    RecyclerView searchRecyclerView;
    DatabaseReference databaseReference;
    ArrayList <String> ImageList;
    ArrayList <String> NameList;
    ArrayList <String> PriceList;
    ArrayList <String> idList;
    ArrayList <String> descList;
    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: Started");
        setupBottomNavigationView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        localDB = new Database(this);


        search_edit_text = (EditText) findViewById(R.id.search_edit_text);
        searchRecyclerView = (RecyclerView) findViewById(R.id.searchRecycler);
        localDB = new Database(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        ImageList = new ArrayList<>();
        NameList = new ArrayList<>();
        PriceList = new ArrayList<>();
        idList = new ArrayList<>();
        descList = new ArrayList<>();

        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    setAdapter(s.toString());
                }else{
                    ImageList.clear();
                    NameList.clear();
                    PriceList.clear();
                    idList.clear();
                    descList.clear();
                    searchRecyclerView.removeAllViews();
                }
            }
        });
    }

    private void setAdapter(final String searchedString) {


        databaseReference.child("Product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int counter = 0;
                ImageList.clear();
                NameList.clear();
                PriceList.clear();
                idList.clear();
                descList.clear();
                searchRecyclerView.removeAllViews();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String ProductID = snapshot.getKey().toString();
                    String Image = snapshot.child("Image").getValue(String.class);
                    String Price = snapshot.child("Price").getValue(String.class);
                    String Name = snapshot.child("Name").getValue(String.class);
                    String Description = snapshot.child("Description").getValue(String.class);
//                    Log.d(TAG, Name);

                    if(Name.toLowerCase().contains(searchedString.toLowerCase())){
                        ImageList.add(Image);
                        NameList.add(Name);
                        PriceList.add(Price);
                        idList.add(ProductID);
                        descList.add(Description);
                        counter++;

                    }else if (Price.toLowerCase().contains(searchedString.toLowerCase())){
                        ImageList.add(Image);
                        NameList.add(Name);
                        PriceList.add(Price);
                        idList.add(ProductID);
                        descList.add(Description);
                        counter++;
                    }else if (ProductID.toLowerCase().contains(searchedString.toLowerCase())){
                        ImageList.add(Image);
                        NameList.add(Name);
                        PriceList.add(Price);
                        idList.add(ProductID);
                        descList.add(Description);
                        counter++;
                    }
                    if(counter==15){
                        break;
                    }
                }

                searchAdapter = new SearchAdapter(SearchActivity.this, ImageList, NameList, PriceList,idList,descList);
                searchRecyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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
            Intent i = new Intent(SearchActivity.this, CartActivity.class);
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
        BottomNavigationViewHelper.enableNavigation(SearchActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
