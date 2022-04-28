package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.View.RecyclerViewTransactions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class FragmentTransactionsController {

    private List<TransactionWithPhotos> dataFromDatabase;

    private CalendarManager calendar;

    private boolean isSetDateFrom, isSetDateTo;
    private List<TransactionWithPhotos> dataToShow;

    public FragmentTransactionsController(){
        isSetDateFrom = false;
        isSetDateTo = false;

        calendar = new CalendarManager();

        dataToShow = new ArrayList<>();
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

    /**
     * Metoda načte data z databáze do seznamu transakcí
     * @param context Třída context activity, ze které je metoda volána
     * @param adapter Adaptér pro přístup k třídě RecyclerViewTransactions
     * @param calendarDateFrom Datum od které mají začínat zobrazené transakce
     * @param calendarDateTo Datum do kterého se mají zobrazovat transakce
     */
    public void loadDataFromDb(Context context, RecyclerViewTransactions adapter, Calendar calendarDateFrom, Calendar calendarDateTo){
        AppDatabase db = AppDatabase.getDbInstance(context);
        dataFromDatabase = db.databaseDao().getAll();
        getDataToShow(calendarDateFrom, calendarDateTo);
        adapter.setTransactionData(dataToShow);
    }

    /**
     * Metoda pro vyfiltrování seznamu s transakcemi k zobrazení dle data OD a DO
     * @param calendarDateFrom Datum od které mají začínat zobrazené transakce
     * @param calendarDateTo Datum do kterého se mají zobrazovat transakce
     */
    private void getDataToShow(Calendar calendarDateFrom, Calendar calendarDateTo){
        if(isSetDateFrom || isSetDateTo) {
            int yearFrom = calendarDateFrom.get(Calendar.YEAR);
            int monthFrom = calendarDateFrom.get(Calendar.MONTH) + 1;
            int dayFrom = calendarDateFrom.get(Calendar.DAY_OF_MONTH);
            String dateFrom = dayFrom + "." + monthFrom + "." + yearFrom;

            int yearTo = calendarDateTo.get(Calendar.YEAR);
            int monthTo = calendarDateTo.get(Calendar.MONTH) + 1;
            int dayTo = calendarDateTo.get(Calendar.DAY_OF_MONTH);
            String dateTo = dayTo + "." + monthTo + "." + yearTo;

            if(isSetDateFrom && isSetDateTo){
                dataToShow = getDataFromInterval(dateFrom, dateTo);
            } else if (isSetDateFrom) {
                dataToShow = getDataFromSpecificDate(dateFrom);
            } else{
                dataToShow = getDataToSpecificDate(dateTo);
            }
        }else{
            dataToShow = dataFromDatabase;
        }
    }

    /**
     * Pomocná metoda pro filtrování dat OD DO
     * @param dateFrom Datum od které mají začínat zobrazené transakce
     * @param dateTo Datum do kterého se mají zobrazovat transakce
     * @return Vyfiltrovaný seznam
     */
    private List<TransactionWithPhotos> getDataFromInterval(String dateFrom, String dateTo){
        long dateFromParsed = calendar.getDateMillis(dateFrom);
        long dateToParsed = calendar.getDateMillis(dateTo);
        List<TransactionWithPhotos> newDataList = new ArrayList<>();

        for (TransactionWithPhotos transaction : dataFromDatabase) {
            long dateOfTransaction = transaction.transaction.date;
            if (dateOfTransaction >= dateFromParsed && dateOfTransaction <= dateToParsed) {
                newDataList.add(transaction);
            }
        }
        return newDataList;
    }

    /**
     * Pomocná metoda pro filtrování dat OD
     * @param dateFrom Datum od které mají začínat zobrazené transakce
     * @return Vyfiltrovaný seznam
     */
    private List<TransactionWithPhotos> getDataFromSpecificDate(String dateFrom){
        long dateFromParsed = calendar.getDateMillis(dateFrom);
        List<TransactionWithPhotos> newDataList = new ArrayList<>();

        for (TransactionWithPhotos transaction : dataFromDatabase) {
            long dateOfTransaction = transaction.transaction.date;
            if (dateOfTransaction >= dateFromParsed) {
                newDataList.add(transaction);
            }
        }
        return newDataList;
    }

    /**
     * Pomocná metoda pro filtrování dat DO
     * @param dateTo Datum do kterého se mají zobrazovat transakce
     * @return Vyfiltrovaný seznam
     */
    private List<TransactionWithPhotos> getDataToSpecificDate(String dateTo){
        long dateToParsed = calendar.getDateMillis(dateTo);
        List<TransactionWithPhotos> newDataList = new ArrayList<>();

        for (TransactionWithPhotos transaction : dataFromDatabase) {
            long dateOfTransaction = transaction.transaction.date;
            if (dateOfTransaction <= dateToParsed) {
                newDataList.add(transaction);
            }
        }
        return newDataList;
    }

    /**
     * Metoda pro obnovení zobrazených dat v recyclerview
     * @param adapter Adaptér pro přístup k třídě RecyclerViewTransactions
     * @param calendarDateFrom Datum od které mají začínat zobrazené transakce
     * @param calendarDateTo Datum do kterého se mají zobrazovat transakce
     */
    public void refreshAdapter(RecyclerViewTransactions adapter, Calendar calendarDateFrom, Calendar calendarDateTo){
        getDataToShow(calendarDateFrom, calendarDateTo);
        adapter.setTransactionData(dataToShow);
    }

    /**
     * Getter pro získání indexu vybrané položky z celkového seznamu transakcí
     * @param position Pozice v seznamu zobrazených transakcí
     * @return Pozice v celkovém seznamu transakcí
     */
    public int getIndexToShow(int position) {
        return dataFromDatabase.indexOf(dataToShow.get(position));
    }
}
