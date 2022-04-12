package com.bobcikprogramming.kryptoevidence.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bobcikprogramming.kryptoevidence.Controller.CryptoSelectionController;
import com.bobcikprogramming.kryptoevidence.Controller.SharedMethods;
import com.bobcikprogramming.kryptoevidence.R;

public class CryptoSelection extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private EditText etSearch;
    private LinearLayout layout;
    private ImageView imgBtnDelete, imgBtnCloseCryptoSelection;

    private RecyclerViewSelection adapter;

    private SharedMethods shared;
    private CryptoSelectionController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_selection);

        shared = new SharedMethods();
        controller = new CryptoSelectionController(this);

        setupUIViews();
        searchOnChange();

        adapter = new RecyclerViewSelection(CryptoSelection.this, controller.getCryptoList(), myClickListener);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Metoda zpracovávající reakci na kliknutí na daný prvek
     * @param view Základní prvek UI komponent
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.layoutSelection:
                shared.hideKeyBoard(this);
                break;
            case R.id.imgBtnCloseCryptoSelection:
                closeActivity(false);
            case R.id.imgBtnDelete:
                etSearch.setText("");
        }
    }

    /**
     * Metoda reagující na kliknutí na nativní android tlačítko zpět a uzavírající activity
     */
    @Override
    public void onBackPressed() {
        closeActivity(false);
    }

    /**
     * Metoda pro inicializování prvků UI
     */
    private void setupUIViews(){
        recyclerView = findViewById(R.id.recyclerViewCryptoSelection);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        imgBtnDelete = findViewById(R.id.imgBtnDelete);
        imgBtnCloseCryptoSelection = findViewById(R.id.imgBtnCloseCryptoSelection);
        etSearch = findViewById(R.id.etSearch);
        layout = findViewById(R.id.layoutSelection);

        imgBtnDelete.setOnClickListener(this);
        imgBtnCloseCryptoSelection.setOnClickListener(this);
        etSearch.setOnClickListener(this);
        layout.setOnClickListener(this);
    }

    /**
     * Metoda pro ukončení activity
     */
    private void closeActivity(boolean changed){
        Intent intent = new Intent();
        intent.putExtra("changed", changed);
        setResult(RESULT_OK, intent );
        finish();
    }

    /**
     * Metoda k aktualizování pole seznamu kryptoměn na základě fráze v edit textu
     */
    private void searchOnChange(){
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searching = charSequence.toString();
                adapter = new RecyclerViewSelection(CryptoSelection.this, controller.filter(searching), myClickListener);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * Metoda slouží k předání ID vybrané kryptoměny z recyclerview do activity, jenž inicializovala aktuální activity
     */
    private View.OnClickListener myClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = (int) view.getTag();
            Intent addActivity = new Intent(CryptoSelection.this, AddTransaction.class);
            addActivity.putExtra("longName", controller.getCryptoList().get(position).longName);
            addActivity.putExtra("id", controller.getCryptoList().get(position).uid);
            addActivityResultLauncher.launch(addActivity);
        }
    };

    /**
     * Metoda zpracující návrat z aktivity
     */
    ActivityResultLauncher<Intent> addActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();
                    boolean close = data.getBooleanExtra("close", false);
                    boolean changed = data.getBooleanExtra("changed", false);
                    if(close){
                        closeActivity(changed);
                    }
                }
            });
}