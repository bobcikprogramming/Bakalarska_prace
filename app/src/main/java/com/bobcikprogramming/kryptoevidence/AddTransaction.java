package com.bobcikprogramming.kryptoevidence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddTransaction extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout btnBuy, btnSell, btnChange;
    private TextView txBuy, txSell, txChange;
    private ImageView btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        setupUIViews();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabAddFragmentBuy()).commit();
        txBuy.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupUIViews(){
        btnBuy = findViewById(R.id.tabButtonBuy);
        btnSell = findViewById(R.id.tabButtonSell);
        btnChange = findViewById(R.id.tabButtonChange);
        txBuy = findViewById(R.id.tabTextViewBuy);
        txSell = findViewById(R.id.tabTextViewSell);
        txChange = findViewById(R.id.tabTextViewChange);
        btnClose = findViewById(R.id.btnCloseAdd);

        btnBuy.setOnClickListener(this);
        btnSell.setOnClickListener(this);
        btnChange.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tabButtonBuy:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabAddFragmentBuy()).commit();
                resetColor();
                txBuy.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.tabButtonSell:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabAddFragmentSell()).commit();
                resetColor();
                txSell.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.tabButtonChange:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabAddFragmentChange()).commit();
                resetColor();
                txChange.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.btnCloseAdd:
                finish();
                break;
        }
    }

    private void resetColor(){
        txBuy.setTextColor(ContextCompat.getColor(this, R.color.white));
        txSell.setTextColor(ContextCompat.getColor(this, R.color.white));
        txChange.setTextColor(ContextCompat.getColor(this, R.color.white));
    }
}