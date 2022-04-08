package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.CryptocurrencyEntity;
import com.bobcikprogramming.kryptoevidence.Model.DataVersionEntity;
import com.bobcikprogramming.kryptoevidence.Model.ExchangeByYearEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoadingScreenController {

    private Context context;

    public LoadingScreenController(Context context){
        this.context = context;
    }

    public boolean checkInternetConnection(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    public int getVersionRate(){
        AppDatabase db = AppDatabase.getDbInstance(context);

        return db.databaseDao().getDataVersionRate();
    }

    public int getVersionCrypto(){
        AppDatabase db = AppDatabase.getDbInstance(context);

        return db.databaseDao().getDataVersionCrypto();
    }

    public void setNetworkRequest(){
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();
    }

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            Toast.makeText(context, "Mám signál", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            final boolean unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);
        }
    };
}
