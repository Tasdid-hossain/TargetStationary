package com.example.targetstationary.Account;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.targetstationary.Category.CategoryActivity;
import com.example.targetstationary.CircleTransform;
import com.example.targetstationary.Home.MainActivity;
import com.example.targetstationary.Model.UserModel;
import com.example.targetstationary.OrderActivity;
import com.example.targetstationary.ProductListActivity;
import com.example.targetstationary.ProductDetails;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.database.Database;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.targetstationary.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.CALL_PHONE;

public class SignIn extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 1971;
    List<AuthUI.IdpConfig> providers;
    Button btn_sign_out,update,btn_vieworder, btn_call;
    TextView fName, total_orders, user_email,  user_address, user_phone, total_favorites, user_type;
    ImageView profile_pic;
    private static final String TAG = "AccountActivity";
    private static final int ACTIVITY_NUM =4;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;


    DatabaseReference database;
    Database localDB;
    DatabaseReference userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Log.d(TAG, "onCreate: Started");
        setupBottomNavigationView();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Orders");
        userData = FirebaseDatabase.getInstance().getReference("Users");
        profile_pic = (ImageView) findViewById(R.id.profile_pic);


        localDB = new Database(this);
        fName = (TextView) findViewById(R.id.first_name);
        user_email = (TextView) findViewById(R.id.user_email);
        user_phone = (TextView) findViewById(R.id.user_phone);
        user_address = (TextView) findViewById(R.id.user_address);
        total_orders = (TextView) findViewById(R.id.total_orders);
        btn_sign_out = (Button) findViewById(R.id.btn_sign_out);
        btn_vieworder = (Button) findViewById(R.id.btn_viewOrders);
        total_favorites = (TextView) findViewById(R.id.total_favorites);
        user_type = (TextView) findViewById(R.id.user_type);
        btn_call = (Button) findViewById(R.id.btn_call);

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:082242358"));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{CALL_PHONE}, 1);
                    }
                }
            }
        });

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(SignIn.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                SQLiteDatabase db = localDB.getReadableDatabase();
                                db.execSQL("delete from OrderDetails");
                                btn_sign_out.setEnabled(false);
                                showSignInOptions();
                                Intent i = new Intent(SignIn.this, MainActivity.class);
                                startActivity(i);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignIn.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        update = (Button) findViewById(R.id.btn_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this, UpdateAccount.class);
                startActivity(i);
            }
        });

        btn_vieworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this, OrderActivity.class);
                startActivity(i);
            }
        });


        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        check();
    }

    private void check() {
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
            showSignInOptions();
        }
        else
        {
            fName.setText(currentUser.getDisplayName());
            user_email.setText(currentUser.getEmail());
            /*if(currentUser.getPhoneNumber().equals("")){
                user_phone.setText("Please update phone number!");
            }else{
                user_phone.setText(currentUser.getPhoneNumber());
            }*/
            loadData();

            btn_sign_out.setEnabled(true);
            database.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int size = (int) dataSnapshot.getChildrenCount();
                    total_orders.setText(String.valueOf(size));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.targetlogo).setTosAndPrivacyPolicyUrls("https://target.com.my/terms-and-conditions.php"
                ,"https://target.com.my/privacy-policy.php")
                .build(),MY_REQUEST_CODE
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode==RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, ""+user.getEmail(),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SignIn.this, MainActivity.class);
                startActivity(i);
                finish();
                btn_sign_out.setEnabled(true);

            }
            else{
                if(response==null){
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(SignIn.this, MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SignIn.this, MainActivity.class);
        startActivity(i);
    }

    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + "Favorites";
        SQLiteDatabase db = localDB.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private void loadData(){
        userData.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if(userModel!=null){
                    user_address.setText(userModel.getAddress());
                    user_phone.setText(userModel.getPhone());
                    if(currentUser.getPhotoUrl()!=null)
                        Picasso.get().load(currentUser.getPhotoUrl().toString()).into(profile_pic);
                    total_favorites.setText(String.valueOf(getItemsCount()));
                    user_type.setText(userModel.getUserType());
                }
                else{
                    user_address.setText("Please update address!");
                    user_phone.setText("Please update phone number!");
                    //total_favorites.setText("No Favorite");
                    user_type.setText("Update your type");
                    //Picasso.get().load(currentUser.getPhotoUrl().toString()).into(profile_pic);
                    if(currentUser.getPhotoUrl()!=null)
                        Picasso.get().load(currentUser.getPhotoUrl().toString()).transform(new CircleTransform()).into(profile_pic);
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
        BottomNavigationViewHelper.enableNavigation(SignIn.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
