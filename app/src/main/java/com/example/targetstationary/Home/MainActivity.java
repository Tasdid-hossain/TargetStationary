package com.example.targetstationary.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.targetstationary.Category.PreSchool;
import com.example.targetstationary.Model.ImageListModel;
import com.example.targetstationary.Model.ProductModel;
import com.example.targetstationary.ProductActivity;
import com.example.targetstationary.ProductDetails;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.MyPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    /*VIEWPAGER*/
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private MyPager myPager;

    /*For event data*/
    FirebaseDatabase database;
    DatabaseReference eventRef;

    /*For UsefulInfo data*/
    DatabaseReference preschool, collegeuni, primaryschool, secondaryschool;
    public static ArrayList<ProductModel> productsPreschool = new ArrayList<ProductModel>();
    public static ArrayList<ProductModel> productsPrimary = new ArrayList<ProductModel>();
    public static ArrayList<ProductModel> productsSecondary = new ArrayList<ProductModel>();
    public static ArrayList<ProductModel> productsCollege = new ArrayList<ProductModel>();

    /*Commenting from Roderick's  PC*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting");
        setupBottomNavigationView();

        /*Firebase init*/
        database = FirebaseDatabase.getInstance();
        eventRef = database.getReference("Events");
        preschool = database.getReference().child("UsefulInfo").child("PreSchool");
        collegeuni = database.getReference().child("UsefulInfo").child("CollegeUni");
        primaryschool = database.getReference().child("UsefulInfo").child("PrimarySchool");
        secondaryschool = database.getReference().child("UsefulInfo").child("SecondarySchool");

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
