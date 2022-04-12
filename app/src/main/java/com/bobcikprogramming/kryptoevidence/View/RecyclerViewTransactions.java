package com.bobcikprogramming.kryptoevidence.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bobcikprogramming.kryptoevidence.Controller.CalendarManager;
import com.bobcikprogramming.kryptoevidence.Controller.SharedMethods;
import com.bobcikprogramming.kryptoevidence.Model.AppDatabase;
import com.bobcikprogramming.kryptoevidence.Model.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.Model.TransactionWithPhotos;
import com.bobcikprogramming.kryptoevidence.R;

import java.math.BigDecimal;
import java.util.List;

public class RecyclerViewTransactions extends RecyclerView.Adapter<RecyclerViewTransactions.ViewHolder>{

    private List<TransactionWithPhotos> dataList;
    private Context context;
    private View.OnClickListener myClickListener;

    private SharedMethods shared;
    private CalendarManager calendar;

    public RecyclerViewTransactions(Context context, View.OnClickListener myClickListener) {
        this.context = context;
        this. myClickListener = myClickListener;
        shared = new SharedMethods();
        calendar = new CalendarManager();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_transactions, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionWithPhotos data = this.dataList.get(position);
        TransactionEntity transaction = data.transaction;

        holder.textViewOperation.setText(transaction.transactionType);
        holder.textViewDate.setText(calendar.getDateFromMillis(transaction.date));

        changeItemViewByTransactionType(transaction.transactionType, holder);
        loadDataToItems(transaction.transactionType, holder, data);

        holder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOperation, textViewDate, tvNameFC, tvNameSC, tvQuantityFC, tvQuantitySC, tvDescriptionFC, tvDescriptionSC;
        private LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewOperation = itemView.findViewById(R.id.textViewOperation);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            tvNameFC = itemView.findViewById(R.id.textViewNameFirstColumn);
            tvNameSC = itemView.findViewById(R.id.textViewNameSecondColumn);
            tvQuantityFC = itemView.findViewById(R.id.textViewQuantityFirstColumn);
            tvQuantitySC = itemView.findViewById(R.id.textViewQuantitySecondColumn);
            tvDescriptionFC = itemView.findViewById(R.id.textViewDescriptionFirstColumn);
            tvDescriptionSC = itemView.findViewById(R.id.textViewDescriptionSecondColumn);

            item = itemView.findViewById(R.id.layoutTransactions);
            itemView.setOnClickListener(myClickListener);
        }
    }

    private void changeItemViewByTransactionType(String transactionType, ViewHolder holder){
        switch (transactionType) {
            case "Nákup":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.green));
                holder.tvDescriptionFC.setText("Koupeno");
                holder.tvDescriptionSC.setText("Platba");
                break;
            case "Prodej":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.red));
                holder.tvDescriptionFC.setText("Prodáno");
                if(shared.getBigDecimal(dataList.get(holder.getAdapterPosition()).transaction.quantityBought).compareTo(BigDecimal.ZERO) < 0){
                    holder.tvDescriptionSC.setText("Ztráta");
                }else {
                    holder.tvDescriptionSC.setText("Zisk");
                }
                break;
            case "Směna":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.blue));
                holder.tvDescriptionFC.setText("Koupeno");
                holder.tvDescriptionSC.setText("Prodáno");
                break;
        }
        holder.tvNameFC.setFocusable(true);
        holder.tvNameSC.setFocusable(true);
    }

    private void loadDataToItems(String transactionType, ViewHolder holder, TransactionWithPhotos data){
        TransactionEntity transaction = data.transaction;
        AppDatabase db = AppDatabase.getDbInstance(context);
        String shortNameBought = db.databaseDao().getCryptoShortNameById(transaction.uidBought);
        String shortNameSold = db.databaseDao().getCryptoShortNameById(transaction.uidSold);
        switch (transactionType){
            case "Nákup":
                holder.tvNameFC.setText(shortNameBought);
                holder.tvQuantityFC.setText(shared.editNumberForTextView(shared.getBigDecimal(transaction.quantityBought).toPlainString()));
                holder.tvNameSC.setText(transaction.currency);
                holder.tvQuantitySC.setText(shared.editNumberForTextView(shared.getBigDecimal(transaction.priceBought).toPlainString()));
                break;
            case "Prodej":
                holder.tvNameFC.setText(shortNameSold);
                holder.tvQuantityFC.setText(shared.editNumberForTextView(shared.getBigDecimal(transaction.quantitySold).toPlainString()));
                holder.tvNameSC.setText(transaction.currency);
                holder.tvQuantitySC.setText(shared.editNumberForTextView(shared.getBigDecimal(transaction.quantityBought).toPlainString()));
                break;
            case "Směna":
                holder.tvNameFC.setText(shortNameBought);
                holder.tvQuantityFC.setText(shared.editNumberForTextView(shared.getBigDecimal(transaction.quantityBought).toPlainString()));
                holder.tvNameSC.setText(shortNameSold);
                holder.tvQuantitySC.setText(shared.editNumberForTextView(shared.getBigDecimal(transaction.quantitySold).toPlainString()));
                break;

        }
    }

    public void setTransactionData(List<TransactionWithPhotos> transactionData){
        this.dataList = transactionData;
        notifyDataSetChanged();
    }


}