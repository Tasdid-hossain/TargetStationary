package com.example.targetstationary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.targetstationary.Model.ProductModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductDetails extends AppCompatActivity {

    TextView singleProduct_name, singleProduct_price, singleProduct_description;
    ImageView singleProduct_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton numberButton;

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
        singleProduct_image = (ImageView) findViewById(R.id.image_singleProduct);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpendedAppbar);

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

                /*Set Image*/
                Picasso.get().load(productModel.getImage()).into(singleProduct_image);
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
