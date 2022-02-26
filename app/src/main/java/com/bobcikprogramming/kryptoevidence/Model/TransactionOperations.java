package com.bobcikprogramming.kryptoevidence.Model;

import android.content.Context;

import java.util.ArrayList;

public class TransactionOperations {

    public TransactionOperations(){}

    public void saveTransactionBuyToDb(Context context, String shortName, String longName, String quantityBought, String price, String fee, String date, String time, String currency, String quantitySold, ArrayList<String> photosPath) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();

        transactionEntity.transactionType = "Nákup";
        transactionEntity.shortNameBought = shortName;
        transactionEntity.longNameBought = longName;
        transactionEntity.quantityBought = quantityBought;
        transactionEntity.priceBought = price;
        transactionEntity.fee = fee;
        transactionEntity.date = date;
        transactionEntity.time = time;
        transactionEntity.currency = currency;
        transactionEntity.quantitySold = quantitySold;

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }
    }

    public void saveTransactionSellToDb(Context context, String shortName, String longName, String quantitySold, String price, String fee, String date, String time, String currency, String quantityBought, ArrayList<String> photosPath) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();

        transactionEntity.transactionType = "Prodej";
        transactionEntity.shortNameSold = shortName;
        transactionEntity.longNameSold = longName;
        transactionEntity.quantitySold = quantitySold;
        transactionEntity.priceSold = price;
        transactionEntity.fee = fee;
        transactionEntity.date = date;
        transactionEntity.time = time;
        transactionEntity.currency = currency;
        transactionEntity.quantityBought = quantityBought;

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }
    }

    public void saveTransactionChangeToDb(Context context, String shortNameBought, String longNameBought, String currency, String quantityBought, String priceBought, String fee, String date, String time, String shortNameSold, String longNameSold, String quantitySold, String priceSold, ArrayList<String> photosPath) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity transactionEntity = new TransactionEntity();
        PhotoEntity photoEntity = new PhotoEntity();

        transactionEntity.transactionType = "Směna";
        transactionEntity.shortNameBought = shortNameBought;
        transactionEntity.longNameBought = longNameBought;
        transactionEntity.currency = currency;
        transactionEntity.quantityBought = quantityBought;
        transactionEntity.priceBought = priceBought;
        transactionEntity.fee = fee;
        transactionEntity.date = date;
        transactionEntity.time = time;
        transactionEntity.shortNameSold = shortNameSold;
        transactionEntity.longNameSold = longNameSold;
        transactionEntity.quantitySold = quantitySold;
        transactionEntity.priceSold = priceSold;

        long uidTransaction = db.databaseDao().insertTransaction(transactionEntity);

        for(String path : photosPath){
            photoEntity.dest = path;
            photoEntity.transactionId = uidTransaction;
            db.databaseDao().insertPhoto(photoEntity);
        }
    }

}