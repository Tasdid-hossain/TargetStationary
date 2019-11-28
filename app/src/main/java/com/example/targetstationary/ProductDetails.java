package com.example.targetstationary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.targetstationary.Cart.CartActivity;
import com.example.targetstationary.Model.ImageListModel;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.Model.ProductModel;
import com.example.targetstationary.Model.Rating;
import com.example.targetstationary.ViewHolder.CommentAdapter;
import com.example.targetstationary.ViewHolder.MyPager;
import com.example.targetstationary.database.Database;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ProductDetails extends AppCompatActivity implements RatingDialogListener {

    TextView singleProduct_name, singleProduct_price, singleProduct_description;
   // ImageView singleProduct_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton numberButton;
    Button cartBtn,feedbackBtn;
    ProductModel currentProduct;
    FloatingActionButton btnRating;
    RatingBar ratingBar;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    CommentAdapter commentAdapter;
    RecyclerView recyclerView_comment;
    RecyclerView.LayoutManager layoutManager;

    TextView textCartItemCount;
    int mCartItemCount = 10;
    Database localDB;

    /*VIEWPAGER*/
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private MyPager myPager;

    ArrayList<String> commentList;
    String ProductID = "";

    FirebaseDatabase database;
    DatabaseReference singleProduct;
    DatabaseReference ratingTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        /*Toolbars*/
        /*Toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Product Details");
        commentList = new ArrayList<>();
        localDB = new Database(this);


        recyclerView_comment = (RecyclerView) findViewById(R.id.recycler_comments_details);
        recyclerView_comment.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_comment.setLayoutManager(layoutManager);

        /*Firebase init*/
        database = FirebaseDatabase.getInstance();
        singleProduct = database.getReference("Product");
        ratingTable = database.getReference("Rating");

        feedbackBtn = (Button) findViewById(R.id.btnFeedback);
        btnRating = (FloatingActionButton) findViewById(R.id.btn_rating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser!=null)
                    showRatingDialog();
                else
                    Toast.makeText(ProductDetails.this, "Please login to rate",Toast.LENGTH_SHORT).show();
            }
        });

        /*View init*/
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        singleProduct_description = (TextView) findViewById(R.id.singleProduct_description);
        singleProduct_name = (TextView) findViewById(R.id.singleProduct_name);
        singleProduct_price = (TextView) findViewById(R.id.singleProduct_price);
        cartBtn =(Button) findViewById(R.id.btnCart);



        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedAppbar);


        /*Get ProductID from intent*/
        if(getIntent()!= null)
            ProductID = getIntent().getStringExtra("ProductID");
        if(!ProductID.isEmpty())
        {
            getDetailProduct(ProductID);
            loadRating(ProductID);
        }

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentList!=null){
                    /*Intent i = new Intent(ProductDetails.this, CommentActivity.class);
                    i.putStringArrayListExtra("commentList",commentList);
                    startActivity(i);*/
                    loadComments();
                }
                else{
                    Toast.makeText(ProductDetails.this, "There's no feedback", Toast.LENGTH_SHORT).show();
                }

            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Database(getBaseContext()).isAddedToCart(ProductID)){
                    Toast.makeText(ProductDetails.this, "Already in cart", Toast.LENGTH_SHORT).show();
                }
                else{
                    new Database(getBaseContext()).addToCart(new OrderModel(
                            ProductID, currentProduct.getName(), numberButton.getNumber() ,currentProduct.getPrice(), "10"
                    ));
                    //Toast.makeText(ProductDetails.this, commentList.get(0),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ProductDetails.this, "Added to cart", Toast.LENGTH_SHORT).show();
                    Intent prodDetails = new Intent(ProductDetails.this, CartActivity.class);
                    startActivity(prodDetails);

                }
            }
        });

    }

    private void loadComments() {
        commentAdapter = new CommentAdapter(commentList, this);
        commentAdapter.notifyDataSetChanged();
        recyclerView_comment.setAdapter(commentAdapter);
    }

    private void loadRating(String productID) {
        Query prodrate = ratingTable.orderByChild("productID").equalTo(productID);
        prodrate.addValueEventListener(new ValueEventListener() {
            int count=0, sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Rating item = postSnapShot.getValue(Rating.class);
                    commentList.add(item.getComment());
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if(count!=0){
                    float average = sum/count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Poor", "Bad", "Okay", "Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this item")
                .setDescription("Rate the item by giving stars")
                .setHint("Write comment here")
                .setCommentBackgroundColor(R.color.com_facebook_button_background_color)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(ProductDetails.this).show();
    }

    private void getDetailProduct(String productID) {
        singleProduct.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentProduct = dataSnapshot.getValue(ProductModel.class);
                ImageListModel imageListModel = dataSnapshot.child("imageList").getValue(ImageListModel.class);

                myPager = new MyPager(getBaseContext());
                myPager.images.add(imageListModel.getImage1());
                myPager.images.add(imageListModel.getImage2());
                myPager.images.add(imageListModel.getImage3());
                myPager.images.add(imageListModel.getImage4());
                viewPager = findViewById(R.id.view_pager);
                viewPager.setAdapter(myPager);
                circleIndicator = findViewById(R.id.circle);
                circleIndicator.setViewPager(viewPager);


                collapsingToolbarLayout.setTitle(currentProduct.getName());
                singleProduct_price.setText(currentProduct.getPrice());
                singleProduct_name.setText(currentProduct.getName());
                singleProduct_description.setText(currentProduct.getDescription());
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

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
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
            Intent i = new Intent(ProductDetails.this, CartActivity.class);
            startActivity(i);
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        Rating rating = new Rating(currentUser.getUid(), ProductID, String.valueOf(i), s);
        ratingTable.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(currentUser.getUid()).exists()){
                    ratingTable.child(currentUser.getUid()).removeValue();
                    ratingTable.child(currentUser.getUid()).setValue(rating);
                }
                else{
                    ratingTable.child(currentUser.getUid()).setValue(rating);
                }

                Toast.makeText(ProductDetails.this, "Thank you for rating!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
