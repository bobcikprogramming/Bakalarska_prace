package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.OwnedCryptoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionOperationModel;

import java.util.ArrayList;

public class TransactionOperationController {

    private ArrayList<Uri> photos;
    private ArrayList<String> photosPath;
    private Context context;

    private ImageManager imgManager;
    private TransactionOperationModel database;

    public TransactionOperationController(Context context){
        this.context = context;

        photos = new ArrayList<>();
        photosPath = new ArrayList<>();
        imgManager = new ImageManager();
        database = new TransactionOperationModel();
    }

    public boolean saveTransactionBuy(String shortName, String longName, String quantityBought, String price, String fee, String date, String time, String currency, String quantitySold){
        if(!photos.isEmpty()){
            photosPath = imgManager.saveImage(context, photos);
        }
        if(photosPath != null) {
            database.saveTransactionBuyToDb(context, shortName, longName, quantityBought, price, fee, date, time, currency, quantitySold, photosPath);
            return true;
        }else {
            return false;
        }
    }

    public boolean saveTransactionSell(String shortName, String longName, String quantitySold, String price, String fee, String date, String time, String currency, String quantityBought){
        if(!photos.isEmpty()){
            photosPath = imgManager.saveImage(context, photos);
        }
        if(photosPath != null) {
            database.saveTransactionSellToDb(context, shortName, longName, quantitySold, price, fee, date, time, currency, quantityBought, photosPath);
            return true;
        }else {
            return false;
        }
    }

    public boolean saveTransactionChange(String shortNameBought, String longNameBought, String currency, String quantityBought, String priceBought, String fee, String date, String time, String shortNameSold, String longNameSold, String quantitySold, String priceSold){
        if(!photos.isEmpty()){
            photosPath = imgManager.saveImage(context, photos);
        }
        if(photosPath != null) {
            database.saveTransactionChangeToDb(context, shortNameBought, longNameBought, currency, quantityBought, priceBought, fee, date, time, shortNameSold, longNameSold, quantitySold, priceSold, photosPath);
            return true;
        }else {
            return false;
        }
    }

    /**
     *
     * @param shortName
     * @param longName
     * @param quantity
     * @param operationType typ prováděné operace (0 = nákup, 1 = prodej, 2 = směna)
     */
    public void changeAmountOfOwnedCrypto(String shortName, String longName, String quantity, int operationType, String... change){
        AppDatabase db = AppDatabase.getDbInstance(context);
        OwnedCryptoEntity ownedCrypto = db.databaseDao().getOwnedCryptoByID(shortName);
        OwnedCryptoEntity ownedCryptoChange = null;
        double amount = ownedCrypto == null ? 0.0 : Double.parseDouble(ownedCrypto.amount);
        double amountChange = 0.0;

        if(operationType == 0){
            amount += Double.parseDouble(quantity);
        }else if(operationType == 1) {
            amount -= Double.parseDouble(quantity);
        }else{
            amount += Double.parseDouble(quantity);
            ownedCryptoChange = db.databaseDao().getOwnedCryptoByID(change[0]);
            amountChange = ownedCryptoChange == null ? 0.0 : Double.parseDouble(ownedCryptoChange.amount);
            amountChange -= Integer.parseInt(change[2]);
        }

        if(ownedCrypto == null){
            database.createOwnedCryptoEntity(context, shortName, longName, String.valueOf(amount));
        }else{
            database.updateOwnedCryptoEntity(context, String.valueOf(amount), ownedCrypto);
        }

        if(operationType == 2){
            if(ownedCryptoChange == null){
                database.createOwnedCryptoEntity(context, change[0], change[1], String.valueOf(amountChange));
            }else{
                database.updateOwnedCryptoEntity(context, String.valueOf(amountChange), ownedCryptoChange);
            }
        }
    }

    public ArrayList<Uri> getPhotos() {
        return photos;
    }

    public void addToPhotos(Uri uri) {
        photos.add(uri);
    }

    public void setPhotos(ArrayList<Uri> photos) {
        this.photos = photos;
    }

    public boolean photosContainsUri(Uri uri){
        return photos.contains(uri);
    }

    public int photosSize(){
        return photos.size();
    }
}
