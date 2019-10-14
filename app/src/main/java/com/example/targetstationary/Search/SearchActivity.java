package com.example.targetstationary.Search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.SearchAdapter;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM =2;

    EditText search_edit_text;
    RecyclerView searchRecyclerView;
    DatabaseReference databaseReference;
    ArrayList <String> ImageList;
    ArrayList <String> NameList;
    ArrayList <String> PriceList;
    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: Started");
        setupBottomNavigationView();

        search_edit_text = (EditText) findViewById(R.id.search_edit_text);
        searchRecyclerView = (RecyclerView) findViewById(R.id.searchRecycler);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        ImageList = new ArrayList<>();
        NameList = new ArrayList<>();
        PriceList = new ArrayList<>();

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
                searchRecyclerView.removeAllViews();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String ProductID = snapshot.getKey();
                    String Image = snapshot.child("Image").getValue(String.class);
                    String Price = snapshot.child("Price").getValue(String.class);
                    String Name = snapshot.child("Name").getValue(String.class);
//                    Log.d(TAG, Name);

                    if(Name.toLowerCase().contains(searchedString.toLowerCase())){
                        ImageList.add(Image);
                        NameList.add(Name);
                        PriceList.add(Price);
                        counter++;

                    }else if (Price.toLowerCase().contains(searchedString.toLowerCase())){
                        ImageList.add(Image);
                        NameList.add(Name);
                        PriceList.add(Price);
                        counter++;
                    }
                    if(counter==15){
                        break;
                    }
                }

                searchAdapter = new SearchAdapter(SearchActivity.this, ImageList, NameList, PriceList);
                searchRecyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
