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
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class TransactionInfo extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private TextView descTransactionT, tvTransactionT, descFirstL, descSecondL, descThirdL, descFourthL, descFifthL, descSixthL, descSeventhL, descEighthL, descNinthL, descTenL;
    private TextView btnBack, btnSave, btnEdit, btnCancel, btnDelete, headline;
    private EditText etFirstL, etSecondL, etThirdL, etFourthL, etFifthL, etSixthL, etSeventhL, etEighthL, etNinthL, etTenthL;
    private Spinner spinnerFirstL, spinnerFourthL, spinnerFifthL;
    private LinearLayout activityBackGround, topBar, layoutEighthLine, layoutNinthLine, layoutTenthLine, underlineSeventhLinem, underlineEighthLine, underlineNinthLine, underlineTenthLine;

    private ArrayList<LinearLayout> extraLayout;
    private ArrayList<EditText> editTextsBuyAndSell;
    private ArrayList<EditText> editTextsChange;
    private ArrayList<TextView> descBuyAndSell;
    private ArrayList<TextView> descChange;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private TransactionWithPhotos transactionWithPhotos;
    boolean change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_info);
        long transactionIDLong = (long) getIntent().getSerializableExtra("transactionID");
        String transactionID = String.valueOf(transactionIDLong);

        setupUIViews();
        extraLayout = new ArrayList<>(Arrays.asList(layoutEighthLine, layoutNinthLine, layoutTenthLine, underlineSeventhLinem, underlineEighthLine, underlineNinthLine, underlineTenthLine));
        editTextsBuyAndSell = new ArrayList<>(Arrays.asList(etSecondL, etThirdL, etFifthL));
        editTextsChange = new ArrayList<>(Arrays.asList(etSecondL, etThirdL, etSixthL, etSeventhL, etEighthL));
        descBuyAndSell = new ArrayList<>(Arrays.asList(descSecondL, descThirdL, descSixthL, descSeventhL));
        descChange = new ArrayList<>(Arrays.asList(descSecondL, descThirdL, descSixthL, descSeventhL, descNinthL, descTenL));
        transactionWithPhotos = loadDataFromDB(transactionID);
        change = false;

        setNavBarVisibility(false);
        setupGUIByTransactionType();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBack:
                Intent intent = new Intent();
                intent.putExtra("changed", change);
                setResult(RESULT_OK, intent );
                finish();
                break;
            case R.id.btnEdit:
                setupGUIForEditByTransactionType(true);
                break;
            case R.id.btnCancel:
                setupGUIForEditByTransactionType(false);
                setupGUIByTransactionType();
                resetColors();
                hideKeyBoard();
                break;
            case R.id.btnSave:
                hideKeyBoard();
                confirmDialog();
            case R.id.activityInfoBackground:
            case R.id.infoTopBar:
                hideKeyBoard();
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.infoFirstLineSpinner:
            case R.id.infoFourthLineSpinner:
            case R.id.infoFifthLineSpinner:
                hideKeyBoard();
                break;
        }
        return false;
    }

    private void setupUIViews(){
        descTransactionT = findViewById(R.id.infoDesTransactionType);
        tvTransactionT = findViewById(R.id.infoTransactionType);
        descFirstL = findViewById(R.id.infoDesFirstLine);
        descSecondL = findViewById(R.id.infoDesSecondLine);
        descThirdL = findViewById(R.id.infoDesThirdLine);
        descFourthL = findViewById(R.id.infoDesFourthLine);
        descFifthL = findViewById(R.id.infoDesFifthLine);
        descSixthL = findViewById(R.id.infoDesSixthLine);
        descSeventhL = findViewById(R.id.infoDesSeventhLine);
        descEighthL = findViewById(R.id.infoDesEighthLine);
        descNinthL = findViewById(R.id.infoDesNinthLine);
        descTenL = findViewById(R.id.infoDesTenthLine);

        etFirstL = findViewById(R.id.infoFirstLine);
        etSecondL = findViewById(R.id.infoSecondLine);
        etThirdL = findViewById(R.id.infoThirdLine);
        etFourthL = findViewById(R.id.infoFourthLine);
        etFifthL = findViewById(R.id.infoFifthLine);
        etSixthL = findViewById(R.id.infoSixthLine);
        etSeventhL = findViewById(R.id.infoSeventhLine);
        etEighthL = findViewById(R.id.infoEighthLine);
        etNinthL = findViewById(R.id.infoNinthLine);
        etTenthL = findViewById(R.id.infoTenthLine);

        spinnerFirstL = findViewById(R.id.infoFirstLineSpinner);
        spinnerFourthL = findViewById(R.id.infoFourthLineSpinner);
        spinnerFifthL = findViewById(R.id.infoFifthLineSpinner);

        layoutEighthLine = findViewById(R.id.infoEighthLineLayout);
        layoutNinthLine = findViewById(R.id.infoNinthLineLayout);
        layoutTenthLine = findViewById(R.id.infoTenthLineLayout);
        underlineSeventhLinem = findViewById(R.id.infoSeventhLineUnderLine);
        underlineEighthLine = findViewById(R.id.infoEighthLineUnderLine);
        underlineNinthLine = findViewById(R.id.infoNinthLineUnderLine);
        underlineTenthLine = findViewById(R.id.infoTenthLineUnderLine);

        headline = findViewById(R.id.infoHeadline);

        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        btnBack.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        activityBackGround = findViewById(R.id.activityInfoBackground);
        topBar = findViewById(R.id.infoTopBar);

        activityBackGround.setOnClickListener(this);
        topBar.setOnClickListener(this);

        spinnerFirstL.setOnTouchListener(this);
        spinnerFourthL.setOnTouchListener(this);
        spinnerFifthL.setOnTouchListener(this);
    }

    private TransactionWithPhotos loadDataFromDB(String transactionID){
        AppDatabase db = AppDatabase.getDbInstance(this);
        return db.databaseDao().getByTransactionID(transactionID);
    }

    private void setupGUIByTransactionType() {
        TransactionEntity transaction = transactionWithPhotos.transaction;
        switch(transaction.transactionType){
            case "Nákup":
                setupGUISameForBuyAndSell(transaction);
                descSecondL.setText("Koupené množství");
                etFirstL.setText(transaction.nameBought);
                etSecondL.setText(transaction.quantityBought);
                etThirdL.setText(transaction.priceBought);

                break;
            case "Prodej":
                setupGUISameForBuyAndSell(transaction);
                descSecondL.setText("Prodané množství");
                etFirstL.setText(transaction.nameSold);
                etSecondL.setText(transaction.quantitySold);
                etThirdL.setText(transaction.priceSold);
                break;
            case "Směna":
                descTransactionT.setText("Typ transakce");
                descFirstL.setText("Nákup kryptoměny");
                descSecondL.setText("Koupené množství");
                descThirdL.setText("Cena za kus");
                descFourthL.setText("Cena v měně");
                descFifthL.setText("Prodej kryptoměny");
                descSixthL.setText("Prodané množství");
                descSeventhL.setText("Cena za kus");
                descEighthL.setText("Poplatek");
                descNinthL.setText("Datum provedení");
                descTenL.setText("Čas provedení");
                tvTransactionT.setText(transaction.transactionType);
                etFirstL.setText(transaction.nameBought);
                etSecondL.setText(transaction.quantityBought);
                etThirdL.setText(transaction.priceBought);
                etFourthL.setText(transaction.currency);
                etFifthL.setText(transaction.nameSold);
                etSixthL.setText(transaction.quantitySold);
                etSeventhL.setText(transaction.priceSold);
                etEighthL.setText(transaction.fee);
                etNinthL.setText(transaction.date);
                etTenthL.setText(transaction.time);
                break;
        }
    }

    private void setupGUISameForBuyAndSell(TransactionEntity transaction){
        for(LinearLayout layout : extraLayout){
            layout.setVisibility(View.GONE);
        }
        descTransactionT.setText("Typ transakce");
        descFirstL.setText("Název kryptoměny");
        descThirdL.setText("Cena za kus");
        descFourthL.setText("Cena v měně");
        descFifthL.setText("Poplatek");
        descSixthL.setText("Datum provedení");
        descSeventhL.setText("Čas provedení");
        tvTransactionT.setText(transaction.transactionType);
        etFourthL.setText(transaction.currency);
        etFifthL.setText(transaction.fee);
        etSixthL.setText(transaction.date);
        etSeventhL.setText(transaction.time);
    }

    private void setupGUIForEditByTransactionType(boolean editMode){
        TransactionEntity transaction = transactionWithPhotos.transaction;
        setNavBarVisibility(editMode);
        switch(transaction.transactionType){
            case "Nákup":
                setupGUIForEditSameForBuyAndSell(editMode, transaction.nameBought, transaction.currency);
                break;
            case "Prodej":
                setupGUIForEditSameForBuyAndSell(editMode, transaction.nameSold, transaction.currency);
                break;
            case "Směna":
                for(EditText editable : editTextsChange){
                    editable.setFocusable(editMode);
                    editable.setFocusableInTouchMode(editMode);
                    editable.setCursorVisible(editMode);
                }
                if(editMode) {
                    etFirstL.setVisibility(View.GONE);
                    spinnerFirstL.setVisibility(View.VISIBLE);
                    etFourthL.setVisibility(View.GONE);
                    spinnerFourthL.setVisibility(View.VISIBLE);
                    etFifthL.setVisibility(View.GONE);
                    spinnerFifthL.setVisibility(View.VISIBLE);
                    etNinthL.setOnClickListener(this);
                    etTenthL.setOnClickListener(this);

                    openCalendar(etNinthL);
                    openClock(etTenthL);

                    spinnerFirstL.setAdapter(getSpinnerAdapter(R.array.test, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                    spinnerFirstL.setSelection(((ArrayAdapter) spinnerFirstL.getAdapter()).getPosition(transaction.nameBought)); // <-- https://stackoverflow.com/a/11072595
                    spinnerFourthL.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                    spinnerFourthL.setSelection(((ArrayAdapter) spinnerFourthL.getAdapter()).getPosition(transaction.currency)); // <-- https://stackoverflow.com/a/11072595
                    spinnerFifthL.setAdapter(getSpinnerAdapter(R.array.test, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                    spinnerFifthL.setSelection(((ArrayAdapter) spinnerFifthL.getAdapter()).getPosition(transaction.nameSold)); // <-- https://stackoverflow.com/a/11072595
                }else{
                    etFirstL.setVisibility(View.VISIBLE);
                    spinnerFirstL.setVisibility(View.GONE);
                    etFourthL.setVisibility(View.VISIBLE);
                    spinnerFourthL.setVisibility(View.GONE);
                    etFifthL.setVisibility(View.VISIBLE);
                    spinnerFourthL.setVisibility(View.GONE);
                    etNinthL.setOnClickListener(null);
                    etTenthL.setOnClickListener(null);
                }
                break;
        }
    }

    private void setupGUIForEditSameForBuyAndSell(boolean editMode, String cryptoName, String currency){
        for(EditText editable : editTextsBuyAndSell){
            editable.setFocusable(editMode);
            editable.setFocusableInTouchMode(editMode);
            editable.setCursorVisible(editMode);
        }
        if(editMode) {
            etFirstL.setVisibility(View.GONE);
            spinnerFirstL.setVisibility(View.VISIBLE);
            etFourthL.setVisibility(View.GONE);
            spinnerFourthL.setVisibility(View.VISIBLE);
            etSixthL.setOnClickListener(this);
            etSeventhL.setOnClickListener(this);

            openCalendar(etSixthL);
            openClock(etSeventhL);

            spinnerFirstL.setAdapter(getSpinnerAdapter(R.array.test, R.layout.spinner_item, R.layout.spinner_dropdown_item));
            spinnerFirstL.setSelection(((ArrayAdapter) spinnerFirstL.getAdapter()).getPosition(cryptoName)); // <-- https://stackoverflow.com/a/11072595
            spinnerFourthL.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
            spinnerFourthL.setSelection(((ArrayAdapter) spinnerFourthL.getAdapter()).getPosition(currency)); // <-- https://stackoverflow.com/a/11072595
        }else{
            etFirstL.setVisibility(View.VISIBLE);
            spinnerFirstL.setVisibility(View.GONE);
            etFourthL.setVisibility(View.VISIBLE);
            spinnerFourthL.setVisibility(View.GONE);
            etSixthL.setOnClickListener(null);
            etSeventhL.setOnClickListener(null);
        }
    }

    private void setNavBarVisibility(boolean editMode){
        if(!editMode){
            btnBack.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            headline.setText("Výpis");
        }else{
            btnBack.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            btnCancel.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            headline.setText("Úprava");
        }
    }

    private boolean updateDatabase(){
        AppDatabase db = AppDatabase.getDbInstance(this);
        TransactionEntity transaction = transactionWithPhotos.transaction;
        TransactionEntity update = new TransactionEntity();
        boolean isEmpty = false;

        update.uidTransaction = transaction.uidTransaction;
        update.transactionType = transaction.transactionType;
        if(transaction.transactionType.equals("Nákup") || transaction.transactionType.equals("Prodej")) {
            if (transaction.transactionType.equals("Nákup")) {
                update.nameBought = spinnerFirstL.getSelectedItem().toString();
                update.quantityBought = getString(etSecondL);
                update.priceBought = getString(etThirdL);
                update.quantitySold = getPrice(etSecondL, etThirdL, etFifthL);
            }else {
                update.nameSold = spinnerFirstL.getSelectedItem().toString();
                update.quantitySold = getString(etSecondL);
                update.priceSold = getString(etThirdL);
                update.quantityBought = getProfit(etSecondL, etThirdL, etFifthL);
            }
            update.currency = getString(etFourthL);

            String transactionFee = getString(etFifthL).isEmpty() ? "0.0" :  getString(etFifthL);
            update.fee = transactionFee;

            update.date = getString(etSixthL);
            update.time = getString(etSeventhL);
            isEmpty = shakeEmptyBuySell() || !checkDateAndTime(descSixthL, descSeventhL, etSixthL, etSeventhL);
        }else if(transaction.transactionType.equals("Směna")){
            update.nameBought = spinnerFirstL.getSelectedItem().toString();
            update.quantityBought = getString(etSecondL);
            update.priceBought = getString(etThirdL);
            update.currency = getString(etFourthL);
            update.nameSold = getString(etFifthL);
            update.quantitySold = getString(etSixthL);
            update.priceSold = getString(etSeventhL);

            String transactionFee = getString(etEighthL).isEmpty() ? "0.0" :  getString(etEighthL);
            update.fee = transactionFee;

            update.date = getString(etNinthL);
            update.time = getString(etTenthL);
            isEmpty = shakeEmptyChange() || !checkDateAndTime(descNinthL, descTenL, etNinthL, etTenthL);
        }

        if(!isEmpty) {
            db.databaseDao().updateTransaction(update);
            transactionWithPhotos.transaction = update;
            return true;
        }
        return false;
    }

    private String getString(EditText toString){
        return toString.getText().toString();
    }

    private String getPrice(EditText etQuantity, EditText etPrice, EditText etFee) {
        double toRound = (editTextToDouble(etQuantity) * editTextToDouble(etPrice)) + editTextToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return String.valueOf(result);
    }

    private String getProfit(EditText etQuantity, EditText etPrice, EditText etFee) {
        double toRound = (editTextToDouble(etQuantity) * editTextToDouble(etPrice)) - editTextToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return String.valueOf(result);
    }

    private Double editTextToDouble(EditText toParse){
        String inString = toParse.getText().toString();
        return inString.isEmpty() ? 0.0 : Double.parseDouble(inString);
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int itemId, int layoutId, int dropDownId){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, itemId, layoutId);
        spinnerAdapter.setDropDownViewResource(dropDownId);
        return spinnerAdapter;
    }

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void openCalendar(EditText etDate){
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialogWindow();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                setDateToTextView(year, month, day, etDate);
            }
        };
    }

    private void openDateDialogWindow(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH); //day of month -> protože měsíce mají různý počet dní
        DatePickerDialog dialog = new DatePickerDialog(
                this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener, year, month, day
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setDateToTextView(int year, int month, int day, EditText etDate){
        month = month + 1; // bere se od 0
        String date = day + "." + month + "." + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy");
        SimpleDateFormat dateFormatSecond = new SimpleDateFormat("dd.MM.yyyy");
        try{
            Date dateFormatToShow = dateFormat.parse(date);
            etDate.setText(dateFormatSecond.format(dateFormatToShow));
        }
        catch (Exception e){
            System.err.println("Chyba při parsování data: "+e);
        }
    }

    public void openClock(EditText etTime){
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialogWindow();
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                setTimeToTextView(hour, minute, etTime);
            }
        };
    }

    private void openTimeDialogWindow(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(
                this, android.R.style.Theme_Holo_Dialog_MinWidth, timeSetListener, hour, minute, true
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setTimeToTextView(int hour, int minute, EditText etTime){
        String time = hour + ":" + minute;
        SimpleDateFormat dateFormat = new SimpleDateFormat("H:m");
        SimpleDateFormat dateFormatSecond = new SimpleDateFormat("HH:mm");
        try{
            Date timeToShow = dateFormat.parse(time);
            etTime.setText(dateFormatSecond.format(timeToShow));
        }
        catch (Exception e){
            System.err.println("Chyba při parsování času: "+e);
        }
    }

    private boolean shakeEmptyBuySell(){
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        boolean findEmpty = false;

        if(etSecondL.getText().toString().isEmpty()){
            findEmpty = true;
            descSecondL.startAnimation(animShake);
            descSecondL.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descSecondL.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        if(etThirdL.getText().toString().isEmpty()){
            findEmpty = true;
            descThirdL.startAnimation(animShake);
            descThirdL.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descThirdL.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        return findEmpty;
    }

    private boolean shakeEmptyChange(){
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        boolean findEmpty = false;

        if(etSecondL.getText().toString().isEmpty()){
            findEmpty = true;
            descSecondL.startAnimation(animShake);
            descSecondL.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descSecondL.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        if(etThirdL.getText().toString().isEmpty()){
            findEmpty = true;
            descThirdL.startAnimation(animShake);
            descThirdL.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descThirdL.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        if(etSixthL.getText().toString().isEmpty()){
            findEmpty = true;
            descSixthL.startAnimation(animShake);
            descSixthL.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descSixthL.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        if(etSeventhL.getText().toString().isEmpty()){
            findEmpty = true;
            descSeventhL.startAnimation(animShake);
            descSeventhL.setTextColor(ContextCompat.getColor(this, R.color.red));
        }else{
            descSeventhL.setTextColor(ContextCompat.getColor(this, R.color.white));
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

    private boolean checkDateAndTime(TextView tvDesDate, TextView tvDesTime, EditText etDate, EditText etTime){
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
                            change = true;
                            setupGUIForEditByTransactionType(false);
                            setupGUIByTransactionType();
                            resetColors();
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

}