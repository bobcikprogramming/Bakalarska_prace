package com.bobcikprogramming.kryptoevidence.Controller;

import android.content.Context;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.View.RecyclerViewPDF;

import java.util.ArrayList;
import java.util.List;

public class FragmentPDFController {

    private String selectedYear;
    private ArrayList<RecyclerViewPDFList> dataList;
    private Context context;

    private RecyclerViewPDF adapter;

    private CalendarManager calendar;

    public FragmentPDFController(Context context){
        this.context = context;

        calendar = new CalendarManager();
        selectedYear = "";
        dataList = new ArrayList<>();
        adapter = new RecyclerViewPDF(context, dataList);
    }

    public void setSelectedYear(String selectedYear){
        this.selectedYear = selectedYear;
        RecyclerViewPDFList newPDF = new RecyclerViewPDFList(selectedYear, calendar.getActualDay());
        dataList.add(newPDF);
        adapter.setDataList(dataList);

        checkIfThereIsATransaction();
    }

    public RecyclerViewPDF getAdapter() {
        return adapter;
    }

    private void checkIfThereIsATransaction(){
        AppDatabase db = AppDatabase.getDbInstance(context);

        String dateFrom = selectedYear + ".01.01";
        String dateTo = selectedYear + ".12.31";
        System.out.println(">>>>>>>>>>>>>>>>>FROM: "+dateFrom+" TO: "+dateTo);
        // Zkontrolovat, zdali v daném roce existuje prodej.
        List<TransactionWithPhotos> salesInYear = db.databaseDao().getUsedSellChangeBetween(dateFrom, dateTo);
        System.out.println(">>>>>>>>>>>>>>>>> size: " + salesInYear.size());
        if(salesInYear.isEmpty()){
            Toast.makeText(context, "Nebyla evidována žádná transakce za dané daňové období.", Toast.LENGTH_LONG).show();
        }else{
            sellValue(salesInYear);
        }
        // Pokud ne:
        // Nevytváří se daňové období (vypsat že neproběhla žádná transakce).
        // Pokud ano:
        // Získat hodnotu prodeje.
        // Získat hodnotu nákupu.
        // Získat hodontu směny.
    }

    private void sellValue(List<TransactionWithPhotos> salesInYear){
        // Sečíst zisky z prodeje od prvního prodeje v roce po poslední (Zaokrouhleno na dvě desetiná místa (kvůli pozdějšímu výpisu v PDF)).
        for(TransactionWithPhotos sell : salesInYear){
            Double fee = Math.round(sell.transaction.fee * 100.0) / 100.0;
            Double profit = (Math.round((Double.parseDouble(sell.transaction.priceSold)) * 100.0) / 100.0) - fee;
            System.out.println(">>>>>>>>>>>>>>>>> " + sell.transaction.priceSold + " zaok.: " + profit);
        }
    }

    private void buyValue(){
        // Vytvořit seznam kryptoměn, které se v daném roce prodali.
        // Pro jednotlivé kryptoměny:
        // Z prvního prodeje vzít první nákup.
        // Z tohoto nákupu začít. Vypočítat jeho náklady zvlášť (usedFromFirst pouze).
        // Brát nákupy až po poslední nákup posledního prodeje v daném roce.
        // Opět z tohoto (posledního) nákupu vypočítat zvlášť. (vzít nákupy od prvního do posledního pro daný prodej,
        //  z celkového možství odečíst amountLeft, první a hodnoty nákupů mezi prvním a posledním.
        //  Výsledná hodnota je množství, které se prodalo z posledního.
        // Poté všechny výsledky sečíst.
    }
}
