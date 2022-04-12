package com.bobcikprogramming.kryptoevidence.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Controller.FirebaseAsyncTask;
import com.bobcikprogramming.kryptoevidence.Controller.LoadingScreenController;
import com.bobcikprogramming.kryptoevidence.Controller.MainActivity;
import com.bobcikprogramming.kryptoevidence.Controller.APIAsyncTask;
import com.bobcikprogramming.kryptoevidence.Controller.TaskDelegate;
import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
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

        controller = new LoadingScreenController(this);
        handler = new Handler();

        setupUIViews();
        setModeofGUI();
        loadingAction();
    }

    private void setupUIViews(){
        tvUpdateInfo = findViewById(R.id.tvUpdateInfo);
        progressBar = findViewById(R.id.progressBar);
    }

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

    private void startAsynctaskAPI() {
        APIAsyncTask asyncTask = new APIAsyncTask(this, this, tvUpdateInfo, progressBar);
        asyncTask.execute();
    }

    private void startAsynctaskFirebase() {
        FirebaseAsyncTask asyncTask = new FirebaseAsyncTask(this, this, tvUpdateInfo, progressBar);
        asyncTask.execute();
    }

    private void showLoadingScreenDelay(){
        handler.postDelayed(new Runnable() {
            public void run() {
                startAsynctaskFirebase();
            }
        }, 2000);
    }

    private void startActivity(){
        Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startActivityDelay(){
        handler.postDelayed(new Runnable() {
            public void run() {
                startActivity();
            }
        }, 2000);
    }

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