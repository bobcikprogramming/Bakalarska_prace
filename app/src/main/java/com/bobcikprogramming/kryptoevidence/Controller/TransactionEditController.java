package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionHistoryEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithHistory;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class TransactionEditController {

    private TransactionWithPhotos transactionWithPhotos;
    private TransactionWithHistory transactionWithHistory;
    private TransactionEntity newTransaction;
    private TransactionHistoryEntity transactionHistory;

    private SharedMethods shared;
    private CalendarManager calendar;
    private ImageManager imgManager;
    private TransactionOperationController transactionOperation;

    private String shortName, longName, shortNameChange, longNameChange;
    private BigDecimal quantityOld, quantityNew, quantityChangeOld, quantityChangeNew;
    private int operationType;
    private boolean changed;
    private Context context;
    private String transactionID;

    public TransactionEditController(String transactionId, Context context, String transactionID){
        this.context = context;
        this.transactionID = transactionID;

        shared = new SharedMethods();
        calendar = new CalendarManager();
        imgManager = new ImageManager();
        transactionOperation = new TransactionOperationController(context);

        loadDataFromDB(transactionId);
    }

    private void loadDataFromDB(String transactionID){
        AppDatabase db = AppDatabase.getDbInstance(context);
        transactionWithPhotos = db.databaseDao().getTransactionByTransactionID(transactionID);
        transactionWithHistory = db.databaseDao().getTransactionByTransactionHistoryID(transactionID);
    }

    public void getUpdateStatus(EditText valueRowFirst, EditText valueRowSecond, Spinner spinnerRowThird, EditText valueRowFifth, EditText valueRowSixth, EditText valueFee, TextView valueDate, TextView valueTime, String shortNameCryptoSell, String longNameCryptoSell){
        TransactionEntity transaction = getTransactionEntity();
        newTransaction = new TransactionEntity();
        transactionHistory = new TransactionHistoryEntity();
        newTransaction.uidTransaction = transaction.uidTransaction;
        newTransaction.transactionType = transaction.transactionType;

        changed = false;

        if(getTransactionType().equals("Nákup") || getTransactionType().equals("Prodej")) {
            if (transaction.transactionType.equals("Nákup")) {
                newTransaction.shortNameBought = transaction.shortNameBought;
                newTransaction.longNameBought = transaction.longNameBought;
                newTransaction.quantityBought = shared.getStringFromBigDecimal(valueRowFirst);
                newTransaction.priceBought = shared.getStringFromBigDecimal(valueRowSecond);
                newTransaction.quantitySold = String.valueOf(shared.getPrice(valueRowFirst, valueRowSecond, valueFee));

                operationType = 0;
                shortName = transaction.shortNameBought;
                longName = transaction.longNameBought;
                quantityOld = shared.getBigDecimal(transaction.quantityBought);
                quantityNew = shared.getBigDecimal(newTransaction.quantityBought);

                if(newTransaction.quantityBought.compareTo(transaction.quantityBought) != 0){
                    transactionHistory.quantityBought = transaction.quantityBought;
                    changed = true;
                }
                if(newTransaction.priceBought.compareTo(transaction.priceBought) != 0){
                    transactionHistory.priceBought = transaction.priceBought;
                    changed = true;
                }
                if(newTransaction.quantitySold.compareTo(transaction.quantitySold) != 0){
                    transactionHistory.quantitySold = transaction.quantitySold;
                    changed = true;
                }
            }else {
                newTransaction.shortNameSold = transaction.shortNameSold;
                newTransaction.longNameSold = transaction.longNameSold;
                newTransaction.quantitySold = shared.getStringFromBigDecimal(valueRowFirst);
                newTransaction.priceSold = shared.getStringFromBigDecimal(valueRowSecond);
                newTransaction.quantityBought = String.valueOf(shared.getProfit(valueRowFirst, valueRowSecond, valueFee));

                operationType = 1;
                shortName = transaction.shortNameSold;
                longName = transaction.longNameSold;
                quantityOld = shared.getBigDecimal(transaction.quantitySold);
                quantityNew = shared.getBigDecimal(newTransaction.quantitySold);


                if(newTransaction.quantitySold.compareTo(transaction.quantitySold) != 0){
                    transactionHistory.quantitySold = transaction.quantitySold;
                    changed = true;
                }
                if(newTransaction.priceSold.compareTo(transaction.priceSold) != 0){
                    transactionHistory.priceSold = transaction.priceSold;
                    changed = true;
                }
                if(newTransaction.quantityBought.compareTo(transaction.quantityBought) != 0){
                    transactionHistory.quantityBought = transaction.quantityBought;
                    changed = true;
                }
            }
            newTransaction.currency = shared.getString(spinnerRowThird);
            newTransaction.fee = shared.getFee(valueFee);
            newTransaction.date = shared.getString(valueDate);
            newTransaction.time = shared.getString(valueTime);

            transactionHistory.transactionType = transaction.transactionType;

            if(!newTransaction.currency.equals(transaction.currency)){
                transactionHistory.currency = transaction.currency;
                changed = true;
            }
            if(newTransaction.fee.compareTo(transaction.fee) != 0){
                transactionHistory.fee = transaction.fee;
                changed = true;
            }
            if(!newTransaction.date.equals(transaction.date)){
                transactionHistory.date = transaction.date;
                changed = true;
            }
            if(!newTransaction.time.equals(transaction.time)){
                transactionHistory.time = transaction.time;
                changed = true;
            }
        }else if(getTransactionType().equals("Směna")){
            newTransaction.uidTransaction = transaction.uidTransaction;
            newTransaction.transactionType = transaction.transactionType;
            newTransaction.shortNameBought = transaction.shortNameBought;
            newTransaction.longNameBought = transaction.longNameBought;
            newTransaction.quantityBought = shared.getStringFromBigDecimal(valueRowFirst);
            newTransaction.priceBought =  shared.getStringFromBigDecimal(valueRowSecond);
            newTransaction.currency =  shared.getString(spinnerRowThird);
            newTransaction.shortNameSold = shortNameCryptoSell == null ? transaction.shortNameSold : shortNameCryptoSell;
            newTransaction.longNameSold = longNameCryptoSell == null ? transaction.longNameSold : longNameCryptoSell;
            newTransaction.quantitySold = shared.getStringFromBigDecimal(valueRowFifth);
            newTransaction.priceSold = shared.getStringFromBigDecimal(valueRowSixth);
            newTransaction.fee = shared.getFee(valueFee);
            newTransaction.date = shared.getString(valueDate);
            newTransaction.time = shared.getString(valueTime);

            operationType = 2;
            shortName = transaction.shortNameBought;
            longName = transaction.longNameBought;
            quantityOld = shared.getBigDecimal(transaction.quantityBought);
            quantityNew = shared.getBigDecimal(newTransaction.quantityBought);
            shortNameChange = transaction.shortNameSold;
            longNameChange = transaction.longNameSold;
            quantityChangeOld = shared.getBigDecimal(transaction.quantitySold);
            quantityChangeNew = shared.getBigDecimal(newTransaction.quantitySold);

            transactionHistory.transactionType = transaction.transactionType;
            if(newTransaction.quantityBought.compareTo(transaction.quantityBought) != 0) {
                transactionHistory.quantityBought = transaction.quantityBought;
                changed = true;
            }
            if(newTransaction.priceBought.compareTo(transaction.priceBought) != 0){
                transactionHistory.priceBought = transaction.priceBought;
                changed = true;
            }
            if (!newTransaction.currency.equals(transaction.currency)) {
                transactionHistory.currency = transaction.currency;
                changed = true;
            }
            if (!newTransaction.shortNameSold.equals(transaction.shortNameSold)) {
                transactionHistory.shortNameSold = transaction.shortNameSold;
                changed = true;
            }
            if (!newTransaction.longNameSold.equals(transaction.longNameSold)) {
                transactionHistory.longNameSold = transaction.longNameSold;
                changed = true;
            }
            if(newTransaction.quantitySold.compareTo(transaction.quantitySold) != 0){
                transactionHistory.quantitySold = transaction.quantitySold;
                changed = true;
            }
            if(newTransaction.priceSold.compareTo(transaction.priceSold) != 0){
                transactionHistory.priceSold = transaction.priceSold;
                changed = true;
            }
            if (newTransaction.fee.compareTo(transaction.fee) != 0) {
                transactionHistory.fee = transaction.fee;
                changed = true;
            }
            if (!newTransaction.date.equals(transaction.date)) {
                transactionHistory.date = transaction.date;
                changed = true;
            }
            if (!newTransaction.time.equals(transaction.time)) {
                transactionHistory.time = transaction.time;
                changed = true;
            }
        }
    }

    public boolean updateDatabase(boolean isEmpty, TextView valueDate, TextView valueTime, TextView descDate, TextView descTime, EditText valueNote){
        AppDatabase db = AppDatabase.getDbInstance(context);
        boolean dateAndTimeCorrect = calendar.checkDateAndTime(context, valueDate, descDate, valueTime, descTime);

        if(!isEmpty && changed && dateAndTimeCorrect){
            transactionHistory.dateOfChange = calendar.getActualDay();
            transactionHistory.timeOfChange = calendar.getActualTime();
            if (!shared.getString(valueNote).isEmpty()) {
                transactionHistory.note = shared.getString(valueNote);
            }

            editOwnedCrypto();

            transactionHistory.parentTransactionId = newTransaction.uidTransaction;
            db.databaseDao().insertOldTransaction(transactionHistory);
            db.databaseDao().updateTransaction(newTransaction);
            transactionWithPhotos.transaction = newTransaction;

            return true;
        }
        return false;
    }

    private void editOwnedCrypto(){
        if(operationType != 2){
            if(quantityOld.compareTo(quantityNew) != 0){
                BigDecimal quantity = quantityNew.subtract(quantityOld);
                transactionOperation.changeAmountOfOwnedCryptoOnEdit(shortName, longName, quantity, operationType, null);
            }
        }else{
            if(!quantityOld.equals(quantityNew)){
                BigDecimal quantity = quantityNew.subtract(quantityOld);
                transactionOperation.changeAmountOfOwnedCryptoOnEdit(shortName, longName, quantity, 0, null);
            }
            if(!quantityChangeOld.equals(quantityChangeNew)){
                BigDecimal quantity =quantityChangeNew.subtract(quantityChangeOld);
                transactionOperation.changeAmountOfOwnedCryptoOnEdit(shortNameChange, longNameChange, quantity, 1, null);
            }
        }
    }

    private void deleteFromOwnedCrypto(){
        if(operationType == 0) {
            BigDecimal quantity = getNegativeQuantity(getTransactionEntity().quantityBought);
            transactionOperation.changeAmountOfOwnedCrypto(getTransactionEntity().shortNameBought, getTransactionEntity().longNameBought, quantity, 0, null);
        }else if(operationType == 1) {
            BigDecimal quantity = getNegativeQuantity(getTransactionEntity().quantitySold);
            transactionOperation.changeAmountOfOwnedCrypto(getTransactionEntity().shortNameSold, getTransactionEntity().longNameSold, quantity, 1, null);
        }else {
            BigDecimal quantityBought = getNegativeQuantity(getTransactionEntity().quantityBought);
            BigDecimal quantitySold = getNegativeQuantity(getTransactionEntity().quantitySold);
            transactionOperation.changeAmountOfOwnedCrypto(getTransactionEntity().shortNameBought, getTransactionEntity().longNameBought, quantityBought, 2,
                    quantitySold, getTransactionEntity().shortNameSold, getTransactionEntity().longNameSold);
        }
    }

    private BigDecimal getNegativeQuantity(String quantity){
        return shared.getBigDecimal(quantity).multiply(shared.getBigDecimal("-1"));
    }

    public void deleteFromDatabase(){
        AppDatabase db = AppDatabase.getDbInstance(context);

        List<PhotoEntity> photos = db.databaseDao().getPhotoByTransactionID(transactionID);

        for(PhotoEntity photo : photos) {
            deleteImage(photo.dest);
        }

        deleteFromOwnedCrypto();

        db.databaseDao().deleteHistory(transactionID);
        db.databaseDao().deletePhotos(transactionID);
        db.databaseDao().deleteTransactionTable(transactionID);
    }

    /** https://stackoverflow.com/a/10716773 */
    private void deleteImage(String path){
        File toDelete = new File(path);
        if(toDelete.exists()){
            toDelete.delete();
        }
    }

    public boolean saveImageToDatabase(Uri uri){
        AppDatabase db = AppDatabase.getDbInstance(context);
        PhotoEntity photoEntity = new PhotoEntity();

        String path = imgManager.saveImage(context, uri);
        if (!path.isEmpty()) {
            photoEntity.dest = path;
            photoEntity.transactionId = Long.parseLong(transactionID);
            db.databaseDao().insertPhoto(photoEntity);
            return true;
        }
        return false;
    }

    public TransactionEntity getTransactionEntity() {
        return transactionWithPhotos.transaction;
    }

    public String getTransactionType(){
        return getTransactionEntity().transactionType;
    }

    public List<PhotoEntity> getPhotos(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        return db.databaseDao().getPhotoByTransactionID(transactionID);
    }

    public String getShortNameBought(){
        return transactionWithHistory.transaction.shortNameBought;
    }

    public String getDate(){
        return getTransactionEntity().date;
    }

    public String getTime(){
        return getTransactionEntity().time;
    }

    public String getShortNameSold(){
        return transactionWithHistory.transaction.shortNameSold;
    }

    public String getLongNameSold(){
        return transactionWithHistory.transaction.longNameSold;
    }
}
