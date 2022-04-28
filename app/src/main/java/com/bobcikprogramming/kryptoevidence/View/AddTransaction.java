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

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class AddTransaction extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout btnBuy, btnSell, btnChange;
    private TextView tvBuy, tvSell, tvChange, tvCryptoName;
    private ImageView btnClose;

    private String longName, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        Bundle extras = getIntent().getExtras();
        longName = extras.getString("longName");
        id = extras.getString("id");

        setupUIViews();
        setStartItem();

        tvCryptoName.setText(longName);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Metoda pro inicializování prvků UI.
     */
    private void setupUIViews(){
        btnBuy = findViewById(R.id.tabButtonBuy);
        btnSell = findViewById(R.id.tabButtonSell);
        btnChange = findViewById(R.id.tabButtonChange);
        tvBuy = findViewById(R.id.tabTextViewBuy);
        tvSell = findViewById(R.id.tabTextViewSell);
        tvChange = findViewById(R.id.tabTextViewChange);

        tvCryptoName = findViewById(R.id.tvCryptoName);
        tvCryptoName.setSelected(true);
        btnClose = findViewById(R.id.btnCloseAdd);

        btnBuy.setOnClickListener(this);
        btnSell.setOnClickListener(this);
        btnChange.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    /**
     * Metoda zpracovávající reakci na kliknutí na daný prvek.
     * @param view Základní prvek UI komponent
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tabButtonBuy:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new AddTransactionTabBuy(id)).commit();
                resetColor();
                tvBuy.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.tabButtonSell:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new AddTransactionTabSell(id)).commit();
                resetColor();
                tvSell.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
                break;
            case R.id.tabButtonChange:
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, new AddTransactionTabChange(id)).commit();
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
     * Metoda pro ukončení aktivity. Je navrácena boolean hodnota uložena pod klíčem "close".
     * @param close pravdivostní hodnota, zda-li bylo okno uzavřeno pomocí tlačítka close
     */
    private void closeActivity(boolean close){
        Intent intent = new Intent();
        intent.putExtra("close", close);
        setResult(RESULT_OK, intent);
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
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, new AddTransactionTabBuy(id)).commit();
        tvBuy.setTextColor(ContextCompat.getColor(this, R.color.navBarSelect));
    }
}