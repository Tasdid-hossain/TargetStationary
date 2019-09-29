package com.example.targetstationary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.targetstationary.Cart.CartActivity;
import com.example.targetstationary.Model.ImageListModel;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.Model.ProductModel;
import com.example.targetstationary.ViewHolder.MyPager;
import com.example.targetstationary.database.Database;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProductDetails extends AppCompatActivity {

    TextView singleProduct_name, singleProduct_price, singleProduct_description;
   // ImageView singleProduct_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton numberButton;
    Button cartBtn;
    ProductModel currentProduct;

    /*VIEWPAGER*/
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private MyPager myPager;

//    private ImageView singleProduct_image;!!!!!!!!!!!!!!!!!!!!!!

    ImageView imageView1, imageView2,imageView3,imageView4;
    public HashMap<String, String> file_maps ;

    String ProductID = "";

    FirebaseDatabase database;
    DatabaseReference singleProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        /*Firebase init*/
        database = FirebaseDatabase.getInstance();
        singleProduct = database.getReference("Product");

        /*View init*/
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        singleProduct_description = (TextView) findViewById(R.id.singleProduct_description);
        singleProduct_name = (TextView) findViewById(R.id.singleProduct_name);
        singleProduct_price = (TextView) findViewById(R.id.singleProduct_price);
        cartBtn =(Button) findViewById(R.id.btnCart);


//        singleProduct_image = (ImageView) findViewById(R.id.image_singleProduct);!!!!!!!!!!!!!!!!!!

      /*  imageView1=(ImageView) findViewById(R.id.imageview1);
        imageView2=(ImageView) findViewById(R.id.imageview2);
        imageView3=(ImageView) findViewById(R.id.imageview3);
        imageView4=(ImageView) findViewById(R.id.imageview4);*/

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedAppbar);

        /**/

        /*Get ProductID from intent*/
        if(getIntent()!= null)
            ProductID = getIntent().getStringExtra("ProductID");
        if(!ProductID.isEmpty())
        {
            getDetailProduct(ProductID);
        }

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new OrderModel(
                        ProductID, currentProduct.getName(),  currentProduct.getPrice(), "10"
                ));

                Toast.makeText(ProductDetails.this, "Added to cart", Toast.LENGTH_SHORT).show();
                Intent prodDetails = new Intent(ProductDetails.this, CartActivity.class);
                startActivity(prodDetails);
            }
        });
    }


    private void getDetailProduct(String productID) {
        singleProduct.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentProduct = dataSnapshot.getValue(ProductModel.class);
                ImageListModel imageListModel = dataSnapshot.child("imageList").getValue(ImageListModel.class);

                /*Set Image*/
/*                Picasso.get().load(imageListModel.getImage1()).into(imageView1);
                Picasso.get().load(imageListModel.getImage2()).into(imageView2);
                Picasso.get().load(imageListModel.getImage3()).into(imageView3);
                Picasso.get().load(imageListModel.getImage4()).into(imageView4);*/

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
}
