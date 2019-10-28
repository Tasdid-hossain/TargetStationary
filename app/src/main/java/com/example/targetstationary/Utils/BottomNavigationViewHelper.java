package com.example.targetstationary.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.example.targetstationary.Account.AccountActivity;
import com.example.targetstationary.Account.SignIn;
import com.example.targetstationary.Cart.CartActivity;
import com.example.targetstationary.Category.CategoryActivity;
import com.example.targetstationary.Home.MainActivity;
import com.example.targetstationary.OrderActivity;
import com.example.targetstationary.R;
import com.example.targetstationary.Search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHelper";

    public static void enableNavigation (final Context context, BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.ic_home:
                        Intent intent1 = new Intent(context, MainActivity.class); //ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        break;
                    case R.id.ic_product:
                        Intent intent2 = new Intent(context, CategoryActivity.class); //ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        break;
                    case R.id.ic_search:
                        Intent intent3 = new Intent(context, SearchActivity.class); //ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        break;
                    case R.id.ic_cart:
                        Intent intent4 = new Intent(context, OrderActivity.class); //ACTIVITY_NUM = 3
                        context.startActivity(intent4);
                        break;
                    case R.id.ic_account:
                        Intent intent5 = new Intent(context, SignIn.class); //ACTIVITY_NUM = 4
                        context.startActivity(intent5);
                        break;

                }

                return false;
            }
        });
    }
}
