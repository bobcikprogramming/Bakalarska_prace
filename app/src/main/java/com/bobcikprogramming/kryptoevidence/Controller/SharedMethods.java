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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public String getStringFromBigDecimal(EditText stringFrom){
        return String.valueOf(getBigDecimal(stringFrom.getText().toString()));
    }

    public String getStringFromBigDecimal(TextView stringFrom){
        return String.valueOf(getBigDecimal(stringFrom.getText().toString()));
    }

    public Double getFee(EditText etFee){
        return getString(etFee).isEmpty() ? 0.0 :  Double.parseDouble(getString(etFee));
    }

    public BigDecimal getBigDecimal(EditText stringFrom) {
        return new BigDecimal(getString(stringFrom));
    }

    public BigDecimal getBigDecimal(String stringFrom) {
        return new BigDecimal(stringFrom);
    }

    public BigDecimal getBigDecimal(Double doubleFrom) {
        return new BigDecimal(doubleFrom);
    }

    public String getStringByEditDouble(EditText stringFrom){
        String toEdit = stringFrom.getText().toString();
        do {
            if(toEdit.charAt(0) == '0'){
                toEdit = toEdit.substring(1);
            }
        }while(toEdit.charAt(0) == '0');

        if(toEdit.charAt(0) == '.'){
            toEdit = "0" + toEdit;
        }
        return toEdit;
    }

    public BigDecimal getPrice(EditText etPrice, EditText etFee) {
        double toRound = editTextToDouble(etPrice) + editTextToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return BigDecimal.valueOf(result);
    }

    public BigDecimal getProfit(EditText etPrice, EditText etFee) {
        double toRound = editTextToDouble(etPrice) - editTextToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return BigDecimal.valueOf(result);
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

    public String editNumberForTextView(String number) {
        Double quantityBought = Double.parseDouble(number);
        if (quantityBought > 999999.0) {
            number = "999 999+";
        }else if(quantityBought < -999999.0){
            number = "-999 999";
        }else if(number.contains(".")){
            if(number.length() > 7) {
                int lenOfInteger = number.split("\\.")[0].length();
                int toCut = 6 - lenOfInteger;
                double round = Math.pow(10, toCut);
                quantityBought = (double) Math.round(quantityBought * round) / round;
                number = "~" + quantityBought;
            }
        }//else{
         //   /** https://stackoverflow.com/a/11149356 */
         //   number = number.replaceAll("...(?!$)", "$0 ");
        //}
        return number;
    }

    public Double getTwoDecimalDouble(Double toEdit){
        return Math.round(toEdit * 100.0) / 100.0;
    }

    public Double getTwoDecimalDouble(String toEdit){
        return Math.round(Double.parseDouble(toEdit) * 100.0) / 100.0;
    }
}
