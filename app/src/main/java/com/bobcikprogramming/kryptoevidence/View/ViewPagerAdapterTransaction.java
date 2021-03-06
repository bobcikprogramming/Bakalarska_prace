package com.bobcikprogramming.kryptoevidence.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bobcikprogramming.kryptoevidence.Controller.TransactionHistoryList;
import com.bobcikprogramming.kryptoevidence.Controller.ViewPagerAdapterTransactionController;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionHistoryEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

/**
 * Třída vytvořena na základě tutoriálu z:
 * Odkaz:   https://www.geeksforgeeks.org/image-slider-in-android-using-viewpager/
 * Datum:   15. září 2020
 * Autor:   onlyklohan
 * Autor:   https://auth.geeksforgeeks.org/user/onlyklohan/articles
 */
public class ViewPagerAdapterTransaction extends PagerAdapter {

    private RecyclerView recyclerViewTransactionInfo, recyclerViewTransactionInfoHistory;
    private LinearLayout historyUnderline, historyLayout, layoutPhotos, historyBackground;
    private TextView historyHeadline;
    private ImageView imvButtonShowPhotos;
    private View itemView;

    private LayoutInflater layoutInflater;
    private Context context;

    private RecyclerViewTransactionsInfo adapter;
    private RecyclerViewTransactionsInfoHistory adapterHistory;

    private ViewPagerAdapterTransactionController controller;

    public ViewPagerAdapterTransaction(Context context, List<TransactionWithPhotos> dataList, List<TransactionHistoryEntity> dataListHistory) {
        controller = new ViewPagerAdapterTransactionController(dataList, dataListHistory);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return controller.getDataList().size(); // TODO
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        itemView = layoutInflater.inflate(R.layout.activity_transaction_info, container, false);

        controller.setPosition(position);

        TransactionEntity transaction = controller.getDataList().get(position).transaction;

        setupUIViews();
        setupHeadline(transaction);
        openGalery(controller.getPosition());
        showPhotosIfNotEmpty();

        adapter = new RecyclerViewTransactionsInfo(itemView.getContext(), transaction.transactionType.equals("Směna") ? controller.getTransactionForChange(context) : controller.getTransactionForBuyOrSell(context)); //TODO framgent by viewpager prostudovat
        recyclerViewTransactionInfo.setAdapter(adapter);

        ArrayList<TransactionHistoryList> historyList = controller.getHistoryList(context);
        adapterHistory = new RecyclerViewTransactionsInfoHistory(itemView.getContext(), historyList); //TODO framgent by viewpager prostudovat
        if(historyList.isEmpty()){
            historyUnderline.setVisibility(View.GONE);
            historyHeadline.setVisibility(View.GONE);
            historyLayout.setVisibility(View.GONE);
            historyBackground.setVisibility(View.GONE);
        }
        recyclerViewTransactionInfoHistory.setAdapter(adapterHistory);

        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    /**
     * Metoda pro inicializování prvků UI.
     */
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
        historyLayout = itemView.findViewById(R.id.historyLayout);
        historyBackground = itemView.findViewById(R.id.historyBackground);

        layoutPhotos = itemView.findViewById(R.id.layoutPhotos);

        imvButtonShowPhotos = itemView.findViewById(R.id.imvButtonShowPhotos);
    }

    /**
     * Metoda pro nastavení nadpisu a viditelnosti indikátoru další transakce.
     * @param transaction
     */
    private void setupHeadline(TransactionEntity transaction){
        TextView headline = itemView.findViewById(R.id.infoOperationType);
        LinearLayout nextItem = itemView.findViewById(R.id.layoutNextItem);
        if(controller.getPosition() == (getCount()-1)){
            nextItem.setVisibility(View.INVISIBLE);
        }else{
            nextItem.setVisibility(View.VISIBLE);
        }
        headline.setText(transaction.transactionType);
    }


    /**
     * Metoda k aktualizování výpisu transakce.
     * @param dataList Pole s daty transakce k výpisu
     * @param dataListHistory Pole s daty historie transakce k výpisu
     * @param position Pozice transakce z pole transakcí
     */
    public void updateDatalists(List<TransactionWithPhotos> dataList, List<TransactionHistoryEntity> dataListHistory, int position){
        controller.setDataList(dataList);
        controller.setDataListHistory(dataListHistory);

        controller.setPosition(position);

        adapter.updateDataList(dataList.get(position).transaction.transactionType.equals("Směna") ? controller.getTransactionForChange(context) : controller.getTransactionForBuyOrSell(context));
        adapter.notifyDataSetChanged();

        ArrayList<TransactionHistoryList> historyList = controller.getHistoryList(context);
        if(historyList.isEmpty()){
            historyUnderline.setVisibility(View.GONE);
            historyHeadline.setVisibility(View.GONE);
            historyLayout.setVisibility(View.GONE);
            historyBackground.setVisibility(View.GONE);
        }

        adapterHistory.updateDataList(historyList);
        adapterHistory.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    /**
     * Metoda k otevření activity TransactionPhotoViewer.
     * @param postion Pozice transakce z pole transakcí
     */
    private void openGalery(int postion){
        imvButtonShowPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoViewer = new Intent(itemView.getContext(), TransactionPhotoViewer.class);
                photoViewer.putExtra("transactionID", String.valueOf(controller.getDataList().get(postion).transaction.uidTransaction));
                itemView.getContext().startActivity(photoViewer);
            }
        });
    }

    /**
     * Metoda k zobrazení ImageView se snímkem, obsahuje-li transakce nějaký snímek.
     */
    public void showPhotosIfNotEmpty(){
        if(!controller.getPhotos().isEmpty()){
            layoutPhotos.setVisibility(View.VISIBLE);
            imvButtonShowPhotos.setImageURI(Uri.parse(controller.getPhotos().get(0).dest));
        }else{
            layoutPhotos.setVisibility(View.GONE);
        }
    }
}