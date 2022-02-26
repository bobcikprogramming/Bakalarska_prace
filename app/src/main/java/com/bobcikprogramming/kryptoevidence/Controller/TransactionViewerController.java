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

    public void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        dataFromDatabase = db.databaseDao().getAll();
        sortListByTime(dataFromDatabase);
        sortListByDate(dataFromDatabase);

        dataFromDatabaseHistory = db.databaseDao().getHistory();
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

    private void sortListByTime(List<TransactionWithPhotos> data) {
        TransactionWithPhotos tmp;
        for (int i = 0; i < data.size() - 1; i++) {
            for (int j = 0; j < data.size() - i - 1; j++) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                try {
                    Date timeFirst = format.parse(data.get(j).transaction.time);
                    Date timeSecond = format.parse(data.get(j + 1).transaction.time);
                    if (timeFirst.compareTo(timeSecond) < 0) {
                        tmp = data.get(j);
                        data.set(j, data.get(j + 1));
                        data.set(j + 1, tmp);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
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
