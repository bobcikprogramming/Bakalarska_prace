package com.bobcikprogramming.kryptoevidence.Controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.bobcikprogramming.kryptoevidence.R;
import com.bobcikprogramming.kryptoevidence.View.FragmentOverView;
import com.bobcikprogramming.kryptoevidence.View.FragmentPDF;
import com.bobcikprogramming.kryptoevidence.View.FragmentTransactions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentOverView fragmentOverView;
    private FragmentTransactions fragmentTransactions;
    private FragmentPDF fragmentPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fragmentOverView = new FragmentOverView();
        fragmentTransactions = new FragmentTransactions();
        fragmentPDF = new FragmentPDF();

        bottomNavigationView = findViewById(R.id.bottomNavBar);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentOverView()).commit();
        selectBottomMenu(R.id.overview);

        selectFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void selectFragment(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.overview:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentOverView).commit();
                        break;
                    case R.id.transactions:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentTransactions, "test").commit();
                        break;
                    case R.id.pdf:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentPDF).commit();
                        break;
                }
                return true;
            }
        });
    }

    public void selectBottomMenu(final int position) {
        Handler uiHandler = new Handler();
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                bottomNavigationView.setSelectedItemId(position);
            }
        });
    }
}