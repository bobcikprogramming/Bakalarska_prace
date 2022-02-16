package com.bobcikprogramming.kryptoevidence.Controller;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SharedMethods {

    public SharedMethods(){}

    public String getString(EditText stringFrom){
        return stringFrom.getText().toString();
    }

    public String getString(TextView stringFrom){
        return stringFrom.getText().toString();
    }

    public String getString(Spinner stringFrom){
        return stringFrom.getSelectedItem().toString();
    }

    public String getFeeString(EditText etFee){
        return getString(etFee).isEmpty() ? "0.0" :  getString(etFee);
    }

    public String getPrice(EditText etQuantity, EditText etPrice, EditText etFee) {
        double toRound = (editTextToDouble(etQuantity) * editTextToDouble(etPrice)) + editTextToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return String.valueOf(result);
    }

    public String getProfit(EditText etQuantity, EditText etPrice, EditText etFee) {
        double toRound = (editTextToDouble(etQuantity) * editTextToDouble(etPrice)) - editTextToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return String.valueOf(result);
    }

    public Double editTextToDouble(EditText toParse){
        String inString = toParse.getText().toString();
        return inString.isEmpty() ? 0.0 : Double.parseDouble(inString);
    }

    public void hideKeyBoard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

}
