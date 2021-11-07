package com.bobcikprogramming.kryptoevidence;

import android.content.Context;
import android.icu.text.SymbolTable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionHistoryEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// https://www.geeksforgeeks.org/image-slider-in-android-using-viewpager/
public class ViewPagerAdapterTransaction extends PagerAdapter{

    private RecyclerView recyclerViewTransactionInfo, recyclerViewTransactionInfoHistory;
    private LinearLayout historyUnderline;
    private TextView historyHeadline;
    private View itemView;

    private LayoutInflater layoutInflater;

    private List<TransactionWithPhotos> dataList;
    private List<TransactionHistoryEntity> dataListHistory;

    private RecyclerViewTransactionsInfo adapter;
    private RecyclerViewTransactionsInfoHistory adapterHistory;

    int position;

    public ViewPagerAdapterTransaction(Context context, List<TransactionWithPhotos> dataList, List<TransactionHistoryEntity> dataListHistory) {
        this.dataList = dataList;
        this.dataListHistory = dataListHistory;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataList.size(); // TODO
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        itemView = layoutInflater.inflate(R.layout.activity_transaction_info, container, false);
        setupUIViews();

        this.position = position;

        TransactionEntity transaction = dataList.get(position).transaction;

        adapter = new RecyclerViewTransactionsInfo(itemView.getContext(), transaction.transactionType.equals("Směna") ? getTransactionForChange(position) : getTransactionForBuyOrSell(position)); //TODO framgent by viewpager prostudovat
        recyclerViewTransactionInfo.setAdapter(adapter);

        adapterHistory = new RecyclerViewTransactionsInfoHistory(itemView.getContext(), getHistoryList(position)); //TODO framgent by viewpager prostudovat
        recyclerViewTransactionInfoHistory.setAdapter(adapterHistory);

        TextView headline = itemView.findViewById(R.id.infoOperationType);
        LinearLayout nextItem = itemView.findViewById(R.id.layoutNextItem);
        if(position == (getCount()-1)){
            nextItem.setVisibility(View.INVISIBLE);
        }else{
            nextItem.setVisibility(View.VISIBLE);
        }
        headline.setText(transaction.transactionType);
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    //https://stackoverflow.com/a/7287121
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    private void setupUIViews(){
        recyclerViewTransactionInfo = itemView.findViewById(R.id.recyclerViewTransactionInfo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
        recyclerViewTransactionInfo.setLayoutManager(linearLayoutManager);
        recyclerViewTransactionInfo.setHasFixedSize(true);

        recyclerViewTransactionInfoHistory = itemView.findViewById(R.id.recyclerViewTransactionInfoHistory);
        LinearLayoutManager linearLayoutManagerHistory = new LinearLayoutManager(itemView.getContext());
        recyclerViewTransactionInfoHistory.setLayoutManager(linearLayoutManagerHistory);
        recyclerViewTransactionInfoHistory.setHasFixedSize(false);

        historyHeadline = itemView.findViewById(R.id.historyHeadline);
        historyUnderline = itemView.findViewById(R.id.historyUnderline);
    }

    public void updateDatalists(List<TransactionWithPhotos> dataList, List<TransactionHistoryEntity> dataListHistory, int position){
        this.dataList = dataList;
        this.dataListHistory = dataListHistory;

        this.position = position;

        System.out.println("..................." + getTransactionForBuyOrSell(position).get(1).getLeftValue());
        System.out.println("-------------------" + dataList.get(position).transaction.transactionType);

        adapter.updateDataList(dataList.get(position).transaction.transactionType.equals("Směna") ? getTransactionForChange(position) : getTransactionForBuyOrSell(position));
        adapter.notifyDataSetChanged();

        adapterHistory.updateDataList(getHistoryList(position));
        adapterHistory.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    private ArrayList<TransactionInfoList> getTransactionForBuyOrSell(int position){
        ArrayList<TransactionInfoList> transactionInfoList = new ArrayList<>();

        TransactionInfoList firstRow = new TransactionInfoList();
        TransactionInfoList secondRow = new TransactionInfoList();
        TransactionInfoList thirdRow = new TransactionInfoList();
        TransactionInfoList fourthRow = new TransactionInfoList();
        TransactionInfoList fifthRow = new TransactionInfoList();
        TransactionInfoList sixthRow = new TransactionInfoList();

        TransactionEntity transaction = dataList.get(position).transaction;

        // Společná parametry
        firstRow.setRightDesc("Zkratka");

        secondRow.setRightDesc("Měna");

        thirdRow.setLeftDesc("Cena za kus");
        thirdRow.setRightDesc("Měna");
        thirdRow.setRightValue(transaction.currency);

        fourthRow.setLeftDesc("Poplatek");
        fourthRow.setRightDesc("Měna");
        fourthRow.setLeftValue(transaction.fee);
        fourthRow.setRightValue(transaction.currency);

        fifthRow.setRightDesc("Měna");
        fifthRow.setRightValue(transaction.currency);

        sixthRow.setLeftDesc("Datum");
        sixthRow.setRightDesc("Čas");
        sixthRow.setLeftValue(transaction.date);
        sixthRow.setRightValue(transaction.time);


        if(transaction.transactionType.equals("Nákup")){
            firstRow.setLeftDesc("Koupená měna");
            firstRow.setLeftValue(transaction.longNameBought);
            firstRow.setRightValue(transaction.shortNameBought);

            secondRow.setLeftDesc("Koupené množství");
            secondRow.setLeftValue(transaction.quantityBought);
            secondRow.setRightValue(transaction.shortNameBought);

            thirdRow.setLeftValue(transaction.priceBought);

            fifthRow.setLeftDesc("Celková cena");
            fifthRow.setLeftValue(transaction.quantitySold);
        }else{
            firstRow.setLeftDesc("Prodaná měna");
            firstRow.setLeftValue(transaction.longNameSold);
            firstRow.setRightValue(transaction.shortNameSold);

            secondRow.setLeftDesc("Prodané množství");
            secondRow.setLeftValue(transaction.quantitySold);
            secondRow.setRightValue(transaction.shortNameSold);

            thirdRow.setLeftValue(transaction.priceSold);

            fifthRow.setLeftDesc("Celkový zisk");
            fifthRow.setLeftValue(transaction.quantityBought);
        }

        transactionInfoList.add(firstRow);
        transactionInfoList.add(secondRow);
        transactionInfoList.add(thirdRow);
        transactionInfoList.add(fourthRow);
        transactionInfoList.add(fifthRow);
        transactionInfoList.add(sixthRow);

        return transactionInfoList;
    }

    private ArrayList<TransactionInfoList> getTransactionForChange(int position){
        ArrayList<TransactionInfoList> transactionInfoList = new ArrayList<>();

        TransactionInfoList firstRow = new TransactionInfoList();
        TransactionInfoList secondRow = new TransactionInfoList();
        TransactionInfoList thirdRow = new TransactionInfoList();
        TransactionInfoList fourthRow = new TransactionInfoList();
        TransactionInfoList fifthRow = new TransactionInfoList();
        TransactionInfoList sixthRow = new TransactionInfoList();
        TransactionInfoList seventhRow = new TransactionInfoList();
        TransactionInfoList eighthRow = new TransactionInfoList();

        TransactionEntity transaction = dataList.get(position).transaction;

        firstRow.setLeftDesc("Koupená měna");
        firstRow.setRightDesc("Zkratka");
        firstRow.setLeftValue(transaction.longNameBought);
        firstRow.setRightValue(transaction.shortNameBought);

        secondRow.setLeftDesc("Koupené množství");
        secondRow.setRightDesc("Měna");
        secondRow.setLeftValue(transaction.quantityBought);
        secondRow.setRightValue(transaction.shortNameBought);

        thirdRow.setLeftDesc("Cena za kus");
        thirdRow.setRightDesc("Měna");
        thirdRow.setLeftValue(transaction.priceBought);
        thirdRow.setRightValue(transaction.currency);

        fourthRow.setLeftDesc("Prodaná měna");
        fourthRow.setRightDesc("Zkratka");
        fourthRow.setLeftValue(transaction.longNameSold);
        fourthRow.setRightValue(transaction.shortNameSold);

        fifthRow.setLeftDesc("Prodané množství");
        fifthRow.setRightDesc("Měna");
        fifthRow.setLeftValue(transaction.quantitySold);
        fifthRow.setRightValue(transaction.shortNameSold);

        sixthRow.setLeftDesc("Cena za kus");
        sixthRow.setRightDesc("Měna");
        sixthRow.setLeftValue(transaction.priceSold);
        sixthRow.setRightValue(transaction.currency);

        seventhRow.setLeftDesc("Poplatek");
        seventhRow.setRightDesc("Měna");
        seventhRow.setLeftValue(transaction.fee);
        seventhRow.setRightValue(transaction.currency);

        eighthRow.setLeftDesc("Datum");
        eighthRow.setRightDesc("Čas");
        eighthRow.setLeftValue(transaction.date);
        eighthRow.setRightValue(transaction.time);

        transactionInfoList.add(firstRow);
        transactionInfoList.add(secondRow);
        transactionInfoList.add(thirdRow);
        transactionInfoList.add(fourthRow);
        transactionInfoList.add(fifthRow);
        transactionInfoList.add(sixthRow);
        transactionInfoList.add(seventhRow);
        transactionInfoList.add(eighthRow);

        return transactionInfoList;
    }

    private ArrayList<TransactionHistoryList> getHistoryList(int position){
        ArrayList<TransactionHistoryEntity> history = getHistoryForActualTransaction(position);
        ArrayList<TransactionHistoryList> historyOfActualTransaction = new ArrayList<>();

        if(history.size() > 0){
            for(TransactionHistoryEntity transactionHistory : history){
                TransactionHistoryList transaction = new TransactionHistoryList();

                transaction.setChangeValueDate(transactionHistory.dateOfChange);
                transaction.setChangeValueTime(transactionHistory.timeOfChange);

                if(transactionHistory.transactionType.equals("Nákup")){
                    transaction.setTransactionType("Nákup");
                    if(transactionHistory.quantityBought != null){
                        transaction.setQuantityBoughtDesc("Koupené množství");
                        transaction.setQuantityBoughtValue(transactionHistory.quantityBought);
                    }
                    if(transactionHistory.priceBought != null){
                        transaction.setPriceBoughtDesc("Cena za kus");
                        transaction.setPriceBoughtValue(transactionHistory.priceBought);
                    }
                }

                if(transactionHistory.transactionType.equals("Prodej")){
                    transaction.setTransactionType("Prodej");
                    if(transactionHistory.quantitySold != null){
                        transaction.setQuantitySoldDesc("Prodané množství");
                        transaction.setQuantitySoldValue(transactionHistory.quantitySold);
                    }
                    if(transactionHistory.priceSold != null){
                        transaction.setPriceSoldDesc("Cena za kus");
                        transaction.setPriceSoldValue(transactionHistory.priceSold);
                    }
                }

                if(transactionHistory.transactionType.equals("Směna")){
                    transaction.setTransactionType("Směna");
                    if(transactionHistory.quantityBought != null){
                        transaction.setQuantityBoughtDesc("Koupené množství");
                        transaction.setQuantityBoughtValue(transactionHistory.quantityBought);
                    }
                    if(transactionHistory.priceBought != null){
                        transaction.setPriceBoughtDesc("Cena za kus");
                        transaction.setPriceBoughtValue(transactionHistory.priceBought);
                    }
                    if(transactionHistory.longNameBought != null){
                        transaction.setLongNameSoldDesc("Prodaná měna");
                        transaction.setLongNameSoldValue(transactionHistory.longNameSold);
                    }
                    if(transactionHistory.quantitySold != null){
                        transaction.setQuantitySoldDesc("Prodané množství");
                        transaction.setQuantitySoldValue(transactionHistory.quantitySold);
                    }
                    if(transactionHistory.priceSold != null){
                        transaction.setPriceSoldDesc("Cena za kus");
                        transaction.setPriceSoldValue(transactionHistory.priceSold);
                    }
                }

                if(transactionHistory.currency != null){
                    transaction.setCurrencyDesc("Cena v měně");
                    transaction.setCurrencyValue(transactionHistory.currency);
                }
                if(transactionHistory.fee != null){
                    transaction.setFeeDesc("Poplatek");
                    transaction.setFeeValue(transactionHistory.fee);
                }
                if(transactionHistory.date != null){
                    transaction.setDateDesc("Datum provedení");
                    transaction.setDateValue(transactionHistory.date);
                }
                if(transactionHistory.time != null){
                    transaction.setTimeDesc("Čas provedení");
                    transaction.setTimeValue(transactionHistory.time);
                }
                if(transactionHistory.note != null){
                    transaction.setNoteDesc("Poznámka o změně");
                    transaction.setNoteValue(transactionHistory.note);
                }

                historyOfActualTransaction.add(transaction);
            }
        }else{
            historyUnderline.setVisibility(View.GONE);
            historyHeadline.setVisibility(View.GONE);

        }

        sortListByTime(historyOfActualTransaction);
        sortListByDate(historyOfActualTransaction);

        return historyOfActualTransaction;
    }

    private ArrayList<TransactionHistoryEntity> getHistoryForActualTransaction(int position){
        ArrayList<TransactionHistoryEntity> historyOfActualTransaction = new ArrayList<>();
        for(TransactionHistoryEntity history : dataListHistory){
            if(history.parentTransactionId != dataList.get(position).transaction.uidTransaction){
                continue;
            }
            historyOfActualTransaction.add(history);
        }
        return historyOfActualTransaction;
    }

    private void sortListByDate(List<TransactionHistoryList> data){
        TransactionHistoryList tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                try{
                    Date dateFirst = format.parse(data.get(j).changeValueDate);
                    Date dateSecond = format.parse(data.get(j+1).changeValueDate);
                    if(dateFirst.compareTo(dateSecond) < 0){
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

    private void sortListByTime(List<TransactionHistoryList> data){
        TransactionHistoryList tmp;
        for(int i = 0; i < data.size() - 1; i++){
            for(int j = 0; j < data.size() - i - 1; j++){
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                try{
                    Date timeFirst = format.parse(data.get(j).changeValueTime);
                    Date timeSecond = format.parse(data.get(j+1).changeValueTime);
                    if(timeFirst.compareTo(timeSecond) < 0){
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
}

