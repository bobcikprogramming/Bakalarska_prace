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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bobcikprogramming.kryptoevidence.addTransaction.AddTransaction;

import java.util.ArrayList;

public class CryptoSelection extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private EditText etSearch;
    private LinearLayout layout;
    private ImageView imgBtnDelete, imgBtnCloseCryptoSelection;

    private RecyclerViewSelection adapter;

    private ArrayList<RecyclerViewSelectionList> cryptoList;
    private ArrayList<RecyclerViewSelectionList> cryptoListToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_selection);
        cryptoList = new ArrayList<>();
        tmpAddCryptoToList();
        cryptoListToShow = cryptoList;
        setupUIViews();
        searchOnChange();

        adapter = new RecyclerViewSelection(CryptoSelection.this, cryptoListToShow, myClickListener);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyBoard();
                return true;
            }
        });
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
                intent.putExtra("changed", false);
                setResult(RESULT_OK, intent );
                finish();
            case R.id.imgBtnDelete:
                etSearch.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("changed", false);
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
                if(searching.length() == 0){
                    cryptoListToShow = cryptoList;
                }else {
                    cryptoListToShow = new ArrayList<>();
                    for (RecyclerViewSelectionList toShow : cryptoList) {
                        if (toShow.longName.toLowerCase().contains(searching.toLowerCase()) || toShow.shortName.toLowerCase().contains(searching.toLowerCase())) {
                                cryptoListToShow.add(toShow);
                        }
                    }
                }
                adapter = new RecyclerViewSelection(CryptoSelection.this, cryptoListToShow, myClickListener);
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
            Intent addActivity = new Intent(CryptoSelection.this, AddTransaction.class);
            addActivity.putExtra("longName", cryptoList.get(position).longName);
            addActivity.putExtra("shortName", cryptoList.get(position).shortName);
            addActivityResultLauncher.launch(addActivity);
        }
    };

    ActivityResultLauncher<Intent> addActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();
                    boolean close = data.getBooleanExtra("close", false);
                    boolean changed = data.getBooleanExtra("changed", false);
                    if(close){
                        Intent intent = new Intent();
                        intent.putExtra("changed", changed);
                        setResult(RESULT_OK, intent );
                        finish();
                    }
                }
            });

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }
}