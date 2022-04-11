package com.bobcikprogramming.kryptoevidence.View;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
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
import com.bobcikprogramming.kryptoevidence.Controller.TransactionOperationController;
import com.bobcikprogramming.kryptoevidence.Model.TransactionOperationModel;
import com.bobcikprogramming.kryptoevidence.R;

import java.util.ArrayList;

public class AddTransactionTabChange extends Fragment implements View.OnClickListener {

    private EditText etQuantityBuy, etQuantitySell, etPriceBuy, etFee;
    private TextView tvDate, tvTime, tvNameSell, tvDesQuantityBuy, tvDesQuantitySell, tvDesPriceBuy, tvDesDate, tvDesTime, tvDesNameSell;
    private Button btnSave;
    private ImageView imvBtnShowPhoto, imgBtnAddPhoto;
    private LinearLayout viewBackgroung;
    private Spinner spinnerCurrency;
    private View view;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private TransactionOperationController controller;
    private SharedMethods shared;
    private CalendarManager calendar;

    private String uidCryptoBuy, uidCryptoSell;
    private String date, time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_add_transaction_tab_change, container, false);

        controller = new TransactionOperationController(getContext());
        shared = new SharedMethods();
        calendar = new CalendarManager();

        setupUIViews();
        date = null;
        time = null;
        openCalendar();
        openClock();

        spinnerCurrency.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));

        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonSaveChange:
                shared.hideKeyBoard(getActivity());
                if(!shakeEmpty() && calendar.checkDateAndTime(getContext(), tvDate, tvDesDate, tvTime, tvDesTime)){
                    boolean saved = controller.saveTransactionChange(uidCryptoBuy, shared.getString(spinnerCurrency),
                            shared.getBigDecimal(etQuantityBuy), shared.getBigDecimal(etPriceBuy), shared.getFee(etFee), calendar.getDateMillis(shared.getString(tvDate)), shared.getString(tvTime),
                            uidCryptoSell, shared.getBigDecimal(etQuantitySell));
                    if(saved){
                        controller.changeAmountOfOwnedCrypto(uidCryptoBuy, shared.getBigDecimal(etQuantityBuy), 2, uidCryptoSell, shared.getBigDecimal(etQuantitySell));
                        clearEditText();
                        Toast.makeText(getContext(), "Transakce byla úspěšně vytvořena.", Toast.LENGTH_SHORT).show();
                        closeActivity();
                    }else{
                        Toast.makeText(getContext(), "Chyba při vytváření transakce.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.fragmentBackgroundChange:
                shared.hideKeyBoard(getActivity());
                break;
            case R.id.imgButtonAddPhotoChange:
                androidGallery.launch("image/*");
                break;
            case R.id.imvButtonShowPhotoChange:
                openPhotoViewerActivity();
                break;
            case R.id.textViewNameChangeSell:
                Intent intent = new Intent(getContext(), CryptoChangeSelection.class);
                intent.putExtra("id", uidCryptoBuy);
                cryptoSelectionForSell.launch(intent);
        }
    }

    public AddTransactionTabChange(String uidCryptoBuy) {
        this.uidCryptoBuy = uidCryptoBuy;
    }

    private void setupUIViews(){
        etQuantityBuy = view.findViewById(R.id.editTextQuantityChangeBuy);
        etQuantitySell = view.findViewById(R.id.editTextQuantityChangeSell);
        etPriceBuy = view.findViewById(R.id.editTextPriceChangeBuy);
        tvNameSell = view.findViewById(R.id.textViewNameChangeSell);
        etFee = view.findViewById(R.id.editTextFeeChange);
        tvDate = view.findViewById(R.id.textViewDateChange);
        tvTime = view.findViewById(R.id.textViewTimeChange);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrencyChange);
        tvDesQuantityBuy = view.findViewById(R.id.descriptionQuantityChangeBuy);
        tvDesQuantitySell = view.findViewById(R.id.descriptionQuantityChangeSell);
        tvDesPriceBuy = view.findViewById(R.id.descriptionPriceChangeBuy);
        tvDesDate = view.findViewById(R.id.descriptionDateChange);
        tvDesTime = view.findViewById(R.id.descriptionTimeChange);
        tvDesNameSell = view.findViewById(R.id.descriptionNameChangeSell);

        viewBackgroung = view.findViewById(R.id.fragmentBackgroundChange);
        viewBackgroung.setOnClickListener(this);

        imvBtnShowPhoto = view.findViewById(R.id.imvButtonShowPhotoChange);

        btnSave = view.findViewById(R.id.buttonSaveChange);
        imgBtnAddPhoto = view.findViewById(R.id.imgButtonAddPhotoChange);

        btnSave.setOnClickListener(this);
        imgBtnAddPhoto.setOnClickListener(this);
        imvBtnShowPhoto.setOnClickListener(this);
        tvNameSell.setOnClickListener(this);
    }

    private void clearEditText(){
        etQuantityBuy.setText("");
        etQuantitySell.setText("");
        etPriceBuy.setText("");
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

    private void openCalendar(){
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.openDateDialogWindow(getActivity(), dateSetListener, date);
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                date = calendar.returnDate(year, month, day);
                tvDate.setText(date);
            }
        };
    }

    private void openClock(){
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.openTimeDialogWindow(getActivity(), timeSetListener, time);
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                time = calendar.returnTime(hour, minute);
                tvTime.setText(time);
            }
        };
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int itemId, int layoutId, int dropDownId){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), itemId, layoutId);
        spinnerAdapter.setDropDownViewResource(dropDownId);
        return spinnerAdapter;
    }

    // https://developer.android.com/training/basics/intents/result
    ActivityResultLauncher<String> androidGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if(!controller.photosContainsUri(uri) && uri != null){
                        controller.addToPhotos(uri);
                    }
                    if(imvBtnShowPhoto.getVisibility() == View.GONE && controller.photosSize() > 0){
                        imvBtnShowPhoto.setImageURI(controller.getPhotos().get(0));
                        imvBtnShowPhoto.setVisibility(View.VISIBLE);
                    }
                }
            });

    ActivityResultLauncher<Intent> appGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        controller.setPhotos(data.getParcelableArrayListExtra("photos"));
                        if(controller.photosSize() > 0) {
                            imvBtnShowPhoto.setImageURI(controller.getPhotos().get(0));
                        }else{
                            imvBtnShowPhoto.setVisibility(View.GONE);
                        }
                    }
                }
            });

    private void openPhotoViewerActivity(){
        Intent photoViewer = new Intent(getContext(), PhotoViewer.class);
        photoViewer.putParcelableArrayListExtra("photos", controller.getPhotos());
        appGallery.launch(photoViewer);
    }

    ActivityResultLauncher<Intent> cryptoSelectionForSell = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();
                    String onReturnUid = data.getStringExtra("uidCrypto");
                    String onReturnLong = data.getStringExtra("longName");
                    if(!onReturnUid.isEmpty()) {
                        uidCryptoSell = onReturnUid;
                        tvNameSell.setText(onReturnLong);
                    }
                }
            });

    private boolean shakeEmpty(){
        boolean findEmpty = false;

        findEmpty = shared.checkIfEmptyAndShake(etQuantityBuy, tvDesQuantityBuy, findEmpty, getContext());
        findEmpty = shared.checkIfEmptyAndShake(tvNameSell, tvDesNameSell, findEmpty, getContext());
        findEmpty = shared.checkIfEmptyAndShake(etQuantitySell, tvDesQuantitySell, findEmpty, getContext());
        findEmpty = shared.checkIfEmptyAndShake(etPriceBuy, tvDesPriceBuy, findEmpty, getContext());
        findEmpty = shared.checkIfEmptyAndShake(tvDate, tvDesDate, findEmpty, getContext());
        findEmpty = shared.checkIfEmptyAndShake(tvTime, tvDesTime, findEmpty, getContext());

        return findEmpty;
    }
}