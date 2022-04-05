package com.bobcikprogramming.kryptoevidence.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Controller.LoadingScreenController;
import com.bobcikprogramming.kryptoevidence.Controller.MainActivity;
import com.bobcikprogramming.kryptoevidence.R;

public class LoadingScreen extends AppCompatActivity {

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
        int actionCode = controller.checkVersion(tvUpdateInfo, progressBar);
        switch (actionCode){
            case 0:
                startActivity();
                break;
            case 1:
                tvUpdateInfo.setText("Při prvním použití je vyžadováno\npřipojení k internetu.");
                break;
        }
    }

    private void startActivity(){
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
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
    protected void onResume() {
        super.onResume();
        System.out.println("test");
    }
}