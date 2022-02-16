package com.bobcikprogramming.kryptoevidence;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionHistoryEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithHistory;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private ImageView imgButtonAddPhoto;
    private LinearLayout layoutRowFourth, layoutRowFifth, layoutRowSixth;
    private LinearLayout underlineRowFourth, underlineRowFifth, underlineRowSixth;
    private LinearLayout fragmentBackgroundEdit;

    TransactionWithPhotos transactionWithPhotos;
    TransactionWithHistory transactionWithHistory;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private String transactionID;

    private String shortNameCryptoSell, longNameCryptoSell;

    private ArrayList<TextView> descBuyAndSell;
    private ArrayList<TextView> descChange;

    private boolean photoChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);

        photoChange = false;

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
                intent.putExtra("photoChange", photoChange);
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
            case R.id.fragmentBackgroundEdit:
                hideKeyBoard();
                break;
            case R.id.valueRowFourth:
                Intent intentSell = new Intent(this, CryptoChangeSelection.class);
                intentSell.putExtra("shortName", transactionWithHistory.transaction.shortNameBought);
                selectActivityResultLauncher.launch(intentSell);
                break;
            case R.id.imgButtonAddPhoto:
                openGallery();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("change", false);
        intent.putExtra("photoChange", photoChange);
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

        valueRowFourth.setOnClickListener(this);

        imgButtonAddPhoto = findViewById(R.id.imgButtonAddPhoto);
        imgButtonAddPhoto.setOnClickListener(this);

        layoutRowFourth = findViewById(R.id.layoutRowFourth);
        layoutRowFifth = findViewById(R.id.layoutRowFifth);
        layoutRowSixth = findViewById(R.id.layoutRowSixth);

        underlineRowFourth = findViewById(R.id.underlineRowFourth);
        underlineRowFifth = findViewById(R.id.underlineRowFifth);
        underlineRowSixth = findViewById(R.id.underlineRowSixth);

        fragmentBackgroundEdit = findViewById(R.id.fragmentBackgroundEdit);
        fragmentBackgroundEdit.setOnClickListener(this);

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

                shortNameCryptoSell = transactionWithHistory.transaction.shortNameSold;
                longNameCryptoSell = transactionWithHistory.transaction.longNameSold;
                break;
        }
    }

    private void loadDataFromDB(String transactionID){
        AppDatabase db = AppDatabase.getDbInstance(this);
        transactionWithPhotos = db.databaseDao().getTransactionByTransactionID(transactionID);
        transactionWithHistory = db.databaseDao().getTransactionByTransactionHistoryID(transactionID);
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

    private int updateDatabase(){
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
            newTransaction.shortNameSold = shortNameCryptoSell;
            newTransaction.longNameSold = longNameCryptoSell;
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

            return 0;
        }

        if(changed && isEmpty){
            return 2;
        }
        return 1;
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
            descRowFirst.setTextColor(ContextCompat.getColor(this, R.color.textViewDescriptionTextColor));
        }

        if(valueRowSecond.getText().toString().isEmpty()){
            findEmpty = true;
            descRowSecond.startAnimation(animShake);
            descRowSecond.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descRowSecond.setTextColor(ContextCompat.getColor(this, R.color.textViewDescriptionTextColor));
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
            descRowFifth.setTextColor(ContextCompat.getColor(this, R.color.textViewDescriptionTextColor));
        }

        if(valueRowSixth.getText().toString().isEmpty()){
            findEmpty = true;
            descRowSixth.startAnimation(animShake);
            descRowSixth.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descRowSixth.setTextColor(ContextCompat.getColor(this, R.color.textViewDescriptionTextColor));
        }

        return findEmpty;
    }

    private void resetColors(){
        String type = transactionWithPhotos.transaction.transactionType;
        if(type.equals("Nákup") || type.equals("Prodej")) {
            for(TextView description : descBuyAndSell) {
                description.setTextColor(ContextCompat.getColor(this, R.color.textViewDescriptionTextColor));
            }
        }else if(type.equals("Směna")){
            for(TextView description : descChange) {
                description.setTextColor(ContextCompat.getColor(this, R.color.textViewDescriptionTextColor));
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
        int month = Integer.parseInt(dateSplit[1]) - 1;
        int year = Integer.parseInt(dateSplit[2]);

        DatePickerDialog dialog = new DatePickerDialog(
                this, R.style.TimeDatePicker, dateSetListener, year, month, day
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
                this, R.style.TimeDatePicker, timeSetListener, hour, minute, true
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try{
            Date actualDate = dateFormat.parse(getActualDay());
            Date transactionDate = dateFormat.parse(etDate.getText().toString());
            // https://stackoverflow.com/questions/2592501/how-to-compare-dates-in-java
            if(actualDate.before(transactionDate)){
                tvDesDate.startAnimation(animShake);
                tvDesDate.setTextColor(ContextCompat.getColor(this, R.color.red));
                return false;
            }else if(!actualDate.after(transactionDate)) {
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
        catch (Exception e){
            System.err.println("Chyba při parsování data: "+e);
            tvDesDate.startAnimation(animShake);
            tvDesDate.setTextColor(ContextCompat.getColor(this, R.color.red));
            return false;
        }
    }

    /*private boolean checkDateAndTime(TextView tvDesDate, TextView tvDesTime, TextView etDate, TextView etTime){
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        Date actualDate = getDateFormat(getActualDay());
        Date transactionDate = getDateFormat(etDate.getText().toString());
        if(actualDate.compareTo(transactionDate) < 0){
            System.out.println("----------Actual date: "+ String.valueOf(actualDate) + " | date: " + String.valueOf(transactionDate));
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
    }*/

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
                        int success = updateDatabase();
                        if(success == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("changed", true);
                            intent.putExtra("photoChange", photoChange);
                            setResult(RESULT_OK, intent );
                            finish();
                        }else if(success == 1){
                            Intent intent = new Intent();
                            intent.putExtra("changed", false);
                            intent.putExtra("photoChange", photoChange);
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
                        List<PhotoEntity> photos = db.databaseDao().getPhotoByTransactionID(transactionID);

                        for(PhotoEntity photo : photos) {
                            deleteImage(photo.dest);
                        }

                        db.databaseDao().deleteHistory(transactionID);
                        db.databaseDao().deletePhotos(transactionID);
                        db.databaseDao().deleteTransactionTable(transactionID);

                        Intent intent = new Intent();
                        intent.putExtra("changed", true);
                        intent.putExtra("deleted", true);
                        intent.putExtra("photoChange", photoChange);
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

    ActivityResultLauncher<Intent> selectActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();
                    String onReturnShort = data.getStringExtra("shortName");
                    String onReturnLong = data.getStringExtra("longName");
                    if(!onReturnShort.isEmpty()) {
                        shortNameCryptoSell = onReturnShort;
                        longNameCryptoSell = onReturnLong;
                        valueRowFourth.setText(longNameCryptoSell);
                    }
                }
            });

    private void openGallery(){
        AppDatabase db = AppDatabase.getDbInstance(this);
        List<PhotoEntity> photos = db.databaseDao().getPhotoByTransactionID(transactionID);
        if(photos.isEmpty()){
            androidGallery.launch("image/*");
        }else{
            Intent gallery = new Intent(TransactionEdit.this, TransactionEditPhotoViewer.class);
            gallery.putExtra("transactionID",transactionID);
            appGallery.launch(gallery);
        }
    }

    ActivityResultLauncher<String> androidGallery = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri != null) {
                    AppDatabase db = AppDatabase.getDbInstance(TransactionEdit.this);
                    PhotoEntity photoEntity = new PhotoEntity();

                    String path = saveImage(uri);
                    if (!path.isEmpty()) {
                        photoEntity.dest = path;
                        photoEntity.transactionId = Long.parseLong(transactionID);
                        db.databaseDao().insertPhoto(photoEntity);

                        photoChange = true;
                    }
                }
            }
        });

    ActivityResultLauncher<Intent> appGallery = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                photoChange = data.getBooleanExtra("photoChange", false);
            }
        });

    // https://stackoverflow.com/a/17674787
    private String saveImage(Uri photo){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File dir = cw.getDir("Images", MODE_PRIVATE);

        Bitmap bitmap;
        FileOutputStream fos = null;

        File myPath = new File(dir, System.currentTimeMillis() + ".jpg");

        try {
            // https://stackoverflow.com/a/4717740
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return String.valueOf(myPath);
    }

    // https://stackoverflow.com/a/10716773
    private void deleteImage(String path){
        File toDelete = new File(path);
        if(toDelete.exists()){
            toDelete.delete();
        }
    }

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }
}