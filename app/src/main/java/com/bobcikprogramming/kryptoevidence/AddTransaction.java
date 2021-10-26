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
    private TextView tvBuy, tvSell, tvChange, tvCryptoName;
    private ImageView btnClose;

    private String longName, shortName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Bundle extras = getIntent().getExtras();
        this.longName = extras.getString("longName");
        this.shortName = extras.getString("shortName");

        setupUIViews();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabAddFragmentBuy(shortName, longName)).commit();
        tvBuy.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));

        tvCryptoName.setText(longName);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupUIViews(){
        btnBuy = findViewById(R.id.tabButtonBuy);
        btnSell = findViewById(R.id.tabButtonSell);
        btnChange = findViewById(R.id.tabButtonChange);
        tvBuy = findViewById(R.id.tabTextViewBuy);
        tvSell = findViewById(R.id.tabTextViewSell);
        tvChange = findViewById(R.id.tabTextViewChange);

        tvCryptoName = findViewById(R.id.tvCryptoName);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabAddFragmentBuy(shortName, longName)).commit();
                resetColor();
                tvBuy.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.tabButtonSell:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabAddFragmentSell(shortName, longName)).commit();
                resetColor();
                tvSell.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.tabButtonChange:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new TabAddFragmentChange(shortName, longName)).commit();
                resetColor();
                tvChange.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.btnCloseAdd:
                Intent intent = new Intent();
                intent.putExtra("close", true);
                intent.putExtra("change", false);
                setResult(RESULT_OK, intent );
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("close", false);
        intent.putExtra("change", false);
        setResult(RESULT_OK, intent );
        finish();
    }

    private void resetColor(){
        tvBuy.setTextColor(ContextCompat.getColor(this, R.color.white));
        tvSell.setTextColor(ContextCompat.getColor(this, R.color.white));
        tvChange.setTextColor(ContextCompat.getColor(this, R.color.white));
    }
}