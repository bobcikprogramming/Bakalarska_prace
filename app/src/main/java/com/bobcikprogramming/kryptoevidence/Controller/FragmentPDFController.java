package com.bobcikprogramming.kryptoevidence.Controller;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.View.RecyclerViewPDF;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FragmentPDFController {

    private String selectedYear;
    private ArrayList<RecyclerViewPDFList> dataList;
    private Context context;

    private RecyclerViewPDF adapter;

    private CalendarManager calendar;
    private SharedMethods shared;
    private PDFGenerator generator;

    private Double TMPEUREXCHANGERATE = 25.65;
    private Double TMPUSDEXCHANGERATE = 21.72;

    public FragmentPDFController(Context context, Activity activity){
        this.context = context;

        calendar = new CalendarManager();
        shared = new SharedMethods();
        generator = new PDFGenerator(context.getAssets(), context, activity);
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
        // Zkontrolovat, zdali v daném roce existuje prodej.
        List<TransactionWithPhotos> salesInYear = db.databaseDao().getUsedSellBetween(dateFrom, dateTo);
        if(salesInYear.isEmpty()){
            // Pokud ne:
            // Nevytváří se daňové období (vypsat že neproběhla žádná transakce).
            Toast.makeText(context, "Nebyla evidována žádná transakce za dané daňové období.", Toast.LENGTH_LONG).show();
        }else{
            ArrayList<SellTransactionPDFList> sellList = sellValue(salesInYear);

            try {
                generator.createPDF(selectedYear, sellList);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Chyba při vytváření PDF. Prosím opakujte akci.", Toast.LENGTH_LONG).show();
            }
        }

        // Pokud ano:
        // Získat hodnotu prodeje.
        /*Double sellValue = sellValue(salesInYear).getTotal();
        System.out.println(">>>>>>>>>>>>>>Prodej: "+sellValue(salesInYear));
        // Získat hodnotu nákupu.
        Double buyValue = buyValue(dateFrom, dateTo);
        System.out.println(">>>>>>>>>>>>>>Nákup: "+buyValue(dateFrom, dateTo));
        // Získat hodontu směny.
        Double changeValue = changeValue(dateFrom, dateTo);
        System.out.println(">>>>>>>>>>>>>>Směna: "+changeValue(dateFrom, dateTo));

        Double result = shared.getTwoDecimalDouble((sellValue + changeValue) - buyValue);

        System.out.println(">>>>>>>>>>>Zisk/ztráta: "+result);*/
    }

    private ArrayList<SellTransactionPDFList> sellValue(List<TransactionWithPhotos> salesInYear){
        // Sečíst zisky z prodeje od prvního prodeje v roce po poslední (Zaokrouhleno na dvě desetiná místa (kvůli pozdějšímu výpisu v PDF)).
        ArrayList<SellTransactionPDFList> sellList = new ArrayList<>();
        SellTransactionPDFList sellTransaction;
        for(TransactionWithPhotos sell : salesInYear){
            TransactionEntity sellEntity = sell.transaction;
            BigDecimal price = shared.getTwoDecimalBigDecimal(sellEntity.priceSold); // TODO: Asi problém že to je v double, možná použít bigdecimal
            BigDecimal fee = shared.getTwoDecimalBigDecimal(sellEntity.fee);
            BigDecimal profit = BigDecimal.ZERO;
            if(sellEntity.currency.equals("EUR")){
                price = shared.getTwoDecimalBigDecimal(Double.parseDouble(sellEntity.priceSold) * TMPEUREXCHANGERATE);
                fee = shared.getTwoDecimalBigDecimal(sellEntity.fee * TMPEUREXCHANGERATE);
            }else if(sellEntity.currency.equals("USD")){
                price = shared.getTwoDecimalBigDecimal(Double.parseDouble(sellEntity.priceSold) * TMPUSDEXCHANGERATE);
                fee = shared.getTwoDecimalBigDecimal(sellEntity.fee * TMPUSDEXCHANGERATE);
            }

            profit = price.subtract(fee);

            sellTransaction = new SellTransactionPDFList(calendar.getDateFormatFromDatabase(sellEntity.date), sellEntity.quantitySold, sellEntity.shortNameSold, String.valueOf(price), String.valueOf(fee), String.valueOf(profit));
            sellList.add(sellTransaction);
        }
        return sellList;
    }

    private Double buyValue(String dateFrom, String dateTo){
        AppDatabase db = AppDatabase.getDbInstance(context);
        Double buyValue = 0.0;

        // Vytvořit seznam kryptoměn, které se v daném roce prodali.
        List<String> shortNameSellInYear = db.databaseDao().getUsedShortNameSellBetween(dateFrom, dateTo);
        // Pro jednotlivé kryptoměny:
        for(String shortName : shortNameSellInYear){
            // Najít první a poslední nákup.
            Double shortNameBuyValue = 0.0;
            List<TransactionWithPhotos> listOfUsedSell = db.databaseDao().getUsedSellBetweenByShortName(shortName, dateFrom, dateTo);
            TransactionEntity firstSell = listOfUsedSell.get(0).transaction;
            TransactionEntity lastSell = listOfUsedSell.get(listOfUsedSell.size()-1).transaction;
            TransactionEntity firstBuy = db.databaseDao().getTransactionByTransactionID(String.valueOf(firstSell.firstTakenFrom)).transaction;
            TransactionEntity lastBuy = db.databaseDao().getTransactionByTransactionHistoryID(String.valueOf(lastSell.lastTakenFrom)).transaction;

            // Získat cenu z prvního nákupu zvlášť.

            if(firstBuy.currency.equals("CZK")){
                Double pricePerPiece = Double.parseDouble(firstBuy.priceBought) / Double.parseDouble(firstBuy.quantityBought);
                shortNameBuyValue += shared.getTwoDecimalDouble(Double.parseDouble(firstSell.usedFromFirst) * pricePerPiece);
            }else if(firstBuy.currency.equals("EUR")){
                Double pricePerPiece = (Double.parseDouble(firstBuy.priceBought) / Double.parseDouble(firstBuy.quantityBought)) * TMPEUREXCHANGERATE;
                shortNameBuyValue += shared.getTwoDecimalDouble(Double.parseDouble(firstSell.usedFromFirst) * pricePerPiece);
            }else if(firstBuy.currency.equals("USD")){
                Double pricePerPiece = (Double.parseDouble(firstBuy.priceBought) / Double.parseDouble(firstBuy.quantityBought)) * TMPUSDEXCHANGERATE;
                shortNameBuyValue += shared.getTwoDecimalDouble(Double.parseDouble(firstSell.usedFromFirst) * pricePerPiece);
            }

            if(firstBuy.uidTransaction != lastBuy.uidTransaction) {
                // Získat cenu z posledního nákupu zvlášť.
                if(lastBuy.currency.equals("CZK")){
                    Double pricePerPiece = Double.parseDouble(lastBuy.priceBought) / Double.parseDouble(lastBuy.quantityBought);
                    shortNameBuyValue += shared.getTwoDecimalDouble(Double.parseDouble(lastSell.usedFromLast) * pricePerPiece);
                }else if(lastBuy.currency.equals("EUR")){
                    Double pricePerPiece = (Double.parseDouble(lastBuy.priceBought) / Double.parseDouble(lastBuy.quantityBought)) * TMPEUREXCHANGERATE;
                    shortNameBuyValue += shared.getTwoDecimalDouble(Double.parseDouble(lastSell.usedFromLast) * pricePerPiece);
                }else if(lastBuy.currency.equals("USD")){
                    Double pricePerPiece = (Double.parseDouble(lastBuy.priceBought) / Double.parseDouble(lastBuy.quantityBought)) * TMPUSDEXCHANGERATE;
                    shortNameBuyValue += shared.getTwoDecimalDouble(Double.parseDouble(lastSell.usedFromLast) * pricePerPiece);
                }

                // Získat ceny všech nákupů mezi prvním a posledním.
                List<TransactionWithPhotos> listOfUsedBuyBetween = db.databaseDao().getUsedBuyChangeBetweenWithoutFirstAndLast(String.valueOf(firstBuy.uidTransaction), String.valueOf(lastBuy.uidTransaction), firstBuy.date, firstBuy.time, lastBuy.date, lastBuy.time, shortName);
                for (TransactionWithPhotos buy : listOfUsedBuyBetween) {
                    if(buy.transaction.currency.equals("CZK")){
                        shortNameBuyValue += shared.getTwoDecimalDouble(buy.transaction.priceBought);
                    }else if(buy.transaction.currency.equals("EUR")){
                        shortNameBuyValue += shared.getTwoDecimalDouble(Double.parseDouble(buy.transaction.priceBought) * TMPEUREXCHANGERATE);
                    }else if(buy.transaction.currency.equals("USD")){
                        shortNameBuyValue += shared.getTwoDecimalDouble(Double.parseDouble(buy.transaction.priceBought) * TMPUSDEXCHANGERATE);
                    }
                }
            }

            buyValue += shortNameBuyValue;
        }
        return buyValue;
    }

    private Double changeValue(String dateFrom, String dateTo){
        AppDatabase db = AppDatabase.getDbInstance(context);

        // Sečíst zisky ze směny od první v roce po poslední (Zaokrouhleno na dvě desetiná místa (kvůli pozdějšímu výpisu v PDF)).
        List<TransactionWithPhotos> salesInYear = db.databaseDao().getUsedChangeBetween(dateFrom, dateTo);
        Double changeValue = 0.0;
        for(TransactionWithPhotos change : salesInYear){
            Double fee;
            Double profit = 0.0;
            if(change.transaction.currency.equals("CZK")){
                fee = shared.getTwoDecimalDouble(change.transaction.fee);
                profit = shared.getTwoDecimalDouble(change.transaction.priceBought) - fee;
            }else if(change.transaction.currency.equals("EUR")){
                fee = shared.getTwoDecimalDouble(change.transaction.fee * TMPEUREXCHANGERATE);
                profit = shared.getTwoDecimalDouble(Double.parseDouble(change.transaction.priceBought) * TMPEUREXCHANGERATE) - fee;
            }else if(change.transaction.currency.equals("USD")){
                fee = shared.getTwoDecimalDouble(change.transaction.fee * TMPUSDEXCHANGERATE);
                profit = shared.getTwoDecimalDouble(Double.parseDouble(change.transaction.priceBought) * TMPUSDEXCHANGERATE) - fee;
            }
            changeValue += profit;
        }
        return changeValue;
    }
}
