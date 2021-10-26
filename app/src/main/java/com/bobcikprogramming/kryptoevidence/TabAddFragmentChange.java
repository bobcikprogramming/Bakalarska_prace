package com.bobcikprogramming.kryptoevidence;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.database.AppDatabase;
import com.bobcikprogramming.kryptoevidence.database.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TabAddFragmentChange extends Fragment implements View.OnClickListener {

    private EditText etQuantityBuy, etQuantitySell, etPriceBuy, etPriceSell, etFee;
    private TextView tvDate, tvTime, tvNameSell, tvDesQuantityBuy, tvDesQuantitySell, tvDesPriceBuy, tvDesPriceSell, tvDesDate, tvDesTime, tvDesNameSell;
    private Button btnSave;
    private ImageButton imgBtnAddPhoto;
    private ImageView imvBtnShowPhoto;
    //private ConstraintLayout viewBackgroung;
    private ScrollView scrollView;
    private LinearLayout viewBackgroung;
    private Spinner spinnerNameBuy, spinnerNameSell, spinnerCurrency;
    private View view;

    private ArrayList<Uri> photos;
    private ArrayList<String> photosPath;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private String shortNameCryptoBuy, longNameCryptoBuy, shortNameCryptoSell, longNameCryptoSell;

    public TabAddFragmentChange(String shortName, String longName) {
        this.shortNameCryptoBuy = shortName;
        this.longNameCryptoBuy = longName;
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
        //spinnerNameSell.setAdapter(getSpinnerAdapter(R.array.test, R.layout.spinner_item, R.layout.spinner_dropdown_item));
        spinnerCurrency.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));

        photos = new ArrayList<>();
        photosPath = new ArrayList<>();

        return view;
    }

    private void setupUIViews(){
        etQuantityBuy = view.findViewById(R.id.editTextQuantityChangeBuy);
        etQuantitySell = view.findViewById(R.id.editTextQuantityChangeSell);
        etPriceBuy = view.findViewById(R.id.editTextPriceChangeBuy);
        tvNameSell = view.findViewById(R.id.textViewNameChangeSell);
        etPriceSell = view.findViewById(R.id.editTextPriceChangeSell);
        etFee = view.findViewById(R.id.editTextFeeChange);
        tvDate = view.findViewById(R.id.textViewDateChange);
        tvTime = view.findViewById(R.id.textViewTimeChange);
        spinnerNameBuy = view.findViewById(R.id.spinnerNameChangeBuy);
        //spinnerNameSell = view.findViewById(R.id.spinnerNameChangeSell);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrencyChange);
        tvDesQuantityBuy = view.findViewById(R.id.descriptionQuantityChangeBuy);
        tvDesQuantitySell = view.findViewById(R.id.descriptionQuantityChangeSell);
        tvDesPriceBuy = view.findViewById(R.id.descriptionPriceChangeBuy);
        tvDesPriceSell = view.findViewById(R.id.descriptionPriceChangeSell);
        tvDesDate = view.findViewById(R.id.descriptionDateChange);
        tvDesTime = view.findViewById(R.id.descriptionTimeChange);
        tvDesNameSell = view.findViewById(R.id.descriptionNameChangeSell);

        viewBackgroung = view.findViewById(R.id.fragmentBackgroundChange);
        viewBackgroung.setOnClickListener(this);
        scrollView = view.findViewById(R.id.scrollViewChange);

        imvBtnShowPhoto = view.findViewById(R.id.imvButtonShowPhotoChange);

        btnSave = view.findViewById(R.id.buttonSaveChange);
        imgBtnAddPhoto = view.findViewById(R.id.imgButtonAddPhotoChange);

        btnSave.setOnClickListener(this);
        imgBtnAddPhoto.setOnClickListener(this);
        imvBtnShowPhoto.setOnClickListener(this);
        tvNameSell.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonSaveChange:
                hideKeyBoard();
                if(!shakeEmpty() && checkDateAndTime()){
                    boolean imgSaveSuccess = true;
                    if(!photos.isEmpty()){
                        imgSaveSuccess = saveImage();
                    }
                    if(imgSaveSuccess) {
                        saveToDb();
                        clearEditText();
                        Toast.makeText(getContext(), "Transakce byla úspěšně vytvořena.", Toast.LENGTH_SHORT).show();
                        photos.clear();
                        photosPath.clear();
                        imvBtnShowPhoto.setVisibility(View.GONE);
                        scrollView.setScrollY(0);
                        Intent intent = new Intent();
                        intent.putExtra("close", true);
                        intent.putExtra("changed", true);
                        getActivity().setResult(RESULT_OK, intent );
                        getActivity().finish();
                    }else {
                        Toast.makeText(getContext(), "Chyba při vytváření transakce.", Toast.LENGTH_SHORT).show();
                        photosPath.clear();
                    }
                }
                break;
            case R.id.fragmentBackgroundChange:
                hideKeyBoard();
                break;
            case R.id.imgButtonAddPhotoChange:
                mGetContent.launch("image/*");
                break;
            case R.id.imvButtonShowPhotoChange:
                openPhotoViewerActivity();
                break;
            case R.id.textViewNameChangeSell:
                Intent intent = new Intent(getContext(), CryptoChangeSelection.class);
                intent.putExtra("shortName", shortNameCryptoBuy);
                selectActivityResultLauncher.launch(intent);
        }
    }

    private void saveToDb() {
        AppDatabase db = AppDatabase.getDbInstance(getContext());
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();
        String transactionFee = etFee.getText().toString().isEmpty() ? "0.0" :  etFee.getText().toString();

        transactionEntity.transactionType = "Směna";
        transactionEntity.shortNameBought = shortNameCryptoBuy;
        transactionEntity.longNameBought = longNameCryptoBuy;
        transactionEntity.currency = spinnerCurrency.getSelectedItem().toString();
        transactionEntity.quantityBought = etQuantityBuy.getText().toString();
        transactionEntity.priceBought = etPriceBuy.getText().toString();
        transactionEntity.fee = transactionFee;
        transactionEntity.date = tvDate.getText().toString();
        transactionEntity.time = tvTime.getText().toString();
        transactionEntity.shortNameSold = shortNameCryptoSell;
        transactionEntity.longNameSold = longNameCryptoSell;
        transactionEntity.quantitySold = etQuantitySell.getText().toString();
        transactionEntity.priceSold = etPriceSell.getText().toString();

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }
    }

    // https://stackoverflow.com/a/17674787
    private boolean saveImage(){
        ContextWrapper cw = new ContextWrapper(getContext().getApplicationContext());
        File dir = cw.getDir("Images", getContext().MODE_PRIVATE);


        for(Uri photo : photos){
            Bitmap bitmap;
            FileOutputStream fos = null;

            File myPath = new File(dir, System.currentTimeMillis() + ".jpg");

            try {
                // https://stackoverflow.com/a/4717740
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photo);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            try {
                fos = new FileOutputStream(myPath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            photosPath.add(String.valueOf(myPath));

        }

        return true;
    }

    private void clearEditText(){
        etQuantityBuy.setText("");
        etQuantitySell.setText("");
        etPriceBuy.setText("");
        etPriceSell.setText("");
        etFee.setText("");
        tvDate.setText("");
        tvTime.setText("");
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
            tvDesDate.startAnimation(animShake);
            tvDesDate.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            return false;
        }else if(actualDate.compareTo(transactionDate) == 0) {
            Date actualTime = getTimeFormat(getActualTime());
            Date transactionTime = getTimeFormat(tvTime.getText().toString());
            if (actualTime.compareTo(transactionTime) < 0) {
                tvDesTime.startAnimation(animShake);
                tvDesTime.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
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

    // https://developer.android.com/training/basics/intents/result
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if(!photos.contains(uri) && uri != null){
                        photos.add(uri);
                    }
                    if(imvBtnShowPhoto.getVisibility() == View.GONE && photos.size() > 0){
                        imvBtnShowPhoto.setImageURI(photos.get(0));
                        imvBtnShowPhoto.setVisibility(View.VISIBLE);
                    }
                    /* Získání bitmapu z URI
                    try {
                        Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                        imvBtnShowPhoto.setImageBitmap(mBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            });

    private void openPhotoViewerActivity(){
        Intent photoViewer = new Intent(getContext(), PhotoViewer.class);
        photoViewer.putParcelableArrayListExtra("photos",photos);
        photoViewActivityResultLauncher.launch(photoViewer);
    }

    ActivityResultLauncher<Intent> photoViewActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        photos = data.getParcelableArrayListExtra("photos");
                        if(photos.size() > 0) {
                            imvBtnShowPhoto.setImageURI(photos.get(0));
                        }else{
                            imvBtnShowPhoto.setVisibility(View.GONE);
                        }
                    }
                }
            });

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
                        tvNameSell.setText(longNameCryptoSell);
                    }
                }
            });

    private boolean shakeEmpty(){
        Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        boolean findEmpty = false;

        if(etQuantityBuy.getText().toString().isEmpty()){
            findEmpty = true;
            tvDesQuantityBuy.startAnimation(animShake);
            tvDesQuantityBuy.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }else{
            tvDesQuantityBuy.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        if(tvNameSell.getText().toString().isEmpty()){
            findEmpty = true;
            tvDesNameSell.startAnimation(animShake);
            tvDesNameSell.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }else{
            tvDesNameSell.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        if(etQuantitySell.getText().toString().isEmpty()){
            findEmpty = true;
            tvDesQuantitySell.startAnimation(animShake);
            tvDesQuantitySell.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }else{
            tvDesQuantitySell.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        if(etPriceBuy.getText().toString().isEmpty()){
            findEmpty = true;
            tvDesPriceBuy.startAnimation(animShake);
            tvDesPriceBuy.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }else{
            tvDesPriceBuy.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        if(etPriceSell.getText().toString().isEmpty()){
            findEmpty = true;
            tvDesPriceSell.startAnimation(animShake);
            tvDesPriceSell.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }else{
            tvDesPriceSell.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        if(tvDate.getText().toString().isEmpty()){
            findEmpty = true;
            tvDesDate.startAnimation(animShake);
            tvDesDate.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }else{
            tvDesDate.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        if(tvTime.getText().toString().isEmpty()){
            findEmpty = true;
            tvDesTime.startAnimation(animShake);
            tvDesTime.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }else{
            tvDesTime.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
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

        /*spinnerNameSell.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyBoard();
                return false;
            }
        });*/
    }

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}