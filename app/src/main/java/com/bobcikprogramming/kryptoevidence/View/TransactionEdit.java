package com.bobcikprogramming.kryptoevidence.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bobcikprogramming.kryptoevidence.Controller.CalendarManager;
import com.bobcikprogramming.kryptoevidence.Controller.SharedMethods;
import com.bobcikprogramming.kryptoevidence.Controller.TransactionEditController;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.R;

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

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private String transactionID;
    private String shortNameCryptoSell, longNameCryptoSell;
    private boolean photoChange;

    private SharedMethods shared;
    private CalendarManager calendar;
    private TransactionEditController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);

        photoChange = false;
        long transactionIDLong = (long) getIntent().getSerializableExtra("transactionID");
        this.transactionID = String.valueOf(transactionIDLong);

        shared = new SharedMethods();
        calendar = new CalendarManager();
        controller = new TransactionEditController(transactionID, this, transactionID);

        setupUIViews();
        setUIByTransactionType();
        setDataByTransactionType();
        openCalendar(controller.getDate());
        openClock(controller.getTime());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCancel:
                closeActivity(false, photoChange, false);
                break;
            case R.id.btnSave:
                shared.hideKeyBoard(this);
                confirmDialog();
                break;
            case R.id.btnDelete:
                shared.hideKeyBoard(this);
                confirmDialogDelete();
                break;
            case R.id.fragmentBackgroundEdit:
                shared.hideKeyBoard(this);
                break;
            case R.id.valueRowFourth:
                Intent intentSell = new Intent(this, CryptoChangeSelection.class);
                intentSell.putExtra("shortName", controller.getShortNameBought());
                openCryptoChangeSelection.launch(intentSell);
                break;
            case R.id.imgButtonAddPhoto:
                openGallery();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity(false, photoChange, false);
    }

    private void closeActivity(boolean changed, boolean photoChange, boolean deleted){
        Intent intent = new Intent();
        intent.putExtra("changed", changed);
        intent.putExtra("photoChange", photoChange);
        intent.putExtra("deleted", deleted);
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
        switch (controller.getTransactionType()){
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

                shortNameCryptoSell = controller.getShortNameSold();
                longNameCryptoSell = controller.getLongNameSold();
                break;
        }
    }

    private void setDataByTransactionType(){
        TransactionEntity transaction = controller.getTransactionEntity();
        switch (controller.getTransactionType()){
            case "Nákup":
                valueRowFirst.setText(transaction.quantityBought);
                valueRowSecond.setText(transaction.priceBought);
                spinnerRowThird.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                spinnerRowThird.setSelection(((ArrayAdapter) spinnerRowThird.getAdapter()).getPosition(transaction.currency)); /** <-- https://stackoverflow.com/a/11072595 */
                break;
            case "Prodej":
                valueRowFirst.setText(transaction.quantitySold);
                valueRowSecond.setText(transaction.priceSold);
                spinnerRowThird.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                spinnerRowThird.setSelection(((ArrayAdapter) spinnerRowThird.getAdapter()).getPosition(transaction.currency)); /** <-- https://stackoverflow.com/a/11072595 */
                break;
            case "Směna":
                valueRowFirst.setText(transaction.quantityBought);
                valueRowSecond.setText(transaction.priceBought);
                spinnerRowThird.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                spinnerRowThird.setSelection(((ArrayAdapter) spinnerRowThird.getAdapter()).getPosition(transaction.currency)); /** <-- https://stackoverflow.com/a/11072595 */
                valueRowFourth.setText(transaction.longNameSold);
                valueRowFifth.setText(transaction.quantitySold);
                valueRowSixth.setText(transaction.priceSold);
                break;
        }
        valueFee.setText(String.valueOf(transaction.fee));
        valueDate.setText(calendar.getDateFormatFromDatabase(transaction.date));
        valueTime.setText(transaction.time);
    }

    private ArrayAdapter<CharSequence> getSpinnerAdapter(int itemId, int layoutId, int dropDownId){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, itemId, layoutId);
        spinnerAdapter.setDropDownViewResource(dropDownId);
        return spinnerAdapter;
    }

    private boolean shakeEmptyBuySell(){
        boolean findEmpty = false;

        findEmpty = shared.checkIfEmptyAndShake(valueRowFirst, descRowFirst, findEmpty, this);
        findEmpty = shared.checkIfEmptyAndShake(valueRowSecond, descRowSecond, findEmpty, this);

        return findEmpty;
    }

    private boolean shakeEmptyChange(){
        boolean findEmpty = shakeEmptyBuySell();

        findEmpty = shared.checkIfEmptyAndShake(valueRowFifth, descRowFifth, findEmpty, this);
        findEmpty = shared.checkIfEmptyAndShake(valueRowSixth, descRowSixth, findEmpty, this);

        return findEmpty;
    }

    public void openCalendar(String date){
        valueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.openDateDialogWindow(TransactionEdit.this, dateSetListener, date);
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                valueDate.setText(calendar.returnDate(year, month, day));
            }
        };
    }

    public void openClock(String time){
        valueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.openTimeDialogWindow(TransactionEdit.this, timeSetListener, time);
            }
        });

        timeSetListener = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                valueTime.setText(calendar.returnTime(hour, minute));
            }
        };
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
                        //int success = updateDatabase();
                        controller.getUpdateStatus(valueRowFirst, valueRowSecond, spinnerRowThird, valueRowFifth, valueRowSixth, valueFee, valueDate, valueTime, shortNameCryptoSell, longNameCryptoSell);
                        boolean isEmpty = false;
                        if(controller.getTransactionType().equals("Směna")){
                            isEmpty = shakeEmptyChange();
                        }else{
                            isEmpty = shakeEmptyBuySell();
                        }
                        boolean success = controller.updateDatabase(isEmpty, valueDate, valueTime, descDate, descTime, valueNote);

                        if(success) {
                            closeActivity(true, photoChange, false);
                        }else{
                            closeActivity(false, photoChange, false);
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
                        controller.getUpdateStatus(valueRowFirst, valueRowSecond, spinnerRowThird, valueRowFifth, valueRowSixth, valueFee, valueDate, valueTime, shortNameCryptoSell, longNameCryptoSell);
                        controller.deleteFromDatabase();
                        closeActivity(true, true, true);
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

    ActivityResultLauncher<Intent> openCryptoChangeSelection = registerForActivityResult(
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
        if(controller.getPhotos().isEmpty()){
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
                    photoChange = controller.saveImageToDatabase(uri);
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
}