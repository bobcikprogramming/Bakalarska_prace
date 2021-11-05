package com.bobcikprogramming.kryptoevidence;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// https://www.geeksforgeeks.org/image-slider-in-android-using-viewpager/
public class ViewPagerAdapterTransaction extends PagerAdapter{

    private RecyclerView recyclerViewTransactionInfo;
    private View itemView;

    private LayoutInflater layoutInflater;

    private List<TransactionWithPhotos> dataList;

    private RecyclerViewTransactionsInfo adapter;

    public ViewPagerAdapterTransaction(Context context, List<TransactionWithPhotos> dataList) {
        this.dataList = dataList;
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

        TransactionEntity transaction = dataList.get(position).transaction;

        adapter = new RecyclerViewTransactionsInfo(itemView.getContext(), transaction.transactionType.equals("Směna") ? getTransactionForChange(position) : getTransactionForBuyOrSell(position)); //TODO framgent by viewpager prostudovat
        recyclerViewTransactionInfo.setAdapter(adapter);

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

    private void setupUIViews(){
        recyclerViewTransactionInfo = itemView.findViewById(R.id.recyclerViewTransactionInfo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
        recyclerViewTransactionInfo.setLayoutManager(linearLayoutManager);
        recyclerViewTransactionInfo.setHasFixedSize(true);
    }

    private ArrayList<RecyclerViewTransactionInfoList> getTransactionForBuyOrSell(int position){
        ArrayList<RecyclerViewTransactionInfoList> transactionInfoList = new ArrayList<>();

        RecyclerViewTransactionInfoList firstRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList secondRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList thirdRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList fourthRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList fifthRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList sixthRow = new RecyclerViewTransactionInfoList();

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
            fifthRow.setLeftValue(getPrice(transaction.quantityBought, transaction.priceBought, transaction.fee));
        }else{
            firstRow.setLeftDesc("Prodaná měna");
            firstRow.setLeftValue(transaction.longNameSold);
            firstRow.setRightValue(transaction.shortNameSold);

            secondRow.setLeftDesc("Prodané množství");
            secondRow.setLeftValue(transaction.quantitySold);
            secondRow.setRightValue(transaction.shortNameSold);

            thirdRow.setLeftValue(transaction.priceSold);

            fifthRow.setLeftDesc("Celkový zisk");
            fifthRow.setLeftValue(getProfit(transaction.quantitySold, transaction.priceSold, transaction.fee));
        }

        transactionInfoList.add(firstRow);
        transactionInfoList.add(secondRow);
        transactionInfoList.add(thirdRow);
        transactionInfoList.add(fourthRow);
        transactionInfoList.add(fifthRow);
        transactionInfoList.add(sixthRow);

        return transactionInfoList;
    }

    private ArrayList<RecyclerViewTransactionInfoList> getTransactionForChange(int position){
        ArrayList<RecyclerViewTransactionInfoList> transactionInfoList = new ArrayList<>();

        RecyclerViewTransactionInfoList firstRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList secondRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList thirdRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList fourthRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList fifthRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList sixthRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList seventhRow = new RecyclerViewTransactionInfoList();
        RecyclerViewTransactionInfoList eighthRow = new RecyclerViewTransactionInfoList();

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

    private String getPrice(String quantity, String price, String fee) {
        double toRound = (Double.parseDouble(quantity) * Double.parseDouble(price)) + Double.parseDouble(fee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return String.valueOf(result);
    }

    private String getProfit(String quantity, String price, String fee) {
        double toRound = (Double.parseDouble(quantity) *  Double.parseDouble(price)) - Double.parseDouble(fee);
        double result = (double)Math.round(toRound * 100d) / 100d;
        return String.valueOf(result);
    }
}

