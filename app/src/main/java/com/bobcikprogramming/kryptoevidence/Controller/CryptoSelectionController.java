package com.bobcikprogramming.kryptoevidence.Controller;

import java.util.ArrayList;

public class CryptoSelectionController {

    private ArrayList<RecyclerViewSelectionList> cryptoList;

    public CryptoSelectionController(){
        cryptoList = new ArrayList<>();
        tmpAddCryptoToList();
    }

    private void tmpAddCryptoToList(){
        RecyclerViewSelectionList btc = new RecyclerViewSelectionList("Bitcoin", "BTC");
        cryptoList.add(btc);
        RecyclerViewSelectionList link = new RecyclerViewSelectionList("Chainlink", "LINK");
        cryptoList.add(link);
        RecyclerViewSelectionList ada = new RecyclerViewSelectionList("Cardano", "ADA");
        cryptoList.add(ada);
        RecyclerViewSelectionList eth = new RecyclerViewSelectionList("Ethereum", "ETH");
        cryptoList.add(eth);
    }

    public ArrayList<RecyclerViewSelectionList> removeSelectedValue(String shortName){
        ArrayList<RecyclerViewSelectionList> cryptoListToShow;
        int objPosToRemove = -1;

        for(RecyclerViewSelectionList toRemove : cryptoList){
            if(toRemove.getShortName().equals(shortName)){
                objPosToRemove = cryptoList.indexOf(toRemove);
            }
        }
        if(objPosToRemove > -1) {
            cryptoList.remove(objPosToRemove);
        }
        cryptoListToShow = cryptoList;
        return cryptoListToShow;
    }

    public ArrayList<RecyclerViewSelectionList> filter(String searching){
        ArrayList<RecyclerViewSelectionList> cryptoListToShow = new ArrayList<>();

        if(searching.length() == 0){
            cryptoListToShow = cryptoList;
        }else {
            for (RecyclerViewSelectionList toShow : cryptoList) {
                if (toShow.getLongName().toLowerCase().contains(searching.toLowerCase()) || toShow.getShortName().toLowerCase().contains(searching.toLowerCase())) {
                    cryptoListToShow.add(toShow);
                }
            }
        }

        return cryptoListToShow;
    }

    public ArrayList<RecyclerViewSelectionList> getCryptoList() {
        return cryptoList;
    }
}
