package com.bobcikprogramming.kryptoevidence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionHistoryEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithHistory;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionEdit extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnCancel, btnDelete, btnSave;
    private TextView descRowFirst, descRowSecond, descRowThird, descRowFourth, descRowFifth, descRowSixth, descDate, descTime;
    private EditText valueRowFirst, valueRowSecond, valueRowFifth, valueRowSixth, valueFee, valueNote;
    private TextView valueDate, valueTime, valueRowFourth;
    private Spinner spinnerRowThird;
    private ImageView imvButtonShowPhoto, imgButtonAddPhoto;
    private LinearLayout layoutRowFourth, layoutRowFifth, layoutRowSixth;
    private LinearLayout underlineRowFourth, underlineRowFifth, underlineRowSixth;

    TransactionWithPhotos transactionWithPhotos;
    TransactionWithHistory transactionWithHistory;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private String transactionID;

    private ArrayList<TextView> descBuyAndSell;
    private ArrayList<TextView> descChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);

        long transactionIDLong = (long) getIntent().getSerializableExtra("transactionID");
        this.transactionID = String.valueOf(transactionIDLong);
        loadDataFromDB(transactionID);

        setupUIViews();
        setUIByTransactionType();
        setDataByTransactionType();
        openCalendar(transactionWithPhotos.transaction.date);
        openClock(transactionWithPhotos.transaction.time);

        descBuyAndSell = new ArrayList<>(Arrays.asList(descRowFirst, descRowSecond, descDate, descTime));
        descChange = new ArrayList<>(Arrays.asList(descRowFirst, descRowSecond, descRowFifth, descRowSixth, descDate, descTime));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCancel:
                Intent intent = new Intent();
                intent.putExtra("changed", false);
                setResult(RESULT_OK, intent );
                finish();
                break;
            case R.id.btnSave:
                hideKeyBoard();
                confirmDialog();
                break;
            case R.id.btnDelete:
                hideKeyBoard();
                confirmDialogDelete();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("change", false);
        setResult(RESULT_OK, intent );
        finish();
    }

    private void setupUIViews(){
        btnCancel = findViewById(R.id.btnCancel);
        btnDelete = findViewById(R.id.btnDelete);
        btnSave = findViewById(R.id.btnSave);

        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        descRowFirst = findViewById(R.id.descRowFirst);
        descRowSecond = findViewById(R.id.descRowSecond);
        descRowThird = findViewById(R.id.descRowThird);
        descRowFourth = findViewById(R.id.descRowFourth);
        descRowFifth = findViewById(R.id.descRowFifth);
        descRowSixth = findViewById(R.id.descRowSixth);
        descDate = findViewById(R.id.descDate);
        descTime = findViewById(R.id.descTime);

        valueRowFirst = findViewById(R.id.valueRowFirst);
        valueRowSecond = findViewById(R.id.valueRowSecond);
        spinnerRowThird = findViewById(R.id.spinnerRowThird);
        valueRowFourth = findViewById(R.id.valueRowFourth);
        valueRowFifth = findViewById(R.id.valueRowFifth);
        valueRowSixth = findViewById(R.id.valueRowSixth);
        valueFee = findViewById(R.id.valueFee);
        valueDate = findViewById(R.id.valueDate);
        valueTime = findViewById(R.id.valueTime);
        valueNote = findViewById(R.id.valueNote);

        imvButtonShowPhoto = findViewById(R.id.imvButtonShowPhoto);
        imgButtonAddPhoto = findViewById(R.id.imgButtonAddPhoto);

        layoutRowFourth = findViewById(R.id.layoutRowFourth);
        layoutRowFifth = findViewById(R.id.layoutRowFifth);
        layoutRowSixth = findViewById(R.id.layoutRowSixth);

        underlineRowFourth = findViewById(R.id.underlineRowFourth);
        underlineRowFifth = findViewById(R.id.underlineRowFifth);
        underlineRowSixth = findViewById(R.id.underlineRowSixth);

    }

    private void setUIByTransactionType(){
        switch (transactionWithPhotos.transaction.transactionType){
            case "Nákup":
                layoutRowFourth.setVisibility(View.GONE);
                layoutRowFifth.setVisibility(View.GONE);
                layoutRowSixth.setVisibility(View.GONE);
                underlineRowFourth.setVisibility(View.GONE);
                underlineRowFifth.setVisibility(View.GONE);
                underlineRowSixth.setVisibility(View.GONE);
                descRowFirst.setText("Koupené množství");
                descRowSecond.setText("Cena za kus");
                descRowThird.setText("Cena v měně");
                break;
            case "Prodej":
                layoutRowFourth.setVisibility(View.GONE);
                layoutRowFifth.setVisibility(View.GONE);
                layoutRowSixth.setVisibility(View.GONE);
                underlineRowFourth.setVisibility(View.GONE);
                underlineRowFifth.setVisibility(View.GONE);
                underlineRowSixth.setVisibility(View.GONE);
                descRowFirst.setText("Prodané množství");
                descRowSecond.setText("Cena za kus");
                descRowThird.setText("Cena v měně");
                break;
            case "Směna":
                layoutRowFourth.setVisibility(View.VISIBLE);
                layoutRowFifth.setVisibility(View.VISIBLE);
                layoutRowSixth.setVisibility(View.VISIBLE);
                underlineRowFourth.setVisibility(View.VISIBLE);
                underlineRowFifth.setVisibility(View.VISIBLE);
                underlineRowSixth.setVisibility(View.VISIBLE);
                descRowFirst.setText("Koupené množství");
                descRowSecond.setText("Cena za kus");
                descRowThird.setText("Cena v měně");
                break;
        }
    }

    private void loadDataFromDB(String transactionID){
        AppDatabase db = AppDatabase.getDbInstance(this);
        transactionWithPhotos = db.databaseDao().getByTransactionID(transactionID);
        transactionWithHistory = db.databaseDao().getByTransactionHistoryID(transactionID);
    }

    private void setDataByTransactionType(){
        TransactionEntity transaction = transactionWithPhotos.transaction;
        switch (transaction.transactionType){
            case "Nákup":
                valueRowFirst.setText(transaction.quantityBought);
                valueRowSecond.setText(transaction.priceBought);
                spinnerRowThird.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                spinnerRowThird.setSelection(((ArrayAdapter) spinnerRowThird.getAdapter()).getPosition(transaction.currency)); // <-- https://stackoverflow.com/a/11072595
                break;
            case "Prodej":
                valueRowFirst.setText(transaction.quantitySold);
                valueRowSecond.setText(transaction.priceSold);
                spinnerRowThird.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                spinnerRowThird.setSelection(((ArrayAdapter) spinnerRowThird.getAdapter()).getPosition(transaction.currency)); // <-- https://stackoverflow.com/a/11072595
                break;
            case "Směna":
                valueRowFirst.setText(transaction.quantityBought);
                valueRowSecond.setText(transaction.priceBought);
                spinnerRowThird.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                spinnerRowThird.setSelection(((ArrayAdapter) spinnerRowThird.getAdapter()).getPosition(transaction.currency)); // <-- https://stackoverflow.com/a/11072595
                valueRowFourth.setText(transaction.longNameSold);
                valueRowFifth.setText(transaction.quantitySold);
                valueRowSixth.setText(transaction.priceSold);
                break;
        }
        valueFee.setText(transaction.fee);
        valueDate.setText(transaction.date);
        valueTime.setText(transaction.time);

    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int itemId, int layoutId, int dropDownId){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, itemId, layoutId);
        spinnerAdapter.setDropDownViewResource(dropDownId);
        return spinnerAdapter;
    }

    private boolean updateDatabase(){
        AppDatabase db = AppDatabase.getDbInstance(this);
        TransactionEntity transaction = transactionWithPhotos.transaction;
        TransactionEntity newTransaction = new TransactionEntity();
        TransactionHistoryEntity oldTransaction = new TransactionHistoryEntity();

        boolean isEmpty = false;
        boolean changed = false;

        newTransaction.uidTransaction = transaction.uidTransaction;
        newTransaction.transactionType = transaction.transactionType;
        if(transaction.transactionType.equals("Nákup") || transaction.transactionType.equals("Prodej")) {
            if (transaction.transactionType.equals("Nákup")) {
                newTransaction.shortNameBought = transaction.shortNameBought;
                newTransaction.longNameBought = transaction.longNameBought;
                newTransaction.quantityBought = getString(valueRowFirst);
                newTransaction.priceBought = getString(valueRowSecond);
                newTransaction.quantitySold = getPrice(valueRowFirst, valueRowSecond, valueFee);

                if(!newTransaction.quantityBought.equals(transaction.quantityBought)){
                    oldTransaction.quantityBought = transaction.quantityBought;
                    changed = true;
                }
                if(!newTransaction.priceBought.equals(transaction.priceBought)){
                    oldTransaction.priceBought = transaction.priceBought;
                    changed = true;
                }
                if(!newTransaction.quantitySold.equals(transaction.quantitySold)){
                    oldTransaction.quantitySold = transaction.quantitySold;
                    changed = true;
                }

            }else {
                newTransaction.shortNameSold = transaction.shortNameSold;
                newTransaction.longNameSold = transaction.longNameSold;
                newTransaction.quantitySold = getString(valueRowFirst);
                newTransaction.priceSold = getString(valueRowSecond);
                newTransaction.quantityBought = getProfit(valueRowFirst, valueRowSecond, valueFee);

                if(!newTransaction.quantitySold.equals(transaction.quantitySold)){
                    oldTransaction.quantitySold = transaction.quantitySold;
                    changed = true;
                }
                if(!newTransaction.priceSold.equals(transaction.priceSold)){
                    oldTransaction.priceSold = transaction.priceSold;
                    changed = true;
                }
                if(!newTransaction.quantityBought.equals(transaction.quantityBought)){
                    oldTransaction.quantityBought = transaction.quantityBought;
                    changed = true;
                }
            }
            newTransaction.currency = getString(spinnerRowThird);

            String transactionFee = getString(valueFee).isEmpty() ? "0.0" :  getString(valueFee);
            newTransaction.fee = transactionFee;

            newTransaction.date = getString(valueDate);
            newTransaction.time = getString(valueTime);

            oldTransaction.transactionType = transaction.transactionType;

            if(!newTransaction.currency.equals(transaction.currency)){
                oldTransaction.currency = transaction.currency;
                changed = true;
            }
            if(!newTransaction.fee.equals(transaction.fee)){
                oldTransaction.fee = transaction.fee;
                changed = true;
            }
            if(!newTransaction.date.equals(transaction.date)){
                oldTransaction.date = transaction.date;
                changed = true;
            }
            if(!newTransaction.time.equals(transaction.time)){
                oldTransaction.time = transaction.time;
                changed = true;
            }

            isEmpty = shakeEmptyBuySell() || !checkDateAndTime(descDate, descTime, valueDate, valueTime);
        }else if(transaction.transactionType.equals("Směna")){
            newTransaction.uidTransaction = transaction.uidTransaction;
            newTransaction.transactionType = transaction.transactionType;
            newTransaction.shortNameBought = transaction.shortNameBought;
            newTransaction.longNameBought = transaction.longNameBought;
            newTransaction.quantityBought = getString(valueRowFirst);
            newTransaction.priceBought = getString(valueRowSecond);
            newTransaction.currency = getString(spinnerRowThird);
            newTransaction.shortNameSold = getString(valueRowFourth); //TODO z navrácení
            newTransaction.longNameSold = getString(valueRowFourth);
            newTransaction.quantitySold = getString(valueRowFifth);
            newTransaction.priceSold = getString(valueRowSixth);
            String transactionFee = getString(valueFee).isEmpty() ? "0.0" : getString(valueFee);
            newTransaction.fee = transactionFee;
            newTransaction.date = getString(valueDate);
            newTransaction.time = getString(valueTime);


            oldTransaction.transactionType = transaction.transactionType;
            if(!newTransaction.quantityBought.equals(transaction.quantityBought)) {
                oldTransaction.quantityBought = transaction.quantityBought;
                changed = true;
            }
            if(!newTransaction.priceBought.equals(transaction.priceBought)){
                oldTransaction.priceBought = transaction.priceBought;
                changed = true;
            }
            if (!newTransaction.currency.equals(transaction.currency)) {
                oldTransaction.currency = transaction.currency;
                changed = true;
            }
            if (!newTransaction.shortNameSold.equals(transaction.shortNameSold)) {
                oldTransaction.shortNameSold = transaction.shortNameSold;
                changed = true;
            }
            if (!newTransaction.longNameSold.equals(transaction.longNameSold)) {
                oldTransaction.longNameSold = transaction.longNameSold;
                changed = true;
            }
            if(!newTransaction.quantitySold.equals(transaction.quantitySold)){
                oldTransaction.quantitySold = transaction.quantitySold;
                changed = true;
            }
            if(!newTransaction.priceSold.equals(transaction.priceSold)){
                oldTransaction.priceSold = transaction.priceSold;
                changed = true;
            }
            if (!newTransaction.fee.equals(transaction.fee)) {
                oldTransaction.fee = transaction.fee;
                changed = true;
            }
            if (!newTransaction.date.equals(transaction.date)) {
                oldTransaction.date = transaction.date;
                changed = true;
            }
            if (!newTransaction.time.equals(transaction.time)) {
                oldTransaction.time = transaction.time;
                changed = true;
            }

            isEmpty = shakeEmptyChange() || !checkDateAndTime(descDate, descTime, valueDate, valueTime);
        }

        if(!isEmpty && changed){
            oldTransaction.dateOfChange = getActualDay();
            oldTransaction.timeOfChange = getActualTime();
            if (!getString(valueNote).isEmpty()) {
                oldTransaction.note = getString(valueNote);
            }

            oldTransaction.parentTransactionId = newTransaction.uidTransaction;
            db.databaseDao().insertOldTransaction(oldTransaction);
            db.databaseDao().updateTransaction(newTransaction);
            transactionWithPhotos.transaction = newTransaction;

            return true;
        }
        return false;
    }

    private String getString(EditText toString){
        return toString.getText().toString();
    }

    private String getString(TextView toString){
        return toString.getText().toString();
    }

    private String getString(Spinner toString){
        return toString.getSelectedItem().toString();
    }

    private String getPrice(EditText etQuantity, EditText etPrice, TextView etFee) {
        double toRound = (editTextToDouble(etQuantity) * editTextToDouble(etPrice)) + textViewToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return String.valueOf(result);
    }

    private String getProfit(EditText etQuantity, EditText etPrice, EditText etFee) {
        double toRound = (editTextToDouble(etQuantity) * editTextToDouble(etPrice)) - textViewToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return String.valueOf(result);
    }

    private Double editTextToDouble(EditText toParse){
        String inString = toParse.getText().toString();
        return inString.isEmpty() ? 0.0 : Double.parseDouble(inString);
    }

    private Double textViewToDouble(TextView toParse){
        String inString = toParse.getText().toString();
        return inString.isEmpty() ? 0.0 : Double.parseDouble(inString);
    }

    private boolean shakeEmptyBuySell(){
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        boolean findEmpty = false;

        if(valueRowFirst.getText().toString().isEmpty()){
            findEmpty = true;
            descRowFirst.startAnimation(animShake);
            descRowFirst.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descRowFirst.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        if(valueRowSecond.getText().toString().isEmpty()){
            findEmpty = true;
            descRowSecond.startAnimation(animShake);
            descRowSecond.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descRowSecond.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        return findEmpty;
    }

    private boolean shakeEmptyChange(){
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        boolean findEmpty = false;

        findEmpty = shakeEmptyBuySell();

        if(valueRowFifth.getText().toString().isEmpty()){
            findEmpty = true;
            descRowFifth.startAnimation(animShake);
            descRowFifth.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descRowFifth.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        if(valueRowSixth.getText().toString().isEmpty()){
            findEmpty = true;
            descRowSixth.startAnimation(animShake);
            descRowSixth.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descRowSixth.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        return findEmpty;
    }

    private void resetColors(){
        String type = transactionWithPhotos.transaction.transactionType;
        if(type.equals("Nákup") || type.equals("Prodej")) {
            for(TextView description : descBuyAndSell) {
                description.setTextColor(ContextCompat.getColor(this, R.color.white));
            }
        }else if(type.equals("Směna")){
            for(TextView description : descChange) {
                description.setTextColor(ContextCompat.getColor(this, R.color.white));
            }
        }
    }

    public void openCalendar(String date){
        valueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialogWindow(date);
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                setDateToTextView(year, month, day);
            }
        };
    }

    private void openDateDialogWindow(String date){
        Calendar calendar = Calendar.getInstance();

        String[] dateSplit = date.split("\\.");
        int day = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int year = Integer.parseInt(dateSplit[2]);

        DatePickerDialog dialog = new DatePickerDialog(
                this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener, year, month, day
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setDateToTextView(int year, int month, int day){
        month = month + 1; // bere se od 0
        String date = day + "." + month + "." + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy");
        SimpleDateFormat dateFormatSecond = new SimpleDateFormat("dd.MM.yyyy");
        try{
            Date dateFormatToShow = dateFormat.parse(date);
            valueDate.setText(dateFormatSecond.format(dateFormatToShow));
        }
        catch (Exception e){
            System.err.println("Chyba při parsování data: "+e);
        }
    }

    public void openClock(String time){
        valueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialogWindow(time);
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                setTimeToTextView(hour, minute);
            }
        };
    }

    private void openTimeDialogWindow(String time){
        Calendar calendar = Calendar.getInstance();

        String[] timeSplit = time.split(":");
        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        TimePickerDialog dialog = new TimePickerDialog(
                this, android.R.style.Theme_Holo_Dialog_MinWidth, timeSetListener, hour, minute, true
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setTimeToTextView(int hour, int minute){
        String time = hour + ":" + minute;
        SimpleDateFormat dateFormat = new SimpleDateFormat("H:m");
        SimpleDateFormat dateFormatSecond = new SimpleDateFormat("HH:mm");
        try{
            Date timeToShow = dateFormat.parse(time);
            valueTime.setText(dateFormatSecond.format(timeToShow));
        }
        catch (Exception e){
            System.err.println("Chyba při parsování času: "+e);
        }
    }

    private boolean checkDateAndTime(TextView tvDesDate, TextView tvDesTime, TextView etDate, TextView etTime){
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        Date actualDate = getDateFormat(getActualDay());
        Date transactionDate = getDateFormat(etDate.getText().toString());
        if(actualDate.compareTo(transactionDate) < 0){
            tvDesDate.startAnimation(animShake);
            tvDesDate.setTextColor(ContextCompat.getColor(this, R.color.red));
            return false;
        }else if(actualDate.compareTo(transactionDate) == 0) {
            Date actualTime = getTimeFormat(getActualTime());
            Date transactionTime = getTimeFormat(etTime.getText().toString());
            if (actualTime.compareTo(transactionTime) < 0) {
                tvDesTime.startAnimation(animShake);
                tvDesTime.setTextColor(ContextCompat.getColor(this, R.color.red));
                return false;
            }
        }
        return true;
    }

    private String getActualDay(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("dd.MM.yyyy");
        String actualDay = dateFormatCompare.format(calendarDate.getTime());
        return actualDay;
    }

    private String getActualTime(){
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat dateFormatCompare = new SimpleDateFormat("HH:mm");
        String actualTime = dateFormatCompare.format(calendarDate.getTime());
        return actualTime;
    }

    private Date getDateFormat(String dateInString){
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        try {
            date = dateFormat.parse(String.valueOf(dateInString));
        }catch (ParseException e) {
            System.err.println("Chyba při parsování data: "+e);
        }
        return date;
    }

    private Date getTimeFormat(String timeInString){
        Date time = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try{
            time = dateFormat.parse(String.valueOf(timeInString));
        }
        catch (Exception e){
            System.err.println("Chyba při parsování času: "+e);
        }
        return time;
    }

    private void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setCancelable(true);
        builder.setTitle("Upravit transakci");
        builder.setMessage("Chcete uložit upravenenou transakci?");
        builder.setPositiveButton("Uložit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(updateDatabase()) {
                            Intent intent = new Intent();
                            intent.putExtra("changed", true);
                            setResult(RESULT_OK, intent );
                            finish();
                        }
                    }
                });
        builder.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmDialogDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setCancelable(true);
        builder.setTitle("Smazat transakci");
        builder.setMessage("Opravdu chcete smazat transakci?");
        builder.setPositiveButton("Smazat",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppDatabase db = AppDatabase.getDbInstance(TransactionEdit.this);
                        db.databaseDao().deleteHistory(transactionID);
                        db.databaseDao().deletePhotos(transactionID);
                        db.databaseDao().deleteTransactionTable(transactionID);

                        Intent intent = new Intent();
                        intent.putExtra("changed", true);
                        intent.putExtra("deleted", true);
                        setResult(RESULT_OK, intent );
                        finish();
                    }
                });
        builder.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }
}