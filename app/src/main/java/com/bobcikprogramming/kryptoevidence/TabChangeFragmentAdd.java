package com.bobcikprogramming.kryptoevidence;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class TabChangeFragmentAdd extends Fragment implements View.OnClickListener {

    private EditText etQuantityBuy, etQuantitySell, etPriceBuy, etPriceSell, etFee;
    private TextView tvDate, tvTime;
    private Button btnSave;
    private ImageButton imgBtnAddPhoto;
    private ConstraintLayout viewBackgroung;
    private Spinner spinnerNameBuy, spinnerNameSell, spinnerCurrency;
    private ScrollView scrollView;
    private View view;
    private ArrayList<EditText> mandatoryField;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

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
        openCalendar();
        openClock();
        hideKeyBoardOnSpinnerTouch();

        spinnerNameBuy.setAdapter(getSpinnerAdapter(R.array.test, R.layout.spinner_item, R.layout.spinner_dropdown_item));
        spinnerNameSell.setAdapter(getSpinnerAdapter(R.array.test, R.layout.spinner_item, R.layout.spinner_dropdown_item));
        spinnerCurrency.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
        mandatoryField = new ArrayList<>(Arrays.asList(etQuantityBuy, etQuantitySell, etPriceBuy, etPriceSell,etFee));
        return view;
    }

    private void setupUIViews(){
        etQuantityBuy = view.findViewById(R.id.editTextQuantityChangeBuy);
        etQuantitySell = view.findViewById(R.id.editTextQuantityChangeSell);
        etPriceBuy = view.findViewById(R.id.editTextPriceChangeBuy);
        etPriceSell = view.findViewById(R.id.editTextPriceChangeSell);
        etFee = view.findViewById(R.id.editTextFeeChange);
        tvDate = view.findViewById(R.id.textViewDateChange);
        tvTime = view.findViewById(R.id.textViewTimeChange);
        spinnerNameBuy = view.findViewById(R.id.spinnerNameChangeBuy);
        spinnerNameSell = view.findViewById(R.id.spinnerNameChangeSell);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrencyChange);

        viewBackgroung = view.findViewById(R.id.fragmentBackgroundChange);
        viewBackgroung.setOnClickListener(this);

        scrollView = view.findViewById(R.id.scrollViewChange);

        btnSave = view.findViewById(R.id.buttonSaveChange);
        btnSave.setOnClickListener(this);
        imgBtnAddPhoto = view.findViewById(R.id.imgButtonAddPhotoChange);
        imgBtnAddPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonSaveChange:
                hideKeyBoard();
                if(!shakeEmpty() && checkDateAndTime()){
                    saveToDb();
                    clearEditText();
                    scrollView.setScrollY(0);
                    Toast.makeText(getContext(), "Transakce úspěšně vytvořena.", Toast.LENGTH_SHORT).show();
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
        transactionEntity.nameBought = spinnerNameBuy.getSelectedItem().toString();
        transactionEntity.currency = spinnerCurrency.getSelectedItem().toString();
        transactionEntity.quantityBought = etQuantityBuy.getText().toString();;
        transactionEntity.priceBought = etPriceBuy.getText().toString();;
        transactionEntity.fee = etFee.getText().toString();;
        transactionEntity.date = tvDate.getText().toString();
        transactionEntity.time = tvTime.getText().toString();
        transactionEntity.nameSold = spinnerNameSell.getSelectedItem().toString();
        transactionEntity.quantitySold = etQuantitySell.getText().toString();;
        transactionEntity.priceSold = etPriceSell.getText().toString();;

        db.databaseDao().insertTransaction(transactionEntity);
    }

    private void clearEditText(){
        etQuantityBuy.setText("");
        etQuantitySell.setText("");
        etPriceBuy.setText("");
        etPriceSell.setText("");
        etFee.setText("");
        tvDate.setText("");
    }

    private void openDateDialogWindow(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH); //day of month -> protože měsíce mají různý počet dní
        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener, year, month, day
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void openCalendar(){
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialogWindow();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                setDateToTextView(year, month, day);
            }
        };
    }

    private void setDateToTextView(int year, int month, int day){
        month = month + 1; // bere se od 0
        String date = day + "." + month + "." + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.yyyy");
        SimpleDateFormat dateFormatSecond = new SimpleDateFormat("dd.MM.yyyy");
        try{
            Date dateFormatToShow = dateFormat.parse(date);
            tvDate.setText(dateFormatSecond.format(dateFormatToShow));
        }
        catch (Exception e){
            System.err.println("Chyba při parsování data: "+e);
        }
    }

    public void openClock(){
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialogWindow();
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                setTimeToTextView(hour, minute);
            }
        };
    }

    private void openTimeDialogWindow(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(
                getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, timeSetListener, hour, minute, true
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
            tvTime.setText(dateFormatSecond.format(timeToShow));
        }
        catch (Exception e){
            System.err.println("Chyba při parsování času: "+e);
        }
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int itemId, int layoutId, int dropDownId){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), itemId, layoutId);
        spinnerAdapter.setDropDownViewResource(dropDownId);
        return spinnerAdapter;
    }

    private boolean checkDateAndTime(){
        Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        Date actualDate = getDateFormat(getActualDay());
        Date transactionDate = getDateFormat(tvDate.getText().toString());
        if(actualDate.compareTo(transactionDate) < 0){
            tvDate.startAnimation(animShake);
            tvDate.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text_empty));
            return false;
        }else if(actualDate.compareTo(transactionDate) == 0) {
            Date actualTime = getTimeFormat(getActualTime());
            Date transactionTime = getTimeFormat(tvTime.getText().toString());
            if (actualTime.compareTo(transactionTime) < 0) {
                tvTime.startAnimation(animShake);
                tvTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text_empty));
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

    // TODO formátování data globalizovat
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

    private boolean shakeEmpty(){
        Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        boolean findEmpty = false;
        for (EditText checking: mandatoryField) {
            if(checking.getText().toString().isEmpty()){
                findEmpty = true;
                checking.startAnimation(animShake);
                checking.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text_empty));
            }
            else{
                checking.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text));
            }
        }
        if(tvDate.getText().toString().isEmpty()){
            findEmpty = true;
            tvDate.startAnimation(animShake);
            tvDate.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text_empty));
        }else{
            tvDate.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text));
        }
        if(tvTime.getText().toString().isEmpty()){
            findEmpty = true;
            tvTime.startAnimation(animShake);
            tvTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text_empty));
        }else{
            tvTime.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text));
        }
        return findEmpty;
    }

    private void hideKeyBoardOnSpinnerTouch(){
        spinnerNameBuy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyBoard();
                return false;
            }
        });

        spinnerNameSell.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyBoard();
                return false;
            }
        });
    }

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}