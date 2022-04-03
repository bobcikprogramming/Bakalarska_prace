package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.OwnedCryptoEntity;
import com.bobcikprogramming.kryptoevidence.Model.PDFEntity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FragmentOverViewController {

    private Context context;
    private List<OwnedCryptoEntity> ownedCryptos;
    private SharedMethods shared;

    private List<PDFEntity> annualList;
    private int position;

    public FragmentOverViewController(Context context) {
        this.context = context;

        shared = new SharedMethods();
        position = -1;

        loadDataFromDb();
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

    private void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        ownedCryptos = db.databaseDao().getAllOwnedCrypto();
        annualList = db.databaseDao().getLatestAnnualReport();
    }

    public ArrayList<OwnedCryptoEntity> getDataToShow(){
        ArrayList<OwnedCryptoEntity> dataToShow = new ArrayList<>();
        for(OwnedCryptoEntity ownedCrypto : ownedCryptos){
            if(shared.getBigDecimal(ownedCrypto.amount).compareTo(shared.getBigDecimal("0.0")) == 1){
                dataToShow.add(ownedCrypto);
            }
        }
        return dataToShow;
    }

    public List<PDFEntity> showAnnualReport(){
        // Pokud jsou data k zobrazení
        if(annualList != null || !annualList.isEmpty()){
            position = annualList.size() - 1;
            return annualList;
        }else{
            return null;
        }
    }

    public int getPosition(){
        return position;
    }
}
