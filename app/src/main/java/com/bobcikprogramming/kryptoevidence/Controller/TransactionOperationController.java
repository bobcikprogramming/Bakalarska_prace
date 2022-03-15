package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.OwnedCryptoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionOperationModel;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionOperationController {

    private ArrayList<Uri> photos;
    private ArrayList<String> photosPath;
    private Context context;

    private ImageManager imgManager;
    private TransactionOperationModel database;
    private CalendarManager calendar;
    private SharedMethods shared;

    private BigDecimal EMPTYBIGDECIMAL;

    public TransactionOperationController(Context context){
        this.context = context;

        photos = new ArrayList<>();
        photosPath = new ArrayList<>();
        imgManager = new ImageManager();
        database = new TransactionOperationModel();
        calendar = new CalendarManager();
        shared = new SharedMethods();

        EMPTYBIGDECIMAL = shared.getBigDecimal("-1.0");
    }

    public boolean saveTransactionBuy(String shortName, String longName, BigDecimal quantityBought, BigDecimal price, Double fee, String date, String time, String currency, BigDecimal quantitySold){
        if(!photos.isEmpty()){
            photosPath = imgManager.saveImage(context, photos);
        }
        if(photosPath != null) {
            long transactionID = database.saveTransactionBuyToDb(context, shortName, longName, quantityBought, price, fee, date, time, currency, quantitySold, photosPath);
            calcFifoOnBuy(transactionID, quantityBought, date, time, shortName);
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
            long transactionID = database.saveTransactionSellToDb(context, shortName, longName, quantitySold, price, fee, date, time, currency, quantityBought, photosPath);
            calcFifoSell(transactionID, quantitySold, date, time, shortName);
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
        BigDecimal amount = ownedCrypto == null ? BigDecimal.ZERO : shared.getBigDecimal(ownedCrypto.amount);
        BigDecimal amountChange = BigDecimal.ZERO;

        if(operationType == 0){
            amount = amount.add(quantity);
        }else if(operationType == 1) {
            amount = amount.subtract(quantity);
        }else{
            amount = amount.add(quantity);
            ownedCryptoChange = db.databaseDao().getOwnedCryptoByID(change[0]);
            amountChange = ownedCryptoChange == null ? BigDecimal.ZERO : shared.getBigDecimal(ownedCryptoChange.amount);
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
    private void calcFifoOnBuy(long transactionID, BigDecimal quantity, String date, String time, String shortName){
        AppDatabase db = AppDatabase.getDbInstance(context);
        BigDecimal amountLeft = quantity;

        db.databaseDao().resetAmoutLeftUsedBuy(date, time);
        resetTransactionSellAfterNewBuy(String.valueOf(transactionID), shortName, date, time);

        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellNotEmptyFrom(date, time, shortName);

        for(TransactionWithPhotos sell : listOfIncompleteSales){
            BigDecimal usedFromFirst = sell.transaction.usedFromFirst.equals("-1.0") ? EMPTYBIGDECIMAL : shared.getBigDecimal(sell.transaction.usedFromFirst);
            long firstTakenFrom = sell.transaction.firstTakenFrom;
            long lastTakenFrom = sell.transaction.lastTakenFrom;
            BigDecimal inSellLeft = amountLeft.compareTo(shared.getBigDecimal(sell.transaction.amountLeft)) < 1 ? shared.getBigDecimal(sell.transaction.amountLeft).subtract(amountLeft) : BigDecimal.ZERO;

            if(amountLeft.compareTo(BigDecimal.ZERO) == 1) {
                amountLeft = amountLeft.compareTo(shared.getBigDecimal(sell.transaction.amountLeft)) < 1 ? BigDecimal.ZERO : amountLeft.subtract(shared.getBigDecimal(sell.transaction.amountLeft));
                if (usedFromFirst.compareTo(EMPTYBIGDECIMAL) == 0) {
                    firstTakenFrom = transactionID;
                    usedFromFirst = shared.getBigDecimal(sell.transaction.amountLeft).subtract(inSellLeft);
                }
                lastTakenFrom = transactionID;
            }

            if (inSellLeft.compareTo(BigDecimal.ZERO) == 1) {
                List<TransactionWithPhotos> listOfNextBuy = db.databaseDao().getNotEmptyBuyTo(sell.transaction.date, sell.transaction.time, shortName);

                for (TransactionWithPhotos nextBuy : listOfNextBuy) {
                    BigDecimal amountOfNextBuy = shared.getBigDecimal(nextBuy.transaction.amountLeft);

                    if(inSellLeft.compareTo(BigDecimal.ZERO) < 1){
                        break;
                    }

                    if(inSellLeft.compareTo(amountOfNextBuy) < 1){
                        amountOfNextBuy = amountOfNextBuy.subtract(inSellLeft);
                        inSellLeft = BigDecimal.ZERO;
                    }else{
                        inSellLeft = inSellLeft.subtract(amountOfNextBuy);
                        amountOfNextBuy = BigDecimal.ZERO;
                    }

                    if (usedFromFirst.compareTo(EMPTYBIGDECIMAL) == 0) {
                        firstTakenFrom = nextBuy.transaction.uidTransaction;
                        usedFromFirst = shared.getBigDecimal(sell.transaction.amountLeft).subtract(inSellLeft);
                    }

                    lastTakenFrom = nextBuy.transaction.uidTransaction;

                    db.databaseDao().updateAmoutLeft(String.valueOf(nextBuy.transaction.uidTransaction), String.valueOf(amountOfNextBuy));
                }

            }
            db.databaseDao().updateFifoCalc(String.valueOf(sell.transaction.uidTransaction), String.valueOf(inSellLeft), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
        }

        db.databaseDao().updateAmoutLeft(String.valueOf(transactionID), String.valueOf(amountLeft));
    }

    private void resetTransactionSellAfterNewBuy(String transactionID, String shortName, String date, String time){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfUsedSell = db.databaseDao().getUsedSellFrom(date, time, shortName);

        for(TransactionWithPhotos sellToReset : listOfUsedSell){
            TransactionWithPhotos firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(sellToReset.transaction.firstTakenFrom));
            String dateFrom = firstBuy.transaction.date;
            Date firstBuyDate = calendar.getDateFormat(dateFrom);
            Date newBuyDate = calendar.getDateFormat(date);
            String timeFrom = firstBuy.transaction.time;
            Date firstBuyTime = calendar.getTimeFormat(timeFrom);
            Date newBuyTime = calendar.getTimeFormat(time);
            /** Pokud je první nákup prováděn až po datu nového nákupu */
            if(firstBuyDate.after(newBuyDate) || (firstBuyDate.equals(newBuyDate) && firstBuyTime.after(newBuyTime))){
                db.databaseDao().updateFifoCalc(String.valueOf(sellToReset.transaction.uidTransaction), String.valueOf(sellToReset.transaction.quantitySold), "-1.0", "-1", "-1");
                return;
            }

            List<TransactionWithPhotos> listOfUsedBuyBetween = db.databaseDao().getUsedBuyBetweenWithoutFirstAndLast(String.valueOf(firstBuy.transaction.uidTransaction), String.valueOf(transactionID), dateFrom, timeFrom, date, time, shortName);
            BigDecimal restAmount = shared.getBigDecimal(sellToReset.transaction.quantitySold).subtract(shared.getBigDecimal(sellToReset.transaction.usedFromFirst));
            for(TransactionWithPhotos used : listOfUsedBuyBetween){
                if(restAmount.compareTo(BigDecimal.ZERO) < 1){
                    break;
                }
                
                if(shared.getBigDecimal(used.transaction.quantityBought).compareTo(restAmount) == 1){
                    restAmount = BigDecimal.ZERO;
                    break;
                }else{
                    restAmount = restAmount.subtract(shared.getBigDecimal(used.transaction.quantityBought));
                }
            }
            
            BigDecimal usedFromFirst = EMPTYBIGDECIMAL;
            long firstTakenFrom = -1;
            long lastTakenFrom = -1;

            if(!sellToReset.transaction.usedFromFirst.equals("-1.0")){
                usedFromFirst = shared.getBigDecimal(sellToReset.transaction.usedFromFirst);
                firstTakenFrom = sellToReset.transaction.firstTakenFrom;
                lastTakenFrom = sellToReset.transaction.lastTakenFrom;
            }

            db.databaseDao().updateFifoCalc(String.valueOf(sellToReset.transaction.uidTransaction), String.valueOf(restAmount), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
        }
    }

    private void calcFifoSell(long transactionID, BigDecimal quantity, String date, String time, String shortName){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfUsedSales = db.databaseDao().getUsedSellAfter(date, time, shortName);
        if(!listOfUsedSales.isEmpty()) {
            TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(listOfUsedSales.get(0).transaction.uidTransaction)).transaction;
            String startingDate = firstBuy.date;
            String startingTime = firstBuy.time;
            resetTransactionBuyAfterNewSell(shortName, startingDate, startingTime, listOfUsedSales);
        }

        db.databaseDao().resetAmoutLeftUsedSell(date, time);

        List<TransactionWithPhotos> listOfAvailableBuys = db.databaseDao().getNotEmptyBuyTo(date, time, shortName);
        setSellAndBuyForNewSell(String.valueOf(transactionID), quantity, listOfAvailableBuys);

        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellNotEmptyAfter(date, time, shortName);
        for(TransactionWithPhotos sell : listOfIncompleteSales) {
            listOfAvailableBuys = db.databaseDao().getNotEmptyBuyTo(sell.transaction.date, sell.transaction.time, shortName);
            if(listOfAvailableBuys.isEmpty()){
                break;
            }
            setSellAndBuyForNewSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeft), listOfAvailableBuys);
        }
    }

    private void resetTransactionBuyAfterNewSell(String shortName, String firstBuyDate, String firstBuyTime, List<TransactionWithPhotos> listOfUsedSales){
        AppDatabase db = AppDatabase.getDbInstance(context);

        long transactionIDOfFirstBuy = listOfUsedSales.get(0).transaction.firstTakenFrom;

        for(TransactionWithPhotos sell : listOfUsedSales){
            if(sell.transaction.firstTakenFrom != transactionIDOfFirstBuy){
                break;
            }
            db.databaseDao().updateAmoutLeftMathAdd(String.valueOf(transactionIDOfFirstBuy), sell.transaction.usedFromFirst);
        }

        List<TransactionWithPhotos> listOfBuys = db.databaseDao().getUsedBuyFrom(String.valueOf(transactionIDOfFirstBuy), firstBuyDate, firstBuyTime, shortName);

        for(TransactionWithPhotos buy : listOfBuys){
            db.databaseDao().resetAmountLeftBuyById(String.valueOf(buy.transaction.uidTransaction));
        }
    }

    private void setSellAndBuyForNewSell(String sellTransactionID, BigDecimal quantity, List<TransactionWithPhotos> listOfAvailableBuys ){
        AppDatabase db = AppDatabase.getDbInstance(context);
        boolean first = true;
        BigDecimal usedFromFirst = EMPTYBIGDECIMAL;
        long firstTakenFrom = -1;
        long lastTakenFrom = -1;


        for(TransactionWithPhotos buy : listOfAvailableBuys) {
            if(quantity.compareTo(BigDecimal.ZERO) < 1) {
                break;
            }

            BigDecimal newAmoutLeftBuy = shared.getBigDecimal(buy.transaction.amountLeft);
            if(shared.getBigDecimal(buy.transaction.amountLeft).compareTo(quantity) < 1){
                quantity = quantity.subtract(shared.getBigDecimal(buy.transaction.amountLeft));
                if(first){
                    usedFromFirst = newAmoutLeftBuy;
                }
                newAmoutLeftBuy = BigDecimal.ZERO;
            }else{
                newAmoutLeftBuy = newAmoutLeftBuy.subtract(quantity);
                if(first){
                    usedFromFirst = quantity;
                }
                quantity = BigDecimal.ZERO;
            }

            if(first){
                firstTakenFrom = buy.transaction.uidTransaction;
                first = false;
            }
            lastTakenFrom = buy.transaction.uidTransaction;
            db.databaseDao().updateAmoutLeft(String.valueOf(buy.transaction.uidTransaction), String.valueOf(newAmoutLeftBuy));
        }
        db.databaseDao().updateFifoCalc(String.valueOf(sellTransactionID), String.valueOf(quantity), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
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
