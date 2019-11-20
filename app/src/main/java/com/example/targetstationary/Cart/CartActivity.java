package com.example.targetstationary.Cart;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.targetstationary.Account.SignIn;
import com.example.targetstationary.Model.OrderModel;
import com.example.targetstationary.Model.Request;
import com.example.targetstationary.ProductDetails;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.ViewHolder.CartAdapter;
import com.example.targetstationary.database.Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private static final int ACTIVITY_NUM =3;
    RecyclerView recyclerViewCart;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    DatabaseReference userAddress;

    public TextView totalPrice;
    ArrayList <OrderModel> cart = new ArrayList<>();
    CartAdapter adapter;
    FloatingActionButton place_order;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String uAddress;

    /*Add payment*/
    private static final int REQUEST_CODE = 1234;
    String API_GET_TOKEN="https://targetstationary.000webhostapp.com/main.php";
    String API_CHECKOUT="https://targetstationary.000webhostapp.com/checkout.php";
    HashMap<String,String> paramsHash;
    String token,amount;
    int paymentType=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Log.d(TAG, "onCreate: Started");
        setupBottomNavigationView();

        /*Toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.tabs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Cart");

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Orders");
        userAddress  = database.getReference("Users");

        recyclerViewCart = (RecyclerView) findViewById(R.id.cartRecycler);
        recyclerViewCart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCart.setLayoutManager(layoutManager);
        totalPrice = (TextView) findViewById(R.id.total);

        mAuth = FirebaseAuth.getInstance();
        place_order= (FloatingActionButton) findViewById(R.id.place_order);
        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser==null){
                    Toast.makeText(CartActivity.this, "Please login to place the order", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(CartActivity.this, SignIn.class);
                    startActivity(i);
                }

                else
                {
                    getAddress();
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setTitle("Purchase type");
                    builder.setMessage("How would you like to receive your product?");
                    builder.setPositiveButton("Pickup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //pickingUp();
                            paymentType=2;
                            submitPayment();
                            dialog.cancel();
                        }
                    });
                    builder.setNeutralButton("Delivery", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //delivering();
                            paymentType=1;
                            submitPayment();
                            dialog.cancel();

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    /**/
                }

            }
        });
        new CartActivity.getToken().execute();
        loadListProduct();

    }
    private void submitPayment(){
        String payValue=totalPrice.getText().toString();
        if(!payValue.isEmpty())
        {
            DropInRequest dropInRequest=new DropInRequest().clientToken(token);
            startActivityForResult(dropInRequest.getIntent(this),REQUEST_CODE);
        }
        else
            Toast.makeText(this, "Enter a valid amount for payment", Toast.LENGTH_SHORT).show();

    }

    private void sendPayments(){
        RequestQueue queue= Volley.newRequestQueue(CartActivity.this);
        StringRequest stringRequest=new StringRequest(com.android.volley.Request.Method.POST, API_CHECKOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.toString().contains("Successful")){
                            Toast.makeText(CartActivity.this, "Payment Success", Toast.LENGTH_SHORT).show();
                            if(paymentType==1){
                                delivering();
                            }
                            else if(paymentType==2){
                                pickingUp();
                            }

                        }
                        else {
                            Toast.makeText(CartActivity.this, "Payment Failed", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Response",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Err1",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(paramsHash==null)
                    return null;
                Map<String,String> params=new HashMap<>();
                for(String key:paramsHash.keySet())
                {
                    params.put(key,paramsHash.get(key));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("Content-type","application/x-www-form-urlencoded");
                return params;
            }
        };
        RetryPolicy mRetryPolicy=new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(mRetryPolicy);
        queue.add(stringRequest);
    }

    private class getToken extends AsyncTask {
        ProgressDialog mDailog;

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client=new HttpClient();
            client.get(API_GET_TOKEN, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    mDailog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            token=responseBody;
                        }
                    });
                }

                @Override
                public void failure(Exception exception) {
                    mDailog.dismiss();
                    Log.d("Err2",exception.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDailog=new ProgressDialog(CartActivity.this,android.R.style.Theme_DeviceDefault_Light_Dialog);
            mDailog.setCancelable(false);
            mDailog.setMessage("Loading Wallet, Please Wait");
            mDailog.show();
        }

        @Override
        protected void onPostExecute(Object o){
            super.onPostExecute(o);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        if(requestCode== REQUEST_CODE){
            if(resultCode==RESULT_OK)
            {
                DropInResult result=data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce= result.getPaymentMethodNonce();
                String strNounce=nonce.getNonce();
                if(!totalPrice.getText().toString().isEmpty())
                {
                    amount=totalPrice.getText().toString();
                    paramsHash=new HashMap<>();
                    paramsHash.put("amount",amount);
                    paramsHash.put("nonce",strNounce);

                    sendPayments();
                }
                else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this, "User canceled", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Exception error=(Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("Err4",error.toString());
            }
        }
    }

    private void delivering() {
        Request request = new Request(
                uAddress.replace("\n",""),
                totalPrice.getText().toString(),
                currentUser.getDisplayName(),
                "Order received for delivery",
                cart
        );
        Toast.makeText(CartActivity.this, "Your order has been placed for delivery!", Toast.LENGTH_SHORT).show();

        requests.child(currentUser.getUid()).child(String.valueOf(Calendar.getInstance().getTime())).setValue(request);
        new Database(getBaseContext()).cleanCart();
        finish();
    }

    private void pickingUp() {
        Request request = new Request(
                uAddress.replace("\n",""),
                totalPrice.getText().toString(),
                currentUser.getDisplayName(),
                "Order received for pickup",
                cart
        );
        Toast.makeText(CartActivity.this, "Your order has been placed for pickup!", Toast.LENGTH_SHORT).show();

        requests.child(currentUser.getUid()).child(String.valueOf(Calendar.getInstance().getTime())).setValue(request);
        new Database(getBaseContext()).cleanCart();
        finish();
                            /*String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 1.541154, 110.315228);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(intent);*/

        String my_data= String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=1.541154,110.315228(My Destination Place)");

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(my_data));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void getAddress(){
        userAddress.child(currentUser.getUid()).child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                    uAddress = dataSnapshot.getValue().toString();
                else
                    uAddress="no address";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    };

    /*BottomNavigationView Setup*/
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(CartActivity.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(false);
    }

    private void loadListProduct(){
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();

        recyclerViewCart.setAdapter(adapter);

        int total= 0;
        for (OrderModel order:cart){

            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }
        /*Locale locale = new Locale("en", "us");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);*/
        totalPrice.setText(String.valueOf(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Delete"))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position){
        cart.remove(position);
        new Database(this).cleanCart();
        for(OrderModel item:cart){
            new Database(this).addToCart(item);
        }
        loadListProduct();
    }

    /*Check auth status*/
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
