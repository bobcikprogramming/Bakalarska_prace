package com.bobcikprogramming.kryptoevidence;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


public class TabBuyFragmentAdd extends Fragment implements View.OnClickListener {

    private EditText etQuantity, etPrice, etFee;
    private TextView tvDate;
    private Button btnSave;
    private ImageButton imgBtnAddPhoto;
    private ConstraintLayout viewBackgroung;
    private ScrollView scrollView;
    private Spinner spinnerCurrency, spinnerNameBuy;
    private View view;
    private ArrayList<EditText> mandatoryField;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    public TabBuyFragmentAdd() {
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
        view = inflater.inflate(R.layout.fragment_add_tab_buy, container, false);
        setupUIViews();
        openCalendar();
        ArrayAdapter<CharSequence> arrayAdapterCurrency = ArrayAdapter.createFromResource(getContext(), R.array.currency, R.layout.spinner_item);
        arrayAdapterCurrency.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ArrayAdapter<CharSequence> arrayAdapterName = ArrayAdapter.createFromResource(getContext(), R.array.test, R.layout.spinner_item);
        arrayAdapterName.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCurrency.setAdapter(arrayAdapterCurrency);
        spinnerNameBuy.setAdapter(arrayAdapterName);
        mandatoryField = new ArrayList<>(Arrays.asList(etQuantity,etPrice,etFee));
        return view;
    }

    private void setupUIViews(){
        etQuantity = view.findViewById(R.id.editTextQuantityBuy);
        etPrice = view.findViewById(R.id.editTextPriceBuy);
        etFee = view.findViewById(R.id.editTextFeeBuy);

        tvDate = view.findViewById(R.id.textViewDateBuy);

        spinnerNameBuy = view.findViewById(R.id.spinnerNameBuy);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrencyBuy);

        viewBackgroung = view.findViewById(R.id.fragmentBackgroundBuy);
        viewBackgroung.setOnClickListener(this);

        scrollView = view.findViewById(R.id.scrollViewBuy);

        btnSave = view.findViewById(R.id.buttonSaveBuy);
        btnSave.setOnClickListener(this);
        imgBtnAddPhoto = view.findViewById(R.id.imgButtonAddPhotoBuy);
        imgBtnAddPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonSaveBuy:
                hideKeyBoard();
                if(!shakeEmpty()){
                    saveToDb();
                    clearEditText();
                    scrollView.setScrollY(0);
                    Toast.makeText(getContext(), "Transakce úspěšně vytvořena.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fragmentBackgroundBuy:
                hideKeyBoard();

        }
    }

    private void saveToDb() {
        AppDatabase db = AppDatabase.getDbInstance(getContext());

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.transactionType = "Nákup";
        transactionEntity.nameBought = spinnerNameBuy.getSelectedItem().toString();
        transactionEntity.quantityBought = etQuantity.getText().toString();
        transactionEntity.priceBought = etPrice.getText().toString();
        transactionEntity.fee = etFee.getText().toString();
        transactionEntity.date = tvDate.getText().toString();
        transactionEntity.nameSold = spinnerCurrency.getSelectedItem().toString();
        transactionEntity.quantitySold = getPrice(etQuantity, etPrice, etFee);

        db.databaseDao().insertTransaction(transactionEntity);
    }

    private void clearEditText(){
        etQuantity.setText("");
        etPrice.setText("");
        etFee.setText("");
        tvDate.setText("");
    }

    private String getPrice(EditText etQuantity, EditText etPrice, EditText etFee) {
        double toRound = (editTextToDouble(etQuantity) * editTextToDouble(etPrice)) + editTextToDouble(etFee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return String.valueOf(result);
    }

    private Double editTextToDouble(EditText toParse){
        return Double.parseDouble(toParse.getText().toString());
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
        /*if(spinnerCurrency.getSelectedItem() == null){
            findEmpty = true;
            spinnerCurrency.startAnimation(animShake);
            spinnerCurrency.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text_empty));
        }else{
            spinnerCurrency.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_edit_text));
        }*/
        return findEmpty;
    }

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}