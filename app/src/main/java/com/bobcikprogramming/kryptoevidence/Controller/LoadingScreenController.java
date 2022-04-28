package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

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

    /**
    * Metoda získá ze souboru nastavení barevného módu.
     * V případě, že soubor neexistuje, dojde k jeho vytvoření a uložení základního nastavení "system",
     * které je i navráceno.
    * @return Nastavení barevného módu
    */
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

    /**
     * Metoda pro uložení nastavení barevného módu.
     * @param mode Nastavení barevného módu
     */
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
}
