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
import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.R;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class TransactionEdit extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnCancel, btnDelete, btnSave;
    private TextView descRowFirst, descRowSecond, descRowThird, descRowFourth, descRowFifth, descRowSixth, descDate, descTime;
    private EditText valueRowFirst, valueRowSecond, valueRowFifth, valueRowSixth, valueFee, valueNote;
    private TextView valueDate, valueTime, valueRowFourth;
    private Spinner spinnerRowThird, spinnerRowSeventh;
    private ImageView imgButtonAddPhoto;
    private LinearLayout layoutRowSecond, layoutRowThird, layoutRowFourth, layoutRowFifth, layoutRowSixth, layoutRowSeventh;
    private LinearLayout underlineRowSecond, underlineRowThird, underlineRowFourth, underlineRowFifth, underlineRowSixth, underlineRowSeventh;
    private LinearLayout fragmentBackgroundEdit;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    private String transactionID;
    private String uidSell;
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

    /**
     * Metoda zpracovávající reakci na kliknutí na daný prvek.
     * @param view Základní prvek UI komponent
     */
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
                intentSell.putExtra("id", controller.getIdBought());
                openCryptoChangeSelection.launch(intentSell);
                break;
            case R.id.imgButtonAddPhoto:
                openGallery();
                break;
        }
    }

    /**
     * Metoda reagující na kliknutí na nativní android tlačítko zpět a uzavírající activity.
     */
    @Override
    public void onBackPressed() {
        closeActivity(false, photoChange, false);
    }

    /**
     * Metoda pro ukončení activity. Jsou navráceny boolean hodnoty pod klíči "changed", "photoChange" a "deleted".
     */
    private void closeActivity(boolean changed, boolean photoChange, boolean deleted){
        Intent intent = new Intent();
        intent.putExtra("changed", changed);
        intent.putExtra("photoChange", photoChange);
        intent.putExtra("deleted", deleted);
        setResult(RESULT_OK, intent );
        finish();
    }

    /**
     * Metoda pro inicializování prvků UI.
     */
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
        spinnerRowSeventh = findViewById(R.id.spinnerRowSeventh);
        valueFee = findViewById(R.id.valueFee);
        valueDate = findViewById(R.id.valueDate);
        valueTime = findViewById(R.id.valueTime);
        valueNote = findViewById(R.id.valueNote);

        valueRowFourth.setOnClickListener(this);

        imgButtonAddPhoto = findViewById(R.id.imgButtonAddPhoto);
        imgButtonAddPhoto.setOnClickListener(this);

        layoutRowSecond = findViewById(R.id.layoutRowSecond);
        layoutRowThird = findViewById(R.id.layoutRowThird);
        layoutRowFourth = findViewById(R.id.layoutRowFourth);
        layoutRowFifth = findViewById(R.id.layoutRowFifth);
        layoutRowSixth = findViewById(R.id.layoutRowSixth);
        layoutRowSeventh = findViewById(R.id.layoutRowSeventh);

        underlineRowSecond = findViewById(R.id.underlineRowSecond);
        underlineRowThird = findViewById(R.id.underlineRowThird);
        underlineRowFourth = findViewById(R.id.underlineRowFourth);
        underlineRowFifth = findViewById(R.id.underlineRowFifth);
        underlineRowSixth = findViewById(R.id.underlineRowSixth);
        underlineRowSeventh = findViewById(R.id.underlineRowSeventh);

        fragmentBackgroundEdit = findViewById(R.id.fragmentBackgroundEdit);
        fragmentBackgroundEdit.setOnClickListener(this);

    }

    /**
     * Metoda pro nastavení prvků UI podle typu transakce.
     */
    private void setUIByTransactionType(){
        switch (controller.getTransactionType()){
            case "Nákup":
                layoutRowFourth.setVisibility(View.GONE);
                layoutRowFifth.setVisibility(View.GONE);
                layoutRowSixth.setVisibility(View.GONE);
                layoutRowSeventh.setVisibility(View.GONE);
                underlineRowFourth.setVisibility(View.GONE);
                underlineRowFifth.setVisibility(View.GONE);
                underlineRowSixth.setVisibility(View.GONE);
                underlineRowSeventh.setVisibility(View.GONE);
                descRowFirst.setText("Koupené množství");
                descRowSecond.setText("Pořizovací cena");
                descRowThird.setText("Cena v měně");
                break;
            case "Prodej":
                layoutRowFourth.setVisibility(View.GONE);
                layoutRowFifth.setVisibility(View.GONE);
                layoutRowSixth.setVisibility(View.GONE);
                layoutRowSeventh.setVisibility(View.GONE);
                underlineRowFourth.setVisibility(View.GONE);
                underlineRowFifth.setVisibility(View.GONE);
                underlineRowSixth.setVisibility(View.GONE);
                underlineRowSeventh.setVisibility(View.GONE);
                descRowFirst.setText("Prodané množství");
                descRowSecond.setText("Prodejní cena");
                descRowThird.setText("Cena v měně");
                break;
            case "Směna":
                layoutRowSecond.setVisibility(View.GONE);
                layoutRowThird.setVisibility(View.GONE);
                layoutRowFourth.setVisibility(View.VISIBLE);
                layoutRowFifth.setVisibility(View.VISIBLE);
                layoutRowFourth.setVisibility(View.VISIBLE);
                layoutRowFifth.setVisibility(View.VISIBLE);
                layoutRowSixth.setVisibility(View.VISIBLE);
                layoutRowSeventh.setVisibility(View.VISIBLE);
                underlineRowSecond.setVisibility(View.GONE);
                underlineRowThird.setVisibility(View.GONE);
                underlineRowFourth.setVisibility(View.VISIBLE);
                underlineRowFifth.setVisibility(View.VISIBLE);
                underlineRowSixth.setVisibility(View.VISIBLE);
                underlineRowSeventh.setVisibility(View.VISIBLE);
                descRowSixth.setText("Cena směny");

                uidSell = controller.getIdSold();
                break;
        }
    }

    /**
     * Metoda pro nastavení hodnoty UI prvků dle typu transakce.
     */
    private void setDataByTransactionType(){
        TransactionEntity transaction = controller.getTransactionEntity();
        switch (controller.getTransactionType()){
            case "Nákup":
                valueRowFirst.setText(shared.getBigDecimal(transaction.quantityBought).toPlainString()); /** <-- https://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html#toPlainString%28%29 */
                valueRowSecond.setText(shared.getBigDecimal(transaction.priceBought).toPlainString());
                spinnerRowThird.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                spinnerRowThird.setSelection(((ArrayAdapter) spinnerRowThird.getAdapter()).getPosition(transaction.currency)); /** <-- https://stackoverflow.com/a/11072595 */
                break;
            case "Prodej":
                valueRowFirst.setText(shared.getBigDecimal(transaction.quantitySold).toPlainString());
                valueRowSecond.setText(shared.getBigDecimal(transaction.priceSold).toPlainString());
                spinnerRowThird.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                spinnerRowThird.setSelection(((ArrayAdapter) spinnerRowThird.getAdapter()).getPosition(transaction.currency)); /** <-- https://stackoverflow.com/a/11072595 */
                break;
            case "Směna":
                valueRowFirst.setText(shared.getBigDecimal(transaction.quantityBought).toPlainString());
                spinnerRowSeventh.setAdapter(getSpinnerAdapter(R.array.currency, R.layout.spinner_item, R.layout.spinner_dropdown_item));
                spinnerRowSeventh.setSelection(((ArrayAdapter) spinnerRowSeventh.getAdapter()).getPosition(transaction.currency)); /** <-- https://stackoverflow.com/a/11072595 */
                valueRowFourth.setText(AppDatabase.getDbInstance(this).databaseDao().getCryptoById(transaction.uidSold).longName);
                valueRowFifth.setText(shared.getBigDecimal(transaction.quantitySold).toPlainString());
                valueRowSixth.setText(shared.getBigDecimal(transaction.priceBought).toPlainString());
                break;
        }
        valueFee.setText(String.valueOf(transaction.fee));
        valueDate.setText(calendar.getDateFromMillis(transaction.date));
        valueTime.setText(transaction.time);
    }

    /**
     * Metoda pro získání adaptéru prvku spinner.
     * @param itemId UI pro položky spinneru
     * @param layoutId UI pro layout spinneru
     * @param dropDownId UI pro layout otevřeného spinneru
     * @return Adaptér spinneru
     */
    private ArrayAdapter<CharSequence> getSpinnerAdapter(int itemId, int layoutId, int dropDownId){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, itemId, layoutId);
        spinnerAdapter.setDropDownViewResource(dropDownId);
        return spinnerAdapter;
    }

    /**
     * Metoda pro kontrolu, zda-li jsou všechna povinná pole vyplněna.
     * @return true - vyplněna, jinak false
     */
    private boolean shakeEmptyBuySell(){
        boolean findEmpty = false;

        findEmpty = shared.checkIfEmptyAndShake(valueRowFirst, descRowFirst, findEmpty, this);
        findEmpty = shared.checkIfEmptyAndShake(valueRowSecond, descRowSecond, findEmpty, this);

        return findEmpty;
    }

    /**
     * Metoda pro kontrolu, zda-li jsou všechna povinná pole vyplněna.
     * @return true - vyplněna, jinak false
     */
    private boolean shakeEmptyChange(){
        boolean findEmpty = false;

        findEmpty = shared.checkIfEmptyAndShake(valueRowFirst, descRowFirst, findEmpty, this);
        findEmpty = shared.checkIfEmptyAndShake(valueRowFifth, descRowFifth, findEmpty, this);
        findEmpty = shared.checkIfEmptyAndShake(valueRowSixth, descRowSixth, findEmpty, this);

        return findEmpty;
    }

    /**
     * Metoda pro výběr data pomocí dialogového okna.
     */
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

    /**
     * Metoda pro výběr času pomocí dialogového okna.
     */
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

    /**
     * Metoda pro otevření dialogového okna k potvrzení uložení změn.
     */
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
                        controller.getUpdateStatus(valueRowFirst, valueRowSecond, spinnerRowThird, valueRowFifth, valueRowSixth, spinnerRowSeventh, valueFee, valueDate, valueTime, uidSell);
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

    /**
     * Metoda pro otevření dialogového okna pro potvrzení smazání transakce.
     */
    private void confirmDialogDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setCancelable(true);
        builder.setTitle("Smazat transakci");
        builder.setMessage("Opravdu chcete smazat transakci?");
        builder.setPositiveButton("Smazat",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        controller.getUpdateStatus(valueRowFirst, valueRowSecond, spinnerRowThird, valueRowFifth, valueRowSixth, spinnerRowSeventh, valueFee, valueDate, valueTime, uidSell);
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

    /**
     * Metoda zpracující návrat z aktivity.
     */
    ActivityResultLauncher<Intent> openCryptoChangeSelection = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                String onReturnUid = data.getStringExtra("uidCrypto");
                String onReturnLong = data.getStringExtra("longName");
                if(!onReturnUid.isEmpty()) {
                    uidSell = onReturnUid;
                    valueRowFourth.setText(onReturnLong);
                }
            }
        });

    /**
     * Metoda pro otevření nativní android galerii, neobsahuje-li transakce snímky,
     * jinak k otevření activity TransactionEditPhotoViewer.
     */
    private void openGallery(){
        if(controller.getPhotos().isEmpty()){
            androidGallery.launch("image/*");
        }else{
            Intent gallery = new Intent(TransactionEdit.this, TransactionEditPhotoViewer.class);
            gallery.putExtra("transactionID",transactionID);
            appGallery.launch(gallery);
        }
    }

    /**
     * Metoda zpracující návrat z aktivity.
     */
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

    /**
     * Metoda zpracující návrat z aktivity.
     */
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