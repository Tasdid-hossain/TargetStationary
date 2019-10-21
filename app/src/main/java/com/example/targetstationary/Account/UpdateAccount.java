package com.example.targetstationary.Account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.targetstationary.Model.UserModel;
import com.example.targetstationary.R;
import com.example.targetstationary.Utils.BottomNavigationViewHelper;
import com.example.targetstationary.database.Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateAccount extends AppCompatActivity {

    private static final String TAG = "AccountActivity";
    private static final int ACTIVITY_NUM =4;
    EditText edit_name, edit_email,  edit_address, edit_payment, edit_phone;
    Button btn_save, btn_cancel;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_address = (EditText) findViewById(R.id.edit_address);
        edit_payment = (EditText) findViewById(R.id.edit_payment);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                Intent i = new Intent(UpdateAccount.this, SignIn.class);
                startActivity(i);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateAccount.this, SignIn.class);
                startActivity(i);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference("Users");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        loadData();
    }

    private void loadData(){
        edit_name.setText(currentUser.getDisplayName());
        edit_email.setText(currentUser.getEmail());
        String address, payment;
        myref.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                if(userModel!=null){
                    edit_address.setText(userModel.getAddress());
                }
                else
                    edit_address.setText("Please enter address");

                if(userModel!=null)
                    edit_payment.setText(userModel.getPayment());
                else
                    edit_payment.setText("Please enter payment method");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveData(){
        String name = edit_name.getText().toString();
        String address = edit_address.getText().toString();
        String payment = edit_payment.getText().toString();
        String phone = edit_phone.getText().toString();
        String email = edit_email.getText().toString();

        UserModel model = new UserModel(name, phone,email,address,payment);

        myref.child(currentUser.getUid()).setValue(model);
        finish();
    }

    /*BottomNavigationView Setup*/
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(UpdateAccount.this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
