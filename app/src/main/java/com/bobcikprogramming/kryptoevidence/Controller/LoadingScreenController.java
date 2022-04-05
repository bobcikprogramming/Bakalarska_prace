package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.DataVersion;
import com.bobcikprogramming.kryptoevidence.Model.ExchangeByYearEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class LoadingScreenController {

    private Context context;

    private DatabaseReference exchangeReference, versionReference, yearReference;

    public LoadingScreenController(Context context){
        this.context = context;

        String firebaseURL = "https://krypto-evidence-default-rtdb.europe-west1.firebasedatabase.app/";
        exchangeReference = FirebaseDatabase.getInstance(firebaseURL).getReference("exchange");
        versionReference = exchangeReference.child("version");
        yearReference = exchangeReference.child("year");

    }

    private boolean checkInternetConnection(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     *
     * @return 0 - žádná data k aktualizování, 1 - první použití bez připojení k internetu
     */
    public int checkVersion(TextView tvUpdateInfo, ProgressBar progressBar){
        AppDatabase db = AppDatabase.getDbInstance(context);
        int returnValue = 0;
        int version = db.databaseDao().getDataVersion();
        if(checkInternetConnection()) {
            getVersionFromFirebase(db, version, tvUpdateInfo, progressBar);
        }else{
            if(version == 0){
                // První spuštění aplikace, data nejsou stažena
                returnValue = 99;
            }
        }

        return returnValue;
    }

    private void getVersionFromFirebase(AppDatabase db, int appDbVersion, TextView tvUpdateInfo, ProgressBar progressBar){
        versionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                int version = Integer.parseInt(dataSnapshot.getValue().toString());
                System.out.println(">>>>>>>>>>>>>>appDbVersion:"+appDbVersion);
                if(version != appDbVersion) {
                    downloadExchangeData(db, appDbVersion, version);
                    showLoading("Stahování aktualizací...", tvUpdateInfo, progressBar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void downloadExchangeData(AppDatabase db, int versionAppDB, int newVersion){
        db.databaseDao().deleteExchange();
        getExchangeByYearFromFirebase(db);
        if(versionAppDB == 0){
            DataVersion dataVersion = new DataVersion();
            dataVersion.version = newVersion;
            db.databaseDao().insertVersion(dataVersion);
        }else {
            db.databaseDao().updateVersion(newVersion);
        }
    }

    private void getExchangeByYearFromFirebase(AppDatabase db){
        yearReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
                ExchangeByYearEntity exchange;
                System.out.println(">>>>>>>>>>>>>>jdu na insert: "+dataSnapshot.getValue().toString());
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    System.out.println(">>>>>>>>>>>>>>insert");
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

    /** https://stackoverflow.com/a/9306962 */
    public String readFromFile(){
        String modeType = "system";

        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new
                    File(context.getFilesDir()+"/mode.txt")));

            modeType = bufferedReader.readLine();

            if(modeType.isEmpty()){
                writeToFile("system");
            }

            bufferedReader.close();
        }catch (Exception e){
            File file = new File(context.getFilesDir()+"/mode.txt");
            if(!file.exists())
            {
                try {
                    file.createNewFile();
                    writeToFile("system");
                }catch (Exception createFileErr){
                    System.err.println(createFileErr);
                }
            }
        }

        return modeType;
    }

    public void writeToFile(String mode){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new
                    File(context.getFilesDir() + "/mode.txt")));
            bufferedWriter.write(mode);
            bufferedWriter.close();
        }catch(Exception e){
            System.err.println(e);
        }
    }

    private void showLoading(String loadText, TextView tvUpdateInfo, ProgressBar progressBar){
        progressBar.setVisibility(View.VISIBLE);
        tvUpdateInfo.setVisibility(View.VISIBLE);
        tvUpdateInfo.setText(loadText);
    }
}
