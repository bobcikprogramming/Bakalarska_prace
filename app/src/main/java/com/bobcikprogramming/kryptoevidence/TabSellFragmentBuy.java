package com.bobcikprogramming.kryptoevidence;

import android.app.Activity;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;

import java.util.ArrayList;
import java.util.Arrays;

public class TabSellFragmentBuy extends Fragment implements View.OnClickListener {

    private EditText etName, etQuantity, etPrice, etFee, etDate, etCurrency;
    private Button btnSave;
    private ImageButton imgBtnAddPhoto, imgBtnCalendar;
    private ConstraintLayout viewBackgroung;
    private View view;
    private ArrayList<EditText> mandatoryField;

    public TabSellFragmentBuy() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_tab_sell, container, false);
        setupUIViews();
        mandatoryField = new ArrayList<>(Arrays.asList(etName,etQuantity,etPrice,etFee,etDate,etCurrency));
        return view;
    }

    private void setupUIViews(){
        etName = view.findViewById(R.id.editTextNameSell);
        etQuantity = view.findViewById(R.id.editTextQuantitySell);
        etPrice = view.findViewById(R.id.editTextPriceSell);
        etFee = view.findViewById(R.id.editTextFeeSell);
        etDate = view.findViewById(R.id.editTextDateSell);
        etCurrency = view.findViewById(R.id.editTextCurrencySell);

        viewBackgroung = view.findViewById(R.id.fragmentBackgroundSell);
        viewBackgroung.setOnClickListener(this);

        btnSave = view.findViewById(R.id.buttonSaveSell);
        btnSave.setOnClickListener(this);
        imgBtnAddPhoto = view.findViewById(R.id.imgButtonAddPhotoSell);
        imgBtnAddPhoto.setOnClickListener(this);
        imgBtnCalendar = view.findViewById(R.id.imgButtonCalendarSell);
        imgBtnCalendar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonSaveSell:
                hideKeyBoard();
                if(!shakeEmpty()){
                    saveToDb();
                    clearEditText();
                }
                break;
            case R.id.fragmentBackgroundSell:
                hideKeyBoard();

        }
    }

    private void saveToDb() {
        AppDatabase db = AppDatabase.getDbInstance(getContext());

        Double cryptoQuantity = editTextToDouble(etQuantity);
        Double cryptoPrice = editTextToDouble(etPrice);
        Double fee = editTextToDouble(etFee);

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.transactionType = "Prodej";
        transactionEntity.nameSold = "BTC";
        transactionEntity.quantitySold = cryptoQuantity;
        transactionEntity.priceSold = cryptoPrice;
        transactionEntity.fee = fee;
        transactionEntity.date = ""; //TODO
        transactionEntity.nameBought = "EUR";
        transactionEntity.quantityBought = getPriceOrProfit(cryptoQuantity, cryptoPrice, fee);

        db.databaseDao().insertTransaction(transactionEntity);
    }

    private void clearEditText(){
        //etName.setText("");
        etQuantity.setText("");
        etPrice.setText("");
        etFee.setText("");
        etDate.setText("");
        //etCurrency.setText("");
    }

    private Double getPriceOrProfit(Double cryptoQuantity, Double cryptoPrice, Double fee) {
        return (cryptoPrice * cryptoQuantity) - fee;
    }

    private Double editTextToDouble(EditText toParse){
        return Double.parseDouble(toParse.getText().toString());
    }

    private boolean shakeEmpty(){
        Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        boolean findEmpty = false;
        for (EditText checking: mandatoryField) {
            if(checking.getText().toString().isEmpty()){
                findEmpty = true;
                checking.startAnimation(animShake);
                if(checking == etDate){
                    imgBtnCalendar.startAnimation(animShake);
                }
                checking.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text_empty));
            }
            else{
                checking.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text));
            }
        }
        return findEmpty;
    }

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}