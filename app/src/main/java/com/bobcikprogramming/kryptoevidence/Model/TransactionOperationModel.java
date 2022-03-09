package com.bobcikprogramming.kryptoevidence.Model;

import android.content.Context;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TransactionOperationModel {

    public TransactionOperationModel(){}

    public long saveTransactionBuyToDb(Context context, String shortName, String longName, BigDecimal quantityBought, BigDecimal price, Double fee, String date, String time, String currency, BigDecimal quantitySold, ArrayList<String> photosPath) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();

        transactionEntity.transactionType = "Nákup";
        transactionEntity.shortNameBought = shortName;
        transactionEntity.longNameBought = longName;
        transactionEntity.quantityBought = String.valueOf(quantityBought);
        transactionEntity.priceBought = String.valueOf(price);
        transactionEntity.fee = fee;
        transactionEntity.date = date;
        transactionEntity.time = time;
        transactionEntity.currency = currency;
        transactionEntity.quantitySold = String.valueOf(quantitySold);

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }

        return uidTransaction;
    }

    public void saveTransactionSellToDb(Context context, String shortName, String longName, BigDecimal quantitySold, BigDecimal price, Double fee, String date, String time, String currency, BigDecimal quantityBought, ArrayList<String> photosPath) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();

        transactionEntity.transactionType = "Prodej";
        transactionEntity.shortNameSold = shortName;
        transactionEntity.longNameSold = longName;
        transactionEntity.quantitySold = String.valueOf(quantitySold);
        transactionEntity.priceSold = String.valueOf(price);
        transactionEntity.fee = fee;
        transactionEntity.date = date;
        transactionEntity.time = time;
        transactionEntity.currency = currency;
        transactionEntity.quantityBought = String.valueOf(quantityBought);

        transactionEntity.amountLeft = String.valueOf(quantitySold);

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }
    }

    public void saveTransactionChangeToDb(Context context, String shortNameBought, String longNameBought, String currency, BigDecimal quantityBought, BigDecimal priceBought, Double fee, String date, String time, String shortNameSold, String longNameSold, BigDecimal quantitySold, BigDecimal priceSold, ArrayList<String> photosPath) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();

        transactionEntity.transactionType = "Směna";
        transactionEntity.shortNameBought = shortNameBought;
        transactionEntity.longNameBought = longNameBought;
        transactionEntity.currency = currency;
        transactionEntity.quantityBought = String.valueOf(quantityBought);
        transactionEntity.priceBought = String.valueOf(priceBought);
        transactionEntity.fee = fee;
        transactionEntity.date = date;
        transactionEntity.time = time;
        transactionEntity.shortNameSold = shortNameSold;
        transactionEntity.longNameSold = longNameSold;
        transactionEntity.quantitySold = String.valueOf(quantitySold);
        transactionEntity.priceSold = String.valueOf(priceSold);

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }
    }

    public void createOwnedCryptoEntity(Context context, String shortName, String longName, BigDecimal amount){
        AppDatabase db = AppDatabase.getDbInstance(context);
        OwnedCryptoEntity ownedCrypto = new OwnedCryptoEntity();
        ownedCrypto.shortName = shortName;
        ownedCrypto.longName = longName;
        ownedCrypto.amount = String.valueOf(amount);
        db.databaseDao().insertOwnedCrypto(ownedCrypto);
    }

    public void updateOwnedCryptoEntity(Context context, BigDecimal amount, OwnedCryptoEntity ownedCrypto){
        AppDatabase db = AppDatabase.getDbInstance(context);
        ownedCrypto.amount = String.valueOf(amount);
        db.databaseDao().updateOwnedCrypto(ownedCrypto);
    }

}
