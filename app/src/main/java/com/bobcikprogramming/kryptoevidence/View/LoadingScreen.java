package com.bobcikprogramming.kryptoevidence.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Controller.FirebaseAsyncTask;
import com.bobcikprogramming.kryptoevidence.Controller.LoadingScreenController;
import com.bobcikprogramming.kryptoevidence.Controller.MainActivity;
import com.bobcikprogramming.kryptoevidence.Controller.APIAsyncTask;
import com.bobcikprogramming.kryptoevidence.Controller.TaskDelegate;
import com.bobcikprogramming.kryptoevidence.R;

public class LoadingScreen extends AppCompatActivity implements TaskDelegate {

    private TextView tvUpdateInfo;
    private ProgressBar progressBar;

    private LoadingScreenController controller;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    Intent.ACTION_MAIN.equals(intent.getAction())) {
                finish();
                return;
            }
        }

        controller = new LoadingScreenController(this);
        handler = new Handler();

        setupUIViews();
        setModeofGUI();
        loadingAction();
    }

    /**
     * Metoda pro inicializování prvků UI
     */
    private void setupUIViews(){
        tvUpdateInfo = findViewById(R.id.tvUpdateInfo);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Metoda pro zpracování načítací obrazovky.
     * Má-li aplikace přístup k internetu, zavolá metodu showLoadingScreenDelay
     * Nemá-li přístup k internetu, zkontroluje zda-li má stažená data, pokud nemá,
     * zobrazí výzvu k připojení k internetu, jinak se zavolá metodu startActivityDelay
     */
    private void loadingAction(){
        boolean isConnected = controller.checkInternetConnection();
        if(!isConnected){
            if(controller.getVersionRate() == 0 ||controller.getVersionCrypto() == 0) {
                tvUpdateInfo.setText("Pro dokončení instalace je vyžadováno připojení k internetu.");
            }else{
                startActivityDelay();
            }
        }else{
            showLoadingScreenDelay();
        }
    }

    /**
     * Spuštění asynchronní operace pro stažení dat z API
     */
    private void startAsynctaskAPI() {
        APIAsyncTask asyncTask = new APIAsyncTask(this, this, tvUpdateInfo, progressBar);
        asyncTask.execute();
    }

    /**
     * Spuštění asynchronní operace pro stažení dat z Firebase
     */
    private void startAsynctaskFirebase() {
        FirebaseAsyncTask asyncTask = new FirebaseAsyncTask(this, this, tvUpdateInfo, progressBar);
        asyncTask.execute();
    }

    /**
     * Metoda vyčká dvě sekundy a poté zavolá metodu startAsynctaskFirebase
     */
    private void showLoadingScreenDelay(){
        handler.postDelayed(new Runnable() {
            public void run() {
                startAsynctaskFirebase();
            }
        }, 2000);
    }

    /**
     * Metoda pro spuštění activity MainActivity
     */
    private void startActivity(){
        Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Metoda vyčká dvě sekundy a poté zavolá metodu startActivity
     */
    private void startActivityDelay(){
        handler.postDelayed(new Runnable() {
            public void run() {
                startActivity();
            }
        }, 2000);
    }

    /**
     * Metoda pro nastavení barevného módu aplikace
     */
    private void setModeofGUI(){
        String modeType = controller.readFromFile();

        if(modeType.equals("dark")){
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }else if(modeType.equals("light")){
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    /**
     * Metoda přijímající výsledek asynchronních operací
     * @param result Výsledek operace
     */
    @Override
    public void TaskCompletionResult(String result) {
        int versionCrypto = controller.getVersionCrypto();
        if(versionCrypto != 0){
            startActivity();
        }else {
            if(!result.equals("api")) {
                startAsynctaskAPI();
            }else {
                tvUpdateInfo.setText("Stahování dat se nezdařilo. Restartujte prosím aplikaci.");
            }
        }
    }
}