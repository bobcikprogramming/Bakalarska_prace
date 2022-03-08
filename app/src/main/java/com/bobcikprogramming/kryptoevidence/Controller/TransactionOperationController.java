package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.OwnedCryptoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionOperationModel;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;

import java.util.ArrayList;
import java.util.List;

public class TransactionOperationController {

    private ArrayList<Uri> photos;
    private ArrayList<String> photosPath;
    private Context context;

    private ImageManager imgManager;
    private TransactionOperationModel database;
    private CalendarManager calendar;

    public TransactionOperationController(Context context){
        this.context = context;

        photos = new ArrayList<>();
        photosPath = new ArrayList<>();
        imgManager = new ImageManager();
        database = new TransactionOperationModel();
        calendar = new CalendarManager();
    }

    public boolean saveTransactionBuy(String shortName, String longName, String quantityBought, String price, String fee, String date, String time, String currency, String quantitySold){
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
        saveAmountOfOwnedCrypto(ownedCrypto, shortName, longName, quantity, operationType, change);
    }

    public void changeAmountOfOwnedCryptoOnEdit(String shortName, String longName, String quantity, int operationType, String... change){
        AppDatabase db = AppDatabase.getDbInstance(context);
        OwnedCryptoEntity ownedCrypto = db.databaseDao().getOwnedCryptoByID(shortName);
        if(ownedCrypto == null){
            return;
        }

        if(operationType == 2){
            saveAmountOfOwnedCrypto(ownedCrypto, shortName, longName, quantity, 0, null);
            saveAmountOfOwnedCrypto(ownedCrypto, change[0], change[1], change[2], 1, null);
        }else {
            saveAmountOfOwnedCrypto(ownedCrypto, shortName, longName, quantity, operationType, null);
        }
    }

    public void saveAmountOfOwnedCrypto(OwnedCryptoEntity ownedCrypto, String shortName, String longName, String quantity, int operationType, String[] change){
        AppDatabase db = AppDatabase.getDbInstance(context);
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
            amountChange -= Double.parseDouble(change[2]);
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

    /**
     * Při evidování nákupu dojde ke kontrole, zda-li není třeba nákup přidat k odpovídajícímu prodeji.
     * @param date
     * @param quantity
     */
    private void calcFifoOnBuy(long transactionID, String date, String quantity, String shortName, String time){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfUsedBuy = db.databaseDao().getUsedBuyAfterNewBuy(date, shortName);
        List<TransactionWithPhotos> listOfUsedSell = db.databaseDao().getUsedSellAfterNewBuy(date, shortName);

        Double amountLeft = Double.parseDouble(quantity);

        /**
         * TODO popřemýšlet jestli to neudělat jako SQL query
         */
        for(TransactionWithPhotos buyToReset : listOfUsedBuy){
            if(calendar.getDateFormat(buyToReset.transaction.date).equals(calendar.getDateFormat(date)) && !calendar.getTimeFormat(buyToReset.transaction.time).after(calendar.getTimeFormat(time))){
                System.out.println(">>>>>>>>>>Su tady?!");
                continue;
            }
            db.databaseDao().updateAmoutLeft(String.valueOf(buyToReset.transaction.uidTransaction), buyToReset.transaction.quantityBought);
            System.out.println(">>>>>>>>>> Obnovuju množství u nákupu na: "+buyToReset.transaction.quantityBought);
        }

        if(listOfUsedBuy.isEmpty()){
            System.out.println(">>>>>>>>>>>>proč?!");
        }

        for(TransactionWithPhotos sellToReset : listOfUsedSell){
            if(calendar.getDateFormat(sellToReset.transaction.date).equals(calendar.getDateFormat(date)) && !calendar.getTimeFormat(sellToReset.transaction.time).after(calendar.getTimeFormat(time))){
                continue;
            }
            db.databaseDao().updateFifoCalc(String.valueOf(sellToReset.transaction.uidTransaction), sellToReset.transaction.quantitySold, null, "-1");
        }

        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellNotEmptyAfterDate(date, shortName);

        for(TransactionWithPhotos sell : listOfIncompleteSales){
            if(calendar.getDateFormat(sell.transaction.date).equals(calendar.getDateFormat(date)) && calendar.getTimeFormat(sell.transaction.time).before(calendar.getTimeFormat(time))){
                continue;
            }
            if(amountLeft <= 0.0) {
                break;
            }

            Double usedFromFirst = sell.transaction.usedFromFirst;
            long firstTakenFrom = sell.transaction.firstTakenFrom;
            Double inSellLeft;

            inSellLeft = amountLeft <= sell.transaction.amountLeft ? sell.transaction.amountLeft - amountLeft : 0.0;
            amountLeft = amountLeft <= sell.transaction.amountLeft ? 0.0 : amountLeft - sell.transaction.amountLeft;
            if(usedFromFirst == null){
                firstTakenFrom = transactionID;
                usedFromFirst = sell.transaction.amountLeft - inSellLeft;
            }

            db.databaseDao().updateFifoCalc(String.valueOf(sell.transaction.uidTransaction), String.valueOf(inSellLeft), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom));

            if (inSellLeft > 0.0) {
                List<TransactionWithPhotos> listOfNextBuy = db.databaseDao().getBuyAfterNewBuy(date, shortName);

                for (TransactionWithPhotos nextBuy : listOfNextBuy) {
                    Double amountOfNextBuy = nextBuy.transaction.amountLeft;

                    if (transactionID == nextBuy.transaction.uidTransaction || amountOfNextBuy <= 0.0 || inSellLeft <= 0.0) {
                        continue;
                    }

                    if(inSellLeft <= amountOfNextBuy){
                        amountOfNextBuy -= inSellLeft;
                        inSellLeft = 0.0;
                    }else{
                        inSellLeft -= amountOfNextBuy;
                        amountOfNextBuy = 0.0;
                    }

                    db.databaseDao().updateAmoutLeft(String.valueOf(nextBuy.transaction.uidTransaction), String.valueOf(amountOfNextBuy));
                }
                db.databaseDao().updateAmoutLeft(String.valueOf(sell.transaction.uidTransaction), String.valueOf(inSellLeft));
            }
        }

        db.databaseDao().updateAmoutLeft(String.valueOf(transactionID), String.valueOf(amountLeft));
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
