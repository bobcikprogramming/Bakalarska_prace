package com.bobcikprogramming.kryptoevidence.Controller;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.CryptocurrencyEntity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIAsyncTask extends AsyncTask<Void, Integer, String> {

    private TaskDelegate delegate;
    private Context context;
    private TextView tvUpdateInfo;
    private ProgressBar progressBar;

    public APIAsyncTask(TaskDelegate delegate, Context context, TextView tvUpdateInfo, ProgressBar progressBar) {
        this.delegate = delegate;
        this.context = context;
        this.tvUpdateInfo = tvUpdateInfo;
        this.progressBar = progressBar;
        AppDatabase.getDbInstance(context).databaseDao().deleteCrypto();
    }

    @Override
    protected String doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        OkHttpClient client = new OkHttpClient();

        String urlApiCurrencyList = "https://api.coingecko.com/api/v3/coins/list";
        Request requestList = new Request.Builder().url(urlApiCurrencyList).build();

        CountDownLatch countDownLatch = new CountDownLatch(1); // https://stackoverflow.com/a/66502384

        client.newCall(requestList).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showLoading("Stahování dat...");
                        }
                    });

                    String jsonData = response.body().string();
                    JSONArray jsonArray;
                    CryptocurrencyEntity cryptoEntity;
                    try {
                        jsonArray = new JSONArray(jsonData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                    for(int i = 0; i < jsonArray.length(); i++) {
                        cryptoEntity = new CryptocurrencyEntity();
                        try {
                            if (jsonArray.getJSONObject(i).getString("symbol").toLowerCase().contains("realtoken")) {
                                continue;
                            }
                            cryptoEntity.uid = jsonArray.getJSONObject(i).getString("id");
                            cryptoEntity.shortName = jsonArray.getJSONObject(i).getString("symbol").toUpperCase();
                            cryptoEntity.longName = jsonArray.getJSONObject(i).getString("name");
                            cryptoEntity.rank = 999999;
                            cryptoEntity.amount = "0";
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                        db.databaseDao().insertCryptocurrency(cryptoEntity);
                    }

                    //int pages = (int)Math.ceil(jsonArray.length() / 250);
                    for(int i = 1; i <= 2; i++){
                        String urlApiCurrencyMarket = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=250&page="+i;
                        Request requestMarket = new Request.Builder().url(urlApiCurrencyMarket).build();

                        CountDownLatch countDownLatch = new CountDownLatch(1);

                        client.newCall(requestMarket).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                countDownLatch.countDown();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String jsonData = response.body().string();
                                JSONArray jsonArray;
                                try {
                                    jsonArray = new JSONArray(jsonData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    return;
                                }

                                for(int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        if (jsonArray.getJSONObject(i).getString("symbol").toLowerCase().contains("realtoken")) {
                                            continue;
                                        }
                                        String rank = jsonArray.getJSONObject(i).getString("market_cap_rank");
                                        String id = jsonArray.getJSONObject(i).getString("id");
                                        if(!rank.equals("null")){
                                            db.databaseDao().updateRankSettingCrypto(id, Integer.parseInt(rank));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                }
                                countDownLatch.countDown();
                            }
                        });
                        try {
                            countDownLatch.await(2L, TimeUnit.MINUTES);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    db.databaseDao().updateVersionCrypto(1);
                }
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(2L, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "api";
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.TaskCompletionResult(result);
    }

    private void showLoading(String loadText){
        progressBar.setVisibility(View.VISIBLE);
        tvUpdateInfo.setVisibility(View.VISIBLE);
        tvUpdateInfo.setText(loadText);
    }
}
