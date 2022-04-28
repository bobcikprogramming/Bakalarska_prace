package com.bobcikprogramming.kryptoevidence.Model;

import android.content.Context;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class TransactionOperationModel {

    public TransactionOperationModel(){}

    /**
     * Metoda pro uložení transakce nákup do databáze.
     * @param context Třída context activity, ze které je metoda volána
     * @param uidBought ID koupené kryptoměny
     * @param quantityBought Koupené množství
     * @param price Cena
     * @param fee Poplatek
     * @param date Datum provedení
     * @param time Čas provedení
     * @param currency Měna
     * @param quantitySold Cena bez poplatku
     * @param photosPath Seznam cest k přiloženým snímkům
     * @return ID vytvořené transakce
     */
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

    /**
     * Metoda pro uložení transakce prodej do databáze.
     * @param context Třída context activity, ze které je metoda volána
     * @param uidSold ID prodané kryptoměny
     * @param quantitySold Prodané množství
     * @param price Cena
     * @param fee Poplatek
     * @param date Datum provedení
     * @param time Čas provedení
     * @param currency Měna
     * @param quantityBought Cena bez poplatku
     * @param photosPath Seznam cest k přiloženým snímkům
     * @return ID vytvořené transakce
     */
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

    /**
     * Metoda pro uložení transakce směna do databáze.
     * @param context Třída context activity, ze které je metoda volána
     * @param uidBought ID koupené kryptoměny
     * @param currency Měna
     * @param quantityBought Koupené množství
     * @param priceBought Cena koupené kryptoměny
     * @param fee Poplatek
     * @param date Datum provedení
     * @param time Čas provedení
     * @param uidSold ID prodané kryptoměny
     * @param quantitySold Prodané množství
     * @param photosPath Seznam cest k přiloženým snímkům
     * @return ID vytvořené transakce
     */
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

    /**
     * Metoda pro aktualizování množství vlastněné kryptoměny.
     * @param context Třída context activity, ze které je metoda volána
     * @param uid ID kryptoměny
     * @param amount Nová hodnota množství
     */
    public void updateAmountOfOwnedCrypto(Context context, String uid, BigDecimal amount){
        AppDatabase db = AppDatabase.getDbInstance(context);
        if(amount.compareTo(BigDecimal.ZERO) == 0){
            db.databaseDao().updateOwnedCrypto(uid, "0");
        }else{
            db.databaseDao().updateOwnedCrypto(uid, amount.toPlainString());
        }
    }

}
