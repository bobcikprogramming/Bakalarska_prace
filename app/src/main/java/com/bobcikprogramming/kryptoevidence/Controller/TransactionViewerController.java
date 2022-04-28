package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.TransactionHistoryEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.View.ViewPagerAdapterTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class TransactionViewerController {

    private ViewPagerAdapterTransaction viewPagerAdapter;
    private List<TransactionWithPhotos> dataFromDatabase;
    private List<TransactionHistoryEntity> dataFromDatabaseHistory;
    private boolean changed;
    private ViewPager transactionViewer;
    private Context context;

    public TransactionViewerController(Context context, ViewPager transactionViewer, int position){
        this.context = context;
        this.transactionViewer = transactionViewer;

        changed = false;

        loadDataFromDb();
        setViewPagerAdapter(position);
    }

    public void setViewPagerAdapter(int position){
        viewPagerAdapter = new ViewPagerAdapterTransaction(context, dataFromDatabase, dataFromDatabaseHistory);
        transactionViewer.setAdapter(viewPagerAdapter);
        transactionViewer.setCurrentItem(position);
    }

    /**
     * Metoda načte data z databáze do seznamu transakcí (dataFromDatabase) a seznamu změn (dataFromDatabaseHistory).
     */
    public void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        dataFromDatabase = db.databaseDao().getAll();
        dataFromDatabaseHistory = db.databaseDao().getHistory();
    }

    public void viewPagerAdapterUpdate(){
        viewPagerAdapter.updateDatalists(dataFromDatabase, dataFromDatabaseHistory, transactionViewer.getCurrentItem());
    }

    public boolean isChanged() {
        return changed;
    }

    public List<TransactionWithPhotos> getDataFromDatabase() {
        return dataFromDatabase;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public ViewPagerAdapterTransaction getViewPagerAdapter() {
        return viewPagerAdapter;
    }
}
