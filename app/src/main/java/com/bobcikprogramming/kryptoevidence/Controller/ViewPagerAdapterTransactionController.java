package com.bobcikprogramming.kryptoevidence.Controller;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Model.PhotoEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionHistoryEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewPagerAdapterTransactionController {

    private List<TransactionWithPhotos> dataList;
    private List<TransactionHistoryEntity> dataListHistory;
    private int position;

    private CalendarManager calendar;

    public ViewPagerAdapterTransactionController(List<TransactionWithPhotos> dataList, List<TransactionHistoryEntity> dataListHistory){
        this.dataList = dataList;
        this.dataListHistory = dataListHistory;

        calendar = new CalendarManager();
    }

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

    public ArrayList<TransactionInfoList> getTransactionForBuyOrSell(){
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

        thirdRow.setLeftDesc("Cena za kus");
        thirdRow.setRightDesc("Měna");
        thirdRow.setRightValue(transaction.currency);

        fourthRow.setLeftDesc("Poplatek");
        fourthRow.setRightDesc("Měna");
        fourthRow.setLeftValue(String.valueOf(transaction.fee));
        fourthRow.setRightValue(transaction.currency);

        fifthRow.setRightDesc("Měna");
        fifthRow.setRightValue(transaction.currency);

        sixthRow.setLeftDesc("Datum");
        sixthRow.setRightDesc("Čas");
        sixthRow.setLeftValue(calendar.getDateFormatFromDatabase(transaction.date));
        sixthRow.setRightValue(transaction.time);


        if(transaction.transactionType.equals("Nákup")){
            firstRow.setLeftDesc("Koupená měna");
            firstRow.setLeftValue(transaction.longNameBought);
            firstRow.setRightValue(transaction.shortNameBought);

            secondRow.setLeftDesc("Koupené množství");
            secondRow.setLeftValue(String.valueOf(transaction.quantityBought));
            secondRow.setRightValue(transaction.shortNameBought);

            thirdRow.setLeftValue(String.valueOf(transaction.priceBought));

            fifthRow.setLeftDesc("Celková cena");
            fifthRow.setLeftValue(String.valueOf(transaction.quantitySold));
        }else{
            firstRow.setLeftDesc("Prodaná měna");
            firstRow.setLeftValue(transaction.longNameSold);
            firstRow.setRightValue(transaction.shortNameSold);

            secondRow.setLeftDesc("Prodané množství");
            secondRow.setLeftValue(String.valueOf(transaction.quantitySold));
            secondRow.setRightValue(transaction.shortNameSold);

            thirdRow.setLeftValue(String.valueOf(transaction.priceSold));

            fifthRow.setLeftDesc("Celkový zisk");
            fifthRow.setLeftValue(String.valueOf(transaction.quantityBought));
        }

        transactionInfoList.add(firstRow);
        transactionInfoList.add(secondRow);
        transactionInfoList.add(thirdRow);
        transactionInfoList.add(fourthRow);
        transactionInfoList.add(fifthRow);
        transactionInfoList.add(sixthRow);

        return transactionInfoList;
    }

    public ArrayList<TransactionInfoList> getTransactionForChange(){
        ArrayList<TransactionInfoList> transactionInfoList = new ArrayList<>();

        TransactionInfoList firstRow = new TransactionInfoList();
        TransactionInfoList secondRow = new TransactionInfoList();
        TransactionInfoList thirdRow = new TransactionInfoList();
        TransactionInfoList fourthRow = new TransactionInfoList();
        TransactionInfoList fifthRow = new TransactionInfoList();
        TransactionInfoList sixthRow = new TransactionInfoList();
        TransactionInfoList seventhRow = new TransactionInfoList();
        TransactionInfoList eighthRow = new TransactionInfoList();

        TransactionEntity transaction = dataList.get(position).transaction;

        firstRow.setLeftDesc("Koupená měna");
        firstRow.setRightDesc("Zkratka");
        firstRow.setLeftValue(transaction.longNameBought);
        firstRow.setRightValue(transaction.shortNameBought);

        secondRow.setLeftDesc("Koupené množství");
        secondRow.setRightDesc("Měna");
        secondRow.setLeftValue(String.valueOf(transaction.quantityBought));
        secondRow.setRightValue(transaction.shortNameBought);

        thirdRow.setLeftDesc("Cena za kus");
        thirdRow.setRightDesc("Měna");
        thirdRow.setLeftValue(String.valueOf(transaction.priceBought));
        thirdRow.setRightValue(transaction.currency);

        fourthRow.setLeftDesc("Prodaná měna");
        fourthRow.setRightDesc("Zkratka");
        fourthRow.setLeftValue(transaction.longNameSold);
        fourthRow.setRightValue(transaction.shortNameSold);

        fifthRow.setLeftDesc("Prodané množství");
        fifthRow.setRightDesc("Měna");
        fifthRow.setLeftValue(String.valueOf(transaction.quantitySold));
        fifthRow.setRightValue(transaction.shortNameSold);

        sixthRow.setLeftDesc("Cena za kus");
        sixthRow.setRightDesc("Měna");
        sixthRow.setLeftValue(String.valueOf(transaction.priceSold));
        sixthRow.setRightValue(transaction.currency);

        seventhRow.setLeftDesc("Poplatek");
        seventhRow.setRightDesc("Měna");
        seventhRow.setLeftValue(String.valueOf(transaction.fee));
        seventhRow.setRightValue(transaction.currency);

        eighthRow.setLeftDesc("Datum");
        eighthRow.setRightDesc("Čas");
        eighthRow.setLeftValue(calendar.getDateFormatFromDatabase(transaction.date));
        eighthRow.setRightValue(transaction.time);

        transactionInfoList.add(firstRow);
        transactionInfoList.add(secondRow);
        transactionInfoList.add(thirdRow);
        transactionInfoList.add(fourthRow);
        transactionInfoList.add(fifthRow);
        transactionInfoList.add(sixthRow);
        transactionInfoList.add(seventhRow);
        transactionInfoList.add(eighthRow);

        return transactionInfoList;
    }

    public ArrayList<TransactionHistoryList> getHistoryList(LinearLayout historyUnderline, LinearLayout historyLayout, TextView historyHeadline){
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
                        transaction.setPriceBoughtDesc("Cena za kus");
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
                        transaction.setPriceSoldDesc("Cena za kus");
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
                        transaction.setPriceBoughtDesc("Cena za kus");
                        transaction.setPriceBoughtValue(String.valueOf(transactionHistory.priceBought));
                    }
                    if(transactionHistory.longNameSold != null){
                        transaction.setLongNameSoldDesc("Prodaná měna");
                        transaction.setLongNameSoldValue(transactionHistory.longNameSold);
                    }
                    if(transactionHistory.quantitySold != null){
                        transaction.setQuantitySoldDesc("Prodané množství");
                        transaction.setQuantitySoldValue(String.valueOf(transactionHistory.quantitySold));
                    }
                    if(transactionHistory.priceSold != null){
                        transaction.setPriceSoldDesc("Cena za kus");
                        transaction.setPriceSoldValue(String.valueOf(transactionHistory.priceSold));
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
                if(transactionHistory.date != null){
                    transaction.setDateDesc("Datum provedení");
                    transaction.setDateValue(calendar.getDateFormatFromDatabase(transactionHistory.date));
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
        }else{
            historyUnderline.setVisibility(View.GONE);
            historyHeadline.setVisibility(View.GONE);
            historyLayout.setVisibility(View.GONE);
        }
        return historyOfActualTransaction;
    }

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

    public List<TransactionHistoryEntity> getDataListHistory() {
        return dataListHistory;
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
