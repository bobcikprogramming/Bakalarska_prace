package com.bobcikprogramming.kryptoevidence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bobcikprogramming.kryptoevidence.Controller.CryptoSelectionController;
import com.bobcikprogramming.kryptoevidence.Controller.SharedMethods;

import java.util.ArrayList;

public class CryptoChangeSelection extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private EditText etSearch;
    private LinearLayout layout;
    private ImageView imgBtnDelete, imgBtnCloseCryptoSelection;

    private RecyclerViewSelection adapter;

    private ArrayList<RecyclerViewSelectionList> cryptoList;
    private ArrayList<RecyclerViewSelectionList> cryptoListToShow;

    private SharedMethods shared;
    private CryptoSelectionController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_selection);

        shared = new SharedMethods();
        controller = new CryptoSelectionController();
        cryptoList = new ArrayList<>();

        Bundle extras = getIntent().getExtras();

        setupUIViews();
        searchOnChange();
        hideKeyBoardOnRecyclerTouch();

        adapter = new RecyclerViewSelection(this, controller.removeSelectedValue(extras.getString("shortName")), myClickListener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.layoutSelection:
                shared.hideKeyBoard(this);
                break;
            case R.id.imgBtnCloseCryptoSelection:
                closeActivity("","");
            case R.id.imgBtnDelete:
                etSearch.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity("", "");
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

    private void closeActivity(String longName, String shortName){
        Intent intent = new Intent();
        intent.putExtra("longName", longName);
        intent.putExtra("shortName", shortName);
        setResult(RESULT_OK, intent );
        finish();
    }

    private void searchOnChange(){
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searching = charSequence.toString();
                adapter = new RecyclerViewSelection(CryptoChangeSelection.this, controller.filter(searching), myClickListener);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // https://stackoverflow.com/a/45711180
    private View.OnClickListener myClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = (int) view.getTag();
            closeActivity(cryptoList.get(position).longName, cryptoList.get(position).shortName);
        }
    };

    private void hideKeyBoardOnRecyclerTouch(){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                shared.hideKeyBoard(CryptoChangeSelection.this);
                return true;
            }
        });
    }
}