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

public class TabChangeFragmentAdd extends Fragment implements View.OnClickListener {

    private EditText etNameBuy, etNameSell, etQuantityBuy, etQuantitySell, etQuantitySold, etPriceBuy, etPriceSell, etFee, etDate;
    private Button btnSave;
    private ImageButton imgBtnAddPhoto, imgBtnCalendar;
    private ConstraintLayout viewBackgroung;
    private View view;
    private ArrayList<EditText> mandatoryField;

    public TabChangeFragmentAdd() {
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
        view = inflater.inflate(R.layout.fragment_add_tab_change, container, false);
        setupUIViews();
        mandatoryField = new ArrayList<>(Arrays.asList(etNameBuy, etNameSell, etQuantityBuy, etQuantitySell, etPriceBuy, etPriceSell,etFee,etDate));
        return view;
    }

    private void setupUIViews(){
        etNameBuy = view.findViewById(R.id.editTextNameChangeBuy);
        etNameSell = view.findViewById(R.id.editTextNameChangeSell);
        etQuantityBuy = view.findViewById(R.id.editTextQuantityChangeBuy);
        etQuantitySell = view.findViewById(R.id.editTextQuantityChangeSell);
        etPriceBuy = view.findViewById(R.id.editTextPriceChangeBuy);
        etPriceSell = view.findViewById(R.id.editTextPriceChangeSell);
        etFee = view.findViewById(R.id.editTextFeeChange);
        etDate = view.findViewById(R.id.editTextDateChange);

        viewBackgroung = view.findViewById(R.id.fragmentBackgroundChange);
        viewBackgroung.setOnClickListener(this);

        btnSave = view.findViewById(R.id.buttonSaveChange);
        btnSave.setOnClickListener(this);
        imgBtnAddPhoto = view.findViewById(R.id.imgButtonAddPhotoChange);
        imgBtnAddPhoto.setOnClickListener(this);
        imgBtnCalendar = view.findViewById(R.id.imgButtonCalendarChange);
        imgBtnCalendar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonSaveChange:
                hideKeyBoard();
                if(!shakeEmpty()){
                    saveToDb();
                    clearEditText();
                }
                break;
            case R.id.fragmentBackgroundChange:
                hideKeyBoard();

        }
    }

    private void saveToDb() {
        AppDatabase db = AppDatabase.getDbInstance(getContext());

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.transactionType = "Směna";
        transactionEntity.nameBought = "BTC";
        transactionEntity.quantityBought = etQuantityBuy.getText().toString();;
        transactionEntity.priceBought = etPriceBuy.getText().toString();;
        transactionEntity.fee = etFee.getText().toString();;
        transactionEntity.date = ""; //TODO
        transactionEntity.nameSold = "LINK";
        transactionEntity.quantitySold = etQuantitySell.getText().toString();;
        transactionEntity.priceSold = etPriceSell.getText().toString();;

        db.databaseDao().insertTransaction(transactionEntity);
    }

    private void clearEditText(){
        //etNameOfBought.setText("");
        //etNameOfSold.setText("");
        etQuantityBuy.setText("");
        etQuantitySell.setText("");
        etPriceBuy.setText("");
        etPriceSell.setText("");
        etFee.setText("");
        etDate.setText("");
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