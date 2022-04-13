package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.CryptocurrencyEntity;
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
    private ArrayList<CryptocurrencyEntity> ownedCryptoList;
    private SharedMethods shared;

    private List<PDFEntity> annualList;
    private int position;

    public FragmentOverViewController(Context context) {
        this.context = context;

        shared = new SharedMethods();

        loadDataFromDb();
    }

    /**
     * Metoda získá ze souboru nastavení barevného módu
     * @return Nastavení barevného módu
     *
     * Metoda inspirována z:
     * Zdroj:   Stack Overflow
     * Dotaz:   https://stackoverflow.com/q/9306155
     * Odpověď: https://stackoverflow.com/a/9306962
     * Autor:   https://stackoverflow.com/users/726863/lalit-poptani
     * Autor:   Lalit Poptani
     * Datum:   16. února 2012
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
     * Metoda pro uložení nastavení barevného módu
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

    /**
     * Metoda pro načtení seznamu s zisky/ztrátou za daňové období z databáze
     */
    private void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        ownedCryptoList = new ArrayList<>(db.databaseDao().getAllOwnedCrypto());
        annualList = db.databaseDao().getLatestAnnualReport();
    }

    /**
     * Getter pro seznam s zisky/ztrátou za daňové období
     * @return Seznam s zisky/ztrátou za daňové období
     */
    public List<PDFEntity> getAnnualReport(){
        return annualList;
    }

    /**
     * Metoda pro získání indexu posledního prvku seznamu annualList
     * @return Index
     */
    public int getLastPosition(){
        position = annualList.size() - 1;
        return position;
    }

    /**
     * Metoda pro filtrování vlastněných kryptoměn podle názvu či symbolu
     * @param searching Fráze k vyhledání
     * @return Seznam obsahující pouze kryptoměny jejichž název či symbol se skládá z dané fráze
     */
    public ArrayList<CryptocurrencyEntity> filter(String searching){
        ArrayList<CryptocurrencyEntity> ownedCryptoListToShow = new ArrayList<>();

        if(searching.length() == 0){
            ownedCryptoListToShow = ownedCryptoList;
        }else {
            for (CryptocurrencyEntity toShow : ownedCryptoList) {
                if (toShow.longName.toLowerCase().contains(searching.toLowerCase()) || toShow.shortName.toLowerCase().contains(searching.toLowerCase())) {
                    ownedCryptoListToShow.add(toShow);
                }
            }
        }

        return ownedCryptoListToShow;
    }
}
