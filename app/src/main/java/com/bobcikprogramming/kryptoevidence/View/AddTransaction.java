package com.bobcikprogramming.kryptoevidence.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.R;

public class AddTransaction extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout btnBuy, btnSell, btnChange;
    private TextView tvBuy, tvSell, tvChange, tvCryptoName;
    private ImageView btnClose;

    private String longName, shortName, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Bundle extras = getIntent().getExtras();
        longName = extras.getString("longName");
        shortName = extras.getString("shortName");
        id = extras.getString("id");

        setupUIViews();
        setStartItem();

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
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new AddTransactionTabBuy(shortName, longName)).commit();
                resetColor();
                tvBuy.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.tabButtonSell:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new AddTransactionTabSell(shortName, longName)).commit();
                resetColor();
                tvSell.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.tabButtonChange:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new AddTransactionTabChange(shortName, longName, id)).commit();
                resetColor();
                tvChange.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.btnCloseAdd:
                closeActivity(true);
                break;
        }
    }

    /**
     * Metoda vrátí uživatele zpět na výběr kryptoměny.
     */
    @Override
    public void onBackPressed() {
        closeActivity(false);
    }

    /**
     * Metoda pro vykonání událostí při uzavření okna
     * @param close pravdivostní hodnota, zda-li bylo okno uzavřeno pomocí tlačítka close
     */
    private void closeActivity(boolean close){
        Intent intent = new Intent();
        intent.putExtra("close", close);
        setResult(RESULT_OK, intent );
        finish();
    }

    /**
     * Metoda pro nastavení barev položek navbaru na původní hodnotu.
     */
    private void resetColor(){
        tvBuy.setTextColor(ContextCompat.getColor(this, R.color.white));
        tvSell.setTextColor(ContextCompat.getColor(this, R.color.white));
        tvChange.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    /**
     * Metoda pro vybrání úvodní položky navbaru při otevření okna.
     */
    private void setStartItem(){
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, new AddTransactionTabBuy(shortName, longName)).commit();
        tvBuy.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
    }
}