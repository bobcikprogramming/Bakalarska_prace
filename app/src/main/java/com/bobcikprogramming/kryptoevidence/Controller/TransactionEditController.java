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
import java.util.Date;
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

    private String uidCrypto, uidCryptoChange, uidChangedCrypto;
    private BigDecimal quantityOld, quantityNew, quantityChangeOld, quantityChangeNew;
    private int operationType;
    private boolean changed;
    private Context context;
    private String transactionID;

    private BigDecimal EMPTYBIGDECIMAL;
    private boolean isBuy, isSell, isChange, dateOrTimeChange, quantityChange, quantityBuyChanged, quantitySellChanged, cryptoSellChange;

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

    public void getUpdateStatus(EditText valueRowFirst, EditText valueRowSecond, Spinner spinnerRowThird, EditText valueRowFifth, EditText valueRowSixth, Spinner spinnerRowSeventh, EditText valueFee, TextView valueDate, TextView valueTime, String uidSell){
        TransactionEntity transaction = getTransactionEntity();
        newTransaction = new TransactionEntity();
        transactionHistory = new TransactionHistoryEntity();
        newTransaction.uidTransaction = transaction.uidTransaction;
        newTransaction.transactionType = transaction.transactionType;

        changed = false;

        if(getTransactionType().equals("Nákup") || getTransactionType().equals("Prodej")) {
            if (transaction.transactionType.equals("Nákup")) {
                isBuy = true;
                newTransaction.uidBought = transaction.uidBought;
                newTransaction.quantityBought = shared.getString(valueRowFirst);
                newTransaction.priceBought = shared.getString(valueRowSecond);
                newTransaction.quantitySold = String.valueOf(shared.getPriceWithoutFee(valueRowSecond, valueFee));

                operationType = 0;
                uidCrypto = transaction.uidBought;
                quantityOld = shared.getBigDecimal(transaction.quantityBought);
                quantityNew = shared.getBigDecimal(newTransaction.quantityBought);

                if(!newTransaction.quantityBought.equals(transaction.quantityBought)){
                    transactionHistory.quantityBought = transaction.quantityBought;
                    quantityChange = true;
                    changed = true;
                }
                if(!newTransaction.priceBought.equals(transaction.priceBought)){
                    transactionHistory.priceBought = transaction.priceBought;
                    changed = true;
                }
                if(!newTransaction.quantitySold.equals(transaction.quantitySold)){
                    transactionHistory.quantitySold = transaction.quantitySold;
                    changed = true;
                }
            }else {
                isSell = true;
                newTransaction.uidSold = transaction.uidSold;
                newTransaction.quantitySold = shared.getString(valueRowFirst);
                newTransaction.priceSold = shared.getString(valueRowSecond);
                newTransaction.quantityBought = String.valueOf(shared.getProfit(valueRowSecond, valueFee));

                operationType = 1;
                uidCrypto = transaction.uidSold;
                quantityOld = shared.getBigDecimal(transaction.quantitySold);
                quantityNew = shared.getBigDecimal(newTransaction.quantitySold);


                if(!newTransaction.quantitySold.equals(transaction.quantitySold)){
                    quantityChange = true;
                    transactionHistory.quantitySold = transaction.quantitySold;
                    changed = true;
                }
                if(!newTransaction.priceSold.equals(transaction.priceSold)){
                    transactionHistory.priceSold = transaction.priceSold;
                    changed = true;
                }
                if(!newTransaction.quantityBought.equals(transaction.quantityBought)){
                    transactionHistory.quantityBought = transaction.quantityBought;
                    changed = true;
                }
            }
            newTransaction.currency = shared.getString(spinnerRowThird);
            newTransaction.fee = shared.getFee(valueFee);
            newTransaction.date = calendar.getDateMillis(shared.getString(valueDate));
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
            if(newTransaction.date != transaction.date){
                transactionHistory.date = transaction.date;
                dateOrTimeChange = true;
                changed = true;
            }else{
                transactionHistory.date = 0;
            }
            if(!newTransaction.time.equals(transaction.time)){
                transactionHistory.time = transaction.time;
                dateOrTimeChange = true;
                changed = true;
            }
        }else if(getTransactionType().equals("Směna")){
            isChange = true;
            newTransaction.uidTransaction = transaction.uidTransaction;
            newTransaction.transactionType = transaction.transactionType;
            newTransaction.uidBought = transaction.uidBought;
            newTransaction.quantityBought = shared.getString(valueRowFirst);
            newTransaction.priceBought =  shared.getString(valueRowSixth);
            newTransaction.currency =  shared.getString(spinnerRowSeventh);
            newTransaction.uidSold = uidSell == null ? transaction.uidSold : uidSell;
            newTransaction.quantitySold = shared.getString(valueRowFifth);
            newTransaction.fee = shared.getFee(valueFee);
            newTransaction.date = calendar.getDateMillis(shared.getString(valueDate));
            newTransaction.time = shared.getString(valueTime);

            operationType = 2;
            uidCrypto = transaction.uidBought;
            quantityOld = shared.getBigDecimal(transaction.quantityBought);
            quantityNew = shared.getBigDecimal(newTransaction.quantityBought);
            uidCryptoChange = newTransaction.uidSold;
            quantityChangeOld = shared.getBigDecimal(transaction.quantitySold);
            quantityChangeNew = shared.getBigDecimal(newTransaction.quantitySold);

            transactionHistory.transactionType = transaction.transactionType;
            if(!newTransaction.quantityBought.equals(transaction.quantityBought)) {
                quantityBuyChanged = true;
                transactionHistory.quantityBought = transaction.quantityBought;
                changed = true;
            }
            if(!newTransaction.priceBought.equals(transaction.priceBought)){
                transactionHistory.priceBought = transaction.priceBought;
                changed = true;
            }
            if (!newTransaction.currency.equals(transaction.currency)) {
                transactionHistory.currency = transaction.currency;
                changed = true;
            }
            if (!newTransaction.uidSold.equals(transaction.uidSold)) {
                transactionHistory.uidSold = transaction.uidSold;
                cryptoSellChange = true;
                uidChangedCrypto = transaction.uidSold;
                changed = true;
            }
            if(!newTransaction.quantitySold.equals(transaction.quantitySold)){
                quantitySellChanged = true;
                transactionHistory.quantitySold = transaction.quantitySold;
                changed = true;
            }
            if (newTransaction.fee.compareTo(transaction.fee) != 0) {
                transactionHistory.fee = transaction.fee;
                changed = true;
            }
            if (newTransaction.date != transaction.date) {
                dateOrTimeChange = true;
                transactionHistory.date = transaction.date;
                changed = true;
            }
            if (!newTransaction.time.equals(transaction.time)) {
                dateOrTimeChange = true;
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
                    fifoEditTimeBuy(String.valueOf(newTransaction.uidTransaction), newTransaction.date, newTransaction.time, newTransaction.uidBought, newTransaction.quantityBought);
                }else{
                    if(quantityChange){
                        fifoEditAmountBuy(String.valueOf(newTransaction.uidTransaction), newTransaction.date, newTransaction.time, newTransaction.uidBought, newTransaction.quantityBought);
                    }else{
                        newTransaction.amountLeft = db.databaseDao().getTransactionByTransactionID(transactionID).transaction.amountLeft;
                    }
                }
            }else if(isSell){
                if(dateOrTimeChange){
                    fifoEditTimeSell(String.valueOf(newTransaction.uidTransaction), newTransaction.date, newTransaction.time, newTransaction.uidSold, newTransaction.quantitySold);
                }else{
                    if(quantityChange){
                        fifoEditAmountSell(String.valueOf(newTransaction.uidTransaction), newTransaction.date, newTransaction.time, newTransaction.uidSold, newTransaction.quantitySold);
                    }else{
                        TransactionEntity changedSell = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
                        newTransaction.amountLeft = changedSell.amountLeft;
                        newTransaction.firstTakenFrom = changedSell.firstTakenFrom;
                        newTransaction.usedFromFirst = changedSell.usedFromFirst;
                        newTransaction.usedFromLast = changedSell.usedFromLast;
                        newTransaction.lastTakenFrom = changedSell.lastTakenFrom;
                    }
                }
            }else if(isChange){
                if(dateOrTimeChange){
                    fifoEditTimeChange(String.valueOf(newTransaction.uidTransaction), newTransaction.date, newTransaction.time, newTransaction.uidBought, newTransaction.uidSold, newTransaction.quantityBought, newTransaction.quantitySold);
                }else{
                    if(quantityBuyChanged || quantitySellChanged){
                        fifoEditAmountChange(String.valueOf(newTransaction.uidTransaction), newTransaction.date, newTransaction.time, newTransaction.uidBought, newTransaction.uidSold, newTransaction.quantityBought, newTransaction.quantitySold);
                    }else if(cryptoSellChange){
                        fifoEditCryptoChange(String.valueOf(newTransaction.uidTransaction), newTransaction.date, newTransaction.time, newTransaction.uidSold, newTransaction.quantitySold);
                    }else{
                        TransactionEntity changedChange = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
                        newTransaction.amountLeft = changedChange.amountLeft;
                        newTransaction.amountLeftChangeSell = changedChange.amountLeftChangeSell;
                        newTransaction.firstTakenFrom = changedChange.firstTakenFrom;
                        newTransaction.usedFromFirst = changedChange.usedFromFirst;
                        newTransaction.usedFromLast = changedChange.usedFromLast;
                        newTransaction.lastTakenFrom = changedChange.lastTakenFrom;
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
                transactionOperation.changeAmountOfOwnedCrypto(uidCrypto, quantity, operationType, null, null);
            }
        }else{
            if (quantityOld.compareTo(quantityNew) != 0) {
                BigDecimal quantity = quantityNew.subtract(quantityOld);
                transactionOperation.changeAmountOfOwnedCrypto(uidCrypto, quantity, 0, null, null);
            }
            if(cryptoSellChange) {
                transactionOperation.changeAmountOfOwnedCrypto(uidChangedCrypto, quantityOld, 0, null, null);
                transactionOperation.changeAmountOfOwnedCrypto(uidCryptoChange, quantityNew, 1, null, null);
            }else {
                if (quantityChangeOld.compareTo(quantityChangeNew) != 0) {
                    BigDecimal quantity = quantityChangeNew.subtract(quantityChangeOld);
                    transactionOperation.changeAmountOfOwnedCrypto(uidCryptoChange, quantity, 1, null, null);
                }
            }
        }
    }

    private void deleteFromOwnedCrypto(){
        if(operationType == 0) {
            BigDecimal quantity = getNegativeQuantity(getTransactionEntity().quantityBought);
            transactionOperation.changeAmountOfOwnedCrypto(getTransactionEntity().uidBought, quantity, 0, null, null);
        }else if(operationType == 1) {
            BigDecimal quantity = getNegativeQuantity(getTransactionEntity().quantitySold);
            transactionOperation.changeAmountOfOwnedCrypto(getTransactionEntity().uidSold, quantity, 1, null, null);
        }else {
            BigDecimal quantityBought = getNegativeQuantity(getTransactionEntity().quantityBought);
            BigDecimal quantitySold = getNegativeQuantity(getTransactionEntity().quantitySold);
            transactionOperation.changeAmountOfOwnedCrypto(getTransactionEntity().uidBought, quantityBought, 2,
                    getTransactionEntity().uidSold, quantitySold);
        }
    }

    private BigDecimal getNegativeQuantity(String quantity){
        return shared.getBigDecimal(quantity).multiply(shared.getBigDecimal("-1"));
    }

    public void deleteFromDatabase(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity toRemove = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        if(toRemove.transactionType.equals("Nákup")) {
            fifoDeleteBuy(transactionID, toRemove.date, toRemove.time, toRemove.uidBought);
        }else if(toRemove.transactionType.equals("Prodej")) {
            fifoDeleteSell(transactionID, toRemove.date, toRemove.time, toRemove.uidSold);
        }else{
            fifoDeleteChange(transactionID, toRemove.date, toRemove.time, toRemove.uidBought, toRemove.uidSold);
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

    private void fifoDeleteBuy(String transactionID, long date, String time, String uidCrypto){
        AppDatabase db = AppDatabase.getDbInstance(context);

        // Všechny prodeje od prvního prodeje který je na tomto nákupu resetovat. První je potřeba udělat zvlášť.
        resetSales(transactionID, date, time, uidCrypto, false);

        // Resetovat všechny nákupy následující po odstraněném.
        db.databaseDao().resetAmountLeftBuyChangeAfterFirst(transactionID, date, time, uidCrypto);

        // Smazat daný nákup.
        db.databaseDao().setTransactionToDeleteById(transactionID);

        // Přepočítat.
        recalculateForEditBuy(date, time, uidCrypto);
    }

    private void resetSales(String transactionID, long date, String time, String uidCrypto, boolean editingTimeBefore){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfSales = db.databaseDao().getUsedSellChangeFrom(date, time, uidCrypto);
        TransactionEntity firstSell = null;

        for(TransactionWithPhotos sell : listOfSales){
            String lastTakenBuyID = String.valueOf(sell.transaction.lastTakenFrom);
            if(editingTimeBefore){
                if (db.databaseDao().findIfExistBuyWithIdForUsedBuyChangeAllFrom(date, time, uidCrypto, lastTakenBuyID) != null) {
                    firstSell = sell.transaction;
                    break;
                }
            }else {
                if (db.databaseDao().findIfExistBuyWithIdForUsedBuyChangeFromFirst(transactionID, date, time, uidCrypto, lastTakenBuyID) != null) {
                    firstSell = sell.transaction;
                    break;
                }
            }
        }

        if(firstSell != null) {
            db.databaseDao().resetAmountLeftUsedSellAfterFirst(String.valueOf(firstSell.uidTransaction), firstSell.date, firstSell.time, uidCrypto);
            db.databaseDao().resetAmountLeftUsedChangeAfterFirst(String.valueOf(firstSell.uidTransaction), firstSell.date, firstSell.time, uidCrypto);
            /** Pro první zjistit kolik bylo vzato z prodeje před smazaným prodejem a tuto hodnotu odečíst od obnoveného množství */
            TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(firstSell.firstTakenFrom)).transaction;
            long dateFrom = firstBuy.date;
            String timeFrom = firstBuy.time;
            List<TransactionWithPhotos> listOfUsedBuyBetween = db.databaseDao().getUsedBuyChangeBetweenWithoutFirstAndLast(String.valueOf(firstSell.firstTakenFrom), transactionID, dateFrom, timeFrom, date, time, uidCrypto);

            BigDecimal restAmount;
            if(listOfUsedBuyBetween.isEmpty()){
                String usedFromFirst = "-1.0";
                String usedFromLast = "-1.0";
                long firstTakenFrom = -1;
                long lastTakenFrom = -1;
                boolean firstIsBetween;

                if(editingTimeBefore){
                    firstIsBetween = db.databaseDao().findIfExistBuyWithIdForUsedBuyChangeAllFrom(date, time, uidCrypto, String.valueOf(firstSell.firstTakenFrom)) != null;
                }else{
                    firstIsBetween = db.databaseDao().findIfExistBuyWithIdForUsedBuyChangeFromFirst(transactionID, date, time, uidCrypto, String.valueOf(firstSell.firstTakenFrom)) != null;
                }
                if (firstIsBetween) {
                    restAmount = shared.getBigDecimal(firstSell.quantitySold);
                } else {
                    restAmount = shared.getBigDecimal(firstSell.quantitySold).subtract(shared.getBigDecimal(firstSell.usedFromFirst));
                    usedFromFirst = firstSell.usedFromFirst;
                    usedFromLast = firstSell.usedFromLast;
                    firstTakenFrom = firstSell.firstTakenFrom;
                    lastTakenFrom = firstSell.firstTakenFrom;
                }
                String amountLeft = restAmount.compareTo(BigDecimal.ZERO) == 0 ? "0" : restAmount.toPlainString();
                if(firstSell.transactionType.equals("Prodej")){
                    db.databaseDao().updateFifoCalc(String.valueOf(firstSell.uidTransaction), amountLeft, usedFromFirst, usedFromLast, String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
                }else{
                    db.databaseDao().updateFifoCalcChangeSell(String.valueOf(firstSell.uidTransaction), amountLeft, usedFromFirst, usedFromLast, String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
                }
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

                String usedFromFirst = firstSell.usedFromFirst;
                String usedFromLast = "-1.0";
                long firstTakenFrom = firstSell.firstTakenFrom;
                long lastTakenFrom = -1;

                if (firstSell.firstTakenFrom == Long.parseLong(transactionID)) {
                    usedFromFirst = "-1.0";
                    firstTakenFrom = -1;
                }
                String amountLeft = restAmount.compareTo(BigDecimal.ZERO) == 0 ? "0" : restAmount.toPlainString();
                if (firstSell.transactionType.equals("Prodej")) {
                    db.databaseDao().updateFifoCalc(String.valueOf(firstSell.uidTransaction), amountLeft, usedFromFirst, usedFromLast, String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
                }else{
                    db.databaseDao().updateFifoCalcChangeSell(String.valueOf(firstSell.uidTransaction), amountLeft, usedFromFirst, usedFromLast, String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
                }
            }
        }
    }

    private void recalculateForEditBuy(long date, String time, String uidCrypto){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellChangeNotEmptyFrom(date, time, uidCrypto);

        for(TransactionWithPhotos sell : listOfIncompleteSales){
            BigDecimal usedFromFirst = sell.transaction.usedFromFirst.equals("-1.0") ? EMPTYBIGDECIMAL : shared.getBigDecimal(sell.transaction.usedFromFirst);
            BigDecimal usedFromLast = sell.transaction.usedFromLast.equals("-1.0") ? EMPTYBIGDECIMAL : shared.getBigDecimal(sell.transaction.usedFromLast);
            long firstTakenFrom = sell.transaction.firstTakenFrom;
            long lastTakenFrom = sell.transaction.lastTakenFrom;
            BigDecimal inSellLeft;
            if(sell.transaction.transactionType.equals("Prodej")){
                inSellLeft = shared.getBigDecimal(sell.transaction.amountLeft);
            }else{
                inSellLeft = shared.getBigDecimal(sell.transaction.amountLeftChangeSell);
            }

            List<TransactionWithPhotos> listOfNextBuy = db.databaseDao().getNotEmptyBuyChangeTo(sell.transaction.date, sell.transaction.time, uidCrypto);

            for (TransactionWithPhotos nextBuy : listOfNextBuy) {
                BigDecimal amountOfNextBuy = shared.getBigDecimal(nextBuy.transaction.amountLeft);

                if(inSellLeft.compareTo(BigDecimal.ZERO) < 1){
                    break;
                }

                if(inSellLeft.compareTo(amountOfNextBuy) < 1){
                    amountOfNextBuy = amountOfNextBuy.subtract(inSellLeft);
                    usedFromLast = inSellLeft;
                    inSellLeft = BigDecimal.ZERO;
                }else{
                    inSellLeft = inSellLeft.subtract(amountOfNextBuy);
                    usedFromLast = amountOfNextBuy;
                    amountOfNextBuy = BigDecimal.ZERO;
                }

                if (usedFromFirst.compareTo(EMPTYBIGDECIMAL) == 0) {
                    firstTakenFrom = nextBuy.transaction.uidTransaction;
                    if(sell.transaction.transactionType.equals("Prodej")) {
                        usedFromFirst = shared.getBigDecimal(sell.transaction.amountLeft).subtract(inSellLeft);
                    }else{
                        usedFromFirst = shared.getBigDecimal(sell.transaction.amountLeftChangeSell).subtract(inSellLeft);
                    }
                }

                lastTakenFrom = nextBuy.transaction.uidTransaction;

                if(amountOfNextBuy.compareTo(BigDecimal.ZERO) == 0){
                    db.databaseDao().updateAmountLeft(String.valueOf(nextBuy.transaction.uidTransaction), "0");
                }else{
                    db.databaseDao().updateAmountLeft(String.valueOf(nextBuy.transaction.uidTransaction), amountOfNextBuy.toPlainString());
                }

            }

            String amountLeft = inSellLeft.compareTo(BigDecimal.ZERO) == 0 ? "0" : inSellLeft.toPlainString();
            if(sell.transaction.transactionType.equals("Prodej")) {
                db.databaseDao().updateFifoCalc(String.valueOf(sell.transaction.uidTransaction), amountLeft, usedFromFirst.toPlainString(), usedFromLast.toPlainString(), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
            }else{
                db.databaseDao().updateFifoCalcChangeSell(String.valueOf(sell.transaction.uidTransaction), amountLeft, usedFromFirst.toPlainString(), usedFromLast.toPlainString(), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
            }
        }
    }

    private void fifoDeleteSell(String transactionID, long date, String time, String uidCrypto){
        // Brát prodeje v cyklu a dokud další prodej nezačíná na jiné transakci než na té, kde první, a přičíst usedFromFirst k prvnímu nákupu
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfUsedSales = db.databaseDao().getUsedSellChangeAllFromFirst(transactionID, date, time, uidCrypto);
        if(!listOfUsedSales.isEmpty()) {
            TransactionEntity firstSell = listOfUsedSales.get(0).transaction;
            int i = 0;
            do {
                db.databaseDao().updateAmountLeftMathAdd(String.valueOf(listOfUsedSales.get(i).transaction.firstTakenFrom), listOfUsedSales.get(i).transaction.usedFromFirst);
                i++;
            } while (i < listOfUsedSales.size() && listOfUsedSales.get(i).transaction.firstTakenFrom == firstSell.firstTakenFrom);

            // Obnovit zbylé nákupy.
            TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(firstSell.firstTakenFrom)).transaction;
            db.databaseDao().resetAmountLeftBuyChangeAfterFirst(String.valueOf(firstBuy.uidTransaction), firstBuy.date, firstBuy.time, uidCrypto);

            // Resetovat všechny prodeje od daného data
            db.databaseDao().resetAmountLeftUsedSellAfterFirst(transactionID, date, time, uidCrypto);
            db.databaseDao().resetAmountLeftUsedChangeAfterFirst(transactionID, date, time, uidCrypto);
        }

        // Smazat daný prodej
        TransactionEntity sellEntity = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        if(sellEntity.transactionType.equals("Prodej")) {
            db.databaseDao().setTransactionToDeleteById(transactionID);
        }else{
            db.databaseDao().setTransactionChangeSellToDeleteById(transactionID);
        }

        if(!listOfUsedSales.isEmpty()) {
            // Přepočítat
            List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellChangeNotEmptyAfterFirst(transactionID, date, time, uidCrypto);
            for (TransactionWithPhotos sell : listOfIncompleteSales) {
                List<TransactionWithPhotos> listOfAvailableBuys = db.databaseDao().getNotEmptyBuyChangeTo(sell.transaction.date, sell.transaction.time, uidCrypto);
                if (listOfAvailableBuys.isEmpty()) {
                    break;
                }
                if (sell.transaction.transactionType.equals("Prodej")) {
                    recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeft), listOfAvailableBuys);
                } else {
                    recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeftChangeSell), listOfAvailableBuys);
                }
            }
        }
    }

    private void recalculateForRemoveSell(String sellTransactionID, BigDecimal quantity, List<TransactionWithPhotos> listOfAvailableBuys){
        AppDatabase db = AppDatabase.getDbInstance(context);
        boolean first = true;
        BigDecimal usedFromFirst = EMPTYBIGDECIMAL;
        BigDecimal usedFromLast = EMPTYBIGDECIMAL;
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
                usedFromLast = newAmoutLeftBuy;
                newAmoutLeftBuy = BigDecimal.ZERO;
            } else {
                newAmoutLeftBuy = newAmoutLeftBuy.subtract(quantity);
                if (first) {
                    usedFromFirst = quantity;
                }
                usedFromLast = quantity;
                quantity = BigDecimal.ZERO;
            }

            if (first) {
                firstTakenFrom = buy.transaction.uidTransaction;
                first = false;
            }
            lastTakenFrom = buy.transaction.uidTransaction;
            if(newAmoutLeftBuy.compareTo(BigDecimal.ZERO) == 0){
                db.databaseDao().updateAmountLeft(String.valueOf(buy.transaction.uidTransaction), "0");
            }else{
                db.databaseDao().updateAmountLeft(String.valueOf(buy.transaction.uidTransaction), newAmoutLeftBuy.toPlainString());
            }

        }
        TransactionEntity sellEntity = db.databaseDao().getTransactionByTransactionHistoryID(sellTransactionID).transaction;
        String amountLeft = quantity.compareTo(BigDecimal.ZERO) == 0 ? "0" : quantity.toPlainString();
        if(sellEntity.transactionType.equals("Prodej")) {
            db.databaseDao().updateFifoCalc(sellTransactionID, amountLeft, usedFromFirst.toPlainString(), usedFromLast.toPlainString(), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
        }else{
            db.databaseDao().updateFifoCalcChangeSell(sellTransactionID, amountLeft, usedFromFirst.toPlainString(), usedFromLast.toPlainString(), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
        }
    }

    private void fifoDeleteChange(String transactionID, long date, String time, String uidBought, String uidSold){
        // -------------------------------------------------------
        // Nákup:
        fifoDeleteBuy(transactionID, date, time, uidBought);
        // -------------------------------------------------------

        // -------------------------------------------------------
        // Prodej:
        fifoDeleteSell(transactionID, date, time, uidSold);
        // -------------------------------------------------------
    }

    private void fifoEditAmountBuy(String transactionID, long date, String time, String uidCrypto, String newAmountLeft){
        AppDatabase db = AppDatabase.getDbInstance(context);

        // Vzít všechny prodeje od data nákupu a najít první (ten, který končí na daném nákupu nebo který začíná před a končí po).
        // První prodej zpracovat zvlášť. Ponechat amountLeft před datem editovaného nákupu.
        // Resetovat všechny prodeje následující za prvním.
        resetSales(transactionID, date, time, uidCrypto, false);

        // Resetovat všechny nákupy následující za editovaným.
        db.databaseDao().resetAmountLeftBuyChangeAfterFirst(transactionID, date, time, uidCrypto);

        // Nastavit novou hodnotu editovanému nákupu.
        db.databaseDao().updateAmountLeft(transactionID, newAmountLeft);

        // Přepočítat.
        recalculateForEditBuy(date, time, uidCrypto);

        // Uložím novou hodnotu amountLeft do aktualizované transakce.
        newTransaction.amountLeft = db.databaseDao().getTransactionByTransactionID(transactionID).transaction.amountLeft;
    }

    private void fifoEditAmountSell(String transactionID, long date, String time, String uidCrypto, String newAmountLeft){
        AppDatabase db = AppDatabase.getDbInstance(context);

        // Vezmu všechny prodeje od toho co měním (včetně toho co měním). A vyberu ten co měním.
        List<TransactionWithPhotos> listOfUsedSales = db.databaseDao().getUsedSellChangeAllFromFirst(transactionID, date, time, uidCrypto);
        if(!listOfUsedSales.isEmpty()) {
            TransactionEntity firstSell = listOfUsedSales.get(0).transaction;

            // Budu je procházet a dokud bude mít prodej první nákup stejný jako měněný prodej, tak tomu prvnímu nákupu přičtu použito z prvního.
            int i = 0;
            do {
                db.databaseDao().updateAmountLeftMathAdd(String.valueOf(listOfUsedSales.get(i).transaction.firstTakenFrom), listOfUsedSales.get(i).transaction.usedFromFirst);
                i++;
            } while (i < listOfUsedSales.size() && listOfUsedSales.get(i).transaction.firstTakenFrom == firstSell.firstTakenFrom);

            // Poté vyresetuji prodeje od měněného.
            db.databaseDao().resetAmountLeftUsedSellAfterFirst(transactionID, date, time, uidCrypto);
            db.databaseDao().resetAmountLeftUsedChangeAfterFirst(transactionID, date, time, uidCrypto);

            // Obnovit zbylé nákupy.
            TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(firstSell.firstTakenFrom)).transaction;
            db.databaseDao().resetAmountLeftBuyChangeAfterFirst(String.valueOf(firstBuy.uidTransaction), firstBuy.date, firstBuy.time, uidCrypto);
        }

        // Nastavím novou hodnotu měněnému.
        TransactionEntity sellEntity = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        if(sellEntity.transactionType.equals("Prodej")) {
            db.databaseDao().updateAmountLeft(transactionID, newAmountLeft);
        }else{
            db.databaseDao().updateAmountLeftChange(transactionID, newAmountLeft);
        }

        if(!listOfUsedSales.isEmpty()) {
            // Přepočítat. Beru včetně prvního (ten zůstal, jen má jinou hodnotu).
            List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellChangeNotEmptyFromFirst(transactionID, date, time, uidCrypto);
            for (TransactionWithPhotos sell : listOfIncompleteSales) {
                List<TransactionWithPhotos> listOfAvailableBuys = db.databaseDao().getNotEmptyBuyChangeTo(sell.transaction.date, sell.transaction.time, uidCrypto);
                if (listOfAvailableBuys.isEmpty()) {
                    break;
                }

                if (sell.transaction.transactionType.equals("Prodej")) {
                    recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeft), listOfAvailableBuys);
                } else {
                    recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeftChangeSell), listOfAvailableBuys);
                }
            }
        }

        // Uložím nové hodnoty amountLeft, firstTakenFrom, usedFromFirst a lastTakenFrom do aktualizované transakce.
        TransactionEntity changedSell = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        if(changedSell.transactionType.equals("Prodej")) {
            newTransaction.amountLeft = changedSell.amountLeft;
        }else{
            newTransaction.amountLeftChangeSell = changedSell.amountLeftChangeSell;
        }
        newTransaction.firstTakenFrom = changedSell.firstTakenFrom;
        newTransaction.usedFromFirst = changedSell.usedFromFirst;
        newTransaction.lastTakenFrom = changedSell.lastTakenFrom;
        newTransaction.usedFromLast = changedSell.usedFromLast;
    }

    private void fifoEditAmountChange(String transactionID, long date, String time, String uidBought, String uidSold, String newAmountLeftBuy, String newAmountLeftSell){
        AppDatabase db = AppDatabase.getDbInstance(context);
        // -------------------------------------------------------
        // Nákup:
        if(quantityBuyChanged) {
            fifoEditAmountBuy(transactionID, date, time, uidBought, newAmountLeftBuy);
        }else{
            newTransaction.amountLeft = db.databaseDao().getTransactionByTransactionID(transactionID).transaction.amountLeft;
        }
        // -------------------------------------------------------

        // -------------------------------------------------------
        // Prodej:
        if(quantitySellChanged) {
            TransactionEntity sellEntity = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
            if (sellEntity.uidSold.equals(uidSold)) {
                // Typ kryptoměny ponechán:
                fifoEditAmountSell(transactionID, date, time, uidSold, newAmountLeftSell);
            } else {
                // Smazat původní.
                TransactionEntity editingTransaction = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
                fifoDeleteSell(transactionID, date, time, editingTransaction.uidSold);

                // Přidat novou.
                transactionOperation.calcFifoSell(Long.parseLong(transactionID), shared.getBigDecimal(newAmountLeftSell), date, time, uidSold);
                updateRestOfChange(transactionID, db);
            }
        }else{
            updateRestOfChange(transactionID, db);
        }
    }

    private void fifoEditTimeBuy(String transactionID, long date, String time, String uidBought, String newAmountLeft){
        AppDatabase db = AppDatabase.getDbInstance(context);

        // Zkontrolovat, zdali je nový čas/datum nákupu před původním.
        TransactionEntity editingTransaction = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        long oldDate = editingTransaction.date;
        Date oldTime = calendar.getTimeFormat(editingTransaction.time);
        long newDate = date;
        Date newTime = calendar.getTimeFormat(time);

        if(newDate < oldDate || (newDate == oldDate && newTime.before(oldTime))){
            // Pokud ano:
            // Vzít všechny prodeje od nového času/data nákupu a najít první (ten, který končí na prvním nákupu v listu nebo který začíná před a končí po).
            // První prodej zpracovat zvlášť. Ponechat amountLeft před prvním nákupem.
            // Resetovat všechny prodeje následující za prvním.
            resetSales(transactionID, date, time, uidBought, true);

            // Resetovat všechny nákupy od nového data včetně.
            db.databaseDao().resetAmountLeftBuyChangeFrom(date, time, uidBought);

            // Nastavit novou hodnotu editovanému nákupu. (amountLeft, date, time)
            db.databaseDao().updateForEditingTime(transactionID, newAmountLeft, date, time);

            // Přepočítat od nového data
            recalculateForEditBuy(date, time, uidBought);
        }else{
            // Pokud ne:
            // Vzít všechny prodeje od data nákupu a najít první (ten, který končí na daném nákupu nebo který začíná před a končí po).
            // První prodej zpracovat zvlášť. Ponechat amountLeft před datem editovaného nákupu.
            // Resetovat všechny prodeje následující za prvním.
            resetSales(transactionID, editingTransaction.date, editingTransaction.time, uidBought, false);

            // Resetovat všechny nákupy následující za editovaným.
            db.databaseDao().resetAmountLeftBuyChangeAfterFirst(transactionID, editingTransaction.date, editingTransaction.time, uidBought);

            // Nastavit novou hodnotu editovanému nákupu. (amountLeft, date, time)
            db.databaseDao().updateForEditingTime(transactionID, newAmountLeft, date, time);

            // Přepočítat od původního data
            recalculateForEditBuy(editingTransaction.date, editingTransaction.time, uidBought);
        }

        // Uložit novou hodnotu amountLeft do newTransaction.
        TransactionEntity changedBuy = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        newTransaction.amountLeft = changedBuy.amountLeft;
    }

    private void fifoEditTimeSell(String transactionID, long date, String time, String uidSold, String newAmountLeft){
        AppDatabase db = AppDatabase.getDbInstance(context);

        // Zkontrolovat, zdali je nový čas/datum prodeje před původním.
        TransactionEntity editingTransaction = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        long oldDate = editingTransaction.date;
        Date oldTime = calendar.getTimeFormat(editingTransaction.time);
        long newDate = date;
        Date newTime = calendar.getTimeFormat(time);
        TransactionEntity firstSell;

        if(newDate < oldDate || (newDate == oldDate && newTime.before(oldTime))) {
            // Pokud ano:
            // Vezmu všechny prodeje od nového času/date (včetně). A vyberu první.
            List<TransactionWithPhotos> listOfUsedSales = db.databaseDao().getUsedSellChangeFrom(date, time, uidSold);
            if(!listOfUsedSales.isEmpty()) {
                firstSell = listOfUsedSales.get(0).transaction;

                // Budu je procházet a dokud bude mít prodej první nákup stejný jako měněný prodej, tak tomu prvnímu nákupu přičtu použito z prvního.
                int i = 0;
                do {
                    db.databaseDao().updateAmountLeftMathAdd(String.valueOf(listOfUsedSales.get(i).transaction.firstTakenFrom), listOfUsedSales.get(i).transaction.usedFromFirst);
                    i++;
                } while (i < listOfUsedSales.size() && listOfUsedSales.get(i).transaction.firstTakenFrom == firstSell.firstTakenFrom);

                // Poté ty prodeje vyresetuji (od toho nového data včetně).
                db.databaseDao().resetAmountLeftUsedSellAllFrom(date, time, uidSold);
                db.databaseDao().resetAmountLeftUsedChangeAllFrom(date, time, uidSold);

                // Obnovit zbylé nákupy.
                TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(firstSell.firstTakenFrom)).transaction;
                db.databaseDao().resetAmountLeftBuyChangeAfterFirst(String.valueOf(firstBuy.uidTransaction), firstBuy.date, firstBuy.time, uidSold);
            }

            // Nastavím novou hodnotu měněnému (amountLeft, date, time).
            TransactionEntity sellEntity = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
            if(sellEntity.transactionType.equals("Prodej")) {
                db.databaseDao().updateForEditingTime(transactionID, newAmountLeft, date, time);
            }else{
                db.databaseDao().updateChangeSellForEditingTime(transactionID, newAmountLeft, date, time);

            }

            if(!listOfUsedSales.isEmpty()) {
                // Přepočítat od nového data. Beru včetně prvního (ten zůstal, jen má jinou hodnotu).
                List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellChangeNotEmptyAllFrom(date, time, uidSold);
                for (TransactionWithPhotos sell : listOfIncompleteSales) {
                    List<TransactionWithPhotos> listOfAvailableBuys = db.databaseDao().getNotEmptyBuyChangeTo(sell.transaction.date, sell.transaction.time, uidSold);
                    if (listOfAvailableBuys.isEmpty()) {
                        break;
                    }
                    if (sell.transaction.transactionType.equals("Prodej")) {
                        recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeft), listOfAvailableBuys);
                    } else {
                        recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeftChangeSell), listOfAvailableBuys);
                    }
                }
            }
        }else {
            // Pokud ne:
            // Vezmu všechny prodeje od toho co měním (včetně toho co měním). A vyberu ten co měním.
            List<TransactionWithPhotos> listOfUsedSales = db.databaseDao().getUsedSellChangeAllFromFirst(transactionID, editingTransaction.date, editingTransaction.time, uidSold);
            if(!listOfUsedSales.isEmpty()) {
                firstSell = listOfUsedSales.get(0).transaction;

                // Budu je procházet a dokud bude mít prodej první nákup stejný jako měněný prodej, tak tomu prvnímu nákupu přičtu použito z prvního.
                int i = 0;
                do {
                    db.databaseDao().updateAmountLeftMathAdd(String.valueOf(listOfUsedSales.get(i).transaction.firstTakenFrom), listOfUsedSales.get(i).transaction.usedFromFirst);
                    i++;
                } while (i < listOfUsedSales.size() && listOfUsedSales.get(i).transaction.firstTakenFrom == firstSell.firstTakenFrom);

                // Poté vyresetuji prodeje od měněného (včetně).
                db.databaseDao().resetAmountLeftUsedSellAllFrom(editingTransaction.date, editingTransaction.time, uidSold);
                db.databaseDao().resetAmountLeftUsedChangeAllFrom(editingTransaction.date, editingTransaction.time, uidSold);

                // Obnovit zbylé nákupy.
                TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(firstSell.firstTakenFrom)).transaction;
                db.databaseDao().resetAmountLeftBuyChangeAfterFirst(String.valueOf(firstBuy.uidTransaction), firstBuy.date, firstBuy.time, uidSold);
            }

            // Nastavím novou hodnotu měněnému (amountLeft, date, time).
            db.databaseDao().updateChangeSellForEditingTime(transactionID, newAmountLeft, date, time);

            System.out.println("---------------------------- před if");
            if(!listOfUsedSales.isEmpty()) {
                System.out.println("---------------------------- v if");
                // Přepočítat od původního. Beru včetně prvního (ten zůstal, jen má jinou hodnotu).
                List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellChangeNotEmptyFromFirst(transactionID, editingTransaction.date, editingTransaction.time, uidSold);
                if(listOfIncompleteSales.isEmpty()){
                    System.out.println("-------------------------------- je  to empty");
                }
                for (TransactionWithPhotos sell : listOfIncompleteSales) {
                    List<TransactionWithPhotos> listOfAvailableBuys = db.databaseDao().getNotEmptyBuyChangeTo(sell.transaction.date, sell.transaction.time, uidSold);
                    if (listOfAvailableBuys.isEmpty()) {
                        System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
                        break;
                    }
                    if (sell.transaction.transactionType.equals("Prodej")) {
                        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                        recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeft), listOfAvailableBuys);
                    } else {
                        recalculateForRemoveSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeftChangeSell), listOfAvailableBuys);
                    }
                }
            }
        }

        // Uložím nové hodnoty amountLeft, firstTakenFrom, usedFromFirst a lastTakenFrom do aktualizované transakce.
        TransactionEntity changedSell = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        if(changedSell.transactionType.equals("Prodej")) {
            newTransaction.amountLeft = changedSell.amountLeft;
        }else{
            newTransaction.amountLeftChangeSell = changedSell.amountLeftChangeSell;
        }
        newTransaction.firstTakenFrom = changedSell.firstTakenFrom;
        newTransaction.usedFromFirst = changedSell.usedFromFirst;
        newTransaction.lastTakenFrom = changedSell.lastTakenFrom;
        newTransaction.usedFromLast = changedSell.usedFromLast;
    }

    private void fifoEditTimeChange(String transactionID, long date, String time, String uidBought, String uidSold, String newAmountLeftBuy, String newAmountLeftSell){
        AppDatabase db = AppDatabase.getDbInstance(context);
        // -------------------------------------------------------
        // Nákup:
        if(quantityBuyChanged) {
            fifoEditAmountBuy(transactionID, date, time, uidBought, newAmountLeftBuy);
        }else {
            newTransaction.amountLeft = db.databaseDao().getTransactionByTransactionID(transactionID).transaction.amountLeft;
        }
        // -------------------------------------------------------

        // -------------------------------------------------------
        // Prodej:
        // Typ kryptoměny ponechán:
        TransactionEntity sellEntity = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        if(!cryptoSellChange){
            System.out.println(">>>>>>>>>>>>>>>CHYBA: změna kryptoměny nezaznamenána");
            fifoEditTimeSell(transactionID, date, time, uidSold, newAmountLeftSell);
        }else {
            System.out.println(">>>>>>>>>>>>>>>změna kryptoměny zaznamenána");
            // Smazat původní.
            TransactionEntity editingTransaction = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
            fifoDeleteSell(transactionID, editingTransaction.date, editingTransaction.time, editingTransaction.uidSold);

            // Přidat novou.
            transactionOperation.calcFifoSell(Long.parseLong(transactionID), shared.getBigDecimal(newAmountLeftSell), date, time, uidSold);
            updateRestOfChange(transactionID, db);
        }
    }

    private void fifoEditCryptoChange(String transactionID, long date, String time, String uidSold, String newAmountLeftSell){
        AppDatabase db = AppDatabase.getDbInstance(context);
        // Smazat původní.
        TransactionEntity editingTransaction = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        fifoDeleteSell(transactionID, date, time, editingTransaction.uidSold);

        // Přidat novou.
        transactionOperation.calcFifoSell(Long.parseLong(transactionID), shared.getBigDecimal(newAmountLeftSell), date, time, uidSold);
        updateRestOfChange(transactionID, db);
    }

    private void updateRestOfChange(String transactionID, AppDatabase db){
        TransactionEntity changedTransaction = db.databaseDao().getTransactionByTransactionID(transactionID).transaction;
        newTransaction.amountLeft = changedTransaction.amountLeft;
        newTransaction.amountLeftChangeSell = changedTransaction.amountLeftChangeSell;
        newTransaction.usedFromFirst = changedTransaction.usedFromFirst;
        newTransaction.usedFromLast = changedTransaction.usedFromLast;
        newTransaction.firstTakenFrom = changedTransaction.firstTakenFrom;
        newTransaction.lastTakenFrom = changedTransaction.lastTakenFrom;
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

    public String getIdBought(){
        return transactionWithHistory.transaction.uidBought;
    }

    public String getIdSold(){
        return transactionWithHistory.transaction.uidSold;
    }

    public String getDate(){
        return calendar.getDateFromMillis(getTransactionEntity().date);
    }

    public String getTime(){
        return getTransactionEntity().time;
    }
}