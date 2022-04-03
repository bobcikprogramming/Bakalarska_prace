package com.bobcikprogramming.kryptoevidence.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.PDFEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.View.RecyclerViewPDF;
import com.bobcikprogramming.kryptoevidence.View.RecyclerViewTransactions;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentPDFController {

    private String selectedYear;
    private List<PDFEntity> dataList;
    private Context context;
    private Activity activity;

    private RecyclerViewPDF adapter;

    private CalendarManager calendar;
    private SharedMethods shared;
    private PDFGenerator generator;

    private Double TMPEUREXCHANGERATE = 25.65;
    private Double TMPUSDEXCHANGERATE = 21.72;

    public FragmentPDFController(Context context, Activity activity){
        this.context = context;
        this.activity = activity;

        calendar = new CalendarManager();
        shared = new SharedMethods();
        selectedYear = "";

        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        loadDataFromDb();
    }

    private void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        dataList = db.databaseDao().getPDF();
        adapter = new RecyclerViewPDF(context, dataList);
    }

    public void setSelectedYear(String selectedYear){
        this.selectedYear = selectedYear;

        createIfThereIsATransaction();
    }

    public RecyclerViewPDF getAdapter() {
        return adapter;
    }

    private void createIfThereIsATransaction(){
        AppDatabase db = AppDatabase.getDbInstance(context);

        long dateFrom = calendar.getDateMillis("01.01." + selectedYear);
        long dateTo = calendar.getDateMillis("31.12." + selectedYear);
        // Zkontrolovat, zdali v daném roce existuje prodej.
        List<TransactionWithPhotos> salesInYear = db.databaseDao().getUsedSellBetween(dateFrom, dateTo);
        if(salesInYear.isEmpty()){
            // Pokud ne:
            // Nevytváří se daňové období (vypsat že neproběhla žádná transakce).
            Toast.makeText(context, "Nebyla evidována žádná transakce za dané daňové období.", Toast.LENGTH_LONG).show();
        }else{
            ArrayList<BuyTransactionPDFList> buyList = buyValue(dateFrom, dateTo);
            ArrayList<SellTransactionPDFList> sellList = sellValue(salesInYear);
            ArrayList<ChangeTransactionPDFList> changeList = changeValue(dateFrom, dateTo);
            generator = new PDFGenerator(context.getAssets(), context, activity, TMPEUREXCHANGERATE, TMPUSDEXCHANGERATE);

            try {
                boolean permissionGaranted = generator.createPDF(selectedYear, buyList, sellList, changeList);
                if(!permissionGaranted){
                    Toast.makeText(context, "Pro vytvoření PDF je zapotřebí povolit přístup k souborům.", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Chyba při vytváření PDF. Prosím opakujte akci.", Toast.LENGTH_LONG).show();
                return;
            }
            saveToDb(generator.getFileName(), generator.getTotal());
        }
    }

    private ArrayList<SellTransactionPDFList> sellValue(List<TransactionWithPhotos> salesInYear){
        // Sečíst zisky z prodeje od prvního prodeje v roce po poslední (Zaokrouhleno na dvě desetiná místa (kvůli pozdějšímu výpisu v PDF)).
        ArrayList<SellTransactionPDFList> sellList = new ArrayList<>();
        SellTransactionPDFList sellTransaction;
        for(TransactionWithPhotos sell : salesInYear){
            TransactionEntity sellEntity = sell.transaction;
            BigDecimal price = shared.getTwoDecimalBigDecimal(sellEntity.priceSold);
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

            sellTransaction = new SellTransactionPDFList(calendar.getDateFromMillis(sellEntity.date), sellEntity.quantitySold, sellEntity.shortNameSold, String.valueOf(price), String.valueOf(fee), String.valueOf(profit));
            sellList.add(sellTransaction);
        }
        return sellList;
    }

    private ArrayList<BuyTransactionPDFList> buyValue(long dateFrom, long dateTo){
        AppDatabase db = AppDatabase.getDbInstance(context);
        ArrayList<BuyTransactionPDFList> buyList = new ArrayList<>();
        BuyTransactionPDFList buyTransaction;

        // Vytvořit seznam kryptoměn, které se v daném roce prodali.
        List<String> shortNameSellInYear = db.databaseDao().getUsedShortNameSellBetween(dateFrom, dateTo);
        // Pro jednotlivé kryptoměny:
        for(String shortName : shortNameSellInYear) {
            // Vzít všechny prodeje od do.
            List<TransactionWithPhotos> listOfUsedSell = db.databaseDao().getUsedSellBetweenByShortName(shortName, dateFrom, dateTo);
            long uidFirst = 0, uidLast = 0;
            BigDecimal takenFromFirst = BigDecimal.ZERO, takenFromLast = BigDecimal.ZERO;
            for(TransactionWithPhotos sellTransaction : listOfUsedSell){
                TransactionEntity sell = sellTransaction.transaction;

                // Uložit UID a množství použitého prvního a posledního nákupu.
                if(uidFirst == 0){
                    uidFirst = sell.firstTakenFrom;
                    takenFromFirst = shared.getBigDecimal(sell.usedFromFirst);

                    uidLast = sell.lastTakenFrom;
                    takenFromLast = shared.getBigDecimal(sell.usedFromLast);

                    continue;
                }

                // Dokud se UID rovná s tím prvním, tak přičítat k množství.
                if(sell.firstTakenFrom == uidFirst){
                    takenFromFirst = takenFromFirst.add(shared.getBigDecimal(sell.usedFromFirst));
                }

                // Pokud se UID posledního rovná s uloženým UID, přičtu k množství. Jinak aktualizuji UID i množství.
                if(sell.lastTakenFrom == uidLast){
                    takenFromLast = takenFromLast.add(shared.getBigDecimal(sell.usedFromLast));
                }else{
                    uidLast = sell.lastTakenFrom;
                    takenFromLast = shared.getBigDecimal(sell.usedFromLast);
                }
            }
            // Poté co se projdou všechny prodeje, budou uloženy hodnoty prvního a posledního.
            /** Zpracuji hodnoty prvního. */
            buyTransaction = getBuyTransaction(uidFirst, takenFromFirst, shortName);
            // Uložím datum a čas prvního.
            String dateFirst = buyTransaction.getDate();
            String timeFirst = buyTransaction.getTime();
            buyList.add(buyTransaction);

            /** Zpracuji hodnoty posledního, pokud se nejedná zároveň o první. */
            String dateLast = buyTransaction.getDate();
            String timeLast = buyTransaction.getTime();
            if(uidFirst != uidLast) {
                buyTransaction = getBuyTransaction(uidLast, takenFromLast, shortName);
                // Uložím datum a čas posledního.
                buyList.add(buyTransaction);
            }

            // Zpracuji hodnoty mezi prvním a posledním.
            // Získám nákupy mezi prvním a posledním prodejem.
            List<TransactionWithPhotos> listOfUsedBuyBetween = db.databaseDao().getUsedBuyChangeBetweenWithoutFirstAndLast(String.valueOf(uidFirst), String.valueOf(uidLast), calendar.getDateMillis(dateFirst), timeFirst, calendar.getDateMillis(dateLast), timeLast, shortName);
            for (TransactionWithPhotos buy : listOfUsedBuyBetween) {
                TransactionEntity buyEntity = buy.transaction;
                // Získáme hodnoty: Cena, Množství, Poplatek.
                BigDecimal price = shared.getBigDecimal(buyEntity.priceBought);
                BigDecimal quantity = shared.getBigDecimal(buyEntity.quantityBought);
                BigDecimal fee = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(buyEntity.fee));

                // Zjistíme celkovou cenu (včetně poplatku).
                BigDecimal total = price.add(fee);

                // Převedeme měnu na CZK, nejedná-li se o CZK.
                if(buyEntity.currency.equals("EUR")){
                    price = shared.getTwoDecimalBigDecimal(price.multiply(shared.getBigDecimal(TMPEUREXCHANGERATE)));
                    fee = shared.getTwoDecimalBigDecimal(fee.multiply(shared.getBigDecimal(TMPEUREXCHANGERATE)));
                    total = shared.getTwoDecimalBigDecimal(total.multiply(shared.getBigDecimal(TMPEUREXCHANGERATE)));
                }else if(buyEntity.currency.equals("USD")){
                    price = shared.getTwoDecimalBigDecimal(price.multiply(shared.getBigDecimal(TMPUSDEXCHANGERATE)));
                    fee = shared.getTwoDecimalBigDecimal(fee.multiply(shared.getBigDecimal(TMPUSDEXCHANGERATE)));
                    total = shared.getTwoDecimalBigDecimal(total.multiply(shared.getBigDecimal(TMPUSDEXCHANGERATE)));
                }

                buyTransaction = new BuyTransactionPDFList(calendar.getDateFromMillis(buyEntity.date), buyEntity.time, quantity.toPlainString(), shortName, price.toString(), fee.toString(), total.toString());
                buyList.add(buyTransaction);
            }

        }
        // Seřadím.
        sortListByTime(buyList);
        sortListByDate(buyList);
        return buyList;
    }

    private BuyTransactionPDFList getBuyTransaction(long uid, BigDecimal takenFrom, String shortName){
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity buy = db.databaseDao().getTransactionByTransactionID(String.valueOf(uid)).transaction;

        // Získáme hodnoty: Cena, Množství, Poplatek.
        BigDecimal price = shared.getBigDecimal(buy.priceBought);
        BigDecimal quantity = shared.getBigDecimal(buy.quantityBought);
        BigDecimal fee = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(buy.fee));

        // Vypočteme cenu za jednu kryptoměnu.
        BigDecimal pricePerPiece;
        if(quantity.compareTo(BigDecimal.ONE) < 0){
            // Pokud je koupené množství menší jedné, tak násobíme.
            pricePerPiece = shared.getTwoDecimalBigDecimal(price.multiply(quantity));
        }else{
            // Jinak dělíme.
            pricePerPiece = price.divide(quantity, 2, RoundingMode.HALF_EVEN);
        }

        // Zjistíme cenu nákupu pro využité množství.
        price = shared.getTwoDecimalBigDecimal(pricePerPiece.multiply(takenFrom));

        // Zjistíme celkovou cenu (včetně poplatku).
        BigDecimal total = price.add(fee);

        // Převedeme měnu na CZK, nejedná-li se o CZK.
        if(buy.currency.equals("EUR")){
            price = shared.getTwoDecimalBigDecimal(price.multiply(shared.getBigDecimal(TMPEUREXCHANGERATE)));
            fee = shared.getTwoDecimalBigDecimal(fee.multiply(shared.getBigDecimal(TMPEUREXCHANGERATE)));
            total = shared.getTwoDecimalBigDecimal(total.multiply(shared.getBigDecimal(TMPEUREXCHANGERATE)));
        }else if(buy.currency.equals("USD")){
            price = shared.getTwoDecimalBigDecimal(price.multiply(shared.getBigDecimal(TMPUSDEXCHANGERATE)));
            fee = shared.getTwoDecimalBigDecimal(fee.multiply(shared.getBigDecimal(TMPUSDEXCHANGERATE)));
            total = shared.getTwoDecimalBigDecimal(total.multiply(shared.getBigDecimal(TMPUSDEXCHANGERATE)));
        }

        // Vrátím nákup.
        return new BuyTransactionPDFList(calendar.getDateFromMillis(buy.date), buy.time, takenFrom.toPlainString(), shortName, price.toString(), fee.toString(), total.toString());
    }

    private ArrayList<ChangeTransactionPDFList> changeValue(long dateFrom, long dateTo){
        AppDatabase db = AppDatabase.getDbInstance(context);
        ArrayList<ChangeTransactionPDFList> changeList = new ArrayList<>();
        ChangeTransactionPDFList changeTransaction;

        // Sečíst zisky ze směny od první v roce po poslední (Zaokrouhleno na dvě desetiná místa (kvůli pozdějšímu výpisu v PDF)).
        List<TransactionWithPhotos> salesInYear = db.databaseDao().getUsedChangeBetween(dateFrom, dateTo);
        for(TransactionWithPhotos change : salesInYear){
            TransactionEntity changeEntity = change.transaction;
            BigDecimal fee = shared.getTwoDecimalBigDecimal(change.transaction.fee);
            BigDecimal profit = shared.getTwoDecimalBigDecimal(change.transaction.priceBought).subtract(fee);
            if(change.transaction.currency.equals("EUR")){
                fee = shared.getTwoDecimalBigDecimal(change.transaction.fee).multiply(shared.getBigDecimal(TMPEUREXCHANGERATE));
                profit = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(change.transaction.priceBought).multiply(shared.getBigDecimal(TMPEUREXCHANGERATE))).subtract(fee);
            }else if(change.transaction.currency.equals("USD")){
                fee = shared.getTwoDecimalBigDecimal(change.transaction.fee).multiply(shared.getBigDecimal(TMPUSDEXCHANGERATE));
                profit = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(change.transaction.priceBought).multiply(shared.getBigDecimal(TMPUSDEXCHANGERATE))).subtract(fee);
            }
            changeTransaction = new ChangeTransactionPDFList(calendar.getDateFromMillis(changeEntity.date), changeEntity.quantityBought, changeEntity.shortNameBought, changeEntity.quantitySold, changeEntity.shortNameSold, profit.toString());
            changeList.add(changeTransaction);
        }
        return changeList;
    }

    private void sortListByDate(ArrayList<BuyTransactionPDFList> data){
        BuyTransactionPDFList tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                try{
                    Date dateFirst = format.parse(data.get(j).getDate());
                    Date dateSecond = format.parse(data.get(j+1).getDate());
                    if(dateFirst.compareTo(dateSecond) > 0){
                        tmp = data.get(j);
                        data.set(j, data.get(j+1));
                        data.set(j+1, tmp);
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sortListByTime(ArrayList<BuyTransactionPDFList> data){
        BuyTransactionPDFList tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                try{
                    Date timeFirst = format.parse(data.get(j).getTime());
                    Date timeSecond = format.parse(data.get(j+1).getTime());
                    if(timeFirst.compareTo(timeSecond) > 0){
                        tmp = data.get(j);
                        data.set(j, data.get(j+1));
                        data.set(j+1, tmp);
                    }
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveToDb(String fileName, BigDecimal total){
        AppDatabase db = AppDatabase.getDbInstance(context);

        PDFEntity pdf = new PDFEntity();
        pdf.fileName = fileName;
        pdf.total = total.toPlainString();
        pdf.year = selectedYear;
        pdf.date = calendar.getActualDateMillis();

        db.databaseDao().insertPDF(pdf);

        dataList.add(pdf);
        adapter.setDataList(dataList);
    }
}
