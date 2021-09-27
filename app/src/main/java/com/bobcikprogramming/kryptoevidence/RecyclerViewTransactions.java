package com.bobcikprogramming.kryptoevidence;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bobcikprogramming.kryptoevidence.database.TransactionEntity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class RecyclerViewTransactions extends RecyclerView.Adapter<RecyclerViewTransactions.ViewHolder>  {

    private List<TransactionEntity> dataList;
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
        TransactionEntity data = this.dataList.get(position);

        holder.textViewOperation.setText(data.transactionType);
        holder.textViewDate.setText(data.date);
        holder.textViewNameBuy.setText(data.nameBought);
        holder.textViewNameSell.setText(data.nameSold);
        holder.textViewQuantityBuy.setText(String.valueOf(data.quantityBought));
        holder.textViewQuantitySell.setText(String.valueOf(data.quantitySold));
        holder.textViewPriceBuy.setText(String.valueOf(data.priceBought));
        holder.textViewPriceSell.setText(String.valueOf(data.priceSold));

        changeItemViewByTransactionType(data.transactionType, holder.textViewOperation, holder.textViewDesPriceBuy, holder.textViewPriceBuy, holder.textViewDesPriceSell, holder.textViewPriceSell);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOperation, textViewDate, textViewNameBuy, textViewNameSell, textViewQuantityBuy, textViewQuantitySell, textViewDesPriceBuy, textViewDesPriceSell, textViewPriceBuy, textViewPriceSell;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewOperation = itemView.findViewById(R.id.textViewOperation);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewNameBuy = itemView.findViewById(R.id.textViewNameBuy);
            textViewNameSell = itemView.findViewById(R.id.textViewNameSell);
            textViewQuantityBuy = itemView.findViewById(R.id.textViewQuantityBuy);
            textViewQuantitySell = itemView.findViewById(R.id.textViewQuantitySell);
            textViewDesPriceBuy = itemView.findViewById(R.id.textViewDescriptionPriceBuy);
            textViewDesPriceSell = itemView.findViewById(R.id.textViewDescriptionPriceSell);
            textViewPriceBuy = itemView.findViewById(R.id.textViewPriceBuy);
            textViewPriceSell = itemView.findViewById(R.id.textViewPriceSell);
        }
    }

    /*public void addToArray(RecyclerViewTransactionsData data){
        dataList.add(0, data);
        notifyItemInserted(0);
    }*/

    private void changeItemViewByTransactionType(String transactionType, TextView headline, TextView tvDesPriceBuy, TextView tvPriceBuy, TextView tvDesPriceSell, TextView tvPriceSell){
        switch (transactionType){
            case "Nákup":
                headline.setTextColor(ContextCompat.getColor(context, R.color.headlineBuy));
                tvDesPriceBuy.setVisibility(View.VISIBLE);
                tvPriceBuy.setVisibility(View.VISIBLE);
                tvDesPriceSell.setVisibility(View.GONE);
                tvPriceSell.setVisibility(View.GONE);
                break;
            case "Prodej":
                headline.setTextColor(ContextCompat.getColor(context, R.color.headlineSell));
                tvDesPriceBuy.setVisibility(View.GONE);
                tvPriceBuy.setVisibility(View.GONE);
                tvDesPriceSell.setVisibility(View.VISIBLE);
                tvPriceSell.setVisibility(View.VISIBLE);
                break;
            case "Směna":
                headline.setTextColor(ContextCompat.getColor(context, R.color.headlineChange));
                tvDesPriceBuy.setVisibility(View.VISIBLE);
                tvPriceBuy.setVisibility(View.VISIBLE);
                tvDesPriceSell.setVisibility(View.VISIBLE);
                tvPriceSell.setVisibility(View.VISIBLE);
                break;

        }
    }

    public void setTransactionData(List<TransactionEntity> transactionData){
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