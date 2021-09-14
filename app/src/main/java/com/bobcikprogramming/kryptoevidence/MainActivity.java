package com.bobcikprogramming.kryptoevidence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavBar);

        FragmentOverView fragmentOverView = new FragmentOverView();
        FragmentAdd fragmentOverAdd = new FragmentAdd();
        FragmentPDF fragmentOverPDF = new FragmentPDF();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentOverView()).commit();
        selectBottomMenu(R.id.overview);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.overview:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentOverView).commit();
                        return true;
                    case R.id.add:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentOverAdd).commit();
                        return true;
                    case R.id.pdf:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentOverPDF).commit();
                        return true;
                }
                return false;
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