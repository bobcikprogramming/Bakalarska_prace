package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.CryptocurrencyEntity;

import java.util.ArrayList;

public class CryptoSelectionController {
    private Context context;

    private ArrayList<CryptocurrencyEntity> cryptoList;
    private ArrayList<CryptocurrencyEntity> cryptoListToShow;

    public CryptoSelectionController(Context context){
        this.context = context;

        loadCryptoFromDb();
    }

    private void loadCryptoFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        cryptoList = new ArrayList<>(db.databaseDao().getAllCrypto());
        cryptoListToShow = cryptoList;
    }

    public ArrayList<CryptocurrencyEntity> removeSelectedValue(String id){
        ArrayList<CryptocurrencyEntity> cryptoListToShow;
        int objPosToRemove = -1;

        for(CryptocurrencyEntity toRemove : cryptoList){
            if(toRemove.uid.equals(id)){
                objPosToRemove = cryptoList.indexOf(toRemove);
            }
        }
        if(objPosToRemove > -1) {
            cryptoList.remove(objPosToRemove);
        }
        cryptoListToShow = cryptoList;
        return cryptoListToShow;
    }

    public ArrayList<CryptocurrencyEntity> filter(String searching){
        cryptoListToShow = new ArrayList<>();
        if(searching.length() == 0){
            cryptoListToShow = cryptoList;
        }else {
            for (CryptocurrencyEntity toShow : cryptoList) {
                if (toShow.longName.toLowerCase().contains(searching.toLowerCase()) || toShow.shortName.toLowerCase().contains(searching.toLowerCase())) {
                    cryptoListToShow.add(toShow);
                }
            }
        }

        return cryptoListToShow;
    }

    public ArrayList<CryptocurrencyEntity> getCryptoList() {
        return cryptoListToShow;
    }
}
