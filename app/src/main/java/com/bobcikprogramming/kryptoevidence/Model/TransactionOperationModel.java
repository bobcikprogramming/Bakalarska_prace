package com.bobcikprogramming.kryptoevidence.Model;

import android.content.Context;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TransactionOperationModel {

    public TransactionOperationModel(){}

    public long saveTransactionBuyToDb(Context context, String uidBought, BigDecimal quantityBought, BigDecimal price, Double fee, long date, String time, String currency, BigDecimal quantitySold, ArrayList<String> photosPath) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();

        transactionEntity.transactionType = "Nákup";
        transactionEntity.uidBought = uidBought;
        transactionEntity.quantityBought = quantityBought.toPlainString();
        transactionEntity.priceBought = price.toPlainString();
        transactionEntity.fee = fee;
        transactionEntity.date = date;
        transactionEntity.time = time;
        transactionEntity.currency = currency;
        transactionEntity.quantitySold = quantitySold.toPlainString();
        transactionEntity.firstTakenFrom = 0;

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }

        return uidTransaction;
    }

    public long saveTransactionSellToDb(Context context, String uidSold, BigDecimal quantitySold, BigDecimal price, Double fee, long date, String time, String currency, BigDecimal quantityBought, ArrayList<String> photosPath) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();

        transactionEntity.transactionType = "Prodej";
        transactionEntity.uidSold = uidSold;
        transactionEntity.quantitySold = quantitySold.toPlainString();
        transactionEntity.priceSold = price.toPlainString();
        transactionEntity.fee = fee;
        transactionEntity.date = date;
        transactionEntity.time = time;
        transactionEntity.currency = currency;
        transactionEntity.quantityBought = quantityBought.toPlainString();

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }

        return uidTransaction;
    }

    public long saveTransactionChangeToDb(Context context, String uidBought, String currency, BigDecimal quantityBought, BigDecimal priceBought, Double fee, long date, String time, String uidSold, BigDecimal quantitySold, ArrayList<String> photosPath) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();

        transactionEntity.transactionType = "Směna";
        transactionEntity.uidBought = uidBought;
        transactionEntity.currency = currency;
        transactionEntity.quantityBought = quantityBought.toPlainString();
        transactionEntity.priceBought = priceBought.toPlainString();
        transactionEntity.fee = fee;
        transactionEntity.date = date;
        transactionEntity.time = time;
        transactionEntity.uidSold = uidSold;
        transactionEntity.quantitySold = quantitySold.toPlainString();

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }

        return uidTransaction;
    }

    public void updateAmountOfOwnedCrypto(Context context, String uid, BigDecimal amount){
        AppDatabase db = AppDatabase.getDbInstance(context);
        if(amount.compareTo(BigDecimal.ZERO) == 0){
            db.databaseDao().updateOwnedCrypto(uid, "0");
        }else{
            db.databaseDao().updateOwnedCrypto(uid, amount.toPlainString());
        }
    }

}
