package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.Uri;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.CryptocurrencyEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionOperationModel;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

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

    /**
     * Metoda k uložení transakce nákup.
     * @param uidBought UID nakoupené kryptoměny
     * @param quantityBought Množství nakoupené kryptoměny
     * @param price Cena
     * @param fee Poplatek
     * @param date Datum
     * @param time Čas
     * @param currency Cena vedena v měně
     * @param quantitySold Cena bez poplatku
     * @return true - uložení proběhlo v pořádku, jinak false
     */
    public boolean saveTransactionBuy(String uidBought, BigDecimal quantityBought, BigDecimal price, Double fee, long date, String time, String currency, BigDecimal quantitySold){
        if(!photos.isEmpty()){
            photosPath = imgManager.saveImage(context, photos);
        }
        if(photosPath != null) {
            long transactionID = database.saveTransactionBuyToDb(context, uidBought, quantityBought, price, fee, date, time, currency, quantitySold, photosPath);
            calcFifoBuy(transactionID, quantityBought, date, time, uidBought);
            return true;
        }else {
            return false;
        }
    }

    /**
     * Metoda k uložení transakce prodej.
     * @param uidSold UID prodané kryptoměny
     * @param quantitySold Množství prodané kryptoměny
     * @param price Cena
     * @param fee Poplatek
     * @param date Datum
     * @param time Čas
     * @param currency Cena vedena v měně
     * @param quantityBought Cena bez poplatku
     * @return true - uložení proběhlo v pořádku, jinak false
     */
    public boolean saveTransactionSell(String uidSold, BigDecimal quantitySold, BigDecimal price, Double fee, long date, String time, String currency, BigDecimal quantityBought){
        if(!photos.isEmpty()){
            photosPath = imgManager.saveImage(context, photos);
        }
        if(photosPath != null) {
            long transactionID = database.saveTransactionSellToDb(context, uidSold, quantitySold, price, fee, date, time, currency, quantityBought, photosPath);
            calcFifoSell(transactionID, quantitySold, date, time, uidSold);
            return true;
        }else {
            return false;
        }
    }

    /**
     * Metoda k uložení transakce směna.
     * @param uidBought UID koupené kryptoměny
     * @param currency Cena vedena v měně
     * @param quantityBought Množství koupené kryptoměny
     * @param priceBought Cena směny
     * @param fee Poplatek
     * @param date Datum
     * @param time Čas
     * @param uidSold UID prodané kryptoměny
     * @param quantitySold Množství prodané kryptoměny
     * @return true - uložení proběhlo v pořádku, jinak false
     */
    public boolean saveTransactionChange(String uidBought, String currency, BigDecimal quantityBought, BigDecimal priceBought, Double fee, long date, String time, String uidSold, BigDecimal quantitySold){
        if(!photos.isEmpty()){
            photosPath = imgManager.saveImage(context, photos);
        }
        if(photosPath != null) {
            long transactionID = database.saveTransactionChangeToDb(context, uidBought, currency, quantityBought, priceBought, fee, date, time, uidSold, quantitySold, photosPath);
            calcFifoChange(transactionID, quantityBought, quantitySold, date, time, uidBought, uidSold);
            return true;
        }else {
            return false;
        }
    }

    /**
     * Metoda slouží k aktualizování hodnoty vlastněné kryptoměny.
     * @param uidCrypto UID kryptoměny
     * @param quantity Množství ke změně
     * @param operationType Typ změny 0 - nákup (přidat), 1 - prodej (odebrat), 2 - směna (přidat pro nakoupenou, odebrat pro prodanou)
     * @param uidChange UID prodané kryptoměny v případě směny, jinak null
     * @param quantityChange Množství ke změně prodané kryptoměny v případě směny, jinak null
     */
    public void saveAmountOfOwnedCrypto(String uidCrypto, BigDecimal quantity, int operationType, String uidChange, BigDecimal quantityChange){
        AppDatabase db = AppDatabase.getDbInstance(context);
        CryptocurrencyEntity ownedCrypto = db.databaseDao().getCryptoById(uidCrypto);
        CryptocurrencyEntity ownedCryptoChange = null;
        BigDecimal amount = shared.getBigDecimal(ownedCrypto.amount);
        BigDecimal amountChange = BigDecimal.ZERO;

        if(operationType == 0){
            amount = amount.add(quantity);
        }else if(operationType == 1) {
            amount = amount.subtract(quantity);
        }else{
            amount = amount.add(quantity);
            ownedCryptoChange = db.databaseDao().getCryptoById(uidChange);
            amountChange = shared.getBigDecimal(ownedCryptoChange.amount);
            amountChange = amountChange.subtract(quantityChange);
        }

        database.updateAmountOfOwnedCrypto(context, uidCrypto, amount);

        if(operationType == 2){
            database.updateAmountOfOwnedCrypto(context, uidChange, amountChange);
        }
    }

    /**
     * Při evidování nákupu dojde ke kontrole, zda-li není třeba nákup přidat k odpovídajícímu prodeji.
     * @param transactionID UID transakce
     * @param quantity Koupené množství
     * @param date Datum transakce
     * @param time Čas transakce
     * @param uidCrypto UID koupené kryptoměny
     */
    private void calcFifoBuy(long transactionID, BigDecimal quantity, long date, String time, String uidCrypto){
        AppDatabase db = AppDatabase.getDbInstance(context);

        db.databaseDao().resetAmountLeftBuyChangeAfterFirst(String.valueOf(transactionID), date, time, uidCrypto);
        resetTransactionSellAfterNewBuy(String.valueOf(transactionID), uidCrypto, date, time);

        recalcForBuy(transactionID, quantity, date, time, uidCrypto);
    }

    /**
     * Metoda slouží k přepočtu transakcí prodej časově následujících po novém nákupu.
     * @param transactionID UID nové transakce nákup
     * @param uidCrypto UID koupené kryptoměny
     * @param date Datum nákupu
     * @param time Čas nákupu
     */
    private void resetTransactionSellAfterNewBuy(String transactionID, String uidCrypto, long date, String time){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfUsedSell = db.databaseDao().getUsedSellChangeFrom(date, time, uidCrypto);

        for(TransactionWithPhotos sellToReset : listOfUsedSell){
            TransactionWithPhotos firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(sellToReset.transaction.firstTakenFrom));
            long dateFrom = firstBuy.transaction.date;
            Date firstBuyDate = calendar.getDateFormat(calendar.getDateFromMillis(dateFrom));
            Date newBuyDate = calendar.getDateFormat(calendar.getDateFromMillis(date));
            String timeFrom = firstBuy.transaction.time;
            Date firstBuyTime = calendar.getTimeFormat(timeFrom);
            Date newBuyTime = calendar.getTimeFormat(time);
            /* Pokud je první nákup prováděn až po datu nového nákupu */
            if(firstBuyDate.after(newBuyDate) || (firstBuyDate.equals(newBuyDate) && firstBuyTime.after(newBuyTime))){
                if(sellToReset.transaction.transactionType.equals("Prodej")) {
                    db.databaseDao().updateFifoCalc(String.valueOf(sellToReset.transaction.uidTransaction), sellToReset.transaction.quantitySold, "-1.0", "-1.0", "-1", "-1");
                }else{
                    db.databaseDao().updateFifoCalcChangeSell(String.valueOf(sellToReset.transaction.uidTransaction), sellToReset.transaction.quantitySold, "-1.0", "-1.0", "-1", "-1");
                }
                continue;
            }

            List<TransactionWithPhotos> listOfUsedBuyBetween = db.databaseDao().getUsedBuyChangeBetweenWithoutFirstAndLast(String.valueOf(firstBuy.transaction.uidTransaction), String.valueOf(transactionID), dateFrom, timeFrom, date, time, uidCrypto);
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
            
            String usedFromFirst = "-1.0";
            String usedFromLast = "-1.0";
            long firstTakenFrom = -1;
            long lastTakenFrom = -1;

            if(!sellToReset.transaction.usedFromFirst.equals("-1.0")){
                usedFromFirst = sellToReset.transaction.usedFromFirst;
                usedFromLast = sellToReset.transaction.usedFromLast;
                firstTakenFrom = sellToReset.transaction.firstTakenFrom;
                lastTakenFrom = sellToReset.transaction.lastTakenFrom;
            }

            String amountLeft = restAmount.compareTo(BigDecimal.ZERO) == 0 ? "0" : restAmount.toPlainString();
            if(sellToReset.transaction.transactionType.equals("Prodej")) {
                db.databaseDao().updateFifoCalc(String.valueOf(sellToReset.transaction.uidTransaction), amountLeft, usedFromFirst, usedFromLast, String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
            }else{
                db.databaseDao().updateFifoCalcChangeSell(String.valueOf(sellToReset.transaction.uidTransaction), amountLeft, usedFromFirst, usedFromLast, String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
            }
        }
    }

    /**
     * Při evidování prodeje dojde ke kontrole, zda-li existuje nákup, ke kterému lze prodej přiřadit-
     * @param transactionID UID nového prodeje
     * @param quantity Prodané množství
     * @param date Datum prodeje
     * @param time Čas prodeje
     * @param uidCrypto UID prodané kryptoměny
     */
    public void calcFifoSell(long transactionID, BigDecimal quantity, long date, String time, String uidCrypto){
        AppDatabase db = AppDatabase.getDbInstance(context);
        List<TransactionWithPhotos> listOfUsedSales = db.databaseDao().getUsedSellChangeAfter(date, time, uidCrypto);
        if(!listOfUsedSales.isEmpty()) {
            TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(listOfUsedSales.get(0).transaction.uidTransaction)).transaction;
            String startingTime = firstBuy.time;
            resetTransactionBuyAfterNewSell(uidCrypto, firstBuy.date, startingTime, listOfUsedSales);
        }

        db.databaseDao().resetAmountLeftUsedSellAfterFirst(String.valueOf(transactionID), date, time, uidCrypto);
        db.databaseDao().resetAmountLeftUsedChangeAfterFirst(String.valueOf(transactionID), date, time, uidCrypto);

        List<TransactionWithPhotos> listOfAvailableBuys = db.databaseDao().getNotEmptyBuyChangeTo(date, time, uidCrypto);
        setSellAndBuyForNewSell(String.valueOf(transactionID), quantity, listOfAvailableBuys);

        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellChangeNotEmptyAfterFirst(String.valueOf(transactionID), date, time, uidCrypto);
        for(TransactionWithPhotos sell : listOfIncompleteSales) {
            listOfAvailableBuys = db.databaseDao().getNotEmptyBuyChangeTo(sell.transaction.date, sell.transaction.time, uidCrypto);
            if(listOfAvailableBuys.isEmpty()){
                break;
            }
            setSellAndBuyForNewSell(String.valueOf(sell.transaction.uidTransaction), shared.getBigDecimal(sell.transaction.amountLeft), listOfAvailableBuys);
        }
    }

    /**
     * Metoda slouží k vyresetování transakcí "Nákup" časově následujícím za novým prodejem.
     * @param uidCrypto
     * @param firstBuyDate
     * @param firstBuyTime
     * @param listOfUsedSales
     */
    private void resetTransactionBuyAfterNewSell(String uidCrypto, long firstBuyDate, String firstBuyTime, List<TransactionWithPhotos> listOfUsedSales){
        AppDatabase db = AppDatabase.getDbInstance(context);

        long transactionIDOfFirstBuy = listOfUsedSales.get(0).transaction.firstTakenFrom;

        for(TransactionWithPhotos sell : listOfUsedSales){
            if(sell.transaction.firstTakenFrom != transactionIDOfFirstBuy){
                break;
            }
            db.databaseDao().updateAmountLeftMathAdd(String.valueOf(transactionIDOfFirstBuy), sell.transaction.usedFromFirst);
        }

        List<TransactionWithPhotos> listOfBuys = db.databaseDao().getUsedBuyChangeFrom(String.valueOf(transactionIDOfFirstBuy), firstBuyDate, firstBuyTime, uidCrypto);

        for(TransactionWithPhotos buy : listOfBuys){
            db.databaseDao().resetAmountLeftBuyById(String.valueOf(buy.transaction.uidTransaction));
        }
    }

    /**
     * Metoda slouží k přepočtu FIFO fronty po přidání nového nákupu.
     * @param sellTransactionID UID transakce "Prodej" ke zpracování
     * @param quantity Množství ke zpracování
     * @param listOfAvailableBuys Dostupné nákupy pro daný prodej
     */
    private void setSellAndBuyForNewSell(String sellTransactionID, BigDecimal quantity, List<TransactionWithPhotos> listOfAvailableBuys){
        AppDatabase db = AppDatabase.getDbInstance(context);
        boolean first = true;
        BigDecimal usedFromFirst = EMPTYBIGDECIMAL;
        BigDecimal usedFromLast = EMPTYBIGDECIMAL;
        long firstTakenFrom = -1;
        long lastTakenFrom = -1;


        for(TransactionWithPhotos buy : listOfAvailableBuys) {
            if(quantity.compareTo(BigDecimal.ZERO) < 1) {
                break;
            }

            BigDecimal newAmoutLeftBuy = shared.getBigDecimal(buy.transaction.amountLeft);
            if(newAmoutLeftBuy.compareTo(quantity) < 1){
                quantity = quantity.subtract(newAmoutLeftBuy);
                if(first){
                    usedFromFirst = newAmoutLeftBuy;
                }
                usedFromLast = newAmoutLeftBuy;
                newAmoutLeftBuy = BigDecimal.ZERO;
            }else{
                newAmoutLeftBuy = newAmoutLeftBuy.subtract(quantity);
                if(first){
                    usedFromFirst = quantity;
                }
                usedFromLast = quantity;
                quantity = BigDecimal.ZERO;
            }

            if(first){
                firstTakenFrom = buy.transaction.uidTransaction;
                first = false;
            }
            String amountLeft = newAmoutLeftBuy.compareTo(BigDecimal.ZERO) == 0 ? "0" : newAmoutLeftBuy.toPlainString();
            lastTakenFrom = buy.transaction.uidTransaction;
            db.databaseDao().updateAmountLeft(String.valueOf(buy.transaction.uidTransaction), amountLeft);
        }
        TransactionEntity sellEntity = db.databaseDao().getTransactionByTransactionHistoryID(sellTransactionID).transaction;
        String amountLeft = quantity.compareTo(BigDecimal.ZERO) == 0 ? "0" : quantity.toPlainString();
        if(sellEntity.transactionType.equals("Prodej")) {
            db.databaseDao().updateFifoCalc(sellTransactionID, amountLeft, String.valueOf(usedFromFirst), String.valueOf(usedFromLast), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
        }else{
            db.databaseDao().updateFifoCalcChangeSell(sellTransactionID, amountLeft, String.valueOf(usedFromFirst), String.valueOf(usedFromLast), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
        }
    }

    /**
     * Zpracování FIFO operace při vytvoření transakce směna.
     * @param transactionID UID transakce
     * @param quantityBuy Koupené množství
     * @param quantitySell Prodané množství
     * @param date Datum transakce
     * @param time Čas transakce
     * @param uidBought UID koupené kryptoměny
     * @param uidSold UID prodané kryptoměny
     */
    private void calcFifoChange(long transactionID, BigDecimal quantityBuy, BigDecimal quantitySell, long date, String time, String uidBought, String uidSold){
        // ----------------------------------------------
        // Nákup:
        calcFifoBuy(transactionID, quantityBuy, date, time, uidBought);
        // ----------------------------------------------

        // ----------------------------------------------
        // Prodej:
        calcFifoSell(transactionID, quantitySell, date, time, uidSold);
        // ----------------------------------------------
    }

    /**
     * Pomocná metoda pro provedení FIFO operace při přidání transakce nákup.
     * @param transactionID UID transakce
     * @param quantity Koupené množství
     * @param date Datum transakce
     * @param time Čas transakce
     * @param uidBought UID koupené kryptoměny
     */
    private void recalcForBuy(long transactionID, BigDecimal quantity, long date, String time, String uidBought) {
        AppDatabase db = AppDatabase.getDbInstance(context);

        BigDecimal amountLeft = quantity;
        List<TransactionWithPhotos> listOfIncompleteSales = db.databaseDao().getSellChangeNotEmptyFrom(date, time, uidBought);

        for(TransactionWithPhotos sell : listOfIncompleteSales){
            BigDecimal usedFromFirst = sell.transaction.usedFromFirst.equals("-1.0") ? EMPTYBIGDECIMAL : shared.getBigDecimal(sell.transaction.usedFromFirst);
            BigDecimal usedFromLast = sell.transaction.usedFromLast.equals("-1.0") ? EMPTYBIGDECIMAL : shared.getBigDecimal(sell.transaction.usedFromLast);
            long firstTakenFrom = sell.transaction.firstTakenFrom;
            long lastTakenFrom = sell.transaction.lastTakenFrom;
            BigDecimal inSellLeft;
            if(sell.transaction.transactionType.equals("Prodej")) {
                inSellLeft = amountLeft.compareTo(shared.getBigDecimal(sell.transaction.amountLeft)) < 1 ? shared.getBigDecimal(sell.transaction.amountLeft).subtract(amountLeft) : BigDecimal.ZERO;
            }else{
                inSellLeft = amountLeft.compareTo(shared.getBigDecimal(sell.transaction.amountLeftChangeSell)) < 1 ? shared.getBigDecimal(sell.transaction.amountLeftChangeSell).subtract(amountLeft) : BigDecimal.ZERO;
            }

            if(amountLeft.compareTo(BigDecimal.ZERO) == 1) {
                if(sell.transaction.transactionType.equals("Prodej")) {
                    amountLeft = amountLeft.compareTo(shared.getBigDecimal(sell.transaction.amountLeft)) < 1 ? BigDecimal.ZERO : amountLeft.subtract(shared.getBigDecimal(sell.transaction.amountLeft));
                }else{
                    amountLeft = amountLeft.compareTo(shared.getBigDecimal(sell.transaction.amountLeftChangeSell)) < 1 ? BigDecimal.ZERO : amountLeft.subtract(shared.getBigDecimal(sell.transaction.amountLeftChangeSell));
                }
                if (usedFromFirst.compareTo(EMPTYBIGDECIMAL) == 0) {
                    firstTakenFrom = transactionID;
                    if(sell.transaction.transactionType.equals("Prodej")) {
                        usedFromFirst = shared.getBigDecimal(sell.transaction.amountLeft).subtract(inSellLeft);
                    }else{
                        usedFromFirst = shared.getBigDecimal(sell.transaction.amountLeftChangeSell).subtract(inSellLeft);
                    }
                }
                lastTakenFrom = transactionID;
                if(sell.transaction.transactionType.equals("Prodej")) {
                    usedFromLast = shared.getBigDecimal(sell.transaction.amountLeft).subtract(inSellLeft);
                }else{
                    usedFromLast = shared.getBigDecimal(sell.transaction.amountLeftChangeSell).subtract(inSellLeft);
                }
            }

            if (inSellLeft.compareTo(BigDecimal.ZERO) == 1) {
                List<TransactionWithPhotos> listOfNextBuy = db.databaseDao().getNotEmptyBuyChangeTo(sell.transaction.date, sell.transaction.time, uidBought);

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

                    lastTakenFrom = nextBuy.transaction.uidTransaction;

                    String amountLeftString = amountOfNextBuy.compareTo(BigDecimal.ZERO) == 0 ? "0" : amountOfNextBuy.toPlainString();
                    db.databaseDao().updateAmountLeft(String.valueOf(nextBuy.transaction.uidTransaction), amountLeftString);
                }

            }

            String amountLeftString = inSellLeft.compareTo(BigDecimal.ZERO) == 0 ? "0" : inSellLeft.toPlainString();
            if(sell.transaction.transactionType.equals("Prodej")) {
                db.databaseDao().updateFifoCalc(String.valueOf(sell.transaction.uidTransaction), amountLeftString, String.valueOf(usedFromFirst), String.valueOf(usedFromLast), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
            }else {
                db.databaseDao().updateFifoCalcChangeSell(String.valueOf(sell.transaction.uidTransaction), amountLeftString, String.valueOf(usedFromFirst), String.valueOf(usedFromLast), String.valueOf(firstTakenFrom), String.valueOf(lastTakenFrom));
            }
        }

        String amountLeftString = amountLeft.compareTo(BigDecimal.ZERO) == 0 ? "0" : amountLeft.toPlainString();
        db.databaseDao().updateAmountLeft(String.valueOf(transactionID), amountLeftString);
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
