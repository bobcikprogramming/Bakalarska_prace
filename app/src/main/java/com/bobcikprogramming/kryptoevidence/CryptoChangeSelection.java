package com.bobcikprogramming.kryptoevidence;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class CryptoChangeSelection extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private EditText etSearch;
    private LinearLayout layout;
    private ImageView imgBtnDelete, imgBtnCloseCryptoSelection;

    private RecyclerViewSelection adapter;

    private ArrayList<RecyclerViewSelectionList> cryptoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_selection);
        cryptoList = new ArrayList<>();
        tmpAddCryptoToList();
        setupUIViews();

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyBoard();
                return true;
            }
        });

        adapter = new RecyclerViewSelection(this, cryptoList, myClickListener);
        recyclerView.setAdapter(adapter);
    }

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

    private void tmpAddCryptoToList(){
        RecyclerViewSelectionList btc = new RecyclerViewSelectionList("Bitcoin", "BTC");
        cryptoList.add(btc);
        RecyclerViewSelectionList link = new RecyclerViewSelectionList("Chainlink", "LINK");
        cryptoList.add(link);
        RecyclerViewSelectionList ada = new RecyclerViewSelectionList("Cardano", "ADA");
        cryptoList.add(ada);
        RecyclerViewSelectionList eth = new RecyclerViewSelectionList("Ethereum", "ETH");
        cryptoList.add(eth);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.layoutSelection:
                hideKeyBoard();
                break;
            case R.id.imgBtnCloseCryptoSelection:
                Intent intent = new Intent();
                intent.putExtra("longName", "");
                intent.putExtra("shortName", "");
                setResult(RESULT_OK, intent );
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("longName", "");
        intent.putExtra("shortName", "");
        setResult(RESULT_OK, intent );
        finish();
    }

    // https://stackoverflow.com/a/45711180
    private View.OnClickListener myClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = (int) view.getTag();
            Intent intent = new Intent();
            intent.putExtra("longName", cryptoList.get(position).longName);
            intent.putExtra("shortName", cryptoList.get(position).shortName);
            setResult(RESULT_OK, intent );
            finish();
        }
    };

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }
}