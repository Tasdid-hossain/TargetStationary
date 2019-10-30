package com.example.targetstationary.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.example.targetstationary.Cart.CartActivity;
import com.example.targetstationary.Category.PreSchool;
import com.example.targetstationary.Interface.ItemClickListener;
import com.example.targetstationary.Model.ImageListModel;
import com.example.targetstationary.Model.ProductModel;
import com.example.targetstationary.ProductListActivity;
import com.example.targetstationary.ProductDetails;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.MyPager;
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
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM =0;
    private static int currentPage = 0;
    private static int NUM_PAGES = 4;
    private ImageView preschoolImage, collegeuniImage, scecondaryschoolImage, primaryschoolImage;
    FirebaseRecyclerAdapter<ProductModel, ProductViewHolder> adapterProduct;
    Query productQuery;

    /*VIEWPAGER*/
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private MyPager myPager;

    /*For event data*/
    FirebaseDatabase database;
    DatabaseReference eventRef;
    DatabaseReference product;
    RecyclerView gridView;

    /*For UsefulInfo data*/
    DatabaseReference preschool, collegeuni, primaryschool, secondaryschool;
    public static ArrayList<ProductModel> productsPreschool = new ArrayList<ProductModel>();
    public static ArrayList<ProductModel> productsPrimary = new ArrayList<ProductModel>();
    public static ArrayList<ProductModel> productsSecondary = new ArrayList<ProductModel>();
    public static ArrayList<ProductModel> productsCollege = new ArrayList<ProductModel>();

    /*Commenting from Roderick's  PC*/
    Database localDB;
    private StaggeredGridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting");
        setupBottomNavigationView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        localDB = new Database(this);

        /*Firebase init*/
        database = FirebaseDatabase.getInstance();
        eventRef = database.getReference("Events");
        preschool = database.getReference().child("UsefulInfo").child("PreSchool");
        collegeuni = database.getReference().child("UsefulInfo").child("CollegeUni");
        primaryschool = database.getReference().child("UsefulInfo").child("PrimarySchool");
        secondaryschool = database.getReference().child("UsefulInfo").child("SecondarySchool");
        product = database.getReference().child("Product");
        productQuery = product;

        /*Init imageviews*/
        primaryschoolImage = (ImageView) findViewById(R.id.primaryschoolImage);
        collegeuniImage = (ImageView) findViewById(R.id.collegeuniImage);
        preschoolImage = (ImageView) findViewById(R.id.preSchoolImage);
        scecondaryschoolImage = (ImageView) findViewById(R.id.secondaryschoolImage);

        /*LOAD DATA*/
        loadProducts(preschool, productsPreschool);
        loadProducts(primaryschool, productsPrimary);
        loadProducts(secondaryschool, productsSecondary);
        loadProducts(collegeuni, productsCollege);

        /*IMGAGE CLICK LISTENER*/
        preschoolImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Goes into PreSchool*/
                Intent Preschool = new Intent(MainActivity.this, PreSchool.class);
                Preschool.putParcelableArrayListExtra("List", productsPreschool);

                startActivity(Preschool);
            }
        });

        primaryschoolImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Goes into PreSchool*/
                Intent Preschool = new Intent(MainActivity.this, PreSchool.class);
                Preschool.putParcelableArrayListExtra("List", productsPrimary);

                startActivity(Preschool);
            }
        });

        scecondaryschoolImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Goes into PreSchool*/
                Intent Preschool = new Intent(MainActivity.this, PreSchool.class);
                Preschool.putParcelableArrayListExtra("List", productsSecondary);

                startActivity(Preschool);
            }
        });

        collegeuniImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Goes into PreSchool*/
                Intent Preschool = new Intent(MainActivity.this, PreSchool.class);
                Preschool.putParcelableArrayListExtra("List", productsCollege);

                startActivity(Preschool);
            }
        });

        viewPager = findViewById(R.id.view_pagerMain);
        getDetailEvent();

        gridView = (RecyclerView) findViewById(R.id.stagger_main);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        loadProductGrid();
        gridView.setLayoutManager(mLayoutManager);
        gridView.setAdapter(adapterProduct);

    }

    private void loadProductGrid() {
        /*This is basically a query that will be included when creating the adapter. Matches the categoryID with the */
        FirebaseRecyclerOptions productOptions = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(productQuery.limitToFirst(6),
                ProductModel.class).build();

        /*The firebase recycler adapter. which takes the products depending on the category from the firebase.
         * It creates a recycler view and shows*/
        adapterProduct = new FirebaseRecyclerAdapter<ProductModel, ProductViewHolder>(productOptions) {


            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductModel model) {
                holder.textProductName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.imageViewProduct);
                holder.textProductPrice.setText(model.getPrice());
                if(localDB.isfavorites(adapterProduct.getRef(position).getKey()))
                    holder.favorite_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                holder.favorite_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isfavorites(adapterProduct.getRef(position).getKey()))
                        {
                            localDB.addtofavorites(adapterProduct.getRef(position).getKey());
                            holder.favorite_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                        }
                        else{
                            localDB.deletefromfavorites(adapterProduct.getRef(position).getKey());
                            holder.favorite_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        }

                    }
                });

                final ProductModel clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(MainActivity.this, ""+clickItem.getName(), Toast.LENGTH_SHORT).show();

                        /*Goes into Product Details*/
                        Intent prodDetails = new Intent(MainActivity.this, ProductDetails.class);
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

    /*Toolbar*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toptoolmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.ic_cartTop){
            Intent i = new Intent(MainActivity.this, CartActivity.class);
            startActivity(i);
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

    /*Load events*/
    private void getDetailEvent() {
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ImageListModel eventListModel = dataSnapshot.getValue(ImageListModel.class);
                myPager = new MyPager(getBaseContext());
                myPager.images.add(eventListModel.getImage1());
                myPager.images.add(eventListModel.getImage2());
                myPager.images.add(eventListModel.getImage3());
                myPager.images.add(eventListModel.getImage4());
                viewPager.setAdapter(myPager);
                circleIndicator = findViewById(R.id.circleMain);
                circleIndicator.setViewPager(viewPager);



                // Auto start of viewpager
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == NUM_PAGES) {
                            currentPage = 0;
                        }
                        viewPager.setCurrentItem(currentPage++, true);
                    }
                };
                Timer swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, 3000, 3000);

                // Pager listener over indicator
                circleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int position) {
                        currentPage = position;

                    }

                    @Override
                    public void onPageScrolled(int pos, float arg1, int arg2) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int pos) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadProducts(DatabaseReference useful, List<ProductModel> prod) {
        useful.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Iterable<DataSnapshot> productList = dataSnapshot.getChildren();

                for (DataSnapshot productDataSnap : productList) {
                    String productID = productDataSnap.getKey();
                    DatabaseReference prodRef = FirebaseDatabase.getInstance().getReference();

                    prodRef.child("Product").child(productID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            prod.add(dataSnapshot.getValue(ProductModel.class));
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
    }

    /*BottomNavigationView Setup*/
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(MainActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
