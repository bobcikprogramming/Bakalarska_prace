package com.bobcikprogramming.kryptoevidence.Controller;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.DataVersionEntity;
import com.bobcikprogramming.kryptoevidence.Model.ExchangeByYearEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FirebaseAsyncTask extends AsyncTask<Void, Integer, String> {

    private TaskDelegate delegate;
    private Context context;
    private TextView tvUpdateInfo;
    private ProgressBar progressBar;

    private DatabaseReference exchangeReference, versionReference, yearReference;

    public FirebaseAsyncTask(TaskDelegate delegate, Context context, TextView tvUpdateInfo, ProgressBar progressBar) {
        this.delegate = delegate;
        this.context = context;
        this.tvUpdateInfo = tvUpdateInfo;
        this.progressBar = progressBar;

        String firebaseURL = "https://krypto-evidence-default-rtdb.europe-west1.firebasedatabase.app/";
        exchangeReference = FirebaseDatabase.getInstance(firebaseURL).getReference("exchange");
        versionReference = exchangeReference.child("version");
        yearReference = exchangeReference.child("year");
    }

    /**
     * Stažení kurzů z databáze Firebase.
     * Limit čekání na operaci 2 minuty.
     * @return Vrací výsledek typu string
     */
    @Override
    protected String doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDbInstance(context);

        CountDownLatch countDownLatch = new CountDownLatch(1); // https://stackoverflow.com/a/66502384
        versionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading("Hledání aktualizací...");
                    }
                });*/
                int version = Integer.parseInt(dataSnapshot.getValue().toString());
                int appDbVersion = db.databaseDao().getDataVersionRate();
                if(version != appDbVersion) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showLoading("Stahování dat...");
                        }
                    });
                    getExchangeByYearFromFirebase(db);
                    updateVersion(db, appDbVersion, version);
                }
                countDownLatch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(2L, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "firebase";
    }

    /**
     * Po skončení operace vrací výsledekem typu string pomocí delegátoru
     * @param result výsledek typu string
     */
    @Override
    protected void onPostExecute(String result) {
        delegate.TaskCompletionResult(result);
    }

    /**
     * Jedná-li se o první stažení, tak metoda vytvoří tabulku verze a nahraje staženou verzi, jinak ji v tabulce aktualizuje
     * @param db Přístup k lokální databízi
     * @param versionAppDB Verze uložená v lokální databázi
     * @param newVersion Verze v databázi Firebase
     */
    private void updateVersion(AppDatabase db, int versionAppDB, int newVersion){
        if(versionAppDB == 0){
            DataVersionEntity dataVersion = new DataVersionEntity();
            dataVersion.versionRate = newVersion;
            db.databaseDao().insertVersion(dataVersion);
        }else {
            db.databaseDao().updateVersionRate(newVersion);
        }
    }

    /**
     * Metoda stáhne a uloží kurzy za jednotlivé roky
     * @param db Přístup k lokální databázi
     */
    private void getExchangeByYearFromFirebase(AppDatabase db){
        db.databaseDao().deleteExchange();
        yearReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                ExchangeByYearEntity exchange;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    int year = Integer.parseInt(snapshot.getKey());
                    double eur = Double.parseDouble(snapshot.child("eur").getValue().toString());
                    double usd = Double.parseDouble(snapshot.child("usd").getValue().toString());
                    exchange = new ExchangeByYearEntity();
                    exchange.year = year;
                    exchange.eur = eur;
                    exchange.usd = usd;
                    db.databaseDao().insertExchange(exchange);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /**
     * Metoda pro zobrazení oznámení o stahování dat
     * @param loadText Stringová hednota s textem k zobrazení
     */
    private void showLoading(String loadText){
        progressBar.setVisibility(View.VISIBLE);
        tvUpdateInfo.setVisibility(View.VISIBLE);
        tvUpdateInfo.setText(loadText);
    }
}
