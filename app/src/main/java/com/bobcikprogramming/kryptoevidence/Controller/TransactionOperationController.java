package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.OwnedCryptoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionOperationModel;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionOperationController {

    private ArrayList<Uri> photos;
    private ArrayList<String> photosPath;
    private Context context;

    private ImageManager imgManager;
    private TransactionOperationModel database;
    private CalendarManager calendar;
    private SharedMethods shared;

    public TransactionOperationController(Context context){
        this.context = context;

        photos = new ArrayList<>();
        photosPath = new ArrayList<>();
        imgManager = new ImageManager();
        database = new TransactionOperationModel();
        calendar = new CalendarManager();
        shared = new SharedMethods();
    }

    public boolean saveTransactionBuy(String shortName, String longName, BigDecimal quantityBought, BigDecimal price, Double fee, String date, String time, String currency, BigDecimal quantitySold){
        if(!photos.isEmpty()){
            photosPath = imgManager.saveImage(context, photos);
        }
        if(photosPath != null) {
            long transactionID = database.saveTransactionBuyToDb(context, shortName, longName, quantityBought, price, fee, date, time, currency, quantitySold, photosPath);
            calcFifoOnBuy(transactionID, date, quantityBought, shortName, time);
            return true;
        }else {
            return false;
        }
    }

    public boolean saveTransactionSell(String shortName, String longName, BigDecimal quantitySold, BigDecimal price, Double fee, String date, String time, String currency, BigDecimal quantityBought){
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

    public boolean saveTransactionChange(String shortNameBought, String longNameBought, String currency, BigDecimal quantityBought, BigDecimal priceBought, Double fee, String date, String time, String shortNameSold, String longNameSold, BigDecimal quantitySold, BigDecimal priceSold){
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
    public void changeAmountOfOwnedCrypto(String shortName, String longName, BigDecimal quantity, int operationType, BigDecimal quantityChange, String... change){
        AppDatabase db = AppDatabase.getDbInstance(context);
        OwnedCryptoEntity ownedCrypto = db.databaseDao().getOwnedCryptoByID(shortName);
        saveAmountOfOwnedCrypto(ownedCrypto, shortName, longName, quantity, operationType, change, quantityChange);
    }

    public void changeAmountOfOwnedCryptoOnEdit(String shortName, String longName, BigDecimal quantity, int operationType, BigDecimal quantityChange, String... change){
        AppDatabase db = AppDatabase.getDbInstance(context);
        OwnedCryptoEntity ownedCrypto = db.databaseDao().getOwnedCryptoByID(shortName);
        if(ownedCrypto == null){
            return;
        }

        if(operationType == 2){
            saveAmountOfOwnedCrypto(ownedCrypto, shortName, longName, quantity, 0, null, null);
            saveAmountOfOwnedCrypto(ownedCrypto, change[0], change[1], quantityChange, 1, null, null);
        }else {
            saveAmountOfOwnedCrypto(ownedCrypto, shortName, longName, quantity, operationType, null, null);
        }
    }

    public void saveAmountOfOwnedCrypto(OwnedCryptoEntity ownedCrypto, String shortName, String longName, BigDecimal quantity, int operationType, String[] change, BigDecimal quantityChange){
        AppDatabase db = AppDatabase.getDbInstance(context);
        OwnedCryptoEntity ownedCryptoChange = null;
        BigDecimal amount = ownedCrypto == null ? shared.getBigDecimal("0.0") : shared.getBigDecimal(ownedCrypto.amount);
        BigDecimal amountChange = shared.getBigDecimal("0.0");

        if(operationType == 0){
            amount = amount.add(quantity);
        }else if(operationType == 1) {
            amount = amount.subtract(quantity);
        }else{
            amount = amount.add(quantity);
            ownedCryptoChange = db.databaseDao().getOwnedCryptoByID(change[0]);
            amountChange = ownedCryptoChange == null ? shared.getBigDecimal("0.0") : shared.getBigDecimal(ownedCryptoChange.amount);
            amountChange = amountChange.subtract(quantityChange);
        }

        if(ownedCrypto == null){
            database.createOwnedCryptoEntity(context, shortName, longName, amount);
        }else{
            database.updateOwnedCryptoEntity(context, amount, ownedCrypto);
        }

        if(operationType == 2){
            if(ownedCryptoChange == null){
                database.createOwnedCryptoEntity(context, change[0], change[1], amountChange);
            }else{
                database.updateOwnedCryptoEntity(context, amountChange, ownedCryptoChange);
            }
        }
    }

    /**
     * Při evidování nákupu dojde ke kontrole, zda-li není třeba nákup přidat k odpovídajícímu prodeji.
     * @param date
     * @param quantity
     */
    private void calcFifoOnBuy(long transactionID, String date, BigDecimal quantity, String shortName, String time){
        AppDatabase db = AppDatabase.getDbInstance(context);
        BigDecimal amountLeft = quantity;

        db.databaseDao().resetAmoutLeftUsedBuy(date, time);
        resetTransactionSellAfterNewBuy(String.valueOf(transactionID), shortName, date, time);

        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellNotEmptyAfterDate(date, time, shortName);

        for(TransactionWithPhotos sell : listOfIncompleteSales){
            BigDecimal usedFromFirst = sell.transaction.usedFromFirst == null ? null : shared.getBigDecimal(sell.transaction.usedFromFirst);
            long firstTakenFrom = sell.transaction.firstTakenFrom;
            BigDecimal inSellLeft = amountLeft.compareTo(shared.getBigDecimal(sell.transaction.amountLeft)) < 1 ? shared.getBigDecimal(sell.transaction.amountLeft).subtract(amountLeft) : shared.getBigDecimal("0.0");

            if(amountLeft.compareTo(shared.getBigDecimal("0.0")) == 1) {
                amountLeft = amountLeft.compareTo(shared.getBigDecimal(sell.transaction.amountLeft)) < 1 ? shared.getBigDecimal("0.0") : amountLeft.subtract(shared.getBigDecimal(sell.transaction.amountLeft));
                if (usedFromFirst == null) {
                    firstTakenFrom = transactionID;
                    usedFromFirst = shared.getBigDecimal(sell.transaction.amountLeft).subtract(inSellLeft);
                }

                db.databaseDao().updateFifoCalc(String.valueOf(sell.transaction.uidTransaction), String.valueOf(inSellLeft), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom));
            }

            if (inSellLeft.compareTo(shared.getBigDecimal("0.0")) == 1) {
                List<TransactionWithPhotos> listOfNextBuy = db.databaseDao().getBuyNotEmptyAfterNewBuy(date, time, shortName);

                for (TransactionWithPhotos nextBuy : listOfNextBuy) {
                    BigDecimal amountOfNextBuy = shared.getBigDecimal(nextBuy.transaction.amountLeft);

                    if(inSellLeft.compareTo(amountOfNextBuy) < 1){
                        amountOfNextBuy = amountOfNextBuy.subtract(inSellLeft);
                        inSellLeft = shared.getBigDecimal("0.0");
                    }else{
                        inSellLeft = inSellLeft.subtract(amountOfNextBuy);
                        amountOfNextBuy = shared.getBigDecimal("0.0");
                    }

                    if (usedFromFirst == null) {
                        firstTakenFrom = nextBuy.transaction.uidTransaction;
                        usedFromFirst = shared.getBigDecimal(sell.transaction.amountLeft).subtract(inSellLeft);
                    }

                    db.databaseDao().updateAmoutLeft(String.valueOf(nextBuy.transaction.uidTransaction), String.valueOf(amountOfNextBuy));
                }

                db.databaseDao().updateFifoCalc(String.valueOf(sell.transaction.uidTransaction), String.valueOf(inSellLeft), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom));
            }
        }

        db.databaseDao().updateAmoutLeft(String.valueOf(transactionID), String.valueOf(amountLeft));
    }

    private void resetTransactionSellAfterNewBuy(String transactionID, String shortName, String date, String time){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfUsedSell = db.databaseDao().getUsedSellAfterNewBuy(date, time, shortName);

        for(TransactionWithPhotos sellToReset : listOfUsedSell){
            String dateFrom = db.databaseDao().getTransactionByTransactionID(String.valueOf(sellToReset.transaction.firstTakenFrom)).transaction.date;
            List<TransactionWithPhotos> listOfUsedBuyBetween = db.databaseDao().getUsedBuyBetween(String.valueOf(transactionID), dateFrom, date, shortName);
            BigDecimal usedAmount = shared.getBigDecimal("0.0");

            for(TransactionWithPhotos used : listOfUsedBuyBetween){
                usedAmount = usedAmount.add((shared.getBigDecimal(used.transaction.quantityBought)).subtract(shared.getBigDecimal(used.transaction.amountLeft)));
            }

            BigDecimal toRemove = shared.getBigDecimal(sellToReset.transaction.quantitySold).subtract(usedAmount);
            String usedFromFirst = null;
            String firstTakenFrom = "-1";

            if(usedAmount.compareTo(shared.getBigDecimal("0.0")) != 0){
                usedFromFirst = sellToReset.transaction.usedFromFirst;
                firstTakenFrom = String.valueOf(sellToReset.transaction.firstTakenFrom);
            }

            db.databaseDao().updateFifoCalc(String.valueOf(sellToReset.transaction.uidTransaction), String.valueOf(toRemove), usedFromFirst, firstTakenFrom);
        }
    }

    private void calcFifoSell(){
        AppDatabase db = AppDatabase.getDbInstance(context);
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
