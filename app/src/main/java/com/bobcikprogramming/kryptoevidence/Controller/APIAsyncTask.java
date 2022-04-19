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

    /**
     * Stažení kryptoměn z API.
     * Přidělení market ranku prvním 500.
     * Limit čekání na operaci 3 minuty.
     * @return Vrací výsledek typu string
     *
     * Vyčkání na dokončení akce inspirován z:
     * Zdroj:   Stack Overflow
     * Dotaz:   https://stackoverflow.com/q/66502210
     * Odpověď: https://stackoverflow.com/a/66502384
     * Autor:   Shlomi Katriel
     * Autor:   https://stackoverflow.com/users/11958566/shlomi-katriel
     * Datum:   6. března 2021
     */
    @Override
    protected String doInBackground(Void... voids) {
        AppDatabase db = AppDatabase.getDbInstance(context);
        OkHttpClient client = new OkHttpClient();

        String urlApiCurrencyList = "https://api.coingecko.com/api/v3/coins/list";
        Request requestList = new Request.Builder().url(urlApiCurrencyList).build();

        CountDownLatch countDownLatch = new CountDownLatch(1);

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

                    boolean result;
                    for(int i = 0; i < jsonArray.length(); i++) {
                        result = saveCrypto(jsonArray, i, db);
                        if(!result){
                            return;
                        }
                    }

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

                                boolean result;
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    result = saveRank(jsonArray, i, db);
                                    if(!result){
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
            countDownLatch.await(3L, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "api";
    }

    /**
     * Metoda pro uložení market ranku do databáze
     * @param jsonArray JsonArray obdržený z API
     * @param pos pozice objektu v jsonArray
     * @param db Přístup k lokální databázi
     * @return true - uložení (nebo přeskočení) proběhlo v pořádku, jinak false
     */
    private boolean saveRank(JSONArray jsonArray, int pos, AppDatabase db){
        try {
            if (jsonArray.getJSONObject(pos).getString("symbol").toLowerCase().contains("realtoken")) {
                return true;
            }
            String rank = jsonArray.getJSONObject(pos).getString("market_cap_rank");
            String id = jsonArray.getJSONObject(pos).getString("id");
            if(!rank.equals("null")){
                db.databaseDao().updateRankSettingCrypto(id, Integer.parseInt(rank));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Metoda pro uložení kryptoměn do databáze
     * @param jsonArray JsonArray obdržený z API
     * @param pos pozice objektu v jsonArray
     * @param db Přístup k lokální databázi
     * @return true - uložení (nebo přeskočení) proběhlo v pořádku, jinak false
     */
    private boolean saveCrypto(JSONArray jsonArray, int pos, AppDatabase db){
        CryptocurrencyEntity cryptoEntity = new CryptocurrencyEntity();
        try {
            if (jsonArray.getJSONObject(pos).getString("symbol").toLowerCase().contains("realtoken")) {
                return true;
            }
            cryptoEntity.uid = jsonArray.getJSONObject(pos).getString("id");
            cryptoEntity.shortName = jsonArray.getJSONObject(pos).getString("symbol").toUpperCase();
            cryptoEntity.longName = jsonArray.getJSONObject(pos).getString("name");
            cryptoEntity.rank = 999999;
            cryptoEntity.amount = "0";
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        db.databaseDao().insertCryptocurrency(cryptoEntity);
        return true;
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
     * Metoda pro zobrazení oznámení o stahování dat
     * @param loadText Stringová hednota s textem k zobrazení
     */
    private void showLoading(String loadText){
        progressBar.setVisibility(View.VISIBLE);
        tvUpdateInfo.setVisibility(View.VISIBLE);
        tvUpdateInfo.setText(loadText);
    }
}
