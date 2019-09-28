package com.example.targetstationary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.targetstationary.Model.ImageListModel;
import com.example.targetstationary.Model.ProductModel;
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
    /*Creating slider*/
    ImageListModel imageListModel;
    private ImageView singleProduct_image;
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
//        singleProduct_image = (ImageView) findViewById(R.id.image_singleProduct);
        imageView1=(ImageView) findViewById(R.id.imageview1);
        imageView2=(ImageView) findViewById(R.id.imageview2);
        imageView3=(ImageView) findViewById(R.id.imageview3);
        imageView4=(ImageView) findViewById(R.id.imageview4);

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
    }


    private void getDetailProduct(String productID) {
        singleProduct.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                ImageListModel imageListModel = dataSnapshot.child("imageList").getValue(ImageListModel.class);

                /*Set Image*/
                Picasso.get().load(imageListModel.getImage1()).into(imageView1);
                Picasso.get().load(imageListModel.getImage2()).into(imageView2);
                Picasso.get().load(imageListModel.getImage3()).into(imageView3);
                Picasso.get().load(imageListModel.getImage4()).into(imageView4);


                collapsingToolbarLayout.setTitle(productModel.getName());
                singleProduct_price.setText(productModel.getPrice());
                singleProduct_name.setText(productModel.getName());
                singleProduct_description.setText(productModel.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
