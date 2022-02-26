package com.bobcikprogramming.kryptoevidence.View;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.Controller.CalendarManager;
import com.bobcikprogramming.kryptoevidence.Controller.ImageManager;
import com.bobcikprogramming.kryptoevidence.Controller.SharedMethods;
import com.bobcikprogramming.kryptoevidence.Model.TransactionOperations;
import com.bobcikprogramming.kryptoevidence.R;

import java.util.ArrayList;

public class AddTransactionTabBuy extends Fragment implements View.OnClickListener {

    private EditText etQuantity, etPrice, etFee;
    private TextView tvDate, tvTime, tvDesQuantity, tvDesPrice, tvDesDate, tvDesTime;
    private Button btnSave;
    private ImageView imvBtnShowPhoto, imgBtnAddPhoto;
    private LinearLayout viewBackgroung;
    private Spinner spinnerCurrency;
    private View view;

    private ArrayList<Uri> photos;
    private ArrayList<String> photosPath;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private String shortName, longName;

    private TransactionOperations database;
    private SharedMethods shared;
    private ImageManager imgManager;
    private CalendarManager calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_add_transaction_tab_buy, container, false);

        database = new TransactionOperations();
        shared = new SharedMethods();
        imgManager = new ImageManager();
        calendar = new CalendarManager();
        photos = new ArrayList<>();
        photosPath = new ArrayList<>();

        setupUIViews();
        hideKeyBoardOnSpinnerTouch();
        openCalendar();
        openClock();

        spinnerCurrency.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));

        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonSaveBuy:
                shared.hideKeyBoard(getActivity());
                if(!shakeEmpty() && calendar.checkDateAndTime(getContext(), tvDate, tvDesDate, tvTime, tvDesTime)){
                    if(!photos.isEmpty()){
                        photosPath = imgManager.saveImage(getContext(), photos);
                    }
                    if(photosPath != null) {
                        database.saveTransactionBuyToDb(getContext(), shortName, longName, shared.getString(etQuantity), shared.getString(etPrice), shared.getFeeString(etFee),
                                shared.getString(tvDate), shared.getString(tvTime), shared.getString(spinnerCurrency), shared.getPrice(etQuantity, etPrice, etFee), photosPath);
                        clearEditText();
                        Toast.makeText(getContext(), "Transakce byla úspěšně vytvořena.", Toast.LENGTH_SHORT).show();
                        closeActivity();
                    }else {
                        Toast.makeText(getContext(), "Chyba při vytváření transakce.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.fragmentBackgroundBuy:
                shared.hideKeyBoard(getActivity());
                break;
            case R.id.imgButtonAddPhotoBuy:
                androidGallery.launch("image/*");
                break;
            case R.id.imvButtonShowPhotoBuy:
                openPhotoViewerActivity();
                break;
        }
    }

    private void setupUIViews(){
        etQuantity = view.findViewById(R.id.editTextQuantityBuy);
        etPrice = view.findViewById(R.id.editTextPriceBuy);
        etFee = view.findViewById(R.id.editTextFeeBuy);
        tvDate = view.findViewById(R.id.textViewDateBuy);
        tvTime = view.findViewById(R.id.textViewTimeBuy);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrencyBuy);
        tvDesQuantity = view.findViewById(R.id.descriptionQuantityBuy);
        tvDesPrice = view.findViewById(R.id.descriptionPriceBuy);
        tvDesDate = view.findViewById(R.id.descriptionDateBuy);
        tvDesTime = view.findViewById(R.id.descriptionTimeBuy);

        viewBackgroung = view.findViewById(R.id.fragmentBackgroundBuy);
        viewBackgroung.setOnClickListener(this);

        btnSave = view.findViewById(R.id.buttonSaveBuy);
        imgBtnAddPhoto = view.findViewById(R.id.imgButtonAddPhotoBuy);
        imvBtnShowPhoto = view.findViewById(R.id.imvButtonShowPhotoBuy);

        btnSave.setOnClickListener(this);
        imgBtnAddPhoto.setOnClickListener(this);
        imvBtnShowPhoto.setOnClickListener(this);
    }

    public AddTransactionTabBuy(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }


    private void clearEditText(){
        etQuantity.setText("");
        etPrice.setText("");
        etFee.setText("");
        tvDate.setText("");
        tvTime.setText("");
    }

    private void closeActivity(){
        Intent intent = new Intent();
        intent.putExtra("close", true);
        intent.putExtra("changed", true);
        getActivity().setResult(RESULT_OK, intent );
        getActivity().finish();
    }

    /** https://stackoverflow.com/a/9768996*/
    private ArrayAdapter<CharSequence> getSpinnerAdapter(int itemId, int layoutId, int dropDownId){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), itemId, layoutId);
        spinnerAdapter.setDropDownViewResource(dropDownId);
        return spinnerAdapter;
    }

    private void openCalendar(){
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.openDateDialogWindow(getActivity(), dateSetListener, null);
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                tvDate.setText(calendar.returnDate(year, month, day));
            }
        };
    }

    private void openClock(){
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.openTimeDialogWindow(getActivity(), timeSetListener, null);
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                tvTime.setText(calendar.returnTime(hour, minute));
            }
        };
    }

    /** https://developer.android.com/training/basics/intents/result */
    ActivityResultLauncher<String> androidGallery = registerForActivityResult(
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
                }
            });

    ActivityResultLauncher<Intent> appGallery = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
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

    private void openPhotoViewerActivity(){
        Intent photoViewer = new Intent(getContext(), PhotoViewer.class);
        photoViewer.putParcelableArrayListExtra("photos",photos);
        appGallery.launch(photoViewer);
    }

    private boolean shakeEmpty(){
        boolean findEmpty = false;

        findEmpty = shared.checkIfEmptyAndShake(etQuantity, tvDesQuantity, findEmpty, getContext());
        findEmpty = shared.checkIfEmptyAndShake(etPrice, tvDesPrice, findEmpty, getContext());
        findEmpty = shared.checkIfEmptyAndShake(tvDate, tvDesDate, findEmpty, getContext());
        findEmpty = shared.checkIfEmptyAndShake(tvTime, tvDesTime, findEmpty, getContext());

        return findEmpty;
    }



    private void hideKeyBoardOnSpinnerTouch(){
        spinnerCurrency.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                shared.hideKeyBoard(getActivity());
                return false;
            }
        });
    }
}