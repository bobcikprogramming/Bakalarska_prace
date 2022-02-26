package com.bobcikprogramming.kryptoevidence.Controller;

import android.app.Activity;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bobcikprogramming.kryptoevidence.R;

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

    public boolean checkIfEmptyAndShake(TextView toCheck, TextView description, boolean prevValue, Context context){
        Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);

        if(getString(toCheck).isEmpty()){
            description.startAnimation(animShake);
            description.setTextColor(ContextCompat.getColor(context, R.color.red));
            return true;
        }else{
            description.setTextColor(ContextCompat.getColor(context, R.color.textViewDescriptionTextColor));
            return prevValue;
        }
    }

    public boolean checkIfEmptyAndShake(EditText toCheck, TextView description, boolean prevValue, Context context){
        Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);

        if(getString(toCheck).isEmpty()){
            description.startAnimation(animShake);
            description.setTextColor(ContextCompat.getColor(context, R.color.red));
            return true;
        }else{
            description.setTextColor(ContextCompat.getColor(context, R.color.textViewDescriptionTextColor));
            return prevValue;
        }
    }

    public String editNumberForTextView(String number){
        Double quantityBought = Double.parseDouble(number);
        if(quantityBought > 999999.0){
            number = "999 999+";
        }else if(number.contains(".")){
            if(number.length() > 7) {
                int lenOfInteger = number.split("\\.")[0].length();
                int toCut = 6 - lenOfInteger;
                double round = Math.pow(10, toCut);
                quantityBought = (double) Math.round(quantityBought * round) / round;
                number = "~" + quantityBought;
            }
        }else{
            /** https://stackoverflow.com/a/11149356 */
            number = number.replaceAll("...(?!$)", "$0 ");
        }
        return number;
    }
}
