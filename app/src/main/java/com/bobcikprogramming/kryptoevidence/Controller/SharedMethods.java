package com.bobcikprogramming.kryptoevidence.Controller;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bobcikprogramming.kryptoevidence.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Třída slouží k obecným funkcím využívaných na více místech.
 */
public class SharedMethods {

    public SharedMethods(){}

    /**
     * Metoda pro získání stringu z edit textového pole.
     * @param stringFrom Ukazatel na edit textové pole
     * @return Získaný string
     */
    public String getString(EditText stringFrom){
        return stringFrom.getText().toString();
    }

    /**
     * Metoda pro získání stringu z text view
     * @param stringFrom Ukazatel na text view
     * @return Získaný string
     */
    public String getString(TextView stringFrom){
        return stringFrom.getText().toString();
    }

    /**
     * Metoda pro získání stringu ze spinneru
     * @param stringFrom Ukazatel na spinner
     * @return Získaný string
     */
    public String getString(Spinner stringFrom){
        return stringFrom.getSelectedItem().toString();
    }

    /**
     * Metoda pro získání hodnoty poplatku
     * @param etFee Ukazatel na edit text poplatku
     * @return Hodnotu poplatku nebo hodnotu 0.0, není-li hodnota uvedena
     */
    public Double getFee(EditText etFee){
        return getString(etFee).isEmpty() ? 0.0 :  Double.parseDouble(getString(etFee));
    }

    /**
     * Metoda pro získání hodnoty BigDecimal z edit textového pole
     * @param stringFrom Ukazatel na edit textové pole
     * @return Hodnota převedena na typ BigDecimal
     */
    public BigDecimal getBigDecimal(EditText stringFrom) {
        return new BigDecimal(getString(stringFrom));
    }

    /**
     * Metoda pro získání hodnoty BigDecimal ze stringu
     * @param stringFrom Hodnota typu string
     * @return Hodnota převedena na typ BigDecimal
     */
    public BigDecimal getBigDecimal(String stringFrom) {
        return new BigDecimal(stringFrom);
    }

    /**
     * Metoda pro získání hodnoty BigDecimal z double
     * @param doubleFrom Hodnota typu Double
     * @return Hodnota převedena na typ BigDecimal
     */
    public BigDecimal getBigDecimal(Double doubleFrom) {
        return new BigDecimal(doubleFrom);
    }


    /**
     * Metoda pro získání ceny bez poplatku
     * @param etPrice Ukazatel na edit textové pole s cenou
     * @param etFee Ukazatel na edit textové pole s poplatkem
     * @return Získaná cena
     */
    public BigDecimal getPriceWithoutFee(EditText etPrice, EditText etFee) {
        double toRound = editTextToDouble(etPrice) - editTextToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return BigDecimal.valueOf(result);
    }

    /**
     * Metoda pro získání zisku (cena bez poplatku)
     * @param etPrice Ukazatel na edit textové pole s cenou
     * @param etFee Ukazatel na edit textové pole s poplatkem
     * @return Získaný zisk
     */
    public BigDecimal getProfit(EditText etPrice, EditText etFee) {
        double toRound = editTextToDouble(etPrice) - editTextToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return BigDecimal.valueOf(result);
    }

    /**
     * Metoda na převod hodnoty z edit textového pole na hodnotu Double
     * @param toParse Ukazatel na edit textové pole
     * @return Získaná hodnota
     */
    public Double editTextToDouble(EditText toParse){
        String inString = toParse.getText().toString();
        return inString.isEmpty() ? 0.0 : Double.parseDouble(inString);
    }

    /**
     * Metoda pro skrytí klávesnice
     * @param activity Třída activity, ve které byla metoda provolána
     */
    public void hideKeyBoard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Metoda pro kontrolo, zda-li byla všechna povinná pole vyplněna
     * @param toCheck Ukazatel na text view, jenž má být vyplněno
     * @param description Ukazatel text view s popisem povinného pole
     * @param prevValue boolean hodnota obsahující výsledek předcházející kontroly
     * @param context Třída context activity, ze které je metoda volána
     * @return Výsledek kontroly v případě nevyplněné hodnoty, jinak prevValue
     */
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

    /**
     * Metoda pro kontrolo, zda-li byla všechna povinná pole vyplněna
     * @param toCheck Ukazatel na edit text, jenž má být vyplněno
     * @param description Ukazatel text view s popisem povinného pole
     * @param prevValue boolean hodnota obsahující výsledek předcházející kontroly
     * @param context Třída context activity, ze které je metoda volána
     * @return Výsledek kontroly v případě nevyplněné hodnoty, jinak prevValue
     */
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

    /**
     * Metoda pro zpracování vyobrazení čísel u přehledu transakcí
     * @param number string obsahující číslo k vyobrazení
     * @return Zpracovaná hodnota
     */
    public String editNumberForTextView(String number) {
        double quantityBought = Double.parseDouble(number);
        if (quantityBought > 999999.0) {
            number = "999 999+";
        }else if(quantityBought < -999999.0){
            number = "-999 999";
        }else if(number.contains(".")){
            if(number.length() > 7) {
                int lenOfInteger = number.split("\\.")[0].length();
                int toCut = 6 - lenOfInteger;
                double round = Math.pow(10, toCut);
                DecimalFormat trailingZeros = new DecimalFormat("0.#");
                quantityBought = (double) Math.round(quantityBought * round) / round;
                number = "~" + trailingZeros.format(quantityBought);
            }
        }
        return number;
    }

    /**
     * Metoda pro zaokrouhlení čísla typu Double na dvě desetinná místa
     * @param toEdit Číslo typu Double
     * @return Zaokrouhlené číslo
     */
    public BigDecimal getTwoDecimalBigDecimal(Double toEdit){
        BigDecimal toRound = getBigDecimal(toEdit);
        return toRound.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * Metoda pro zaokrouhlení čísla typu string na dvě desetinná místa
     * @param toEdit Číslo typu string
     * @return Zaokrouhlené číslo
     */
    public BigDecimal getTwoDecimalBigDecimal(String toEdit){
        BigDecimal toRound = getBigDecimal(toEdit);
        return toRound.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * Metoda pro zaokrouhlení čísla typu BigDecimal na dvě desetinná místa
     * @param toEdit Číslo typu BigDecimal
     * @return Zaokrouhlené číslo
     */
    public BigDecimal getTwoDecimalBigDecimal(BigDecimal toEdit){
        return toEdit.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * Metoda pro zaokrouhlení čísla typu String na X desetinných míst
     * @param toEdit Číslo typu String
     * @param x Počet desetinných míst
     * @return Zaokrouhlené číslo
     */
    public BigDecimal getXDecimalBigDecimal(String toEdit, int x){
        BigDecimal toRound = getBigDecimal(toEdit);
        return toRound.setScale(x, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * Metoda na převod display metrics hodnoty na pixely
     * @param dp Display metrics hodnota
     * @param context Třída context activity, ze které je metoda volána
     * @return Hodnotu v pixelech
     *
     * Metoda byla inspirována z:
     * Zdroj:   Stack Overflow
     * Dotaz:   https://stackoverflow.com/q/8309354
     * Odpověď: https://stackoverflow.com/a/17410076
     * Autor:   Bachi
     * Autor:   https://stackoverflow.com/users/454667/bachi
     * Datum:   1. července 2013
     */
    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
