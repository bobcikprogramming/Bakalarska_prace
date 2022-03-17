package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;
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

    private BigDecimal EMPTYBIGDECIMAL;
    private boolean isBuy, isSell, isChange, dateOrTimeChange, quantityChange;

    public TransactionEditController(String transactionId, Context context, String transactionID){
        this.context = context;
        this.transactionID = transactionID;

        shared = new SharedMethods();
        calendar = new CalendarManager();
        imgManager = new ImageManager();
        transactionOperation = new TransactionOperationController(context);

        EMPTYBIGDECIMAL = shared.getBigDecimal("-1.0");

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
                isBuy = true;
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
                    quantityChange = true;
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
                isSell = true;
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
                    quantityChange = true;
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
                dateOrTimeChange = true;
                changed = true;
            }
            if(!newTransaction.time.equals(transaction.time)){
                transactionHistory.time = transaction.time;
                dateOrTimeChange = true;
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

            if(isBuy){
                if(dateOrTimeChange){
                    if(quantityChange){

                    }else{

                    }
                }else{
                    if(quantityChange){
                        fifoEditAmountBuy(String.valueOf(newTransaction.uidTransaction), newTransaction.date, newTransaction.time, newTransaction.shortNameBought, newTransaction.quantityBought);
                    }
                }
            }else if(isSell){
                if(dateOrTimeChange){
                    if(quantityChange){

                    }else{

                    }
                }else{
                    if(quantityChange){
                        fifoEditAmountSell(String.valueOf(newTransaction.uidTransaction), newTransaction.date, newTransaction.time, newTransaction.shortNameSold, newTransaction.quantitySold);
                    }
                }
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
        TransactionEntity toRemove = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        if(toRemove.transactionType.equals("Nákup")) {
            fifoDeleteBuy(transactionID, toRemove.date, toRemove.time, toRemove.shortNameBought);
        }else{
            fifoDeleteSell(transactionID, toRemove.date, toRemove.time, toRemove.shortNameSold);
        }

        List<PhotoEntity> photos = db.databaseDao().getPhotoByTransactionID(transactionID);

        for(PhotoEntity photo : photos) {
            deleteImage(photo.dest);
        }

        deleteFromOwnedCrypto();

        db.databaseDao().deleteHistory(transactionID);
        db.databaseDao().deletePhotos(transactionID);
        db.databaseDao().deleteTransactionTable(transactionID);
    }

    private void fifoDeleteBuy(String transactionID, String date, String time, String shortName){
        AppDatabase db = AppDatabase.getDbInstance(context);
        //List<TransactionWithPhotos> usedBuys = db.databaseDao().getUsedBuyFrom(transactionID ,date, time, shortName);

        // Všechny prodeje od prvního prodeje který je na tomto nákupu resetovat. První je potřeba udělat zvlášť.
        resetSales(transactionID, date, time, shortName);

        // Resetovat všechny nákupy následující po odstraněném.
        db.databaseDao().resetAmountLeftBuyAfterFirst(transactionID, date, time, shortName);
        /*for(TransactionWithPhotos buy : usedBuys) { // TODO predelat na sql query
            db.databaseDao().resetAmountLeftBuyById(String.valueOf(buy.transaction.uidTransaction));
        }*/

        // Smazat daný nákup.
        db.databaseDao().setTransactionToDeleteById(transactionID);

        // Přepočítat.
        recalculateForEditBuy(date, time, shortName);
    }

    private void resetSales(String transactionID, String date, String time, String shortName){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfSales = db.databaseDao().getUsedSellFrom(date, time, shortName);
        TransactionEntity firstSell = null;

        for(TransactionWithPhotos sell : listOfSales){
            String lastTakenBuyID = String.valueOf(sell.transaction.lastTakenFrom);
            if(!db.databaseDao().findIfExistBuyWithIdForUsedBuyAfter(transactionID, date, time, shortName, lastTakenBuyID).isEmpty()){
                firstSell = sell.transaction;
                break;
            }
        }

        if(firstSell != null) {
            db.databaseDao().resetAmoutLeftUsedSellAfterFirst(String.valueOf(firstSell.uidTransaction), firstSell.date, firstSell.time, shortName);
            /** Pro první zjistit kolik bylo vzato z prodeje před smazaným prodejem a tuto hodnotu odečíst od obnoveného množství */
            TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(firstSell.firstTakenFrom)).transaction;
            String dateFrom = firstBuy.date;
            String timeFrom = firstBuy.time;
            List<TransactionWithPhotos> listOfUsedBuyBetween = db.databaseDao().getUsedBuyBetweenWithoutFirstAndLast(String.valueOf(firstSell.firstTakenFrom), transactionID, dateFrom, timeFrom, date, time, shortName);

            BigDecimal restAmount;
            if(listOfUsedBuyBetween.isEmpty()){
                BigDecimal usedFromFirst = EMPTYBIGDECIMAL;
                long firstTakenFrom = -1;
                long lastTakenFrom = -1;
                if (!db.databaseDao().findIfExistBuyWithIdForUsedBuyAfter(transactionID, date, time, shortName, String.valueOf(firstSell.firstTakenFrom)).isEmpty()) {
                    restAmount = shared.getBigDecimal(firstSell.quantitySold);
                } else {
                    restAmount = shared.getBigDecimal(firstSell.quantitySold).subtract(shared.getBigDecimal(firstSell.usedFromFirst));
                    usedFromFirst = shared.getBigDecimal(firstSell.usedFromFirst);
                    firstTakenFrom = firstSell.firstTakenFrom;
                }
                db.databaseDao().updateFifoCalc(String.valueOf(firstSell.uidTransaction), String.valueOf(restAmount), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
            }else {
                restAmount = shared.getBigDecimal(firstSell.quantitySold).subtract(shared.getBigDecimal(firstSell.usedFromFirst));

                for (TransactionWithPhotos used : listOfUsedBuyBetween) {
                    if (restAmount.compareTo(BigDecimal.ZERO) < 1) {
                        break;
                    }

                    if (shared.getBigDecimal(used.transaction.quantityBought).compareTo(restAmount) == 1) {
                        restAmount = BigDecimal.ZERO;
                        break;
                    } else {
                        restAmount = restAmount.subtract(shared.getBigDecimal(used.transaction.quantityBought));
                    }
                }

                BigDecimal usedFromFirst = shared.getBigDecimal(firstSell.usedFromFirst);
                long firstTakenFrom = firstSell.firstTakenFrom;
                long lastTakenFrom = -1;

                if (firstSell.firstTakenFrom == Long.parseLong(transactionID)) {
                    usedFromFirst = EMPTYBIGDECIMAL;
                    firstTakenFrom = -1;
                }
                db.databaseDao().updateFifoCalc(String.valueOf(firstSell.uidTransaction), String.valueOf(restAmount), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
            }
        }
    }

    private void recalculateForEditBuy(String date, String time, String shortName){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellNotEmptyFrom(date, time, shortName);

        for(TransactionWithPhotos sell : listOfIncompleteSales){
            BigDecimal usedFromFirst = sell.transaction.usedFromFirst.equals("-1.0") ? EMPTYBIGDECIMAL : shared.getBigDecimal(sell.transaction.usedFromFirst);
            long firstTakenFrom = sell.transaction.firstTakenFrom;
            long lastTakenFrom = sell.transaction.lastTakenFrom;
            BigDecimal inSellLeft = shared.getBigDecimal(sell.transaction.amountLeft);

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

            db.databaseDao().updateFifoCalc(String.valueOf(sell.transaction.uidTransaction), String.valueOf(inSellLeft), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
        }
    }

    private void fifoDeleteSell(String transactionID, String date, String time, String shortName){
        // Brát prodeje v cyklu a dokud další prodej nezačíná na jiné transakci než na té, kde první, a přičíst usedFromFirst k prvnímu nákupu
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfUsedSales = db.databaseDao().getUsedSellAllFromFirst(transactionID, date, time, shortName);
        TransactionEntity firstSell = listOfUsedSales.get(0).transaction;
        int i = 0;
        do {
            db.databaseDao().updateAmoutLeftMathAdd(String.valueOf(listOfUsedSales.get(i).transaction.firstTakenFrom), listOfUsedSales.get(i).transaction.usedFromFirst);
            i++;
        }while(i < listOfUsedSales.size() && listOfUsedSales.get(i).transaction.firstTakenFrom == firstSell.firstTakenFrom);

        // Obnovit zbylé nákupy.
        TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(firstSell.firstTakenFrom)).transaction;
        db.databaseDao().resetAmountLeftBuyAfterFirst(String.valueOf(firstBuy.uidTransaction), firstBuy.date, firstBuy.time, shortName);

        // Resetovat všechny prodeje od daného data
        db.databaseDao().resetAmoutLeftUsedSellAfterFirst(transactionID, date, time, shortName);

        // Smazat daný prodej
        db.databaseDao().setTransactionToDeleteById(transactionID);

        // Přepočítat
        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellNotEmptyAfterFirst(transactionID, date, time, shortName);
        for(TransactionWithPhotos sell : listOfIncompleteSales) {
            List<TransactionWithPhotos> listOfAvailableBuys = db.databaseDao().getNotEmptyBuyTo(sell.transaction.date, sell.transaction.time, shortName);
            if(listOfAvailableBuys.isEmpty()){
                break;
            }

            recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeft), listOfAvailableBuys);
        }
    }

    private void recalculateForRemoveSell(String sellTransactionID, BigDecimal quantity, List<TransactionWithPhotos> listOfAvailableBuys){
        AppDatabase db = AppDatabase.getDbInstance(context);
        boolean first = true;
        BigDecimal usedFromFirst = EMPTYBIGDECIMAL;
        long firstTakenFrom = -1;
        long lastTakenFrom = -1;


        for (TransactionWithPhotos buy : listOfAvailableBuys) {
            if (quantity.compareTo(BigDecimal.ZERO) < 1) {
                break;
            }

            BigDecimal newAmoutLeftBuy = shared.getBigDecimal(buy.transaction.amountLeft);
            if (newAmoutLeftBuy.compareTo(quantity) < 1) {
                quantity = quantity.subtract(newAmoutLeftBuy);
                if (first) {
                    usedFromFirst = newAmoutLeftBuy;
                }
                newAmoutLeftBuy = BigDecimal.ZERO;
            } else {
                newAmoutLeftBuy = newAmoutLeftBuy.subtract(quantity);
                if (first) {
                    usedFromFirst = quantity;
                }
                quantity = BigDecimal.ZERO;
            }

            if (first) {
                firstTakenFrom = buy.transaction.uidTransaction;
                first = false;
            }
            lastTakenFrom = buy.transaction.uidTransaction;
            db.databaseDao().updateAmoutLeft(String.valueOf(buy.transaction.uidTransaction), String.valueOf(newAmoutLeftBuy));
        }
        db.databaseDao().updateFifoCalc(sellTransactionID, String.valueOf(quantity), String.valueOf(usedFromFirst), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));

    }

    private void fifoEditAmountBuy(String transactionID, String date, String time, String shortName, String newAmountLeft){
        AppDatabase db = AppDatabase.getDbInstance(context);

        // Vzít všechny prodeje od data nákupu a najít první (ten, který končí na daném prodeji nebo který začíná před a končí po).
        // První prodej zpracovat zvlášť. Ponechat amountLeft před datem editovaného nákupu.
        // Resetovat všechny prodeje následující za prvním.
        resetSales(transactionID, date, time, shortName);

        // Resetovat všechny nákupy následující za editovaným.
        db.databaseDao().resetAmountLeftBuyAfterFirst(transactionID, date, time, shortName);

        // Nastavit novou hodnotu editovanému nákupu.
        db.databaseDao().updateAmoutLeft(transactionID, newAmountLeft);

        // Přepočítat.
        recalculateForEditBuy(date, time, shortName);

        // Uložím novou hodnotu amountLeft do aktualizované transakce.
        newTransaction.amountLeft = db.databaseDao().getTransactionByTransactionID(transactionID).transaction.amountLeft;
    }

    private void fifoEditAmountSell(String transactionID, String date, String time, String shortName, String newAmountLeft){
        AppDatabase db = AppDatabase.getDbInstance(context);

        // Vezmu všechny prodeje od toho co měním (včetně toho co měním). A vyberu ten co měním.
        List<TransactionWithPhotos> listOfUsedSales = db.databaseDao().getUsedSellAllFromFirst(transactionID, date, time, shortName);
        TransactionEntity firstSell = listOfUsedSales.get(0).transaction;

        // Budu je procházet a dokud bude mít prodej první nákup stejný jako měněný prodej, tak tomu prvnímu nákupu přičtu použito z prvního.
        int i = 0;
        do {
            db.databaseDao().updateAmoutLeftMathAdd(String.valueOf(listOfUsedSales.get(i).transaction.firstTakenFrom), listOfUsedSales.get(i).transaction.usedFromFirst);
            i++;
        }while(i < listOfUsedSales.size() && listOfUsedSales.get(i).transaction.firstTakenFrom == firstSell.firstTakenFrom);

        // Poté vyresetuji prodeje od měněného.
        db.databaseDao().resetAmoutLeftUsedSellAfterFirst(transactionID, date, time, shortName);

        // Nastavím novou hodnotu měněnému.
        db.databaseDao().updateAmoutLeft(transactionID, newAmountLeft);

        // Obnovit zbylé nákupy.
        TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(firstSell.firstTakenFrom)).transaction;
        db.databaseDao().resetAmountLeftBuyAfterFirst(String.valueOf(firstBuy.uidTransaction), firstBuy.date, firstBuy.time, shortName);

        // Přepočítat. Beru včetně prvního (ten zůstal, jen má jinou hodnotu).
        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellNotEmptyFromFirst(transactionID, date, time, shortName);
        for(TransactionWithPhotos sell : listOfIncompleteSales) {
            List<TransactionWithPhotos> listOfAvailableBuys = db.databaseDao().getNotEmptyBuyTo(sell.transaction.date, sell.transaction.time, shortName);
            if(listOfAvailableBuys.isEmpty()){
                break;
            }

            recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeft), listOfAvailableBuys);
        }

        // Uložím nové hodnoty amountLeft, firstTakenFrom, usedFromFirst a lastTakenFrom do aktualizované transakce.
        TransactionEntity changedSell = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        newTransaction.amountLeft = changedSell.amountLeft;
        newTransaction.firstTakenFrom = changedSell.firstTakenFrom;
        newTransaction.usedFromFirst = changedSell.usedFromFirst;
        newTransaction.lastTakenFrom = changedSell.lastTakenFrom;
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