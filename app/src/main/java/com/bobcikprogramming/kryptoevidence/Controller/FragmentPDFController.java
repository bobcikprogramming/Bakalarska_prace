package com.bobcikprogramming.kryptoevidence.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.ExchangeByYearEntity;
import com.bobcikprogramming.kryptoevidence.Model.PDFEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.R;
import com.bobcikprogramming.kryptoevidence.View.RecyclerViewPDF;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
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

    private Double DEFAULT_EUR_EXCHANGERATE = 26.0;
    private Double DEFAULT_USD_EXCHANGERATE = 21.0;

    private Double eurExchangeRate, usdExchangeRate;
    private boolean correctRate;

    public FragmentPDFController(Context context, Activity activity){
        this.context = context;
        this.activity = activity;

        calendar = new CalendarManager();
        shared = new SharedMethods();
        selectedYear = "";

        loadDataFromDb();
    }

    /**
     * Metoda pro načtení pole s PDF soubory z databáze
     */
    private void loadDataFromDb(){
        AppDatabase db = AppDatabase.getDbInstance(context);
        dataList = db.databaseDao().getPDF();
        adapter = new RecyclerViewPDF(context, dataList);
    }

    /**
     * Setter pro nastavení hodnoty selectedYear
     * @param selectedYear Hodnota, jenž má být nastavena
     */
    public void setSelectedYear(String selectedYear){
        this.selectedYear = selectedYear;

        createIfThereIsATransaction();
    }

    public RecyclerViewPDF getAdapter() {
        return adapter;
    }

    /**
     * Metoda pro kontrolu zda-li obsahuje dané daňové období nějaký prodej, pokud ano, volá metodu createPDF,
     * případně confirmDialogUnfinishedCreate, nemá-li prodej evidován nákup
     */
    private void createIfThereIsATransaction(){
        AppDatabase db = AppDatabase.getDbInstance(context);

        setExchangeRate();

        long dateFrom = calendar.getDateMillis("01.01." + selectedYear);
        long dateTo = calendar.getDateMillis("31.12." + selectedYear);
        // Zkontrolovat, zdali v daném roce existuje prodej.

        List<TransactionWithPhotos> unfinishedSalesInYear = db.databaseDao().getUnfinishedSellBetween(dateFrom, dateTo);
        List<TransactionWithPhotos> salesInYear = db.databaseDao().getUsedSellBetween(dateFrom, dateTo);
        if(unfinishedSalesInYear == null || unfinishedSalesInYear.isEmpty()){
            if(salesInYear.isEmpty()){
                // Pokud ne:
                // Nevytváří se daňové období (vypsat že neproběhla žádná transakce).
                Toast.makeText(context, "Nebyla evidována žádná transakce za dané daňové období.", Toast.LENGTH_LONG).show();
            }else {
                createPDF(dateFrom, dateTo, salesInYear);
            }
        }else{
            // Zobrazit upozornění
            confirmDialogUnfinishedCreate(dateFrom, dateTo, salesInYear);
        }

    }

    /**
     * Metoda pro nastavení kurzu
     */
    private void setExchangeRate() {
        AppDatabase db = AppDatabase.getDbInstance(context);
        ExchangeByYearEntity exchange = db.databaseDao().getExchange(Integer.parseInt(selectedYear));
        if(exchange != null){
            eurExchangeRate = exchange.eur;
            usdExchangeRate = exchange.usd;
            correctRate = true;
        }else{
            List<ExchangeByYearEntity> exchangeList = db.databaseDao().getExchangeListTo(Integer.parseInt(selectedYear));
            if(exchangeList != null || !exchangeList.isEmpty()){
                eurExchangeRate = exchangeList.get(exchangeList.size()-1).eur;
                usdExchangeRate = exchangeList.get(exchangeList.size()-1).usd;
            }else {
                eurExchangeRate = DEFAULT_EUR_EXCHANGERATE;
                usdExchangeRate = DEFAULT_USD_EXCHANGERATE;
            }
            correctRate = false;
        }
    }

    /**
     * Metoda pro vytvoření PDF záznamu
     * @param dateFrom Začátek daňového období
     * @param dateTo Konec daňového období
     * @param salesInYear Pole prodejů v daném daňovém období
     */
    private void createPDF(long dateFrom, long dateTo, List<TransactionWithPhotos> salesInYear){
        ArrayList<BuyTransactionPDFList> buyList = buyValue(dateFrom, dateTo);
        ArrayList<SellTransactionPDFList> sellList = sellValue(salesInYear);
        ArrayList<ChangeTransactionPDFList> changeList = changeValue(dateFrom, dateTo);
        generator = new PDFGenerator(context.getAssets(), context, eurExchangeRate, usdExchangeRate, correctRate);

        try {
            boolean created = generator.createPDF(selectedYear, buyList, sellList, changeList);
            if(!created){
                Toast.makeText(context, "Chyba při vytváření PDF. Prosím opakujte akci.", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Chyba při vytváření PDF. Prosím opakujte akci.", Toast.LENGTH_LONG).show();
            return;
        }
        saveToDb(generator.getFileName(), generator.getTotal());
    }

    /**
     * Metoda pro získání seznamu prodejů k výpisu za dané daňové období
     * @param salesInYear Seznam prodejů v daném daňovém období
     * @return ArrayList prodejů k výpisu
     */
    private ArrayList<SellTransactionPDFList> sellValue(List<TransactionWithPhotos> salesInYear){
        AppDatabase db = AppDatabase.getDbInstance(context);
        // Sečíst zisky z prodeje od prvního prodeje v roce po poslední (Zaokrouhleno na dvě desetiná místa (kvůli pozdějšímu výpisu v PDF)).
        ArrayList<SellTransactionPDFList> sellList = new ArrayList<>();
        SellTransactionPDFList sellTransaction;
        for(TransactionWithPhotos sell : salesInYear){
            TransactionEntity sellEntity = sell.transaction;
            BigDecimal price = shared.getTwoDecimalBigDecimal(sellEntity.priceSold);
            BigDecimal fee = shared.getTwoDecimalBigDecimal(sellEntity.fee);
            BigDecimal profit = BigDecimal.ZERO;
            if(sellEntity.currency.equals("EUR")){
                price = shared.getTwoDecimalBigDecimal(Double.parseDouble(sellEntity.priceSold) * eurExchangeRate);
                fee = shared.getTwoDecimalBigDecimal(sellEntity.fee * eurExchangeRate);
            }else if(sellEntity.currency.equals("USD")){
                price = shared.getTwoDecimalBigDecimal(Double.parseDouble(sellEntity.priceSold) * usdExchangeRate);
                fee = shared.getTwoDecimalBigDecimal(sellEntity.fee * usdExchangeRate);
            }

            profit = price.subtract(fee);
            String shortNameSold = db.databaseDao().getCryptoShortNameById(sellEntity.uidSold);

            sellTransaction = new SellTransactionPDFList(calendar.getDateFromMillis(sellEntity.date), sellEntity.quantitySold, shortNameSold, String.valueOf(price), String.valueOf(fee), String.valueOf(profit));
            sellList.add(sellTransaction);
        }
        return sellList;
    }

    /**
     * Metoda pro získání seznamu nákupů k výpisu za dané daňové období
     * @param dateFrom Začátek daňového období
     * @param dateTo Konec daňového období
     * @return ArrayList nákupů k výpisu
     */
    private ArrayList<BuyTransactionPDFList> buyValue(long dateFrom, long dateTo){
        AppDatabase db = AppDatabase.getDbInstance(context);
        ArrayList<BuyTransactionPDFList> buyList = new ArrayList<>();
        BuyTransactionPDFList buyTransaction;

        // Vytvořit seznam kryptoměn, které se v daném roce prodali.
        List<String> salesInYear = db.databaseDao().getUsedSalesChangesBetween(dateFrom, dateTo);
        // Pro jednotlivé kryptoměny:
        for(String uidCrypto : salesInYear) {
            // Vzít všechny prodeje od do.
            List<TransactionWithPhotos> listOfUsedSell = db.databaseDao().getUsedSellChangeBetweenById(uidCrypto, dateFrom, dateTo);
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
            String shortName = db.databaseDao().getCryptoShortNameById(uidCrypto);
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
                dateLast = buyTransaction.getDate();
                timeLast = buyTransaction.getTime();
            }

            // Zpracuji hodnoty mezi prvním a posledním.
            // Získám nákupy mezi prvním a posledním prodejem.
            List<TransactionWithPhotos> listOfUsedBuyBetween = db.databaseDao().getUsedBuyChangeBetweenWithoutFirstAndLast(String.valueOf(uidFirst), String.valueOf(uidLast), calendar.getDateMillis(dateFirst), timeFirst, calendar.getDateMillis(dateLast), timeLast, uidCrypto);
            for (TransactionWithPhotos buy : listOfUsedBuyBetween) {
                TransactionEntity buyEntity = buy.transaction;
                // Získáme hodnoty: Cena, Množství, Poplatek.
                boolean isFromBuy = buy.transaction.transactionType.equals("Nákup");
                BigDecimal price = isFromBuy ? shared.getBigDecimal(buyEntity.quantitySold) : shared.getBigDecimal(buyEntity.priceBought).subtract(shared.getBigDecimal(buyEntity.fee));;
                BigDecimal quantity = shared.getBigDecimal(buyEntity.quantityBought);
                BigDecimal fee = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(buyEntity.fee));

                // Zjistíme celkovou cenu (včetně poplatku).
                BigDecimal total = price.add(fee);

                // Převedeme měnu na CZK, nejedná-li se o CZK.
                if(buyEntity.currency.equals("EUR")){
                    price = shared.getTwoDecimalBigDecimal(price.multiply(shared.getBigDecimal(eurExchangeRate)));
                    fee = shared.getTwoDecimalBigDecimal(fee.multiply(shared.getBigDecimal(eurExchangeRate)));
                    total = shared.getTwoDecimalBigDecimal(total.multiply(shared.getBigDecimal(eurExchangeRate)));
                }else if(buyEntity.currency.equals("USD")){
                    price = shared.getTwoDecimalBigDecimal(price.multiply(shared.getBigDecimal(usdExchangeRate)));
                    fee = shared.getTwoDecimalBigDecimal(fee.multiply(shared.getBigDecimal(usdExchangeRate)));
                    total = shared.getTwoDecimalBigDecimal(total.multiply(shared.getBigDecimal(usdExchangeRate)));
                }

                buyTransaction = new BuyTransactionPDFList(calendar.getDateFromMillis(buyEntity.date), buyEntity.time, quantity.toPlainString(), shortName, price.toString(), fee.toString(), total.toString());
                buyList.add(buyTransaction);
            }

        }
        // Seřadím.
        buyList = sortListByTime(buyList);
        buyList = sortListByDate(buyList);
        return buyList;
    }

    /**
     * Pomocná metoda pro vytvoření položky k uložení do seznamu nákupů
     * @param uid UID daného nákupu
     * @param usedFrom Množství, jenž bylo z daného nákupu prodána
     * @param shortName Symbol kryptoměny
     * @return Třída BuyTransactionPDFList obsahující informace o nákupu, jenž bude uložena do seznamu
     */
    private BuyTransactionPDFList getBuyTransaction(long uid, BigDecimal usedFrom, String shortName){
        AppDatabase db = AppDatabase.getDbInstance(context);
        TransactionEntity buy = db.databaseDao().getTransactionByTransactionID(String.valueOf(uid)).transaction;

        // Získáme hodnoty: Cena, Množství, Poplatek.
        boolean isFromBuy = buy.transactionType.equals("Nákup");
        BigDecimal price = isFromBuy ? shared.getBigDecimal(buy.quantitySold) : shared.getBigDecimal(buy.priceBought).subtract(shared.getBigDecimal(buy.fee));;
        BigDecimal quantity = shared.getBigDecimal(buy.quantityBought);
        BigDecimal fee = shared.getBigDecimal(buy.fee);

        // Vypočteme cenu a poplatek za jednu kryptoměnu.
        BigDecimal pricePerPiece, feePerPiece;
        if(quantity.compareTo(BigDecimal.ZERO) == 0){
            // Pokud je koupené množství menší jedné, tak násobíme.
            pricePerPiece = BigDecimal.ZERO;
            feePerPiece = BigDecimal.ZERO;
        }else{
            // Jinak dělíme.
            pricePerPiece = price.divide(quantity, MathContext.DECIMAL128);
            feePerPiece = fee.divide(quantity,  MathContext.DECIMAL128);
        }

        // Zjistíme cenu nákupu pro využité množství.
        price = shared.getTwoDecimalBigDecimal(pricePerPiece.multiply(usedFrom));
        fee = shared.getTwoDecimalBigDecimal(feePerPiece.multiply(usedFrom));

        // Zjistíme celkovou cenu (včetně poplatku).
        BigDecimal total = price.add(fee);

        // Převedeme měnu na CZK, nejedná-li se o CZK.
        if(buy.currency.equals("EUR")){
            price = shared.getTwoDecimalBigDecimal(price.multiply(shared.getBigDecimal(eurExchangeRate)));
            fee = shared.getTwoDecimalBigDecimal(fee.multiply(shared.getBigDecimal(eurExchangeRate)));
            total = shared.getTwoDecimalBigDecimal(total.multiply(shared.getBigDecimal(eurExchangeRate)));
        }else if(buy.currency.equals("USD")){
            price = shared.getTwoDecimalBigDecimal(price.multiply(shared.getBigDecimal(usdExchangeRate)));
            fee = shared.getTwoDecimalBigDecimal(fee.multiply(shared.getBigDecimal(usdExchangeRate)));
            total = shared.getTwoDecimalBigDecimal(total.multiply(shared.getBigDecimal(usdExchangeRate)));
        }

        // Vrátím nákup.
        return new BuyTransactionPDFList(calendar.getDateFromMillis(buy.date), buy.time, usedFrom.toPlainString(), shortName, price.toString(), fee.toString(), total.toString());
    }

    /**
     * Metoda pro získání seznamu směn k výpisu za dané daňové období
     * @param dateFrom Začátek daňového období
     * @param dateTo Konec daňového období
     * @return Seznam směn k výpisu
     */
    private ArrayList<ChangeTransactionPDFList> changeValue(long dateFrom, long dateTo){
        AppDatabase db = AppDatabase.getDbInstance(context);
        ArrayList<ChangeTransactionPDFList> changeList = new ArrayList<>();
        ChangeTransactionPDFList changeTransaction;

        // Sečíst zisky ze směny od první v roce po poslední (Zaokrouhleno na dvě desetiná místa (kvůli pozdějšímu výpisu v PDF)).
        List<TransactionWithPhotos> salesInYear = db.databaseDao().getUsedChangeBetween(dateFrom, dateTo);
        for(TransactionWithPhotos change : salesInYear){
            TransactionEntity changeEntity = change.transaction;
            BigDecimal fee = shared.getTwoDecimalBigDecimal(change.transaction.fee);
            BigDecimal profit = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(change.transaction.priceBought).subtract(fee));
            if(change.transaction.currency.equals("EUR")){
                fee = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(change.transaction.fee).multiply(shared.getBigDecimal(eurExchangeRate)));
                profit = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(change.transaction.priceBought).multiply(shared.getBigDecimal(eurExchangeRate)).subtract(fee));
            }else if(change.transaction.currency.equals("USD")){
                fee = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(change.transaction.fee).multiply(shared.getBigDecimal(usdExchangeRate)));
                profit = shared.getTwoDecimalBigDecimal(shared.getBigDecimal(change.transaction.priceBought).multiply(shared.getBigDecimal(usdExchangeRate)).subtract(fee));
            }
            String shortNameBought = db.databaseDao().getCryptoShortNameById(changeEntity.uidBought);
            String shortNameSold = db.databaseDao().getCryptoShortNameById(changeEntity.uidSold);

            changeTransaction = new ChangeTransactionPDFList(calendar.getDateFromMillis(changeEntity.date), changeEntity.quantityBought, shortNameBought, changeEntity.quantitySold, shortNameSold, profit.toString());
            changeList.add(changeTransaction);
        }
        return changeList;
    }

    /**
     * Metoda k seřazení seznamu podle data
     * @param data Seznam k seřazení
     * @return Seřadený seznam
     */
    private ArrayList<BuyTransactionPDFList> sortListByDate(ArrayList<BuyTransactionPDFList> data){
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
        return data;
    }

    /**
     * Metoda k seřazení seznamu podle času
     * @param data Seznam k seřazení
     * @return Seřadený seznam
     */
    private ArrayList<BuyTransactionPDFList> sortListByTime(ArrayList<BuyTransactionPDFList> data){
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
        return data;
    }

    /**
     * Metoda k uložení informací o vytvořeném PDF do databáze
     */
    private void saveToDb(String fileName, BigDecimal total){
        AppDatabase db = AppDatabase.getDbInstance(context);

        PDFEntity pdf = new PDFEntity();
        pdf.fileName = fileName;
        pdf.total = total.toPlainString();
        pdf.year = selectedYear;
        pdf.date = calendar.getActualDateMillis();
        pdf.created = calendar.getActualDateTimeMillis();

        db.databaseDao().insertPDF(pdf);

        dataList = db.databaseDao().getPDF();
        adapter.setDataList(dataList);
    }

    /**
     * Metoda pro zobrazení dialogového okna při vytváření PDF záznamu v případě, že nemají všechny prodeje správně evidováno nabytí kryptoměny
     * @param dateFrom Daňové období od
     * @param dateTo Daňové období do
     * @param salesInYear Seznam prodejů v daňovém období
     */
    private void confirmDialogUnfinishedCreate(long dateFrom, long dateTo, List<TransactionWithPhotos> salesInYear){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialog);
        builder.setCancelable(true);
        builder.setTitle("Neúplný prodej");
        builder.setMessage("V daném daňovém období se nachází jeden, či více prodejů, " +
                "jenž nemají kompletně evidován nákup prodávané kryptoměny.\n\n" +
                "Přejete si pokračovat v generování PDF?");
        builder.setPositiveButton("Pokračovat",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createPDF(dateFrom, dateTo, salesInYear);
                    }
                });
        builder.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
