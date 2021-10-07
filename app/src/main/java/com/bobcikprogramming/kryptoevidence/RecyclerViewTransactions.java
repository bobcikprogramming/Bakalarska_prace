package com.bobcikprogramming.kryptoevidence;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;
import com.bobcikprogramming.kryptoevidence.database.TransactionWithPhotos;

import java.util.List;

public class RecyclerViewTransactions extends RecyclerView.Adapter<RecyclerViewTransactions.ViewHolder>{

    private List<TransactionWithPhotos> dataList;
    private Context context;

    public RecyclerViewTransactions(Context context/*, ArrayList<RecyclerViewRaidingAdvancedData> dataList*/) {
        this.context = context;

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
        holder.textViewDate.setText(transaction.date);
        holder.textViewTime.setText(transaction.time);

        changeItemViewByTransactionType(transaction.transactionType, holder, data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOperation, textViewDate, textViewTime, tvNameFL, tvNameSL, tvQuantityFL, tvQuantitySL, tvDesPriceFL, tvDesPriceSL, tvPriceFL, tvPriceSL, tvDescriptionFL, tvDescriptionSL;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewOperation = itemView.findViewById(R.id.textViewOperation);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            tvNameFL = itemView.findViewById(R.id.textViewNameFirstLine);
            tvNameSL = itemView.findViewById(R.id.textViewNameSecondLine);
            tvQuantityFL = itemView.findViewById(R.id.textViewQuantityFirstLine);
            tvQuantitySL = itemView.findViewById(R.id.textViewQuantitySecondLine);
            tvDesPriceFL = itemView.findViewById(R.id.textViewDescriptionPriceBuy);
            tvDesPriceSL = itemView.findViewById(R.id.textViewDescriptionPriceSell);
            tvPriceFL = itemView.findViewById(R.id.textViewPriceFirst);
            tvPriceSL = itemView.findViewById(R.id.textViewPriceSecondLine);
            tvDescriptionFL = itemView.findViewById(R.id.textViewDescriptionFirstLine);
            tvDescriptionSL = itemView.findViewById(R.id.textViewDescriptionSecondLine);
        }
    }

    /*public void addToArray(RecyclerViewTransactionsData data){
        dataList.add(0, data);
        notifyItemInserted(0);
    }*/

    private void changeItemViewByTransactionType(String transactionType, ViewHolder holder, TransactionWithPhotos data){
        TransactionEntity transaction = data.transaction;
        switch (transactionType){
            case "Nákup":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.headlineBuy));
                holder.tvDesPriceFL.setVisibility(View.VISIBLE);
                holder.tvPriceFL.setVisibility(View.VISIBLE);
                holder.tvDesPriceSL.setVisibility(View.GONE);
                holder.tvPriceSL.setVisibility(View.GONE);

                holder.tvDescriptionFL.setText("Koupeno:");
                holder.tvDescriptionSL.setText("Platba:");

                holder.tvNameFL.setText(transaction.nameBought);
                holder.tvQuantityFL.setText(String.valueOf(transaction.quantityBought));
                holder.tvPriceFL.setText(String.valueOf(transaction.priceBought + " " + transaction.currency));
                holder.tvNameSL.setText(transaction.currency);
                holder.tvQuantitySL.setText(String.valueOf(transaction.quantitySold));
                break;
            case "Prodej":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.headlineSell));
                holder.tvDesPriceFL.setVisibility(View.VISIBLE);
                holder.tvPriceFL.setVisibility(View.VISIBLE);
                holder.tvDesPriceSL.setVisibility(View.GONE);
                holder.tvPriceSL.setVisibility(View.GONE);

                holder.tvDescriptionFL.setText("Prodáno:");
                holder.tvDescriptionSL.setText("Zisk:");

                holder.tvNameFL.setText(transaction.nameSold);
                holder.tvQuantityFL.setText(String.valueOf(transaction.quantitySold));
                holder.tvPriceFL.setText(String.valueOf(transaction.priceSold + " " + transaction.currency));
                holder.tvNameSL.setText(transaction.currency);
                holder.tvQuantitySL.setText(String.valueOf(transaction.quantityBought));
                break;
            case "Směna":
                holder.textViewOperation.setTextColor(ContextCompat.getColor(context, R.color.headlineChange));
                holder.tvDesPriceFL.setVisibility(View.VISIBLE);
                holder.tvPriceFL.setVisibility(View.VISIBLE);
                holder.tvDesPriceSL.setVisibility(View.VISIBLE);
                holder.tvPriceSL.setVisibility(View.VISIBLE);

                holder.tvDescriptionFL.setText("Prodáno:");
                holder.tvDescriptionSL.setText("Zisk:");

                holder.tvNameFL.setText(transaction.nameSold);
                holder.tvQuantityFL.setText(String.valueOf(transaction.quantitySold));
                holder.tvPriceFL.setText(String.valueOf(transaction.priceSold + " " + transaction.currency));
                holder.tvNameSL.setText(transaction.nameBought);
                holder.tvQuantitySL.setText(String.valueOf(transaction.quantityBought));
                holder.tvPriceSL.setText(String.valueOf(transaction.priceBought + " " + transaction.currency));
                break;

        }
    }

    public void setTransactionData(List<TransactionWithPhotos> transactionData){
        this.dataList = transactionData;
        Toast.makeText(context, String.valueOf(this.dataList.size()), Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    public void removeFromArray(int position){
        dataList.remove(position);
        notifyDataSetChanged();
    }
}

class RecyclerViewTransactionsData {

    private String operation, nameBuy, nameSell;
    private Double quantityBuy, quantitySell;

    public RecyclerViewTransactionsData(String operation, String nameBuy, Double quantityBuy, String nameSell, Double quantitySell){
        this.operation = operation;
        this.nameBuy = nameBuy;
        this.quantityBuy = quantityBuy;
        this.nameSell = nameSell;
        this.quantitySell = quantitySell;
    }

    public String getOperation() {
        return operation;
    }

    public String getNameBuy() {
        return nameBuy;
    }

    public Double getQuantityBuy() {
        return quantityBuy;
    }

    public String getNameSell() {
        return nameSell;
    }

    public Double getQuantitySell() {
        return quantitySell;
    }
}