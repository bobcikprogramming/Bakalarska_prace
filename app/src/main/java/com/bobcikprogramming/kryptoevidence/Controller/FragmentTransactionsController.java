package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.RecyclerViewTransactions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FragmentTransactionsController {

    private List<TransactionWithPhotos> dataFromDatabase;

    private boolean isSetDateFrom, isSetDateTo;

    public FragmentTransactionsController(){
        isSetDateFrom = false;
        isSetDateTo = false;
    }

    public boolean isSetDateFrom() {
        return isSetDateFrom;
    }

    public void setSetDateFrom(boolean setDateFrom) {
        isSetDateFrom = setDateFrom;
    }

    public boolean isSetDateTo() {
        return isSetDateTo;
    }

    public void setSetDateTo(boolean setDateTo) {
        isSetDateTo = setDateTo;
    }

    public void loadDataFromDb(Context context, RecyclerViewTransactions adapter, Calendar calendarDateFrom, Calendar calendarDateTo){
        AppDatabase db = AppDatabase.getDbInstance(context);
        dataFromDatabase = db.databaseDao().getAll();
        sortListByTime(dataFromDatabase);
        sortListByDate(dataFromDatabase);
        adapter.setTransactionData(getDataToShow(dataFromDatabase, calendarDateFrom, calendarDateTo));
    }

    private void sortListByDate(List<TransactionWithPhotos> data){
        TransactionWithPhotos tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                try{
                    Date dateFirst = format.parse(data.get(j).transaction.date);
                    Date dateSecond = format.parse(data.get(j+1).transaction.date);
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
    }

    private void sortListByTime(List<TransactionWithPhotos> data){
        TransactionWithPhotos tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                try{
                    Date timeFirst = format.parse(data.get(j).transaction.time);
                    Date timeSecond = format.parse(data.get(j+1).transaction.time);
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
    }

    private List<TransactionWithPhotos> getDataToShow(List<TransactionWithPhotos> dataFromDatabase, Calendar calendarDateFrom, Calendar calendarDateTo){
        if(isSetDateFrom || isSetDateTo) {
            int yearFrom = calendarDateFrom.get(Calendar.YEAR);
            int monthFrom = calendarDateFrom.get(Calendar.MONTH) + 1;
            int dayFrom = calendarDateFrom.get(Calendar.DAY_OF_MONTH);
            String dateFrom = dayFrom + "." + monthFrom + "." + yearFrom;

            int yearTo = calendarDateTo.get(Calendar.YEAR);
            int monthTo = calendarDateTo.get(Calendar.MONTH) + 1;
            int dayTo = calendarDateTo.get(Calendar.DAY_OF_MONTH);
            String dateTo = dayTo + "." + monthTo + "." + yearTo;

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            List<TransactionWithPhotos> newDataList = new ArrayList<TransactionWithPhotos>();

            if(isSetDateFrom && isSetDateTo){
                return getDataFromInterval(dataFromDatabase, dateFrom, dateTo, dateFormat);
            } else if (isSetDateFrom) {
                return getDataFromSpecificDate(dataFromDatabase, dateFrom, dateFormat);
            } else{
                return getDataToSpecificDate(dataFromDatabase, dateTo, dateFormat);
            }
        }else{
            return dataFromDatabase;
        }
    }

    private List<TransactionWithPhotos> getDataFromInterval(List<TransactionWithPhotos> dataFromDatabase, String dateFrom, String dateTo, SimpleDateFormat dateFormat){
        Date dateFromParsed = null;
        Date dateToParsed = null;
        List<TransactionWithPhotos> newDataList = new ArrayList<>();

        try {
            dateFromParsed = dateFormat.parse(dateFrom);
            dateToParsed = dateFormat.parse(dateTo);
        } catch (Exception e) {
            System.err.println("Chyba při parsování data: " + e);
            return dataFromDatabase;
        }

        for (TransactionWithPhotos transaction : dataFromDatabase) {
            try {
                Date dateOfTransaction = dateFormat.parse(transaction.transaction.date);
                /** https://stackoverflow.com/questions/2592501/how-to-compare-dates-in-java */
                if (!dateOfTransaction.before(dateFromParsed) && !dateOfTransaction.after(dateToParsed)) {
                    newDataList.add(transaction);
                }
            } catch (Exception e) {
                System.err.println("Chyba při parsování data: " + e);
                return dataFromDatabase;
            }
        }
        return newDataList;
    }

    private List<TransactionWithPhotos> getDataFromSpecificDate(List<TransactionWithPhotos> dataFromDatabase, String dateFrom, SimpleDateFormat dateFormat){
        Date dateFromParsed = null;
        List<TransactionWithPhotos> newDataList = new ArrayList<>();

        try {
            dateFromParsed = dateFormat.parse(dateFrom);
        } catch (Exception e) {
            System.err.println("Chyba při parsování data: " + e);
            return dataFromDatabase;
        }

        for (TransactionWithPhotos transaction : dataFromDatabase) {
            try {
                Date dateOfTransaction = dateFormat.parse(transaction.transaction.date);
                /** https://stackoverflow.com/questions/2592501/how-to-compare-dates-in-java */
                if (!dateOfTransaction.before(dateFromParsed)) {
                    newDataList.add(transaction);
                }
            } catch (Exception e) {
                System.err.println("Chyba při parsování data: " + e);
                return dataFromDatabase;
            }
        }
        return newDataList;
    }

    private List<TransactionWithPhotos> getDataToSpecificDate(List<TransactionWithPhotos> dataFromDatabase, String dateTo, SimpleDateFormat dateFormat){
        Date dateToParsed;
        List<TransactionWithPhotos> newDataList = new ArrayList<>();

        try {
            dateToParsed = dateFormat.parse(dateTo);
        } catch (Exception e) {
            System.err.println("Chyba při parsování data: " + e);
            return dataFromDatabase;
        }

        for (TransactionWithPhotos transaction : dataFromDatabase) {
            try {
                Date dateOfTransaction = dateFormat.parse(transaction.transaction.date);
                // https://stackoverflow.com/questions/2592501/how-to-compare-dates-in-java
                if (!dateOfTransaction.after(dateToParsed)) {
                    newDataList.add(transaction);
                }
            } catch (Exception e) {
                System.err.println("Chyba při parsování data: " + e);
                return dataFromDatabase;
            }
        }
        return newDataList;
    }

    public void refreshAdapter(RecyclerViewTransactions adapter, Calendar calendarDateFrom, Calendar calendarDateTo){
        adapter.setTransactionData(getDataToShow(dataFromDatabase, calendarDateFrom, calendarDateTo));
    }
}
