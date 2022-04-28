package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.CryptocurrencyEntity;
import com.bobcikprogramming.kryptoevidence.Model.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionHistoryEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class ViewPagerAdapterTransactionController {

    private List<TransactionWithPhotos> dataList;
    private List<TransactionHistoryEntity> dataListHistory;
    private int position;

    private CalendarManager calendar;
    private SharedMethods shared;

    public ViewPagerAdapterTransactionController(List<TransactionWithPhotos> dataList, List<TransactionHistoryEntity> dataListHistory){
        this.dataList = dataList;
        this.dataListHistory = dataListHistory;

        calendar = new CalendarManager();
        shared = new SharedMethods();
    }

    /**
     * Metoda slouží k seřazení seznamu dle data (od nejvyššího po nejnižší).
     * @param data Seznam k seřazení
     * @return Seřazený seznam
     */
    public ArrayList<TransactionHistoryList> sortListByDate(ArrayList<TransactionHistoryList> data){
        TransactionHistoryList tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                try{
                    Date dateFirst = format.parse(data.get(j).getChangeValueDate());
                    Date dateSecond = format.parse(data.get(j+1).getChangeValueDate());
                    if(dateFirst.compareTo(dateSecond) < 0){
                        tmp = data.get(j);
                        data.set(j, data.get(j+1));
                        data.set(j+1, tmp);
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * Metoda slouží k seřazení seznamu dle času (od nejvyššího po nejnižší).
     * @param data Seznam k seřazení
     * @return Seřazený seznam
     */
    public ArrayList<TransactionHistoryList> sortListByTime(ArrayList<TransactionHistoryList> data){
        TransactionHistoryList tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                try{
                    Date timeFirst = format.parse(data.get(j).getChangeValueTime());
                    Date timeSecond = format.parse(data.get(j+1).getChangeValueTime());
                    if(timeFirst.compareTo(timeSecond) < 0){
                        tmp = data.get(j);
                        data.set(j, data.get(j+1));
                        data.set(j+1, tmp);
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * Metoda slouží k převedení transakce nákup nebo prodej do vyobrazovací podoby (přidání odpovídajícího popisu).
     * @param context Třída context activity, ze které je metoda volána
     * @return Seznam řádků s daty k vyobrazení
     */
    public ArrayList<TransactionInfoList> getTransactionForBuyOrSell(Context context){
        AppDatabase db = AppDatabase.getDbInstance(context);
        ArrayList<TransactionInfoList> transactionInfoList = new ArrayList<>();

        TransactionInfoList firstRow = new TransactionInfoList();
        TransactionInfoList secondRow = new TransactionInfoList();
        TransactionInfoList thirdRow = new TransactionInfoList();
        TransactionInfoList fourthRow = new TransactionInfoList();
        TransactionInfoList fifthRow = new TransactionInfoList();
        TransactionInfoList sixthRow = new TransactionInfoList();

        TransactionEntity transaction = dataList.get(position).transaction;

        // Společná parametry
        firstRow.setRightDesc("Zkratka");

        secondRow.setRightDesc("Měna");

        fourthRow.setLeftDesc("Poplatek");
        fourthRow.setRightDesc("Měna");
        fourthRow.setLeftValue(String.valueOf(transaction.fee));
        fourthRow.setRightValue(transaction.currency);

        fifthRow.setRightDesc("Měna");
        fifthRow.setRightValue(transaction.currency);

        sixthRow.setLeftDesc("Datum");
        sixthRow.setRightDesc("Čas");
        sixthRow.setLeftValue(calendar.getDateFromMillis(transaction.date));
        sixthRow.setRightValue(transaction.time);


        if(transaction.transactionType.equals("Nákup")){
            CryptocurrencyEntity cryptoBuy = db.databaseDao().getCryptoById(transaction.uidBought);
            String shortNameBought = cryptoBuy.shortName;
            String longNameBought = cryptoBuy.longName;

            firstRow.setLeftDesc("Koupená měna");
            firstRow.setLeftValue(longNameBought);
            firstRow.setRightValue(shortNameBought);

            secondRow.setLeftDesc("Koupené množství");
            secondRow.setLeftValue(shared.getBigDecimal(transaction.quantityBought).toPlainString());
            secondRow.setRightValue(shortNameBought);

            thirdRow.setLeftDesc("Pořizovací cena");
            thirdRow.setRightDesc("Měna");
            thirdRow.setRightValue(transaction.currency);
            thirdRow.setLeftValue(shared.getBigDecimal(transaction.priceBought).toPlainString());

            fifthRow.setLeftDesc("Poř. cena bez poplatku");
            fifthRow.setLeftValue(shared.getBigDecimal(transaction.quantitySold).toPlainString());
        }else{
            CryptocurrencyEntity cryptoSell = db.databaseDao().getCryptoById(transaction.uidSold);
            String shortNameSold = cryptoSell.shortName;
            String longNameSold = cryptoSell.longName;

            firstRow.setLeftDesc("Prodaná měna");
            firstRow.setLeftValue(longNameSold);
            firstRow.setRightValue(shortNameSold);

            secondRow.setLeftDesc("Prodané množství");
            secondRow.setLeftValue(shared.getBigDecimal(transaction.quantitySold).toPlainString());
            secondRow.setRightValue(shortNameSold);

            thirdRow.setLeftDesc("Prodejní cena");
            thirdRow.setRightDesc("Měna");
            thirdRow.setRightValue(transaction.currency);
            thirdRow.setLeftValue(shared.getBigDecimal(transaction.priceSold).toPlainString());

            fifthRow.setLeftDesc("Čistá prodejní cena");
            fifthRow.setLeftValue(shared.getBigDecimal(transaction.quantityBought).toPlainString());
        }

        transactionInfoList.add(firstRow);
        transactionInfoList.add(secondRow);
        transactionInfoList.add(thirdRow);
        transactionInfoList.add(fourthRow);
        transactionInfoList.add(fifthRow);
        transactionInfoList.add(sixthRow);

        return transactionInfoList;
    }

    /**
     * Metoda slouží k převedení transakci směna do vyobrazovací podoby (přidání odpovídajícího popisu).
     * @param context Třída context activity, ze které je metoda volána
     * @return Seznam řádků s daty k vyobrazení
     */
    public ArrayList<TransactionInfoList> getTransactionForChange(Context context){
        AppDatabase db = AppDatabase.getDbInstance(context);
        ArrayList<TransactionInfoList> transactionInfoList = new ArrayList<>();
        TransactionEntity transaction = dataList.get(position).transaction;

        CryptocurrencyEntity cryptoBuy = db.databaseDao().getCryptoById(transaction.uidBought);
        String shortNameBought = cryptoBuy.shortName;
        String longNameBought = cryptoBuy.longName;

        CryptocurrencyEntity cryptoSell = db.databaseDao().getCryptoById(transaction.uidSold);
        String shortNameSold = cryptoSell.shortName;
        String longNameSold = cryptoSell.longName;

        TransactionInfoList firstRow = new TransactionInfoList();
        TransactionInfoList secondRow = new TransactionInfoList();
        TransactionInfoList thirdRow = new TransactionInfoList();
        TransactionInfoList fourthRow = new TransactionInfoList();
        TransactionInfoList fifthRow = new TransactionInfoList();
        TransactionInfoList sixthRow = new TransactionInfoList();
        TransactionInfoList eighthRow = new TransactionInfoList();

        firstRow.setLeftDesc("Koupená měna");
        firstRow.setRightDesc("Zkratka");
        firstRow.setLeftValue(longNameBought);
        firstRow.setRightValue(shortNameBought);

        secondRow.setLeftDesc("Koupené množství");
        secondRow.setRightDesc("Měna");
        secondRow.setLeftValue(shared.getBigDecimal(transaction.quantityBought).toPlainString());
        secondRow.setRightValue(shortNameBought);

        thirdRow.setLeftDesc("Cena směny");
        thirdRow.setRightDesc("Měna");
        thirdRow.setLeftValue(shared.getBigDecimal(transaction.priceBought).toPlainString());
        thirdRow.setRightValue(transaction.currency);

        fourthRow.setLeftDesc("Prodaná měna");
        fourthRow.setRightDesc("Zkratka");
        fourthRow.setLeftValue(longNameSold);
        fourthRow.setRightValue(shortNameSold);

        fifthRow.setLeftDesc("Prodané množství");
        fifthRow.setRightDesc("Měna");
        fifthRow.setLeftValue(shared.getBigDecimal(transaction.quantitySold).toPlainString());
        fifthRow.setRightValue(shortNameSold);

        sixthRow.setLeftDesc("Poplatek");
        sixthRow.setRightDesc("Měna");
        sixthRow.setLeftValue(String.valueOf(transaction.fee));
        sixthRow.setRightValue(transaction.currency);

        eighthRow.setLeftDesc("Datum");
        eighthRow.setRightDesc("Čas");
        eighthRow.setLeftValue(calendar.getDateFromMillis(transaction.date));
        eighthRow.setRightValue(transaction.time);

        transactionInfoList.add(firstRow);
        transactionInfoList.add(secondRow);
        transactionInfoList.add(thirdRow);
        transactionInfoList.add(fourthRow);
        transactionInfoList.add(fifthRow);
        transactionInfoList.add(sixthRow);
        transactionInfoList.add(eighthRow);

        return transactionInfoList;
    }

    /**
     * Metoda slouží k převedení historie transakce do vyobrazovací podoby historie (přidání odpovídajícího popisu).
     * @param context Třída context activity, ze které je metoda volána
     * @return Seznam řádků s daty k vyobrazení
     */
    public ArrayList<TransactionHistoryList> getHistoryList(Context context){
        ArrayList<TransactionHistoryEntity> history = getHistoryForActualTransaction(position);
        ArrayList<TransactionHistoryList> historyOfActualTransaction = new ArrayList<>();

        if(history.size() > 0){
            for(TransactionHistoryEntity transactionHistory : history){
                TransactionHistoryList transaction = new TransactionHistoryList();

                transaction.setChangeValueDate(transactionHistory.dateOfChange);
                transaction.setChangeValueTime(transactionHistory.timeOfChange);

                if(transactionHistory.transactionType.equals("Nákup")){
                    transaction.setTransactionType("Nákup");
                    if(transactionHistory.quantityBought != null){
                        transaction.setQuantityBoughtDesc("Koupené množství");
                        transaction.setQuantityBoughtValue(String.valueOf(transactionHistory.quantityBought));
                    }
                    if(transactionHistory.priceBought != null){
                        transaction.setPriceBoughtDesc("Pořizovací cena");
                        transaction.setPriceBoughtValue(String.valueOf(transactionHistory.priceBought));
                    }
                }

                if(transactionHistory.transactionType.equals("Prodej")){
                    transaction.setTransactionType("Prodej");
                    if(transactionHistory.quantitySold != null){
                        transaction.setQuantitySoldDesc("Prodané množství");
                        transaction.setQuantitySoldValue(String.valueOf(transactionHistory.quantitySold));
                    }
                    if(transactionHistory.priceSold != null){
                        transaction.setPriceSoldDesc("Prodejní cena");
                        transaction.setPriceSoldValue(String.valueOf(transactionHistory.priceSold));
                    }
                }

                if(transactionHistory.transactionType.equals("Směna")){
                    transaction.setTransactionType("Směna");
                    if(transactionHistory.quantityBought != null){
                        transaction.setQuantityBoughtDesc("Koupené množství");
                        transaction.setQuantityBoughtValue(String.valueOf(transactionHistory.quantityBought));
                    }
                    if(transactionHistory.priceBought != null){
                        transaction.setPriceBoughtDesc("Cena směny");
                        transaction.setPriceBoughtValue(String.valueOf(transactionHistory.priceBought));
                    }
                    if(transactionHistory.uidSold != null){
                        String longNameSold = AppDatabase.getDbInstance(context).databaseDao().getCryptoById(transactionHistory.uidSold).longName;
                        transaction.setLongNameSoldDesc("Prodaná měna");
                        transaction.setLongNameSoldValue(longNameSold);
                    }
                    if(transactionHistory.quantitySold != null){
                        transaction.setQuantitySoldDesc("Prodané množství");
                        transaction.setQuantitySoldValue(String.valueOf(transactionHistory.quantitySold));
                    }
                }

                if(transactionHistory.currency != null){
                    transaction.setCurrencyDesc("Cena v měně");
                    transaction.setCurrencyValue(transactionHistory.currency);
                }
                if(transactionHistory.fee != null){
                    transaction.setFeeDesc("Poplatek");
                    transaction.setFeeValue(String.valueOf(transactionHistory.fee));
                }
                if(transactionHistory.date != 0){
                    transaction.setDateDesc("Datum provedení");
                    transaction.setDateValue(calendar.getDateFromMillis(transactionHistory.date));
                }
                if(transactionHistory.time != null){
                    transaction.setTimeDesc("Čas provedení");
                    transaction.setTimeValue(transactionHistory.time);
                }
                if(transactionHistory.note != null){
                    transaction.setNoteDesc("Poznámka o změně");
                    transaction.setNoteValue(transactionHistory.note);
                }

                historyOfActualTransaction.add(transaction);
            }
            historyOfActualTransaction = sortListByTime(historyOfActualTransaction);
            historyOfActualTransaction = sortListByDate(historyOfActualTransaction);
        }
        return historyOfActualTransaction;
    }

    /**
     * Metoda slouží k získání seznamu změn transakce z databáze.
     * @param position Pozice transakce
     * @return Seznam změn transakce
     */
    private ArrayList<TransactionHistoryEntity> getHistoryForActualTransaction(int position){
        ArrayList<TransactionHistoryEntity> historyOfActualTransaction = new ArrayList<>();
        for(TransactionHistoryEntity history : dataListHistory){
            if(history.parentTransactionId != dataList.get(position).transaction.uidTransaction){
                continue;
            }
            historyOfActualTransaction.add(history);
        }
        return historyOfActualTransaction;
    }

    public List<TransactionWithPhotos> getDataList() {
        return dataList;
    }

    public void setDataList(List<TransactionWithPhotos> dataList) {
        this.dataList = dataList;
    }

    public void setDataListHistory(List<TransactionHistoryEntity> dataListHistory) {
        this.dataListHistory = dataListHistory;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<PhotoEntity> getPhotos(){
        return dataList.get(position).photos;
    }
}
